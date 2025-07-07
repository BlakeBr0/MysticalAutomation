package com.blakebr0.mysticalautomation.crafting.recipe;

import com.blakebr0.mysticalautomation.init.ModRecipeSerializers;
import com.blakebr0.mysticalautomation.init.ModRecipeTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;

public class FarmerRecipe implements Recipe<RecipeInput> {
    private final NonNullList<Ingredient> ingredients;
    private final Holder<Block> crop;
    private final int stages;
    private final List<FarmerResult> results;

    private final Ingredient seeds;
    private final Ingredient soil;
    private final Ingredient crux;

    public FarmerRecipe(Ingredient seeds, Ingredient soil, Ingredient crux, Holder<Block> crop, List<FarmerResult> results) {
        this.seeds = seeds;
        this.soil = soil;
        this.crux = crux;
        this.crop = crop;
        this.stages = 3; // TODO

        this.ingredients = NonNullList.of(Ingredient.EMPTY, seeds, soil, crux);
        this.results = results;
    }

    @Override
    public boolean matches(RecipeInput input, Level level) {
        if (input.size() > this.ingredients.size())
            return false;

        for (int i = 0; i < input.size(); i++) {
            var ingredient = this.ingredients.get(i);
            var stack = input.getItem(i);

            if (!ingredient.test(stack))
                return false;
        }

        return true;
    }

    @Override
    public ItemStack assemble(RecipeInput input, HolderLookup.Provider provider) {
        return this.results.getFirst().stack().copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width <= 3 && height == 1;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return this.results.getFirst().stack();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.ingredients;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.FARMER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.FARMER.get();
    }

    public Holder<Block> getCrop() {
        return this.crop;
    }

    public int getStages() {
        return this.stages;
    }

    public List<FarmerResult> getResults() {
        return this.results;
    }

    public List<ItemStack> getRolledResults() {
        var results = new ArrayList<ItemStack>();

        for (var result : this.results) {
            if (result.chance > Math.random())
                results.add(result.stack.copy());
        }

        return results;
    }

    public record FarmerResult(ItemStack stack, float chance) {
        public static final MapCodec<FarmerResult> MAP_CODEC = RecordCodecBuilder.mapCodec(builder ->
                builder.group(
                        ItemStack.CODEC.fieldOf("item").forGetter(result -> result.stack),
                        Codec.FLOAT.fieldOf("chance").forGetter(result -> result.chance)
                ).apply(builder, FarmerResult::new)
        );
        public static final Codec<FarmerResult> CODEC = MAP_CODEC.codec();
        public static final StreamCodec<RegistryFriendlyByteBuf, FarmerResult> STREAM_CODEC = StreamCodec.composite(
                ItemStack.STREAM_CODEC,
                FarmerResult::stack,
                ByteBufCodecs.FLOAT,
                FarmerResult::chance,
                FarmerResult::new
        );
    }

    public static class Serializer implements RecipeSerializer<FarmerRecipe> {
        private static final StreamCodec<RegistryFriendlyByteBuf, Holder<Block>> BLOCK_STREAM_CODEC = ByteBufCodecs.holderRegistry(Registries.BLOCK);
        private static final MapCodec<FarmerRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(builder ->
                builder.group(
                        Ingredient.CODEC.fieldOf("seeds").forGetter(recipe -> recipe.seeds),
                        Ingredient.CODEC.fieldOf("soil").forGetter(recipe -> recipe.soil),
                        Ingredient.CODEC.optionalFieldOf("crux", Ingredient.EMPTY).forGetter(recipe -> recipe.crux),
                        BuiltInRegistries.BLOCK.holderByNameCodec().fieldOf("crop").forGetter(recipe -> recipe.crop),
                        FarmerResult.CODEC.listOf().fieldOf("results").forGetter(recipe -> recipe.results)
                ).apply(builder, FarmerRecipe::new)
        );
        private static final StreamCodec<RegistryFriendlyByteBuf, FarmerRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::toNetwork, Serializer::fromNetwork
        );

        @Override
        public MapCodec<FarmerRecipe> codec() {
            return MAP_CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, FarmerRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static FarmerRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            var seeds = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            var soil = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            var crux = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);

            var crop = BLOCK_STREAM_CODEC.decode(buffer);

            var size = buffer.readVarInt();
            var results = new ArrayList<FarmerResult>();

            for (int i = 0; i < size; i++) {
                results.add(FarmerResult.STREAM_CODEC.decode(buffer));
            }

            return new FarmerRecipe(seeds, soil, crux, crop, results);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, FarmerRecipe recipe) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.seeds);
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.soil);
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.crux);

            BLOCK_STREAM_CODEC.encode(buffer, recipe.crop);

            buffer.writeVarInt(recipe.results.size());

            for (var result : recipe.results) {
                FarmerResult.STREAM_CODEC.encode(buffer, result);
            }
        }
    }
}

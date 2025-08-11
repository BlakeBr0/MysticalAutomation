package com.blakebr0.mysticalautomation.crafting.recipe;

import com.blakebr0.mysticalautomation.api.crafting.IFarmerRecipe;
import com.blakebr0.mysticalautomation.init.ModRecipeSerializers;
import com.blakebr0.mysticalautomation.init.ModRecipeTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class FarmerRecipe implements IFarmerRecipe {
    private final NonNullList<Ingredient> ingredients;
    private final int stages;
    private final List<FarmerResult> results;

    private final Ingredient seeds;
    private final Ingredient soil;
    private final Ingredient crux;

    private final int inputCount;

    public FarmerRecipe(Ingredient seeds, Ingredient soil, Ingredient crux, int stages, List<FarmerResult> results) {
        this.seeds = seeds;
        this.soil = soil;
        this.crux = crux;
        this.ingredients = NonNullList.of(Ingredient.EMPTY, seeds, soil, crux);
        this.stages = stages;
        this.results = results;

        this.inputCount = (int) this.ingredients.stream().filter(i -> !i.isEmpty()).count();
    }

    @Override
    public boolean matches(RecipeInput input, Level level) {
        if (input.size() != this.inputCount)
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

    @Override
    public int getStages() {
        return this.stages;
    }

    @Override
    public List<FarmerResult> getResults() {
        return this.results;
    }

    @Override
    public List<ItemStack> getRolledResults() {
        var results = new ArrayList<ItemStack>();

        for (var result : this.results) {
            if (result.chance() > Math.random())
                results.add(result.stack().copy());
        }

        return results;
    }

    public static class Serializer implements RecipeSerializer<FarmerRecipe> {
        private static final MapCodec<FarmerRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(builder ->
                builder.group(
                        Ingredient.CODEC.fieldOf("seeds").forGetter(recipe -> recipe.seeds),
                        Ingredient.CODEC.fieldOf("soil").forGetter(recipe -> recipe.soil),
                        Ingredient.CODEC.optionalFieldOf("crux", Ingredient.EMPTY).forGetter(recipe -> recipe.crux),
                        Codec.INT.fieldOf("stages").forGetter(recipe -> recipe.stages),
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

            var stages = ByteBufCodecs.VAR_INT.decode(buffer);

            var size = buffer.readVarInt();
            var results = new ArrayList<FarmerResult>();

            for (int i = 0; i < size; i++) {
                results.add(FarmerResult.STREAM_CODEC.decode(buffer));
            }

            return new FarmerRecipe(seeds, soil, crux, stages, results);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, FarmerRecipe recipe) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.seeds);
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.soil);
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.crux);

            ByteBufCodecs.VAR_INT.encode(buffer, recipe.stages);

            buffer.writeVarInt(recipe.results.size());

            for (var result : recipe.results) {
                FarmerResult.STREAM_CODEC.encode(buffer, result);
            }
        }
    }
}

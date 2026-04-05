package com.blakebr0.mysticalautomation.crafting.recipe;

import com.blakebr0.mysticalautomation.api.crafting.IFarmerRecipe;
import com.blakebr0.mysticalautomation.init.ModBlocks;
import com.blakebr0.mysticalautomation.init.ModRecipeTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FarmerRecipe implements IFarmerRecipe {
    public static final MapCodec<FarmerRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(builder ->
            builder.group(
                    Ingredient.CODEC.fieldOf("seeds").forGetter(recipe -> recipe.seeds),
                    Ingredient.CODEC.fieldOf("soil").forGetter(recipe -> recipe.soil),
                    Ingredient.CODEC.optionalFieldOf("crux").forGetter(recipe -> recipe.crux),
                    Codec.INT.fieldOf("stages").forGetter(recipe -> recipe.stages),
                    FarmerResult.CODEC.listOf().fieldOf("results").forGetter(recipe -> recipe.results)
            ).apply(builder, FarmerRecipe::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, FarmerRecipe> STREAM_CODEC = StreamCodec.of(
            FarmerRecipe::toNetwork, FarmerRecipe::fromNetwork
    );
    public static final RecipeSerializer<FarmerRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    private final List<Ingredient> ingredients;
    private final int stages;
    private final List<FarmerResult> results;

    private final Ingredient seeds;
    private final Ingredient soil;
    private final Optional<Ingredient> crux;

    private final int inputCount;

    private PlacementInfo placementInfo;

    public FarmerRecipe(Ingredient seeds, Ingredient soil, Optional<Ingredient> crux, int stages, List<FarmerResult> results) {
        this.seeds = seeds;
        this.soil = soil;
        this.crux = crux;
        this.stages = stages;
        this.results = results;

        this.ingredients = new ArrayList<>();
        this.ingredients.add(seeds);
        this.ingredients.add(soil);
        crux.ifPresent(this.ingredients::add);

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
    public ItemStack assemble(RecipeInput input) {
        return this.results.getFirst().stack().create();
    }

    @Override
    public PlacementInfo placementInfo() {
        if (this.placementInfo == null) {
            this.placementInfo = PlacementInfo.create(this.ingredients);
        }

        return this.placementInfo;
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of(new ShapelessCraftingRecipeDisplay(
                this.ingredients.stream().map(Ingredient::display).toList(),
                new SlotDisplay.ItemStackSlotDisplay(this.results.getFirst().stack()),
                new SlotDisplay.ItemSlotDisplay(ModBlocks.FARMER.get().asItem())
        ));
    }

    @Override
    public RecipeSerializer<FarmerRecipe> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeType<IFarmerRecipe> getType() {
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
                results.add(result.stack().create());
        }

        return results;
    }

    private static FarmerRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
        var seeds = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
        var soil = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
        var crux = Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC.decode(buffer);

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
        Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC.encode(buffer, recipe.crux);

        ByteBufCodecs.VAR_INT.encode(buffer, recipe.stages);

        buffer.writeVarInt(recipe.results.size());

        for (var result : recipe.results) {
            FarmerResult.STREAM_CODEC.encode(buffer, result);
        }
    }
}

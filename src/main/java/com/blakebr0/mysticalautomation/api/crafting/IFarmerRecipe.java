package com.blakebr0.mysticalautomation.api.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.List;

/**
 * Used to represent a Farmer recipe for the recipe type
 */
public interface IFarmerRecipe extends Recipe<RecipeInput> {
    /**
     * The required number of growth stages this plant must go through before being harvested
     * @return the number of growth stages
     */
    int getStages();

    /**
     * The list of all possible {@link FarmerResult}s for this recipe
     * @return the possible {@link FarmerResult}s
     */
    List<FarmerResult> getResults();

    /**
     * Returns an output/rolled list of outputs as a result of a successful harvest
     * @return the rolled {@link FarmerResult}s
     */
    List<ItemStack> getRolledResults();

    /**
     * Represents a possible result item and the associated chance of it rolling
     * @param stack the result item
     * @param chance the chance of getting this item
     */
    record FarmerResult(ItemStack stack, float chance) {
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
}

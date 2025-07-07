package com.blakebr0.mysticalautomation.init;

import com.blakebr0.mysticalautomation.MysticalAutomation;
import com.blakebr0.mysticalautomation.crafting.recipe.FarmerRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> REGISTRY = DeferredRegister.create(Registries.RECIPE_SERIALIZER, MysticalAutomation.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FarmerRecipe>> FARMER = REGISTRY.register("farmer", FarmerRecipe.Serializer::new);
}

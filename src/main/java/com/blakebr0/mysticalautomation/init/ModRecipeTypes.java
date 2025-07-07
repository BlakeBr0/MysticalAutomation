package com.blakebr0.mysticalautomation.init;

import com.blakebr0.mysticalautomation.MysticalAutomation;
import com.blakebr0.mysticalautomation.crafting.recipe.FarmerRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModRecipeTypes {
    public static final DeferredRegister<RecipeType<?>> REGISTRY = DeferredRegister.create(Registries.RECIPE_TYPE, MysticalAutomation.MOD_ID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<FarmerRecipe>> FARMER = REGISTRY.register("farmer", () -> RecipeType.simple(MysticalAutomation.resource("farmer")));
}

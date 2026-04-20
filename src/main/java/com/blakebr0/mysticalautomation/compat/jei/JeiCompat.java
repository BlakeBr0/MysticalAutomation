package com.blakebr0.mysticalautomation.compat.jei;

import com.blakebr0.mysticalagriculture.api.MysticalAgricultureAPI;
import com.blakebr0.mysticalagriculture.api.crafting.IAwakeningRecipe;
import com.blakebr0.mysticalagriculture.api.crafting.IEnchanterRecipe;
import com.blakebr0.mysticalagriculture.api.crafting.IInfusionRecipe;
import com.blakebr0.mysticalautomation.MysticalAutomation;
import com.blakebr0.mysticalautomation.client.handler.ClientRecipeHandler;
import com.blakebr0.mysticalautomation.client.screen.AwakeningAltarnatorScreen;
import com.blakebr0.mysticalautomation.client.screen.CrafterScreen;
import com.blakebr0.mysticalautomation.client.screen.EnchanternatorScreen;
import com.blakebr0.mysticalautomation.client.screen.FarmerScreen;
import com.blakebr0.mysticalautomation.client.screen.InfusionAltarnatorScreen;
import com.blakebr0.mysticalautomation.compat.jei.category.FarmerCategory;
import com.blakebr0.mysticalautomation.compat.jei.category.FertilizerCategory;
import com.blakebr0.mysticalautomation.compat.jei.recipe.FertilizerFakeRecipe;
import com.blakebr0.mysticalautomation.init.ModBlocks;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public final class JeiCompat implements IModPlugin {
    private static final IRecipeHolderType<IEnchanterRecipe> ENCHANTER_RECIPE_TYPE = IRecipeHolderType.create(MysticalAgricultureAPI.resource("enchanter"));
    private static final IRecipeHolderType<IInfusionRecipe>  INFUSION_ALTARNATOR_RECIPE_TYPE = IRecipeHolderType.create(MysticalAgricultureAPI.resource("infusion"));
    private static final IRecipeHolderType<IAwakeningRecipe> AWAKENING_ALTARNATOR_RECIPE_TYPE = IRecipeHolderType.create(MysticalAgricultureAPI.resource("awakening"));

    public static final Identifier UID = MysticalAutomation.resource("jei_plugin");

    @Override
    public Identifier getPluginUid() {
        return UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        var guiHelper = registration.getJeiHelpers().getGuiHelper();

        registration.addRecipeCategories(
                new FarmerCategory(guiHelper),
                new FertilizerCategory(guiHelper)
        );
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addCraftingStation(mezz.jei.api.constants.RecipeTypes.CRAFTING, new ItemStack(ModBlocks.CRAFTER.get()));
        registration.addCraftingStation(FarmerCategory.RECIPE_TYPE, new ItemStack(ModBlocks.FARMER.get()));
        registration.addCraftingStation(FertilizerCategory.RECIPE_TYPE, new ItemStack(ModBlocks.FERTILIZER.get()));
        registration.addCraftingStation(ENCHANTER_RECIPE_TYPE, new ItemStack(ModBlocks.ENCHANTERNATOR.get()));
        registration.addCraftingStation(INFUSION_ALTARNATOR_RECIPE_TYPE, new ItemStack(ModBlocks.INFUSION_ALTARNATOR.get()));
        registration.addCraftingStation(AWAKENING_ALTARNATOR_RECIPE_TYPE, new ItemStack(ModBlocks.AWAKENING_ALTARNATOR.get()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(FarmerCategory.RECIPE_TYPE, ClientRecipeHandler.FARMER_RECIPES);
        registration.addRecipes(FertilizerCategory.RECIPE_TYPE, FertilizerFakeRecipe.createAll());
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(CrafterScreen.class, 114, 48, 24, 16, mezz.jei.api.constants.RecipeTypes.CRAFTING);
        registration.addRecipeClickArea(FarmerScreen.class, 85, 52, 24, 16, FarmerCategory.RECIPE_TYPE);
        registration.addRecipeClickArea(EnchanternatorScreen.class, 143, 47, 24, 16, ENCHANTER_RECIPE_TYPE);
        registration.addRecipeClickArea(InfusionAltarnatorScreen.class, 133, 49, 24, 16, INFUSION_ALTARNATOR_RECIPE_TYPE);
        registration.addRecipeClickArea(AwakeningAltarnatorScreen.class, 133, 49, 24, 16, AWAKENING_ALTARNATOR_RECIPE_TYPE);

        registration.addGhostIngredientHandler(CrafterScreen.class, new GhostIngredientHandler<>());
        registration.addGhostIngredientHandler(EnchanternatorScreen.class, new GhostIngredientHandler<>());
        registration.addGhostIngredientHandler(InfusionAltarnatorScreen.class, new GhostIngredientHandler<>());
        registration.addGhostIngredientHandler(AwakeningAltarnatorScreen.class, new GhostIngredientHandler<>());
    }
}

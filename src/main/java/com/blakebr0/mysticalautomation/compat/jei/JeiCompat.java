package com.blakebr0.mysticalautomation.compat.jei;

import com.blakebr0.cucumber.helper.RecipeHelper;
import com.blakebr0.mysticalagriculture.api.MysticalAgricultureAPI;
import com.blakebr0.mysticalagriculture.api.crafting.IAwakeningRecipe;
import com.blakebr0.mysticalagriculture.api.crafting.IEnchanterRecipe;
import com.blakebr0.mysticalagriculture.api.crafting.IInfusionRecipe;
import com.blakebr0.mysticalautomation.MysticalAutomation;
import com.blakebr0.mysticalautomation.client.screen.AwakeningAltarnatorScreen;
import com.blakebr0.mysticalautomation.client.screen.CrafterScreen;
import com.blakebr0.mysticalautomation.client.screen.EnchanternatorScreen;
import com.blakebr0.mysticalautomation.client.screen.FarmerScreen;
import com.blakebr0.mysticalautomation.client.screen.InfusionAltarnatorScreen;
import com.blakebr0.mysticalautomation.compat.jei.category.FarmerCategory;
import com.blakebr0.mysticalautomation.compat.jei.category.FertilizerCategory;
import com.blakebr0.mysticalautomation.compat.jei.recipe.FertilizerFakeRecipe;
import com.blakebr0.mysticalautomation.init.ModBlocks;
import com.blakebr0.mysticalautomation.init.ModRecipeTypes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public final class JeiCompat implements IModPlugin {
    private static final RecipeType<IEnchanterRecipe> ENCHANTER_RECIPE_TYPE = RecipeType.create(MysticalAgricultureAPI.MOD_ID, "enchanter", IEnchanterRecipe.class);
    private static final RecipeType<IInfusionRecipe>  INFUSION_ALTARNATOR_RECIPE_TYPE = RecipeType.create(MysticalAgricultureAPI.MOD_ID, "infusion", IInfusionRecipe.class);
    private static final RecipeType<IAwakeningRecipe> AWAKENING_ALTARNATOR_RECIPE_TYPE = RecipeType.create(MysticalAgricultureAPI.MOD_ID, "awakening", IAwakeningRecipe.class);

    public static final ResourceLocation UID = MysticalAutomation.resource("jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
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
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.CRAFTER.get()), mezz.jei.api.constants.RecipeTypes.CRAFTING);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.FARMER.get()), FarmerCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.FERTILIZER.get()), FertilizerCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.ENCHANTERNATOR.get()), ENCHANTER_RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.INFUSION_ALTARNATOR.get()), INFUSION_ALTARNATOR_RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.AWAKENING_ALTARNATOR.get()), AWAKENING_ALTARNATOR_RECIPE_TYPE);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        var level = Minecraft.getInstance().level;
        if (level != null) {
            var manager = level.getRecipeManager();

            registration.addRecipes(FarmerCategory.RECIPE_TYPE, RecipeHelper.byTypeValues(manager, ModRecipeTypes.FARMER.get()));
        }

        registration.addRecipes(FertilizerCategory.RECIPE_TYPE, FertilizerFakeRecipe.createAll());
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(CrafterScreen.class, 114, 48, 24, 16, mezz.jei.api.constants.RecipeTypes.CRAFTING);
        registration.addRecipeClickArea(FarmerScreen.class, 99, 52, 24, 16, FarmerCategory.RECIPE_TYPE);
        registration.addRecipeClickArea(EnchanternatorScreen.class, 143, 47, 24, 16, ENCHANTER_RECIPE_TYPE);
        registration.addRecipeClickArea(InfusionAltarnatorScreen.class, 133, 49, 24, 16, INFUSION_ALTARNATOR_RECIPE_TYPE);
        registration.addRecipeClickArea(AwakeningAltarnatorScreen.class, 133, 49, 24, 16, AWAKENING_ALTARNATOR_RECIPE_TYPE);
    }
}

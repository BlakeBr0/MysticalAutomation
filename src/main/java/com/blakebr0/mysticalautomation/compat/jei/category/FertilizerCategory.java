package com.blakebr0.mysticalautomation.compat.jei.category;

import com.blakebr0.cucumber.util.Localizable;
import com.blakebr0.mysticalautomation.MysticalAutomation;
import com.blakebr0.mysticalautomation.compat.jei.recipe.FertilizerFakeRecipe;
import com.blakebr0.mysticalautomation.init.ModBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.widgets.IScrollGridWidgetFactory;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class FertilizerCategory implements IRecipeCategory<FertilizerFakeRecipe> {
    public static final RecipeType<FertilizerFakeRecipe> RECIPE_TYPE = RecipeType.create(MysticalAutomation.MOD_ID, "fertilizer", FertilizerFakeRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;
    private final Component title;
    private final IScrollGridWidgetFactory<?> scrollGridFactory;

    public FertilizerCategory(IGuiHelper helper) {
        this.background = helper.createBlankDrawable(142, 110);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.FERTILIZER.get()));
        this.title = Localizable.of("jei.category.mysticalautomation.fertilizer").build();
        this.scrollGridFactory = helper.createScrollGridFactory(7, 6);
    }

    @Override
    public RecipeType<FertilizerFakeRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return this.title;
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FertilizerFakeRecipe recipe, IFocusGroup focuses) {
        for (var stack : recipe.getItems()) {
            builder.addSlotToWidget(RecipeIngredientRole.OUTPUT, this.scrollGridFactory).addItemStack(stack);
        }
    }
}

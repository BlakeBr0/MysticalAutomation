package com.blakebr0.mysticalautomation.compat.jei.category;

import com.blakebr0.mysticalautomation.MysticalAutomation;
import com.blakebr0.mysticalautomation.compat.jei.recipe.FertilizerFakeRecipe;
import com.blakebr0.mysticalautomation.init.ModBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.widgets.IScrollBoxWidget;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class FertilizerCategory implements IRecipeCategory<FertilizerFakeRecipe> {
    public static final IRecipeType<FertilizerFakeRecipe> RECIPE_TYPE = IRecipeType.create(MysticalAutomation.resource("fertilizer"), FertilizerFakeRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;
    private final Component title;
    private final IScrollBoxWidget scrollGridFactory;

    public FertilizerCategory(IGuiHelper helper) {
        this.background = helper.createBlankDrawable(142, 110);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.FERTILIZER.get()));
        this.title = Component.translatable("jei.category.mysticalautomation.fertilizer");
        this.scrollGridFactory = helper.createScrollBoxWidget(7, 6, 0, 0);
    }

    @Override
    public IRecipeType<FertilizerFakeRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return this.title;
    }

    @Override
    public int getWidth() {
        return this.background.getWidth();
    }

    @Override
    public int getHeight() {
        return this.background.getHeight();
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FertilizerFakeRecipe recipe, IFocusGroup focuses) {
        for (var stack : recipe.getItems()) {
//            TODO scroll grid factory
//            builder.addSlot(RecipeIngredientRole.OUTPUT, this.scrollGridFactory).addItemStack(stack);
        }
    }
}

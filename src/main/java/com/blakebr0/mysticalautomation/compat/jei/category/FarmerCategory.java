package com.blakebr0.mysticalautomation.compat.jei.category;

import com.blakebr0.cucumber.util.Formatting;
import com.blakebr0.mysticalautomation.MysticalAutomation;
import com.blakebr0.mysticalautomation.crafting.recipe.FarmerRecipe;
import com.blakebr0.mysticalautomation.init.ModBlocks;
import com.blakebr0.mysticalautomation.lib.ModTooltips;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IScrollBoxWidget;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;

public class FarmerCategory implements IRecipeCategory<RecipeHolder<FarmerRecipe>> {
    private static final Identifier TEXTURE = MysticalAutomation.resource("textures/jei/farmer.png");
    public static final IRecipeHolderType<FarmerRecipe> RECIPE_TYPE = IRecipeHolderType.create(MysticalAutomation.resource("farmer"));

    private final IDrawable background;
    private final IDrawable icon;
    private final Component title;
    private final IScrollBoxWidget scrollGridFactory;

    public FarmerCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 127, 62);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.FARMER.get()));
        this.title = Component.translatable("jei.category.mysticalautomation.farmer");
        this.scrollGridFactory = helper.createScrollBoxWidget(3, 3, 56, 4);
    }

    @Override
    public IRecipeType<RecipeHolder<FarmerRecipe>> getRecipeType() {
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
    public void getTooltip(ITooltipBuilder tooltip, RecipeHolder<FarmerRecipe> recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (mouseX > 26 && mouseX < 48 && mouseY > 23 && mouseY < 39) {
            tooltip.add(ModTooltips.STAGES.args(Formatting.number(recipe.value().getStages())).color(ChatFormatting.WHITE).toComponent());
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<FarmerRecipe> recipe, IFocusGroup focuses) {
        var displays = recipe.value().display();
        if (!displays.isEmpty() && displays.getFirst() instanceof ShapelessCraftingRecipeDisplay display) {
            var ingredients = display.ingredients();

            builder.addSlot(RecipeIngredientRole.INPUT, 1, 1).add(ingredients.get(0));
            builder.addSlot(RecipeIngredientRole.INPUT, 1, 23).add(ingredients.get(1));
            builder.addSlot(RecipeIngredientRole.INPUT, 1, 45).add(ingredients.get(2));

            for (var result : recipe.value().getResults()) {
//                TODO scrolling grid
//                builder.addSlotToWidget(RecipeIngredientRole.OUTPUT, this.scrollGridFactory)
//                        .addItemStack(result.stack())
//                        .addRichTooltipCallback((slots, tooltip) -> {
//                            var chance = result.chance() * 100;
//                            tooltip.add(MysticalCompat.Tooltips.CHANCE.args(Formatting.percent(chance)).toComponent());
//                        });
            }
        }
    }
}

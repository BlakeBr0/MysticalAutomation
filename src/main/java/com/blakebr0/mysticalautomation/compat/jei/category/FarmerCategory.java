package com.blakebr0.mysticalautomation.compat.jei.category;

import com.blakebr0.cucumber.util.Formatting;
import com.blakebr0.mysticalautomation.MysticalAutomation;
import com.blakebr0.mysticalautomation.api.crafting.IFarmerRecipe;
import com.blakebr0.mysticalautomation.compat.MysticalCompat;
import com.blakebr0.mysticalautomation.init.ModBlocks;
import com.blakebr0.mysticalautomation.lib.ModTooltips;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

public class FarmerCategory implements IRecipeCategory<RecipeHolder<IFarmerRecipe>> {
    private static final Identifier TEXTURE = MysticalAutomation.resource("textures/jei/farmer.png");

    public static final IRecipeHolderType<IFarmerRecipe> RECIPE_TYPE = IRecipeHolderType.create(MysticalAutomation.resource("farmer"));

    private final IDrawable background;
    private final IDrawable icon;
    private final Component title;

    public FarmerCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 127, 62);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.FARMER.get()));
        this.title = Component.translatable("jei.category.mysticalautomation.farmer");
    }

    @Override
    public IRecipeType<RecipeHolder<IFarmerRecipe>> getRecipeType() {
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
    public void draw(RecipeHolder<IFarmerRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor gfx, double mouseX, double mouseY) {
        this.background.draw(gfx);
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, RecipeHolder<IFarmerRecipe> recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (mouseX > 26 && mouseX < 48 && mouseY > 23 && mouseY < 39) {
            tooltip.add(ModTooltips.STAGES.args(Formatting.number(recipe.value().getStages())).color(ChatFormatting.WHITE).toComponent());
        }
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, RecipeHolder<IFarmerRecipe> recipe, IFocusGroup focuses) {
        var outputs = builder.getRecipeSlots().getSlots(RecipeIngredientRole.OUTPUT);
        var scrollGrid = builder.addScrollGridWidget(outputs, 3, 3);
        scrollGrid.setPosition(56, 4);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<IFarmerRecipe> recipeHolder, IFocusGroup focuses) {
        var recipe = recipeHolder.value();
        var seeds = recipe.getSeedsIngredient();
        var soil = recipe.getSoilIngredient();
        var crux = recipe.getCruxIngredient();

        builder.addSlot(RecipeIngredientRole.INPUT, 1, 1).add(seeds);
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 23).add(soil);

        crux.ifPresent(i -> builder.addSlot(RecipeIngredientRole.INPUT, 1, 45).add(i));

        for (var result : recipe.getResults()) {
            builder.addOutputSlot()
                    .add(result.stack().create())
                    .addRichTooltipCallback((_, tooltip) -> {
                        var chance = result.chance() * 100;
                        tooltip.add(MysticalCompat.Tooltips.CHANCE.args(Formatting.percent(chance)).toComponent());
                    });
        }
    }
}

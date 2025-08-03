package com.blakebr0.mysticalautomation.compat.jei.category;

import com.blakebr0.cucumber.util.Formatting;
import com.blakebr0.cucumber.util.Localizable;
import com.blakebr0.mysticalautomation.MysticalAutomation;
import com.blakebr0.mysticalautomation.compat.MysticalCompat;
import com.blakebr0.mysticalautomation.crafting.recipe.FarmerRecipe;
import com.blakebr0.mysticalautomation.init.ModBlocks;
import com.blakebr0.mysticalautomation.lib.ModTooltips;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IScrollGridWidgetFactory;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class FarmerCategory implements IRecipeCategory<FarmerRecipe> {
    private static final ResourceLocation TEXTURE = MysticalAutomation.resource("textures/jei/farmer.png");
    public static final RecipeType<FarmerRecipe> RECIPE_TYPE = RecipeType.create(MysticalAutomation.MOD_ID, "farmer", FarmerRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;
    private final Component title;
    private final IScrollGridWidgetFactory<?> scrollGridFactory;

    public FarmerCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 127, 62);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.FARMER.get()));
        this.title = Localizable.of("jei.category.mysticalautomation.farmer").build();
        this.scrollGridFactory = helper.createScrollGridFactory(3, 3);

        this.scrollGridFactory.setPosition(56, 4);
    }

    @Override
    public RecipeType<FarmerRecipe> getRecipeType() {
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
    public void getTooltip(ITooltipBuilder tooltip, FarmerRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (mouseX > 26 && mouseX < 48 && mouseY > 23 && mouseY < 39) {
            tooltip.add(ModTooltips.STAGES.args(Formatting.number(recipe.getStages())).color(ChatFormatting.WHITE).build());
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FarmerRecipe recipe, IFocusGroup focuses) {
        var inputs = recipe.getIngredients();

        builder.addSlot(RecipeIngredientRole.INPUT, 1, 1).addIngredients(inputs.get(0));
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 23).addIngredients(inputs.get(1));
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 45).addIngredients(inputs.get(2));

        for (var result : recipe.getResults()) {
            builder.addSlotToWidget(RecipeIngredientRole.OUTPUT, this.scrollGridFactory)
                    .addItemStack(result.stack())
                    .addRichTooltipCallback((slots, tooltip) -> {
                        var chance = result.chance() * 100;
                        tooltip.add(MysticalCompat.Tooltips.CHANCE.args(Formatting.percent(chance)).build());
                    });
        }
    }
}

package com.blakebr0.mysticalautomation.crafting;

import com.blakebr0.cucumber.event.RecipeManagerLoadingEvent;
import com.blakebr0.mysticalagriculture.api.MysticalAgricultureAPI;
import com.blakebr0.mysticalagriculture.api.crop.Crop;
import com.blakebr0.mysticalautomation.MysticalAutomation;
import com.blakebr0.mysticalautomation.compat.MysticalCompat;
import com.blakebr0.mysticalautomation.config.ModConfigs;
import com.blakebr0.mysticalautomation.crafting.recipe.FarmerRecipe;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.bus.api.SubscribeEvent;

import java.util.List;

public final class DynamicRecipeManager {
    public static final DynamicRecipeManager INSTANCE = new DynamicRecipeManager();

    @SubscribeEvent
    public void onRecipeManagerLoading(RecipeManagerLoadingEvent event) {
        if (ModConfigs.FARMER_DYNAMIC_MYSTICAL_AGRICULTURE_RECIPES.get()) {
            for (var crop : MysticalAgricultureAPI.getCropRegistry().getCrops()) {
                if (crop.isEnabled()) {
                    var id = MysticalAutomation.resource("farmer/mysticalagriculture/" + crop.getName());
                    var recipe = createResourceSeedRecipe(crop);

                    event.addRecipe(new RecipeHolder<>(id, recipe));
                }
            }
        }
    }

    private static FarmerRecipe createResourceSeedRecipe(Crop crop) {
        var seeds = crop.getSeedsItem() != null ? Ingredient.of(crop.getSeedsItem()) : Ingredient.EMPTY;
        var farmland = crop.getTier().getFarmland() != null ? Ingredient.of(crop.getTier().getFarmland()) : Ingredient.EMPTY;
        var crux = crop.getCruxBlock() != null ? Ingredient.of(crop.getCruxBlock()) : Ingredient.EMPTY;
        var results = List.of(
                new FarmerRecipe.FarmerResult(new ItemStack(crop.getEssenceItem()), 1.0F),
                new FarmerRecipe.FarmerResult(new ItemStack(MysticalCompat.Items.FERTILIZED_ESSENCE), 0.05F)
        );

        return new FarmerRecipe(seeds, farmland, crux, Holder.direct(crop.getCropBlock()), results);
    }
}

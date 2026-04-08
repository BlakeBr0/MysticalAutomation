package com.blakebr0.mysticalautomation.crafting;

import com.blakebr0.cucumber.event.RecipeManagerLoadingEvent;
import com.blakebr0.mysticalagriculture.api.MysticalAgricultureAPI;
import com.blakebr0.mysticalagriculture.api.crop.Crop;
import com.blakebr0.mysticalautomation.MysticalAutomation;
import com.blakebr0.mysticalautomation.compat.MysticalCompat;
import com.blakebr0.mysticalautomation.config.ModConfigs;
import com.blakebr0.mysticalautomation.crafting.recipe.FarmerRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.bus.api.SubscribeEvent;

import java.util.List;
import java.util.Optional;

public final class DynamicRecipeManager {
    public static final DynamicRecipeManager INSTANCE = new DynamicRecipeManager();

    @SubscribeEvent
    public void onRecipeManagerLoading(RecipeManagerLoadingEvent event) {
        if (ModConfigs.FARMER_DYNAMIC_MYSTICAL_AGRICULTURE_RECIPES.get()) {
            for (var crop : MysticalAgricultureAPI.getCropRegistry().getCrops()) {
                if (crop.isEnabled() && !"inferium".equals(crop.getName())) {
                    var recipe = createResourceSeedRecipe(crop);

                    if (recipe != null) {
                        event.addRecipe(recipe);
                    }
                }
            }
        }
    }

    private static RecipeHolder<FarmerRecipe> createResourceSeedRecipe(Crop crop) {
        var seeds = crop.getSeedsItem();
        if (seeds == null)
            return null;

        var farmland = crop.getTier().getFarmlandBlock();
        if (farmland == null)
            return null;

        var essence = crop.getEssenceItem();
        if (essence == null)
            return null;

        var crux = crop.getCruxBlock() != null ? Optional.of(Ingredient.of(crop.getCruxBlock())) : Optional.<Ingredient>empty();
        var stages = crop.getCropBlock() != null ? crop.getCropBlock().getMaxAge() : 7;

        var fertilizedEssenceChance = (float) MysticalAgricultureAPI.getConfigValues().getFertilizedEssenceDropChance();

        var results = List.of(
                new FarmerRecipe.FarmerResult(new ItemStackTemplate(crop.getEssenceItem()), 1.0F),
                new FarmerRecipe.FarmerResult(new ItemStackTemplate(MysticalCompat.Items.FERTILIZED_ESSENCE), fertilizedEssenceChance)
        );

        var id = MysticalAutomation.resource("farmer/mysticalagriculture/" + crop.getNameWithSuffix("seeds"));

        return new RecipeHolder<>(
                ResourceKey.create(Registries.RECIPE, id),
                new FarmerRecipe(Ingredient.of(seeds), Ingredient.of(farmland), crux, stages, results)
        );
    }
}

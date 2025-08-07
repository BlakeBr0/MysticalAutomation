package com.blakebr0.mysticalautomation.compat.crafttweaker;

import com.blakebr0.mysticalautomation.crafting.recipe.FarmerRecipe;
import com.blakebr0.mysticalautomation.init.ModRecipeTypes;
import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.CraftTweakerConstants;
import com.blamejared.crafttweaker.api.action.recipe.ActionAddRecipe;
import com.blamejared.crafttweaker.api.action.recipe.ActionRemoveRecipe;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.ingredient.IIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import org.openzen.zencode.java.ZenCodeType;

import java.util.Comparator;
import java.util.Map;

@ZenCodeType.Name("mods.mysticalautomation.FarmerCrafting")
@ZenRegister
public final class FarmerCrafting implements IRecipeManager<FarmerRecipe> {
    @Override
    public RecipeType<FarmerRecipe> getRecipeType() {
        return ModRecipeTypes.FARMER.get();
    }

    @ZenCodeType.Method
    public void addRecipe(String name, IIngredient seeds, IIngredient soil, int stages, Map<IItemStack, Float> results) {
        this.addRecipe(name, seeds, soil, IIngredient.fromIngredient(Ingredient.EMPTY), stages, results);
    }

    @ZenCodeType.Method
    public void addRecipe(String name, IIngredient seeds, IIngredient soil, IIngredient crux, int stages, Map<IItemStack, Float> results) {
        var id = CraftTweakerConstants.rl(this.fixRecipeName(name));
        var recipe = new FarmerRecipe(
                seeds.asVanillaIngredient(),
                soil.asVanillaIngredient(),
                crux.asVanillaIngredient(),
                stages,
                results.entrySet().stream()
                        .map(entry -> new FarmerRecipe.FarmerResult(entry.getKey().getInternal(), entry.getValue()))
                        .sorted(Comparator.comparing(FarmerRecipe.FarmerResult::chance).reversed())
                        .toList()
        );

        CraftTweakerAPI.apply(new ActionAddRecipe<>(this, new RecipeHolder<>(id, recipe)));
    }

    @ZenCodeType.Method
    public void removeBySeeds(IItemStack seeds) {
        CraftTweakerAPI.apply(new ActionRemoveRecipe<>(this, holder -> holder.value()
                .getIngredients().getFirst().test(seeds.getInternal())));
    }
}

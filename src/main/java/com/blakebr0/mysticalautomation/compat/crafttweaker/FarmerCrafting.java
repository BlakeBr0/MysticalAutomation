package com.blakebr0.mysticalautomation.compat.crafttweaker;

import com.blakebr0.mysticalautomation.crafting.recipe.FarmerRecipe;
import com.blakebr0.mysticalautomation.init.ModRecipeTypes;
import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.CraftTweakerConstants;
import com.blamejared.crafttweaker.api.action.recipe.ActionAddRecipe;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.ingredient.IIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager;
import net.minecraft.core.Holder;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import org.openzen.zencode.java.ZenCodeType;

import java.util.Map;

@ZenCodeType.Struct
@ZenCodeType.Name("mods.mysticalautomation.FarmerCrafting")
@ZenRegister
public final class FarmerCrafting implements IRecipeManager<FarmerRecipe> {
    @Override
    public RecipeType<FarmerRecipe> getRecipeType() {
        return ModRecipeTypes.FARMER.get();
    }

    @ZenCodeType.Method
    public void addRecipe(String name, IIngredient seeds, IIngredient soil, Block crop, Map<IItemStack, Float> results) {
        this.addRecipe(name, seeds, soil, IIngredient.fromIngredient(Ingredient.EMPTY), crop, results);
    }

    @ZenCodeType.Method
    public void addRecipe(String name, IIngredient seeds, IIngredient soil, IIngredient crux, Block crop, Map<IItemStack, Float> results) {
        var id = CraftTweakerConstants.rl(this.fixRecipeName(name));
        var recipe = new FarmerRecipe(
                seeds.asVanillaIngredient(),
                soil.asVanillaIngredient(),
                crux.asVanillaIngredient(),
                Holder.direct(crop),
                results.entrySet().stream()
                        .map(entry -> new FarmerRecipe.FarmerResult(entry.getKey().getInternal(), entry.getValue()))
                        .toList()
        );

        CraftTweakerAPI.apply(new ActionAddRecipe<>(this, new RecipeHolder<>(id, recipe)));
    }
}

package com.blakebr0.mysticalautomation.compat.jei.recipe;

import com.blakebr0.mysticalautomation.lib.ModTags;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class FertilizerFakeRecipe {
    public final NonNullList<ItemStack> stacks;

    public FertilizerFakeRecipe() {
        var stacks = NonNullList.<ItemStack>create();

        for (var item : BuiltInRegistries.ITEM.getTagOrEmpty(ModTags.Items.FERTILIZERS)) {
            stacks.add(new ItemStack(item));
        }

        this.stacks = stacks;
    }

    public NonNullList<ItemStack> getItems() {
        return this.stacks;
    }

    public static List<FertilizerFakeRecipe> createAll() {
        return List.of(new FertilizerFakeRecipe());
    }
}

package com.blakebr0.mysticalautomation.util;

import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public interface IFakeRecipeContainer {
    void setFakeRecipeSlot(Slot slot, ItemStack stack);
}

package com.blakebr0.mysticalautomation.container.slot;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * This slot only exists as a way to sync an item to the client
 */
public class HiddenSlot extends Slot {
    public HiddenSlot(Container container, int x, int y) {
        super(container, 0, x, y);
    }

    @Override
    public boolean mayPickup(Player player) {
        return false;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isActive() {
        return false;
    }
}

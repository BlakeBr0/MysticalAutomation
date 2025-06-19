package com.blakebr0.mysticalautomation.container.slot;

import com.blakebr0.cucumber.inventory.BaseItemStackHandler;
import com.blakebr0.cucumber.inventory.slot.BaseItemStackHandlerSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class FakeSlot extends BaseItemStackHandlerSlot {
    private final BaseItemStackHandler inventory;

    public FakeSlot(BaseItemStackHandler container, int slot, int x, int y) {
        super(container, slot, x, y);
        this.inventory = container;
    }

    @Override
    public boolean mayPickup(Player player) {
        return false;
    }

    @Override
    public void set(ItemStack stack) {
        if (!stack.isEmpty()) {
            stack.setCount(1);
        }

        this.inventory.setStackInSlot(this.getContainerSlot(), stack);
        this.setChanged();
    }
}

package com.blakebr0.mysticalautomation.container.slot;

import com.blakebr0.cucumber.inventory.CItemStacksHandler;
import com.blakebr0.cucumber.inventory.slot.CSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class FakeSlot extends CSlot {
    public FakeSlot(CItemStacksHandler container, int slot, int x, int y) {
        super(container, slot, x, y);
    }

    @Override
    public boolean mayPickup(Player player) {
        return false;
    }

    @Override
    protected void setStackCopy(ItemStack stack) {
        if (!stack.isEmpty()) {
            stack.setCount(1);
        }

        super.setStackCopy(stack);
        this.setChanged();
    }
}

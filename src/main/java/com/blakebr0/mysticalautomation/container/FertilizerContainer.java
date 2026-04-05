package com.blakebr0.mysticalautomation.container;

import com.blakebr0.cucumber.container.BaseContainerMenu;
import com.blakebr0.cucumber.inventory.CItemStacksHandler;
import com.blakebr0.cucumber.inventory.slot.CSlot;
import com.blakebr0.cucumber.util.QuickMover;
import com.blakebr0.mysticalagriculture.api.machine.IMachineUpgrade;
import com.blakebr0.mysticalagriculture.api.machine.MachineUpgradeItemStackHandler;
import com.blakebr0.mysticalautomation.init.ModMenuTypes;
import com.blakebr0.mysticalautomation.lib.ModTags;
import com.blakebr0.mysticalautomation.tileentity.FertilizerTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;

public class FertilizerContainer extends BaseContainerMenu {
    private final ContainerData data;
    private final QuickMover mover;

    public FertilizerContainer(int id, Inventory playerInventory, FriendlyByteBuf buffer) {
        this(id, playerInventory, FertilizerTileEntity.createInventoryHandler(), new MachineUpgradeItemStackHandler(), new SimpleContainerData(4), buffer.readBlockPos());
    }

    public FertilizerContainer(int id, Inventory playerInventory, CItemStacksHandler inventory, MachineUpgradeItemStackHandler upgradeInventory, ContainerData data, BlockPos pos) {
        super(ModMenuTypes.FERTILIZER.get(), id, pos);
        this.data = data;
        this.mover = new QuickMover(this::moveItemStackTo);

        this.addSlot(new ResourceHandlerSlot(upgradeInventory, upgradeInventory::set, 0, 152, 9));

        // input slots
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 4; j++) {
                this.addSlot(new CSlot(inventory, j + i * 4, 75 + (j * 18), 39 + (i * 18)));
            }
        }

        // fuel slot
        this.addSlot(new CSlot(inventory, 8, 30, 56));

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 112 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 170));
        }

        this.mover.after(10)
                .add((slot, stack, player) -> stack.getItem() instanceof IMachineUpgrade, 0, 1) // machine upgrade
                .add((slot, stack, player) -> stack.is(ModTags.Items.FERTILIZERS), 1, 8) // inputs
                .add((slot, stack, player) -> stack.getBurnTime(null, player.level().fuelValues()) > 0, 9, 1) // fuel
                .add((slot, stack, player) -> slot < this.slots.size() - 9, this.slots.size() - 9, 9) // hotbar
                .add((slot, stack, player) -> slot >= this.slots.size() - 9, this.slots.size() - 36, 27); // inventory
        this.mover.fallback(10, 36);

        this.addDataSlots(data);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        var itemstack = ItemStack.EMPTY;
        var slot = this.slots.get(index);

        if (slot.hasItem()) {
            var itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (!this.mover.run(index, itemstack1, player)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
        }

        return itemstack;
    }

    public int getEnergyStored() {
        return this.data.get(0);
    }

    public int getMaxEnergyStored() {
        return this.data.get(1);
    }

    public int getFuelLeft() {
        return this.data.get(2);
    }

    public int getFuelItemValue() {
        return this.data.get(3);
    }
}

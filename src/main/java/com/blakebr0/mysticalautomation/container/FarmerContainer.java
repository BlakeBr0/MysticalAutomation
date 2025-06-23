package com.blakebr0.mysticalautomation.container;

import com.blakebr0.cucumber.container.BaseContainerMenu;
import com.blakebr0.cucumber.inventory.BaseItemStackHandler;
import com.blakebr0.cucumber.inventory.slot.BaseItemStackHandlerSlot;
import com.blakebr0.mysticalagriculture.api.machine.IMachineUpgrade;
import com.blakebr0.mysticalagriculture.api.machine.MachineUpgradeItemStackHandler;
import com.blakebr0.mysticalautomation.compat.MysticalCompat;
import com.blakebr0.mysticalautomation.init.ModMenuTypes;
import com.blakebr0.mysticalautomation.tileentity.EnchanternatorTileEntity;
import com.blakebr0.mysticalautomation.tileentity.FarmerTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;

public class FarmerContainer extends BaseContainerMenu {
    private final ContainerData data;

    public FarmerContainer(int id, Inventory playerInventory, FriendlyByteBuf buffer) {
        this(id, playerInventory, FarmerTileEntity.createInventoryHandler(), new MachineUpgradeItemStackHandler(), new SimpleContainerData(8), buffer.readBlockPos());
    }

    public FarmerContainer(int id, Inventory playerInventory, BaseItemStackHandler inventory, MachineUpgradeItemStackHandler upgradeInventory, ContainerData data, BlockPos pos) {
        super(ModMenuTypes.FARMER.get(), id, pos);
        this.data = data;

        this.addSlot(new SlotItemHandler(upgradeInventory, 0, 192, 9));

        // input slots
        this.addSlot(new BaseItemStackHandlerSlot(inventory, 0, 62, 33));
        this.addSlot(new BaseItemStackHandlerSlot(inventory, 1, 62, 33));
        this.addSlot(new BaseItemStackHandlerSlot(inventory, 2, 62, 33));

        // fuel slot
        this.addSlot(new BaseItemStackHandlerSlot(inventory, 3, 30, 56));

        // output slot
        this.addSlot(new BaseItemStackHandlerSlot(inventory, 4, 162, 74));

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 29 + j * 18, 112 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInventory, i, 29 + i * 18, 170));
        }

        this.addDataSlots(data);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        var itemstack = ItemStack.EMPTY;
        var slot = this.slots.get(index);

        if (slot.hasItem()) {
            var itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (index > 9) {
                if (itemstack1.getItem() instanceof IMachineUpgrade) {
                    if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (MysticalCompat.isEssence(itemstack1) || MysticalCompat.isInfusionCrystal(itemstack1)) {
                    if (!this.moveItemStackTo(itemstack1, 1, 8, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (itemstack1.getBurnTime(null) > 0) {
                    if (!this.moveItemStackTo(itemstack1, 8, 9, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 37) {
                    if (!this.moveItemStackTo(itemstack1, 37, 46, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 46 && !this.moveItemStackTo(itemstack1, 10, 36, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 10, 46, false)) {
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

    public int getProgress() {
        return this.data.get(4);
    }

    public int getOperationTime() {
        return this.data.get(5);
    }
}

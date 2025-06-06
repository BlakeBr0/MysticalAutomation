package com.blakebr0.mysticalautomation.container;

import com.blakebr0.cucumber.container.BaseContainerMenu;
import com.blakebr0.cucumber.inventory.BaseItemStackHandler;
import com.blakebr0.cucumber.inventory.slot.BaseItemStackHandlerSlot;
import com.blakebr0.mysticalagriculture.api.machine.IMachineUpgrade;
import com.blakebr0.mysticalagriculture.api.machine.MachineUpgradeItemStackHandler;
import com.blakebr0.mysticalautomation.compat.MysticalCompat;
import com.blakebr0.mysticalautomation.init.ModMenuTypes;
import com.blakebr0.mysticalautomation.tilentity.InfuserTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;

public class InfuserContainer extends BaseContainerMenu {
    private final ContainerData data;

    private InfuserContainer(MenuType<?> type, int id, Inventory playerInventory, FriendlyByteBuf buffer) {
        this(type, id, playerInventory, InfuserTileEntity.createInventoryHandler(), new MachineUpgradeItemStackHandler(), new SimpleContainerData(8), buffer.readBlockPos());
    }

    private InfuserContainer(MenuType<?> type, int id, Inventory playerInventory, BaseItemStackHandler inventory, MachineUpgradeItemStackHandler upgradeInventory, ContainerData data, BlockPos pos) {
        super(type, id, pos);
        this.data = data;

        this.addSlot(new SlotItemHandler(upgradeInventory, 0, 192, 9));

        // infusion crystal slot
        this.addSlot(new BaseItemStackHandlerSlot(inventory, 0, 62, 33));

        // mystical agriculture essence slots
        for (int i = 0; i < 5; i++) {
            this.addSlot(new BaseItemStackHandlerSlot(inventory, 1 + i, 102 + i * 18, 33));
        }

        // mystical agradditions essence slot
        this.addSlot(new BaseItemStackHandlerSlot(inventory, 6, 192, 33));

        // fuel slot
        this.addSlot(new BaseItemStackHandlerSlot(inventory, 7, 30, 56));

        // output slot
        this.addSlot(new BaseItemStackHandlerSlot(inventory, 8, 162, 74));

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

    public static InfuserContainer create(int windowId, Inventory playerInventory, FriendlyByteBuf buffer) {
        return new InfuserContainer(ModMenuTypes.INFUSER.get(), windowId, playerInventory, buffer);
    }

    public static InfuserContainer create(int windowId, Inventory playerInventory, BaseItemStackHandler inventory, MachineUpgradeItemStackHandler upgradeInventory, ContainerData data, BlockPos pos) {
        return new InfuserContainer(ModMenuTypes.INFUSER.get(), windowId, playerInventory, inventory, upgradeInventory, data, pos);
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

    public int getProgressingIndex() {
        return this.data.get(6);
    }

    public int getSelectedIndex() {
        return this.data.get(7);
    }
}

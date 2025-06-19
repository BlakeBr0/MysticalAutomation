package com.blakebr0.mysticalautomation.container;

import com.blakebr0.cucumber.container.BaseContainerMenu;
import com.blakebr0.cucumber.inventory.BaseItemStackHandler;
import com.blakebr0.cucumber.inventory.slot.BaseItemStackHandlerSlot;
import com.blakebr0.mysticalagriculture.api.machine.IMachineUpgrade;
import com.blakebr0.mysticalagriculture.api.machine.MachineUpgradeItemStackHandler;
import com.blakebr0.mysticalautomation.container.slot.FakeSlot;
import com.blakebr0.mysticalautomation.init.ModMenuTypes;
import com.blakebr0.mysticalautomation.tilentity.CrafterTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.SlotItemHandler;

public class CrafterContainer extends BaseContainerMenu {
    private final ContainerData data;
    private final BaseItemStackHandler matrix;
    private ItemStack result;

    public CrafterContainer(int id, Inventory playerInventory, FriendlyByteBuf buffer) {
        this(id, playerInventory, CrafterTileEntity.createInventoryHandler(), CrafterTileEntity.createRecipeInventoryHandler(), new MachineUpgradeItemStackHandler(), new SimpleContainerData(6), buffer.readBlockPos());
    }

    public CrafterContainer(int id, Inventory playerInventory, BaseItemStackHandler inventory, BaseItemStackHandler recipeInventory, MachineUpgradeItemStackHandler upgradeInventory, ContainerData data, BlockPos pos) {
        super(ModMenuTypes.CRAFTER.get(), id, pos);
        this.data = data;
        this.matrix = recipeInventory;
        this.result = ItemStack.EMPTY;

        this.addSlot(new SlotItemHandler(upgradeInventory, 0, 152, 9));

        // input slots
        for (int i = 0; i < 9; i++) {
            this.addSlot(new BaseItemStackHandlerSlot(inventory, i, 8 + i * 18, 101));
        }

        // fuel slot
        this.addSlot(new BaseItemStackHandlerSlot(inventory, 9, 30, 56));

        // output slot
        this.addSlot(new BaseItemStackHandlerSlot(inventory, 10, 148, 48));

        // recipe slots
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                this.addSlot(new FakeSlot(recipeInventory, j + i * 3, 56 + j * 18, 30 + i * 18));
            }
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 135 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 193));
        }

        this.addDataSlots(data);
        this.onRecipeChanged(playerInventory.player.level());
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        var itemstack = ItemStack.EMPTY;
        var slot = this.slots.get(index);

        if (slot.hasItem()) {
            var itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (index > 10) {
                if (itemstack1.getItem() instanceof IMachineUpgrade) {
                    if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (itemstack1.getBurnTime(null) > 0) {
                    if (!this.moveItemStackTo(itemstack1, 10, 11, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    if (!this.moveItemStackTo(itemstack1, 1, 9, false)) {
                        return ItemStack.EMPTY;
                    } else {
                        if (index < 48) {
                            if (!this.moveItemStackTo(itemstack1, 48, 57, false)) {
                                return ItemStack.EMPTY;
                            }
                        } else if (index < 57 && !this.moveItemStackTo(itemstack1, 21, 47, false)) {
                            return ItemStack.EMPTY;
                        }
                    }
                }
            } else if (!this.moveItemStackTo(itemstack1, 21, 57, false)) {
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

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        var slot = slotId < 0 ? null : this.slots.get(slotId);
        if (slot instanceof FakeSlot) {
            if (button == 2) {
                slot.set(ItemStack.EMPTY);
            } else {
                var carried = this.getCarried();
                slot.set(carried.isEmpty() ? ItemStack.EMPTY : carried.copy());
            }

            this.onRecipeChanged(player.level());
            return;
        }

        super.clicked(slotId, button, clickType, player);
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

    public ItemStack getResult() {
        return this.result;
    }

    private void onRecipeChanged(Level level) {
        var input = this.matrix.toCraftingInput(3, 3);
        this.result = level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, input, level)
                .map(r -> r.value().assemble(input, level.registryAccess()))
                .orElse(ItemStack.EMPTY);
    }
}

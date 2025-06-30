package com.blakebr0.mysticalautomation.container;

import com.blakebr0.cucumber.container.BaseContainerMenu;
import com.blakebr0.cucumber.helper.StackHelper;
import com.blakebr0.cucumber.inventory.BaseItemStackHandler;
import com.blakebr0.cucumber.inventory.slot.BaseItemStackHandlerSlot;
import com.blakebr0.cucumber.util.QuickMover;
import com.blakebr0.mysticalagriculture.api.machine.IMachineUpgrade;
import com.blakebr0.mysticalagriculture.api.machine.MachineUpgradeItemStackHandler;
import com.blakebr0.mysticalautomation.compat.MysticalCompat;
import com.blakebr0.mysticalautomation.container.slot.FakeSlot;
import com.blakebr0.mysticalautomation.init.ModMenuTypes;
import com.blakebr0.mysticalautomation.tileentity.AwakeningAltarnatorTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.SlotItemHandler;

public class AwakeningAltarnatorContainer extends BaseContainerMenu {
    private final ContainerData data;
    private final BaseItemStackHandler matrix;
    private final QuickMover mover;
    private ItemStack result;

    public AwakeningAltarnatorContainer(int id, Inventory playerInventory, FriendlyByteBuf buffer) {
        this(id, playerInventory, AwakeningAltarnatorTileEntity.createInventoryHandler(), AwakeningAltarnatorTileEntity.createRecipeInventoryHandler(), new MachineUpgradeItemStackHandler(), new SimpleContainerData(6), buffer.readBlockPos());
    }

    public AwakeningAltarnatorContainer(int id, Inventory playerInventory, BaseItemStackHandler inventory, BaseItemStackHandler recipeInventory, MachineUpgradeItemStackHandler upgradeInventory, ContainerData data, BlockPos pos) {
        super(ModMenuTypes.AWAKENING_ALTARNATOR.get(), id, pos);
        this.data = data;
        this.matrix = recipeInventory;
        this.mover = new QuickMover(this::moveItemStackTo);
        this.result = ItemStack.EMPTY;

        this.addSlot(new SlotItemHandler(upgradeInventory, 0, 172, 9));

        // input slots
        for (int i = 0; i < 9; i++) {
            this.addSlot(new BaseItemStackHandlerSlot(inventory, i, 18 + i * 18, 101));
        }

        // fuel slot
        this.addSlot(new BaseItemStackHandlerSlot(inventory, 9, 30, 56));

        // output slot
        this.addSlot(new BaseItemStackHandlerSlot(inventory, 10, 168, 49));

        // recipe slots
        this.addSlot(new FakeSlot(recipeInventory, 0, 84, 48));
        this.addSlot(new FakeSlot(recipeInventory, 1, 59, 23));
        this.addSlot(new FakeSlot(recipeInventory, 2, 84, 20));
        this.addSlot(new FakeSlot(recipeInventory, 3, 109, 23));
        this.addSlot(new FakeSlot(recipeInventory, 4, 56, 48));
        this.addSlot(new FakeSlot(recipeInventory, 5, 111, 48));
        this.addSlot(new FakeSlot(recipeInventory, 6, 59, 73));
        this.addSlot(new FakeSlot(recipeInventory, 7, 84, 76));
        this.addSlot(new FakeSlot(recipeInventory, 8, 109, 73));

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 18 + j * 18, 135 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInventory, i, 18 + i * 18, 193));
        }

        this.mover.after(21)
                .add((slot, stack, player) -> stack.getItem() instanceof IMachineUpgrade, 0, 1) // machine upgrade
                .add((slot, stack, player) -> this.isRecipeInput(stack), 1, 9) // inputs
                .add((slot, stack, player) -> stack.getBurnTime(null) > 0, 10, 1) // fuel
                .add((slot, stack, player) -> slot < this.slots.size() - 9, this.slots.size() - 9, 9) // hotbar
                .add((slot, stack, player) -> slot >= this.slots.size() - 9, this.slots.size() - 36, 27); // inventory
        this.mover.fallback(21, 36);

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

    public ItemStack getResult() {
        return this.result;
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

    private void onRecipeChanged(Level level) {
        var input = this.matrix.toCraftingInput(3, 3);
        var recipe = level.getRecipeManager().getRecipeFor(MysticalCompat.RecipeTypes.AWAKENING.get(), input, level).map(RecipeHolder::value).orElse(null);

        this.result = recipe == null ? ItemStack.EMPTY : recipe.assemble(input, level.registryAccess());
    }

    private boolean isRecipeInput(ItemStack stack) {
        for (int i = 0; i < this.matrix.getSlots(); i++) {
            var matrixStack = this.matrix.getStackInSlot(i);
            if (StackHelper.areItemsEqual(stack, matrixStack))
                return true;
        }

        return false;
    }
}

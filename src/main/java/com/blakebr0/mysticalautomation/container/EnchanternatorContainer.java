package com.blakebr0.mysticalautomation.container;

import com.blakebr0.cucumber.container.BaseContainerMenu;
import com.blakebr0.cucumber.crafting.ShapelessCraftingInput;
import com.blakebr0.cucumber.helper.StackHelper;
import com.blakebr0.cucumber.inventory.BaseItemStackHandler;
import com.blakebr0.cucumber.inventory.slot.BaseItemStackHandlerSlot;
import com.blakebr0.cucumber.util.QuickMover;
import com.blakebr0.mysticalagriculture.api.machine.IMachineUpgrade;
import com.blakebr0.mysticalagriculture.api.machine.MachineUpgradeItemStackHandler;
import com.blakebr0.mysticalautomation.compat.MysticalCompat;
import com.blakebr0.mysticalautomation.container.slot.FakeSlot;
import com.blakebr0.mysticalautomation.container.slot.HiddenSlot;
import com.blakebr0.mysticalautomation.init.ModMenuTypes;
import com.blakebr0.mysticalautomation.tileentity.EnchanternatorTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.SlotItemHandler;

import java.util.List;

public class EnchanternatorContainer extends BaseContainerMenu {
    private final ContainerData data;
    private final BaseItemStackHandler matrix;
    private final QuickMover mover;
    private final Slot result;

    public EnchanternatorContainer(int id, Inventory playerInventory, FriendlyByteBuf buffer) {
        this(id, playerInventory, EnchanternatorTileEntity.createInventoryHandler(), EnchanternatorTileEntity.createRecipeInventoryHandler(), new MachineUpgradeItemStackHandler(), new SimpleContainerData(7), buffer.readBlockPos());
    }

    public EnchanternatorContainer(int id, Inventory playerInventory, BaseItemStackHandler inventory, BaseItemStackHandler recipeInventory, MachineUpgradeItemStackHandler upgradeInventory, ContainerData data, BlockPos pos) {
        super(ModMenuTypes.ENCHANTERNATOR.get(), id, pos);
        this.data = data;
        this.matrix = recipeInventory;
        this.mover = new QuickMover(this::moveItemStackTo);

        this.addSlot(new SlotItemHandler(upgradeInventory, 0, 182, 9));

        // input slots
        this.addSlot(new BaseItemStackHandlerSlot(inventory, 0, 56, 67));
        this.addSlot(new BaseItemStackHandlerSlot(inventory, 1, 78, 67));
        this.addSlot(new BaseItemStackHandlerSlot(inventory, 2, 118, 67));

        // fuel slot
        this.addSlot(new BaseItemStackHandlerSlot(inventory, 3, 30, 56));

        // output slot
        this.addSlot(new BaseItemStackHandlerSlot(inventory, 4, 178, 47));

        // recipe slots
        this.addSlot(new FakeSlot(recipeInventory, 0, 56, 47));
        this.addSlot(new FakeSlot(recipeInventory, 1, 78, 47));
        this.addSlot(new FakeSlot(recipeInventory, 2, 118, 47));

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 23 + j * 18, 112 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInventory, i, 23 + i * 18, 170));
        }

        // this must be last since it still counts towards the slot count and needs to be accounted for.
        // note that this is accounted for in the mover indexes below (9 slots for hotbar + 1 for this)
        this.result = this.addSlot(new HiddenSlot(new ResultContainer(), 0, 0)); // recipe result

        this.mover.after(9)
                .add((slot, stack, player) -> stack.getItem() instanceof IMachineUpgrade, 0, 1) // machine upgrade
                .add((slot, stack, player) -> this.isRecipeInput(stack), 1, 3) // inputs
                .add((slot, stack, player) -> stack.getBurnTime(null) > 0, 4, 1) // fuel
                .add((slot, stack, player) -> slot < this.slots.size() - 10, this.slots.size() - 10, 9) // hotbar
                .add((slot, stack, player) -> slot >= this.slots.size() - 10, this.slots.size() - 37, 27); // inventory
        this.mover.fallback(9, 36);

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
        return this.result.getItem();
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

    public int getSelectedLevel() {
        return this.data.get(6);
    }

    private void onRecipeChanged(Level level) {
        // to show the proper ghost item as the result, we need to both pretend to have the maximum number of materials
        // to account for all requirements
        var items = List.of(
                this.matrix.getStackInSlot(0).copyWithCount(512),
                this.matrix.getStackInSlot(1).copyWithCount(512),
                this.matrix.getStackInSlot(2)
        );

        var input = new ShapelessCraftingInput(items);
        var recipe = level.getRecipeManager().getRecipeFor(MysticalCompat.RecipeTypes.ENCHANTER.get(), input, level).map(RecipeHolder::value).orElse(null);
        var item = recipe == null ? ItemStack.EMPTY : recipe.assemble(input, level.registryAccess());

        this.result.set(item);
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

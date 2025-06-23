package com.blakebr0.mysticalautomation.tileentity;

import com.blakebr0.cucumber.energy.DynamicEnergyStorage;
import com.blakebr0.cucumber.helper.StackHelper;
import com.blakebr0.cucumber.inventory.BaseItemStackHandler;
import com.blakebr0.cucumber.inventory.OnContentsChangedFunction;
import com.blakebr0.cucumber.inventory.SidedInventoryWrapper;
import com.blakebr0.cucumber.tileentity.BaseInventoryTileEntity;
import com.blakebr0.cucumber.util.ContainerDataBuilder;
import com.blakebr0.mysticalagriculture.api.machine.IUpgradeableMachine;
import com.blakebr0.mysticalagriculture.api.machine.MachineUpgradeItemStackHandler;
import com.blakebr0.mysticalagriculture.api.machine.MachineUpgradeTier;
import com.blakebr0.mysticalautomation.block.InfuserBlock;
import com.blakebr0.mysticalautomation.compat.MysticalCompat;
import com.blakebr0.mysticalautomation.container.InfuserContainer;
import com.blakebr0.mysticalautomation.init.ModTileEntities;
import com.blakebr0.mysticalautomation.util.EssenceTier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.IntStream;

public class InfuserTileEntity extends BaseInventoryTileEntity implements MenuProvider, IUpgradeableMachine {
    private static final int INFUSION_CRYSTAL_SLOT = 0;
    private static final int[] INPUT_SLOTS = IntStream.rangeClosed(1, 6).toArray();
    private static final int FUEL_SLOT = 7;
    private static final int OUTPUT_SLOT = 8;

    public static final int FUEL_TICK_MULTIPLIER = 20;
    public static final int OPERATION_TIME = 100;
    public static final int FUEL_USAGE = 20;
    public static final int FUEL_CAPACITY = 80000;

    private final BaseItemStackHandler inventory;
    private final MachineUpgradeItemStackHandler upgradeInventory;
    private final DynamicEnergyStorage energy;
    private final SidedInventoryWrapper[] sidedInventoryWrappers;

    private final ContainerData dataAccess;

    private MachineUpgradeTier tier;
    private int progress;
    private int progressingIndex;
    private int selectedIndex;
    private int fuelLeft;
    private int fuelItemValue;
    private boolean isRunning;

    public InfuserTileEntity(BlockPos pos, BlockState state) {
        super(ModTileEntities.INFUSER.get(), pos, state);
        this.inventory = createInventoryHandler((slot -> this.setChanged()));
        this.upgradeInventory = new MachineUpgradeItemStackHandler();
        this.energy = new DynamicEnergyStorage(FUEL_CAPACITY, this::setChangedFast);
        this.sidedInventoryWrappers = SidedInventoryWrapper.create(this.inventory, List.of(Direction.UP, Direction.DOWN, Direction.NORTH), this::canInsertStackSided, null);

        this.dataAccess = ContainerDataBuilder.builder()
                .sync(this.energy::getEnergyStored, this.energy::setEnergyStored)
                .sync(this.energy::getMaxEnergyStored, this.energy::setMaxEnergyStorage)
                .sync(() -> this.fuelLeft, value -> this.fuelLeft = value)
                .sync(() -> this.fuelItemValue, value -> this.fuelItemValue = value)
                .sync(() -> this.progress, value -> this.progress = value)
                .sync(this::getOperationTime, value -> {})
                .sync(() -> this.progressingIndex, value -> this.progressingIndex = value)
                .sync(() -> this.selectedIndex, value -> this.selectedIndex = value)
                .build();
    }

    @Override
    public BaseItemStackHandler getInventory() {
        return this.inventory;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.mysticalautomation.infuser");
    }

    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new InfuserContainer(i, inventory, this.inventory, this.upgradeInventory, this.dataAccess, this.getBlockPos());
    }

    @Override
    public MachineUpgradeItemStackHandler getUpgradeInventory() {
        return this.upgradeInventory;
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.loadAdditional(tag, lookup);

        this.progress = tag.getInt("Progress");
        this.progressingIndex = tag.getInt("ProgressingIndex");
        this.selectedIndex = tag.getInt("SelectedIndex");
        this.fuelLeft = tag.getInt("FuelLeft");
        this.fuelItemValue = tag.getInt("FuelItemValue");
        this.energy.deserializeNBT(lookup, tag.get("Energy"));
        this.upgradeInventory.deserializeNBT(lookup, tag.getCompound("UpgradeInventory"));
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.saveAdditional(tag, lookup);

        tag.putInt("Progress", this.progress);
        tag.putInt("ProgressingIndex", this.progressingIndex);
        tag.putInt("SelectedIndex", this.selectedIndex);
        tag.putInt("FuelLeft", this.fuelLeft);
        tag.putInt("FuelItemValue", this.fuelItemValue);
        tag.putInt("Energy", this.energy.getEnergyStored());
        tag.put("UpgradeInventory", this.upgradeInventory.serializeNBT(lookup));
    }

    public static void tick(Level level, BlockPos pos, BlockState state, InfuserTileEntity tile) {
        if (tile.energy.getEnergyStored() < tile.energy.getMaxEnergyStored()) {
            var fuel = tile.inventory.getStackInSlot(FUEL_SLOT);

            if (tile.fuelLeft <= 0 && !fuel.isEmpty()) {
                tile.fuelItemValue = fuel.getBurnTime(null);

                if (tile.fuelItemValue > 0) {
                    tile.fuelLeft = tile.fuelItemValue *= FUEL_TICK_MULTIPLIER;
                    tile.inventory.setStackInSlot(FUEL_SLOT, StackHelper.shrink(fuel, 1, true));

                    tile.setChangedFast();
                }
            }

            if (tile.fuelLeft > 0) {
                var fuelPerTick = Math.min(Math.min(tile.fuelLeft, tile.getFuelUsage() * 2), tile.energy.getMaxEnergyStored() - tile.energy.getEnergyStored());

                tile.fuelLeft -= tile.energy.receiveEnergy(fuelPerTick, false);

                if (tile.fuelLeft <= 0)
                    tile.fuelItemValue = 0;

                tile.setChangedFast();
            }
        }

        var tier = tile.getMachineTier();

        if (tier != tile.tier) {
            tile.tier = tier;

            if (tier == null) {
                tile.energy.resetMaxEnergyStorage();
            } else {
                tile.energy.setMaxEnergyStorage(tier.getFuelCapacity(FUEL_CAPACITY));
            }

            tile.setChangedFast();
        }

        var wasRunning = tile.isRunning;
        tile.isRunning = false;

        if (tile.energy.getEnergyStored() >= tile.getFuelUsage()) {
            var crystal = tile.inventory.getStackInSlot(INFUSION_CRYSTAL_SLOT);
            // when selectedIndex is 0, that means were on the lowest tier already and aren't going to do anything
            if (!crystal.isEmpty() && tile.selectedIndex > 0) {
                tile.progressingIndex = tile.getNextProgressingIndex();

                var processingStack = tile.getProcessingItemStack();
                var essenceTier = EssenceTier.fromIndex(tile.progressingIndex);

                if (!processingStack.isEmpty() && essenceTier != null && essenceTier.getNextTier() != null && essenceTier.getNextTier().getItem() != null) {
                    tile.isRunning = true;

                    if (tile.progress >= tile.getOperationTime()) {
                        var result = new ItemStack(essenceTier.getNextTier().getItem());
                        var outputSlot = tile.progressingIndex + 1 == tile.selectedIndex ? OUTPUT_SLOT : INPUT_SLOTS[tile.progressingIndex + 1];
                        var outputStack = tile.inventory.getStackInSlot(outputSlot);

                        if (StackHelper.canCombineStacks(result, outputStack)) {
                            tile.inventory.setStackInSlot(outputSlot, StackHelper.combineStacks(result, outputStack));
                            tile.inventory.setStackInSlot(INPUT_SLOTS[tile.progressingIndex], StackHelper.shrink(processingStack, 4, false));
                            tile.inventory.setStackInSlot(INFUSION_CRYSTAL_SLOT, crystal.getCraftingRemainingItem());

                            tile.progress = 0;
                        }
                    } else {
                        tile.progress++;
                        tile.energy.extractEnergy(tile.getFuelUsage(), false);
                    }

                    tile.setChangedFast();
                } else {
                    tile.progress = 0;
                }
            } else {
                tile.progress = 0;
            }
        }

        if (wasRunning != tile.isRunning) {
            level.setBlock(pos, state.setValue(InfuserBlock.RUNNING, tile.isRunning), 3);

            tile.setChangedFast();
        }
    }

    public static BaseItemStackHandler createInventoryHandler() {
        return createInventoryHandler(null);
    }

    public static BaseItemStackHandler createInventoryHandler(@Nullable OnContentsChangedFunction onContentsChanged) {
        return BaseItemStackHandler.create(9, onContentsChanged, handler -> {
            for (var slot : INPUT_SLOTS) {
                handler.addSlotLimit(slot, 512);
            }

            handler.setCanInsert((slot, stack) -> switch (slot) {
                case 0 -> MysticalCompat.isInfusionCrystal(stack);
                case 1 -> stack.is(MysticalCompat.Items.INFERIUM_ESSENCE);
                case 2 -> stack.is(MysticalCompat.Items.PRUDENTIUM_ESSENCE);
                case 3 -> stack.is(MysticalCompat.Items.TERTIUM_ESSENCE);
                case 4 -> stack.is(MysticalCompat.Items.IMPERIUM_ESSENCE);
                case 5 -> stack.is(MysticalCompat.Items.SUPREMIUM_ESSENCE);
                case 6 -> MysticalCompat.Items.INSANIUM_ESSENCE.isBound() && stack.is(MysticalCompat.Items.INSANIUM_ESSENCE);
                default -> true;
            });

            handler.setOutputSlots(OUTPUT_SLOT);
            handler.setCanExtract(slot ->
                    slot == OUTPUT_SLOT || (slot == FUEL_SLOT && !FurnaceBlockEntity.isFuel(handler.getStackInSlot(slot)))
            );
        });
    }

    public DynamicEnergyStorage getEnergy() {
        return this.energy;
    }

    public IItemHandler getSidedInventory(@Nullable Direction direction) {
        return switch (direction) {
            case UP -> this.sidedInventoryWrappers[0];
            case DOWN -> this.sidedInventoryWrappers[1];
            case null, default -> this.sidedInventoryWrappers[2];
        };
    }

    public void setSelectedIndex(int index) {
        this.selectedIndex = Math.clamp(index, 0, INPUT_SLOTS.length - 1);
        if (index < this.progressingIndex)
            this.progress = 0;
        this.setChangedFast();
    }

    private ItemStack getProcessingItemStack() {
        return this.inventory.getStackInSlot(INPUT_SLOTS[this.progressingIndex]);
    }

    private int getOperationTime() {
        return this.tier == null ? OPERATION_TIME : this.tier.getOperationTime(OPERATION_TIME);
    }

    private int getFuelUsage() {
        return this.tier == null ? FUEL_USAGE : this.tier.getFuelUsage(FUEL_USAGE);
    }

    private int getNextProgressingIndex() {
        if (this.progress > 0) {
            return this.progressingIndex;
        }

        for (var i = this.selectedIndex - 1; i >= 0; i--) {
            var stack = this.inventory.getStackInSlot(INPUT_SLOTS[i]);
            if (!stack.isEmpty() && stack.getCount() >= 4)
                return i;
        }

        return 0;
    }

    private boolean canInsertStackSided(int slot, ItemStack stack, @Nullable Direction direction) {
        if (direction == null)
            return true;
        if (slot == INFUSION_CRYSTAL_SLOT && direction == Direction.UP)
            return MysticalCompat.isInfusionCrystal(stack);
        if (ArrayUtils.contains(INPUT_SLOTS, slot) && direction == Direction.UP)
            return MysticalCompat.isEssence(stack);
        if (slot == FUEL_SLOT && direction == Direction.NORTH)
            return FurnaceBlockEntity.isFuel(stack);

        return false;
    }
}

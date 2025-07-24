package com.blakebr0.mysticalautomation.tileentity;

import com.blakebr0.cucumber.energy.DynamicEnergyStorage;
import com.blakebr0.cucumber.helper.StackHelper;
import com.blakebr0.cucumber.inventory.BaseItemStackHandler;
import com.blakebr0.cucumber.inventory.CachedRecipe;
import com.blakebr0.cucumber.inventory.OnContentsChangedFunction;
import com.blakebr0.cucumber.inventory.SidedInventoryWrapper;
import com.blakebr0.cucumber.tileentity.BaseInventoryTileEntity;
import com.blakebr0.cucumber.util.ContainerDataBuilder;
import com.blakebr0.mysticalagriculture.api.machine.IUpgradeableMachine;
import com.blakebr0.mysticalagriculture.api.machine.MachineUpgradeItemStackHandler;
import com.blakebr0.mysticalagriculture.api.machine.MachineUpgradeTier;
import com.blakebr0.mysticalautomation.block.FarmerBlock;
import com.blakebr0.mysticalautomation.container.FarmerContainer;
import com.blakebr0.mysticalautomation.crafting.recipe.FarmerRecipe;
import com.blakebr0.mysticalautomation.init.ModRecipeTypes;
import com.blakebr0.mysticalautomation.init.ModTileEntities;
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
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.IntStream;

public class FarmerTileEntity extends BaseInventoryTileEntity implements MenuProvider, IUpgradeableMachine {
    private static final int[] INPUT_SLOTS = IntStream.rangeClosed(0, 2).toArray();
    private static final int FUEL_SLOT = 3;
    private static final int[] OUTPUT_SLOTS = IntStream.rangeClosed(4, 12).toArray();

    public static final int FUEL_TICK_MULTIPLIER = 20;
    public static final int OPERATION_TIME = 100;
    public static final int FUEL_USAGE = 20;
    public static final int FUEL_CAPACITY = 80000;

    private final BaseItemStackHandler inventory;
    private final MachineUpgradeItemStackHandler upgradeInventory;
    private final DynamicEnergyStorage energy;
    private final SidedInventoryWrapper[] sidedInventoryWrappers;
    private final CachedRecipe<RecipeInput, FarmerRecipe> recipe;

    private final ContainerData dataAccess;

    private MachineUpgradeTier tier;
    private int progress;
    private int stages;
    private int stageProgress;
    private int fuelLeft;
    private int fuelItemValue;
    private boolean isRunning;

    public FarmerTileEntity(BlockPos pos, BlockState state) {
        super(ModTileEntities.FARMER.get(), pos, state);
        this.inventory = createInventoryHandler(slot -> this.setChanged());
        this.upgradeInventory = new MachineUpgradeItemStackHandler();
        this.energy = new DynamicEnergyStorage(FUEL_CAPACITY, this::setChangedFast);
        this.sidedInventoryWrappers = SidedInventoryWrapper.create(this.inventory, List.of(Direction.UP, Direction.DOWN, Direction.NORTH), this::canInsertStackSided, null);
        this.recipe = new CachedRecipe<>(ModRecipeTypes.FARMER.get());

        this.dataAccess = ContainerDataBuilder.builder()
                .sync(this.energy::getEnergyStored, this.energy::setEnergyStored)
                .sync(this.energy::getMaxEnergyStored, this.energy::setMaxEnergyStorage)
                .sync(() -> this.fuelLeft, value -> this.fuelLeft = value)
                .sync(() -> this.fuelItemValue, value -> this.fuelItemValue = value)
                .sync(() -> this.progress, value -> this.progress = value)
                .sync(this::getOperationTime, value -> {})
                .sync(() -> this.stages, value -> this.stages = value)
                .sync(() -> this.stageProgress, value -> this.stageProgress = value)
                .sync(this::getStageOperationTime, value -> {})
                .build();
    }

    @Override
    public BaseItemStackHandler getInventory() {
        return this.inventory;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.mysticalautomation.farmer");
    }

    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new FarmerContainer(i, inventory, this.inventory, this.upgradeInventory, this.dataAccess, this.getBlockPos());
    }

    @Override
    public MachineUpgradeItemStackHandler getUpgradeInventory() {
        return this.upgradeInventory;
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.loadAdditional(tag, lookup);

        this.progress = tag.getInt("Progress");
        this.stages = tag.getInt("Stages");
        this.stageProgress = tag.getInt("StageProgress");
        this.fuelLeft = tag.getInt("FuelLeft");
        this.fuelItemValue = tag.getInt("FuelItemValue");
        this.energy.deserializeNBT(lookup, tag.get("Energy"));
        this.upgradeInventory.deserializeNBT(lookup, tag.getCompound("UpgradeInventory"));
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.saveAdditional(tag, lookup);

        tag.putInt("Progress", this.progress);
        tag.putInt("Stages", this.stages);
        tag.putInt("StageProgress", this.stageProgress);
        tag.putInt("FuelLeft", this.fuelLeft);
        tag.putInt("FuelItemValue", this.fuelItemValue);
        tag.putInt("Energy", this.energy.getEnergyStored());
        tag.put("UpgradeInventory", this.upgradeInventory.serializeNBT(lookup));
    }

    public static void tick(Level level, BlockPos pos, BlockState state, FarmerTileEntity tile) {
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
            var recipe = tile.getActiveRecipe();
            if (recipe != null) {
                tile.isRunning = true;
                tile.stages = recipe.getStages();
                tile.energy.extractEnergy(tile.getFuelUsage(), false);

                if (tile.stageProgress >= tile.getStageOperationTime()) {
                    tile.progress++;

                    if (tile.progress >= tile.getOperationTime()) {
                        var results = recipe.getRolledResults();

                        for (var result : results) {
                            tile.addItemToInventory(result);
                        }

                        tile.reset();
                    }
                } else {
                    tile.stageProgress++;
                }

                tile.setChangedFast();
            } else {
                if (tile.progress > 0 || tile.stageProgress > 0 || tile.stages > 0) {
                    tile.reset();
                    tile.setChangedFast();
                }
            }
        }

        if (wasRunning != tile.isRunning) {
            level.setBlock(pos, state.setValue(FarmerBlock.RUNNING, tile.isRunning), 3);

            tile.setChangedFast();
        }
    }

    public static BaseItemStackHandler createInventoryHandler() {
        return createInventoryHandler(null);
    }

    public static BaseItemStackHandler createInventoryHandler(@Nullable OnContentsChangedFunction onContentsChanged) {
        return BaseItemStackHandler.create(16, onContentsChanged, handler -> {
            handler.setCanInsert((slot, stack) -> switch (slot) {
                default -> true;
            });

            for (var slot : INPUT_SLOTS) {
                handler.addSlotLimit(slot, 1);
            }

            handler.setOutputSlots(OUTPUT_SLOTS);
            handler.setCanExtract(slot ->
                    ArrayUtils.contains(OUTPUT_SLOTS, slot) || (slot == FUEL_SLOT && !FurnaceBlockEntity.isFuel(handler.getStackInSlot(slot)))
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

    private void reset() {
        this.progress = 0;
        this.stages = 0;
        this.stageProgress = 0;
    }

    @Nullable
    private FarmerRecipe getActiveRecipe() {
        return this.recipe.checkAndGet(this.toCraftingInput(), this.level);
    }

    private int getOperationTime() {
        return this.tier == null ? OPERATION_TIME : this.tier.getOperationTime(OPERATION_TIME);
    }

    private int getFuelUsage() {
        return this.tier == null ? FUEL_USAGE : this.tier.getFuelUsage(FUEL_USAGE);
    }

    private int getStageOperationTime() {
        return this.tier == null ? OPERATION_TIME * this.stages : this.tier.getOperationTime(OPERATION_TIME) * this.stages;
    }

    private CraftingInput toCraftingInput() {
        return this.inventory.toCraftingInput(1, 3);
    }

    private void addItemToInventory(ItemStack stack) {
        var remaining = stack.getCount();
        for (var slot : OUTPUT_SLOTS) {
            var stackInSlot = this.inventory.getStackInSlot(slot);

            if (stackInSlot.isEmpty()) {
                this.inventory.setStackInSlot(slot, stack.copy());
                return;
            }

            if (StackHelper.areStacksEqual(stackInSlot, stack)) {
                var insertSize = Math.min(remaining, stackInSlot.getMaxStackSize() - stackInSlot.getCount());

                this.inventory.setStackInSlot(slot, StackHelper.grow(stackInSlot, insertSize));

                remaining -= insertSize;
            }

            if (remaining == 0)
                return;
        }
    }

    private boolean canInsertStackSided(int slot, ItemStack stack, @Nullable Direction direction) {
        if (direction == null)
            return true;
        if (ArrayUtils.contains(INPUT_SLOTS, slot) && direction == Direction.UP)
            return true;
        if (slot == FUEL_SLOT && direction == Direction.NORTH)
            return FurnaceBlockEntity.isFuel(stack);

        return false;
    }
}

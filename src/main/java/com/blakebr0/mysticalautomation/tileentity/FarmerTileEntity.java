package com.blakebr0.mysticalautomation.tileentity;

import com.blakebr0.cucumber.energy.CEnergyStorage;
import com.blakebr0.cucumber.inventory.CItemStacksHandler;
import com.blakebr0.cucumber.inventory.CachedRecipe;
import com.blakebr0.cucumber.inventory.OnContentsChangedFunction;
import com.blakebr0.cucumber.inventory.SidedInventoryWrapper;
import com.blakebr0.cucumber.tileentity.BaseInventoryTileEntity;
import com.blakebr0.cucumber.util.ContainerDataBuilder;
import com.blakebr0.mysticalagriculture.api.machine.IUpgradeableMachine;
import com.blakebr0.mysticalagriculture.api.machine.MachineUpgradeItemStackHandler;
import com.blakebr0.mysticalagriculture.api.machine.MachineUpgradeTier;
import com.blakebr0.mysticalautomation.api.crafting.IFarmerRecipe;
import com.blakebr0.mysticalautomation.block.FarmerBlock;
import com.blakebr0.mysticalautomation.container.FarmerContainer;
import com.blakebr0.mysticalautomation.init.ModRecipeTypes;
import com.blakebr0.mysticalautomation.init.ModTileEntities;
import com.blakebr0.mysticalautomation.util.RecipeIngredientCache;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class FarmerTileEntity extends BaseInventoryTileEntity implements MenuProvider, IUpgradeableMachine {
    private static final int[] INPUT_SLOTS = IntStream.rangeClosed(0, 2).toArray();
    private static final int FUEL_SLOT = 3;
    private static final int[] OUTPUT_SLOTS = IntStream.rangeClosed(4, 12).toArray();

    public static final int FUEL_TICK_MULTIPLIER = 20;
    public static final int OPERATION_TIME = 800;
    public static final int FUEL_USAGE = 20;
    public static final int FUEL_CAPACITY = 80000;

    private final CItemStacksHandler inventory;
    private final MachineUpgradeItemStackHandler upgradeInventory;
    private final CEnergyStorage energy;
    private final SidedInventoryWrapper[] sidedInventoryWrappers;
    private final CachedRecipe<RecipeInput, IFarmerRecipe> recipe;

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
        this.inventory = createInventoryHandler((_, _) -> this.setChanged(), this::getLevel);
        this.upgradeInventory = new MachineUpgradeItemStackHandler();
        this.energy = new CEnergyStorage(FUEL_CAPACITY, _ -> this.setChangedFast());
        this.sidedInventoryWrappers = SidedInventoryWrapper.create(this.inventory, List.of(Direction.UP, Direction.DOWN, Direction.NORTH), this::canInsertStackSided, null);
        this.recipe = new CachedRecipe<>(ModRecipeTypes.FARMER.get());

        this.dataAccess = ContainerDataBuilder.builder()
                .sync(this.energy::getAmountAsInt, this.energy::set)
                .sync(this.energy::getCapacityAsInt, this.energy::setMaxCapacity)
                .sync(() -> this.fuelLeft, value -> this.fuelLeft = value)
                .sync(() -> this.fuelItemValue, value -> this.fuelItemValue = value)
                .sync(() -> this.progress, value -> this.progress = value)
                .sync(this::getOperationTime)
                .sync(() -> this.stages, value -> this.stages = value)
                .sync(() -> this.stageProgress, value -> this.stageProgress = value)
                .sync(this::getStageOperationTime)
                .build();
    }

    @Override
    public CItemStacksHandler getInventory() {
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
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        this.progress = input.getIntOr("progress", 0);
        this.stages = input.getIntOr("stages", 0);
        this.stageProgress = input.getIntOr("stage_progress", 0);
        this.fuelLeft = input.getIntOr("fuel_left", 0);
        this.fuelItemValue = input.getIntOr("fuel_item_value", 0);
        this.energy.deserialize(input.childOrEmpty("energy"));
        this.upgradeInventory.deserialize(input.childOrEmpty("upgrade_inventory"));
    }

    @Override
    public void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);

        output.putInt("progress", this.progress);
        output.putInt("stages", this.stages);
        output.putInt("stage_progress", this.stageProgress);
        output.putInt("fuel_left", this.fuelLeft);
        output.putInt("fuel_item_value", this.fuelItemValue);
        output.putChild("energy", this.energy);
        output.putChild("upgrade_inventory", this.upgradeInventory);
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        super.preRemoveSideEffects(pos, state);

        if (this.level != null) {
            Containers.dropItemStack(this.level, pos.getX(), pos.getY(), pos.getZ(), this.upgradeInventory.getStackCopy());
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, FarmerTileEntity tile) {
        if (tile.energy.getAmountAsInt() < tile.energy.getCapacityAsInt()) {
            var fuel = tile.inventory.getResource(FUEL_SLOT);

            try (var tx = Transaction.openRoot()) {
                if (tile.fuelLeft <= 0 && !fuel.isEmpty()) {
                    tile.fuelItemValue = fuel.toStack().getBurnTime(null, level.fuelValues());

                    if (tile.fuelItemValue > 0) {
                        tile.fuelLeft = tile.fuelItemValue *= FUEL_TICK_MULTIPLIER;
                        tile.inventory.extract(FUEL_SLOT, fuel, 1, tx, true);

                        tile.setChangedFast();
                    }
                }

                if (tile.fuelLeft > 0) {
                    var fuelPerTick = Math.min(Math.min(tile.fuelLeft, tile.getFuelUsage() * 2), tile.energy.getCapacityAsInt() - tile.energy.getAmountAsInt());

                    tile.fuelLeft -= tile.energy.insert(fuelPerTick, tx);

                    if (tile.fuelLeft <= 0)
                        tile.fuelItemValue = 0;

                    tile.setChangedFast();
                }

                tx.commit();
            }
        }

        var tier = tile.getMachineTier();

        if (tier != tile.tier) {
            tile.tier = tier;

            if (tier == null) {
                tile.energy.resetMaxCapacity();
            } else {
                tile.energy.setMaxCapacity(tier.getFuelCapacity(FUEL_CAPACITY));
            }

            tile.setChangedFast();
        }

        var wasRunning = tile.isRunning;
        tile.isRunning = false;

        if (tile.energy.getAmountAsInt() >= tile.getFuelUsage()) {
            var recipe = tile.getActiveRecipe();
            if (recipe != null) {
                try (var tx = Transaction.openRoot()) {
                    tile.isRunning = true;
                    tile.stages = recipe.getStages();
                    tile.energy.extract(tile.getFuelUsage(), tx);

                    if (tile.stageProgress >= tile.getStageOperationTime()) {
                        tile.progress++;

                        if (tile.progress >= tile.getOperationTime()) {
                            var results = recipe.getRolledResults();

                            for (var result : results) {
                                var remaining = result.count();
                                for (var slot : OUTPUT_SLOTS) {
                                    remaining = ItemUtil.insertItemReturnRemaining(tile.inventory, slot, result, false, tx).count();
                                    if (remaining == 0)
                                        return;
                                }
                            }

                            tile.reset();
                        }
                    } else {
                        tile.stageProgress++;
                    }

                    tx.commit();
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

    public CEnergyStorage getEnergy() {
        return this.energy;
    }

    public ItemStacksResourceHandler getSidedInventory(@Nullable Direction direction) {
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
    private IFarmerRecipe getActiveRecipe() {
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
        return this.inventory.toCraftingInput(1, 3, INPUT_SLOTS[0], INPUT_SLOTS[0] + INPUT_SLOTS.length);
    }

    private boolean canInsertStackSided(int slot, ItemResource resource, @Nullable Direction direction) {
        if (direction == null)
            return true;
        if (ArrayUtils.contains(INPUT_SLOTS, slot) && direction == Direction.UP)
            return true;
        if (slot == FUEL_SLOT && direction == Direction.NORTH)
            return this.level != null && this.level.fuelValues().isFuel(resource.toStack());

        return false;
    }

    public static CItemStacksHandler createInventoryHandler() {
        return createInventoryHandler(null, () -> null);
    }

    public static CItemStacksHandler createInventoryHandler(@Nullable OnContentsChangedFunction onContentsChanged, Supplier<Level> level) {
        return CItemStacksHandler.create(16, onContentsChanged, handler -> {
            handler.setCanInsert((slot, resource) -> switch (slot) {
                case 0 -> RecipeIngredientCache.INSTANCE.isValidInput(RecipeIngredientCache.Key.FARMER_SEEDS, resource.toStack());
                case 1 -> RecipeIngredientCache.INSTANCE.isValidInput(RecipeIngredientCache.Key.FARMER_SOIL, resource.toStack());
                case 2 -> RecipeIngredientCache.INSTANCE.isValidInput(RecipeIngredientCache.Key.FARMER_CRUX, resource.toStack());
                default -> true;
            });

            for (var slot : INPUT_SLOTS) {
                handler.addSlotLimit(slot, 1);
            }

            handler.setOutputSlots(OUTPUT_SLOTS);
            handler.setCanExtract(slot ->
                    ArrayUtils.contains(OUTPUT_SLOTS, slot) || (slot == FUEL_SLOT && level.get() == null || level.get() != null && level.get().fuelValues().isFuel(handler.getResource(slot).toStack()))
            );
        });
    }
}

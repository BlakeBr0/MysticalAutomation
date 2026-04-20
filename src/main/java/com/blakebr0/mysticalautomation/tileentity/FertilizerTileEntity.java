package com.blakebr0.mysticalautomation.tileentity;

import com.blakebr0.cucumber.energy.CEnergyStorage;
import com.blakebr0.cucumber.inventory.CItemStacksHandler;
import com.blakebr0.cucumber.inventory.OnContentsChangedFunction;
import com.blakebr0.cucumber.inventory.SidedInventoryWrapper;
import com.blakebr0.cucumber.tileentity.BaseInventoryTileEntity;
import com.blakebr0.cucumber.util.ContainerDataBuilder;
import com.blakebr0.mysticalagriculture.api.machine.IUpgradeableMachine;
import com.blakebr0.mysticalagriculture.api.machine.MachineUpgradeItemStackHandler;
import com.blakebr0.mysticalagriculture.api.machine.MachineUpgradeTier;
import com.blakebr0.mysticalautomation.block.FertilizerBlock;
import com.blakebr0.mysticalautomation.container.FertilizerContainer;
import com.blakebr0.mysticalautomation.init.ModTileEntities;
import com.blakebr0.mysticalautomation.lib.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.IntStream;

public class FertilizerTileEntity extends BaseInventoryTileEntity implements MenuProvider, IUpgradeableMachine {
    private static final int[] INPUT_SLOTS = IntStream.rangeClosed(0, 7).toArray();
    private static final int FUEL_SLOT = 8;

    public static final int FUEL_TICK_MULTIPLIER = 20;
    public static final int OPERATION_TIME = 200;
    public static final int FUEL_USAGE = 40;
    public static final int SCAN_FUEL_USAGE = 10;
    public static final int FUEL_CAPACITY = 80000;
    public static final int BASE_RANGE = 1;

    private final CItemStacksHandler inventory;
    private final MachineUpgradeItemStackHandler upgradeInventory;
    private final CEnergyStorage energy;
    private final SidedInventoryWrapper[] sidedInventoryWrappers;

    private final ContainerData dataAccess;

    private MachineUpgradeTier tier;
    private int progress;
    private int lastScanIndex = -1;
    private int fuelLeft;
    private int fuelItemValue;
    private boolean isRunning;

    public FertilizerTileEntity(BlockPos pos, BlockState state) {
        super(ModTileEntities.FERTILIZER.get(), pos, state);
        this.inventory = createInventoryHandler((_, _) -> this.setChanged());
        this.upgradeInventory = new MachineUpgradeItemStackHandler();
        this.energy = new CEnergyStorage(FUEL_CAPACITY, _ -> this.setChangedFast());
        this.sidedInventoryWrappers = SidedInventoryWrapper.create(this.inventory, List.of(Direction.UP, Direction.DOWN, Direction.NORTH), this::canInsertStackSided, null);

        this.dataAccess = ContainerDataBuilder.builder()
                .sync(this.energy::getAmountAsInt, this.energy::set)
                .sync(this.energy::getCapacityAsInt, this.energy::setMaxCapacity)
                .sync(() -> this.fuelLeft, value -> this.fuelLeft = value)
                .sync(() -> this.fuelItemValue, value -> this.fuelItemValue = value)
                .build();
    }

    @Override
    public CItemStacksHandler getInventory() {
        return this.inventory;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.mysticalautomation.fertilizer");
    }

    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new FertilizerContainer(i, inventory, this.inventory, this.upgradeInventory, this.dataAccess, this.getBlockPos());
    }

    @Override
    public MachineUpgradeItemStackHandler getUpgradeInventory() {
        return this.upgradeInventory;
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        this.progress = input.getIntOr("Progress", 0);
        this.lastScanIndex = input.getIntOr("LastScanIndex", 0);
        this.fuelLeft = input.getIntOr("fuel_left", 0);
        this.fuelItemValue = input.getIntOr("fuel_item_value", 0);
        this.energy.deserialize(input.childOrEmpty("energy"));
        this.upgradeInventory.deserialize(input.childOrEmpty("upgrade_inventory"));
    }

    @Override
    public void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);

        output.putInt("progress", this.progress);
        output.putInt("last_scan_index", this.lastScanIndex);
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

    public static void tick(Level level, BlockPos pos, BlockState state, FertilizerTileEntity tile) {
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

        if (tile.energy.getAmountAsInt() >= tile.getFuelUsage() && !level.hasNeighborSignal(pos)) {
            var slot = tile.findNextFertilizerSlot();
            if (slot != -1) {
                tile.isRunning = true;
                tile.progress++;

                if (tile.progress >= tile.getOperationTime()) {
                    var nextPos = tile.findNextPosition(state.getValue(FertilizerBlock.FACING));
                    var plantState = level.getBlockState(nextPos);
                    var plantBlock = plantState.getBlock();

                    try (var tx = Transaction.openRoot()) {
                        if (plantBlock instanceof BonemealableBlock bonemealable && bonemealable.isValidBonemealTarget(level, nextPos, plantState)) {
                            var stack = ItemUtil.getStack(tile.inventory, slot);
                            var hitResult = new BlockHitResult(nextPos.getCenter(), Direction.DOWN, nextPos, false);
                            var context = new UseOnContext(level, null, InteractionHand.MAIN_HAND, stack, hitResult);

                            if (stack.getItem().useOn(context) == InteractionResult.SUCCESS) {
                                tile.energy.extract(tile.getFuelUsage(), tx);
                            } else {
                                tile.energy.extract(SCAN_FUEL_USAGE, tx);
                            }
                        } else {
                            tile.energy.extract(SCAN_FUEL_USAGE, tx);
                        }

                        tx.commit();
                    }

                    tile.progress = 0;
                }

                tile.setChangedFast();
            } else {
                if (tile.progress > 0) {
                    tile.progress = 0;
                    tile.setChangedFast();
                }
            }
        }

        if (wasRunning != tile.isRunning) {
            level.setBlock(pos, state.setValue(FertilizerBlock.RUNNING, tile.isRunning), 3);

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

    private int getOperationTime() {
        return this.tier == null ? OPERATION_TIME : this.tier.getOperationTime(OPERATION_TIME);
    }

    private int getFuelUsage() {
        return this.tier == null ? FUEL_USAGE : this.tier.getFuelUsage(FUEL_USAGE);
    }

    private int findNextFertilizerSlot() {
        for (var slot : INPUT_SLOTS) {
            var stack = this.inventory.getResource(slot);
            if (!stack.isEmpty())
                return slot;
        }

        return -1;
    }

    private BlockPos findNextPosition(Direction direction) {
        var range = this.tier != null ? BASE_RANGE + this.tier.getAddedRange() : BASE_RANGE;
        var size = range * 2 + 1;

        var index = this.lastScanIndex + 1;
        if (index >= (int) Math.pow(size, 2)) {
            index = 0;
        }

        this.lastScanIndex = index;

        var xOffset = (index % size) - range;
        var zOffset = (index / size) - range;

        var center = this.getBlockPos().relative(direction, range + 1);

        return switch (direction) {
            case Direction.NORTH -> new BlockPos(center.getX() + xOffset, center.getY(), center.getZ() - zOffset);
            case Direction.SOUTH -> new BlockPos(center.getX() - xOffset, center.getY(), center.getZ() + zOffset);
            case Direction.EAST -> new BlockPos(center.getX() + zOffset, center.getY(), center.getZ() + xOffset);
            case Direction.WEST -> new BlockPos(center.getX() - zOffset, center.getY(), center.getZ() - xOffset);
            default -> center;
        };
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
        return createInventoryHandler(null);
    }

    public static CItemStacksHandler createInventoryHandler(@Nullable OnContentsChangedFunction onContentsChanged) {
        return CItemStacksHandler.create(9, onContentsChanged, handler -> {
            handler.setCanInsert((slot, stack) -> {
                if (ArrayUtils.contains(INPUT_SLOTS, slot)) {
                    return stack.is(ModTags.Items.FERTILIZERS);
                }

                return true;
            });
        });
    }
}

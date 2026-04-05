package com.blakebr0.mysticalautomation.tileentity;

import com.blakebr0.cucumber.energy.CEnergyStorage;
import com.blakebr0.cucumber.helper.ItemResourceHelper;
import com.blakebr0.cucumber.inventory.CItemStacksHandler;
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
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
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

public class InfuserTileEntity extends BaseInventoryTileEntity implements MenuProvider, IUpgradeableMachine {
    private static final int INFUSION_CRYSTAL_SLOT = 0;
    private static final int[] INPUT_SLOTS = IntStream.rangeClosed(1, 6).toArray();
    private static final int FUEL_SLOT = 7;
    private static final int OUTPUT_SLOT = 8;

    public static final int FUEL_TICK_MULTIPLIER = 20;
    public static final int OPERATION_TIME = 100;
    public static final int FUEL_USAGE = 20;
    public static final int FUEL_CAPACITY = 80000;

    private final CItemStacksHandler inventory;
    private final MachineUpgradeItemStackHandler upgradeInventory;
    private final CEnergyStorage energy;
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
        this.inventory = createInventoryHandler((_, _) -> this.setChanged(), this::getLevel);
        this.upgradeInventory = new MachineUpgradeItemStackHandler();
        this.energy = new CEnergyStorage(FUEL_CAPACITY, _ -> this.setChangedFast());
        this.sidedInventoryWrappers = SidedInventoryWrapper.create(this.inventory, List.of(Direction.UP, Direction.DOWN, Direction.NORTH), this::canInsertStackSided, null);

        this.dataAccess = ContainerDataBuilder.builder()
                .sync(this.energy::getAmountAsInt, this.energy::set)
                .sync(this.energy::getCapacityAsInt, this.energy::setMaxCapacity)
                .sync(() -> this.fuelLeft, value -> this.fuelLeft = value)
                .sync(() -> this.fuelItemValue, value -> this.fuelItemValue = value)
                .sync(() -> this.progress, value -> this.progress = value)
                .sync(this::getOperationTime)
                .sync(() -> this.progressingIndex, value -> this.progressingIndex = value)
                .sync(() -> this.selectedIndex, value -> this.selectedIndex = value)
                .build();
    }

    @Override
    public CItemStacksHandler getInventory() {
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
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        this.progress = input.getIntOr("Progress", 0);
        this.progressingIndex = input.getIntOr("ProgressingIndex", 0);
        this.selectedIndex = input.getIntOr("SelectedIndex", 0);
        this.fuelLeft = input.getIntOr("FuelLeft", 0);
        this.fuelItemValue = input.getIntOr("FuelItemValue", 0);
        this.energy.deserialize(input.childOrEmpty("Energy"));
        this.upgradeInventory.deserialize(input.childOrEmpty("UpgradeInventory"));
    }

    @Override
    public void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);

        output.putInt("Progress", this.progress);
        output.putInt("ProgressingIndex", this.progressingIndex);
        output.putInt("SelectedIndex", this.selectedIndex);
        output.putInt("FuelLeft", this.fuelLeft);
        output.putInt("FuelItemValue", this.fuelItemValue);
        output.putChild("Energy", this.energy);
        output.putChild("UpgradeInventory", this.upgradeInventory);
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        super.preRemoveSideEffects(pos, state);

        if (this.level != null) {
            Containers.dropItemStack(this.level, pos.getX(), pos.getY(), pos.getZ(), this.upgradeInventory.getStackCopy());
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, InfuserTileEntity tile) {
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
            var crystal = tile.inventory.getResource(INFUSION_CRYSTAL_SLOT);
            // when selectedIndex is 0, that means were on the lowest tier already and aren't going to do anything
            if (!crystal.isEmpty() && tile.selectedIndex > 0) {
                tile.progressingIndex = tile.getNextProgressingIndex();

                var processingStack = tile.getProcessingItemStack();
                var essenceTier = EssenceTier.fromIndex(tile.progressingIndex);

                if (!processingStack.isEmpty() && essenceTier != null && essenceTier.getNextTier() != null && essenceTier.getNextTier().getItem() != null) {
                    tile.isRunning = true;

                    try (var tx = Transaction.openRoot()) {
                        if (tile.progress >= tile.getOperationTime()) {
                            var result = new ItemStack(essenceTier.getNextTier().getItem());
                            var outputSlot = tile.progressingIndex + 1 == tile.selectedIndex ? OUTPUT_SLOT : INPUT_SLOTS[tile.progressingIndex + 1];

                            if (ItemResourceHelper.canCombine(tile.inventory, outputSlot, result)) {
                                tile.inventory.insert(outputSlot, ItemResource.of(result), result.count(), tx, true);
                                tile.inventory.extract(INPUT_SLOTS[tile.progressingIndex], ItemResource.of(processingStack), 4, tx, true);

                                var remainder = crystal.toStack().getCraftingRemainder();

                                tile.inventory.set(INFUSION_CRYSTAL_SLOT, ItemResource.of(remainder), 1);

                                tile.progress = 0;
                                tile.setChangedFast();
                            }
                        } else {
                            tile.progress++;
                            tile.energy.extract(tile.getFuelUsage(), tx);
                            tile.setChangedFast();
                        }

                        tx.commit();
                    }
                } else {
                    if (tile.progress > 0) {
                        tile.progress = 0;
                        tile.setChangedFast();
                    }
                }
            } else {
                if (tile.progress > 0) {
                    tile.progress = 0;
                    tile.setChangedFast();
                }
            }
        }

        if (wasRunning != tile.isRunning) {
            level.setBlock(pos, state.setValue(InfuserBlock.RUNNING, tile.isRunning), 3);

            tile.setChangedFast();
        }
    }

    public CEnergyStorage getEnergy() {
        return this.energy;
    }

    public int getSelectedIndex() {
        return this.selectedIndex;
    }

    public ItemStacksResourceHandler getSidedInventory(@Nullable Direction direction) {
        return switch (direction) {
            case UP -> this.sidedInventoryWrappers[0];
            case DOWN -> this.sidedInventoryWrappers[1];
            case null, default -> this.sidedInventoryWrappers[2];
        };
    }

    public void setSelectedIndex(int index) {
        if (index == this.selectedIndex)
            return;

        this.selectedIndex = Math.clamp(index, 0, INPUT_SLOTS.length - 1);

        if (index <= this.progressingIndex)
            this.progress = 0;

        this.setChangedFast();
    }

    private ItemStack getProcessingItemStack() {
        // when there's no valid processing index, it's -1
        if (this.progressingIndex < 0)
            return ItemStack.EMPTY;

        return ItemUtil.getStack(this.inventory, INPUT_SLOTS[this.progressingIndex]);
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
            var amount = this.inventory.getAmountAsInt(INPUT_SLOTS[i]);
            if (amount >= 4)
                return i;
        }

        return -1;
    }

    private boolean canInsertStackSided(int slot, ItemResource resource, @Nullable Direction direction) {
        if (direction == null)
            return true;
        if (slot == INFUSION_CRYSTAL_SLOT && direction == Direction.UP)
            return MysticalCompat.isInfusionCrystal(resource.toStack());
        if (ArrayUtils.contains(INPUT_SLOTS, slot) && direction == Direction.UP)
            return MysticalCompat.isEssence(resource.toStack());
        if (slot == FUEL_SLOT && direction == Direction.NORTH)
            return this.level != null && this.level.fuelValues().isFuel(resource.toStack());

        return false;
    }

    public static CItemStacksHandler createInventoryHandler() {
        return createInventoryHandler(null, () -> null);
    }

    public static CItemStacksHandler createInventoryHandler(@Nullable OnContentsChangedFunction onContentsChanged, Supplier<Level> level) {
        return CItemStacksHandler.create(9, onContentsChanged, handler -> {
            for (var slot : INPUT_SLOTS) {
                handler.addSlotLimit(slot, 512);
            }

            handler.setCanInsert((slot, resource) -> switch (slot) {
                case 0 -> MysticalCompat.isInfusionCrystal(resource.toStack());
                case 1 -> resource.is(MysticalCompat.Items.INFERIUM_ESSENCE);
                case 2 -> resource.is(MysticalCompat.Items.PRUDENTIUM_ESSENCE);
                case 3 -> resource.is(MysticalCompat.Items.TERTIUM_ESSENCE);
                case 4 -> resource.is(MysticalCompat.Items.IMPERIUM_ESSENCE);
                case 5 -> resource.is(MysticalCompat.Items.SUPREMIUM_ESSENCE);
                case 6 -> MysticalCompat.Items.INSANIUM_ESSENCE.isBound() && resource.is(MysticalCompat.Items.INSANIUM_ESSENCE);
                default -> true;
            });

            handler.setOutputSlots(OUTPUT_SLOT);
            handler.setCanExtract(slot ->
                    slot == OUTPUT_SLOT || (slot == FUEL_SLOT && level.get() == null || level.get() != null && level.get().fuelValues().isFuel(handler.getResource(slot).toStack()))
            );
        });
    }
}

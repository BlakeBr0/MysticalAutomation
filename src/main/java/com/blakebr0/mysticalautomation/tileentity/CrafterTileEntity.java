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
import com.blakebr0.mysticalautomation.block.CrafterBlock;
import com.blakebr0.mysticalautomation.container.CrafterContainer;
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
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class CrafterTileEntity extends BaseInventoryTileEntity implements MenuProvider, IUpgradeableMachine {
    private static final int[] INPUT_SLOTS = IntStream.rangeClosed(0, 8).toArray();
    private static final int FUEL_SLOT = 9;
    private static final int OUTPUT_SLOT = 10;

    public static final int FUEL_TICK_MULTIPLIER = 20;
    public static final int OPERATION_TIME = 100;
    public static final int FUEL_USAGE = 20;
    public static final int FUEL_CAPACITY = 80000;

    private final BaseItemStackHandler inventory;
    private final BaseItemStackHandler recipeInventory;
    private final MachineUpgradeItemStackHandler upgradeInventory;
    private final DynamicEnergyStorage energy;
    private final SidedInventoryWrapper[] sidedInventoryWrappers;
    private final CachedRecipe<CraftingInput, CraftingRecipe> recipe;

    private final ContainerData dataAccess;

    private MachineUpgradeTier tier;
    private int progress;
    private int fuelLeft;
    private int fuelItemValue;
    private boolean isRunning;
    private boolean isGridChanged = true;

    public CrafterTileEntity(BlockPos pos, BlockState state) {
        super(ModTileEntities.CRAFTER.get(), pos, state);
        this.recipeInventory = createRecipeInventoryHandler(slot -> {
            this.isGridChanged = true;
            this.setChangedFast();
        });
        this.inventory = createInventoryHandler(this.recipeInventory, slot -> this.setChanged());
        this.upgradeInventory = new MachineUpgradeItemStackHandler();
        this.energy = new DynamicEnergyStorage(FUEL_CAPACITY, this::setChangedFast);
        this.sidedInventoryWrappers = SidedInventoryWrapper.create(this.inventory, List.of(Direction.UP, Direction.DOWN, Direction.NORTH), this::canInsertStackSided, null);
        this.recipe = new CachedRecipe<>(RecipeType.CRAFTING);

        this.dataAccess = ContainerDataBuilder.builder()
                .sync(this.energy::getEnergyStored, this.energy::setEnergyStored)
                .sync(this.energy::getMaxEnergyStored, this.energy::setMaxEnergyStorage)
                .sync(() -> this.fuelLeft, value -> this.fuelLeft = value)
                .sync(() -> this.fuelItemValue, value -> this.fuelItemValue = value)
                .sync(() -> this.progress, value -> this.progress = value)
                .sync(this::getOperationTime, value -> {})
                .build();
    }

    @Override
    public BaseItemStackHandler getInventory() {
        return this.inventory;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.mysticalautomation.crafter");
    }

    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new CrafterContainer(i, inventory, this.inventory, this.recipeInventory, this.upgradeInventory, this.dataAccess, this.getBlockPos());
    }

    @Override
    public MachineUpgradeItemStackHandler getUpgradeInventory() {
        return this.upgradeInventory;
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.loadAdditional(tag, lookup);

        this.progress = tag.getInt("Progress");
        this.fuelLeft = tag.getInt("FuelLeft");
        this.fuelItemValue = tag.getInt("FuelItemValue");
        this.energy.deserializeNBT(lookup, tag.get("Energy"));
        this.recipeInventory.deserializeNBT(lookup, tag.getCompound("RecipeInventory"));
        this.upgradeInventory.deserializeNBT(lookup, tag.getCompound("UpgradeInventory"));
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.saveAdditional(tag, lookup);

        tag.putInt("Progress", this.progress);
        tag.putInt("FuelLeft", this.fuelLeft);
        tag.putInt("FuelItemValue", this.fuelItemValue);
        tag.putInt("Energy", this.energy.getEnergyStored());
        tag.put("RecipeInventory", this.recipeInventory.serializeNBT(lookup));
        tag.put("UpgradeInventory", this.upgradeInventory.serializeNBT(lookup));
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CrafterTileEntity tile) {
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
                var inputs = tile.getInputResult(recipe);
                if (inputs.hasAll) {
                    tile.isRunning = true;

                    if (tile.progress >= tile.getOperationTime()) {
                        var result = recipe.assemble(tile.toCraftingInput(), level.registryAccess());
                        var output = tile.inventory.getStackInSlot(OUTPUT_SLOT);

                        if (StackHelper.canCombineStacks(result, output)) {
                            int[] amounts = inputs.amounts;
                            for (int i = 0; i < amounts.length; i++) {
                                var amount = amounts[i];
                                var input = tile.inventory.getStackInSlot(INPUT_SLOTS[i]);

                                tile.inventory.setStackInSlot(INPUT_SLOTS[i], StackHelper.shrink(input, amount, true));
                            }

                            tile.inventory.setStackInSlot(OUTPUT_SLOT, StackHelper.combineStacks(output, result));

                            tile.progress = 0;
                            tile.setChangedFast();
                        }
                    } else {
                        tile.progress++;
                        tile.energy.extractEnergy(tile.getFuelUsage(), false);
                        tile.setChangedFast();
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
            level.setBlock(pos, state.setValue(CrafterBlock.RUNNING, tile.isRunning), 3);

            tile.setChangedFast();
        }
    }

    public static BaseItemStackHandler createInventoryHandler() {
        return createInventoryHandler(createRecipeInventoryHandler(), null);
    }

    public static BaseItemStackHandler createInventoryHandler(BaseItemStackHandler recipeInventory, @Nullable OnContentsChangedFunction onContentsChanged) {
        return BaseItemStackHandler.create(11, onContentsChanged, handler -> {
            handler.setCanInsert((slot, stack) -> {
                if (ArrayUtils.contains(INPUT_SLOTS, slot)) {
                    var recipeStack = recipeInventory.getStackInSlot(slot);
                    return !recipeStack.isEmpty() && StackHelper.areStacksEqual(recipeStack, stack);
                }

                return true;
            });

            handler.setOutputSlots(OUTPUT_SLOT);
            handler.setCanExtract(slot ->
                    slot == OUTPUT_SLOT || (slot == FUEL_SLOT && !FurnaceBlockEntity.isFuel(handler.getStackInSlot(slot)))
            );
        });
    }

    public static BaseItemStackHandler createRecipeInventoryHandler() {
        return createRecipeInventoryHandler(null);
    }

    public static BaseItemStackHandler createRecipeInventoryHandler(@Nullable OnContentsChangedFunction onContentsChanged) {
        return BaseItemStackHandler.create(9, onContentsChanged, handler -> {});
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

    @Nullable
    public CraftingRecipe getActiveRecipe() {
        if (this.isGridChanged) {
            this.isGridChanged = false;
            return this.recipe.checkAndGet(this.toCraftingInput(), this.level);
        }

        return this.recipe.get();
    }

    private int getOperationTime() {
        return this.tier == null ? OPERATION_TIME : this.tier.getOperationTime(OPERATION_TIME);
    }

    private int getFuelUsage() {
        return this.tier == null ? FUEL_USAGE : this.tier.getFuelUsage(FUEL_USAGE);
    }

    private CraftingInput toCraftingInput() {
        return this.recipeInventory.toCraftingInput(3, 3);
    }

    private InputResult getInputResult(CraftingRecipe recipe) {
        var amounts = new int[INPUT_SLOTS.length];
        var remaining = new int[INPUT_SLOTS.length];

        for (int i = 0; i < INPUT_SLOTS.length; i++) {
            remaining[i] = this.inventory.getStackInSlot(INPUT_SLOTS[i]).getCount();
        }

        var ingredients = recipe.getIngredients();
        var required = 0;

        for (var ingredient : ingredients) {
            if (ingredient.isEmpty())
                continue;

            required++;

            for (int j = 0; j < INPUT_SLOTS.length; j++) {
                var slot = INPUT_SLOTS[j];
                var stack = this.inventory.getStackInSlot(slot);
                if (remaining[j] > 0 && ingredient.test(stack)) {
                    remaining[j]--;
                    amounts[j]++;
                }
            }
        }

        return new InputResult(Arrays.stream(amounts).sum() == required, amounts);
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

    private record InputResult(boolean hasAll, int[] amounts) { }
}

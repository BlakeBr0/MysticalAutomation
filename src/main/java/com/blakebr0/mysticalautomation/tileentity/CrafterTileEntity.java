package com.blakebr0.mysticalautomation.tileentity;

import com.blakebr0.cucumber.energy.CEnergyStorage;
import com.blakebr0.cucumber.helper.ItemResourceHelper;
import com.blakebr0.cucumber.inventory.CItemStacksHandler;
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
import net.minecraft.network.chat.Component;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
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

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class CrafterTileEntity extends BaseInventoryTileEntity implements MenuProvider, IUpgradeableMachine {
    private static final int[] INPUT_SLOTS = IntStream.rangeClosed(0, 8).toArray();
    private static final int FUEL_SLOT = 9;
    private static final int OUTPUT_SLOT = 10;

    public static final int FUEL_TICK_MULTIPLIER = 20;
    public static final int OPERATION_TIME = 200;
    public static final int FUEL_USAGE = 20;
    public static final int FUEL_CAPACITY = 80000;

    private final CItemStacksHandler inventory;
    private final CItemStacksHandler recipeInventory;
    private final MachineUpgradeItemStackHandler upgradeInventory;
    private final CEnergyStorage energy;
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
        this.recipeInventory = createRecipeInventoryHandler((_, _) -> {
            this.isGridChanged = true;
            this.setChangedFast();
        });
        this.inventory = createInventoryHandler(this.recipeInventory, (_, _) -> this.setChanged(), this::getLevel);
        this.upgradeInventory = new MachineUpgradeItemStackHandler();
        this.energy = new CEnergyStorage(FUEL_CAPACITY, _ -> this.setChangedFast());
        this.sidedInventoryWrappers = SidedInventoryWrapper.create(this.inventory, List.of(Direction.UP, Direction.DOWN, Direction.NORTH), this::canInsertStackSided, null);
        this.recipe = new CachedRecipe<>(RecipeType.CRAFTING);

        this.dataAccess = ContainerDataBuilder.builder()
                .sync(this.energy::getAmountAsInt, this.energy::set)
                .sync(this.energy::getCapacityAsInt, this.energy::setMaxCapacity)
                .sync(() -> this.fuelLeft, value -> this.fuelLeft = value)
                .sync(() -> this.fuelItemValue, value -> this.fuelItemValue = value)
                .sync(() -> this.progress, value -> this.progress = value)
                .sync(this::getOperationTime)
                .build();
    }

    @Override
    public CItemStacksHandler getInventory() {
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
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        this.progress = input.getIntOr("Progress", 0);
        this.fuelLeft = input.getIntOr("FuelLeft", 0);
        this.fuelItemValue = input.getIntOr("FuelItemValue",0);
        this.energy.deserialize(input.childOrEmpty("Energy"));
        this.recipeInventory.deserialize(input.childOrEmpty("RecipeInventory"));
        this.upgradeInventory.deserialize(input.childOrEmpty("UpgradeInventory"));
    }

    @Override
    public void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);

        output.putInt("Progress", this.progress);
        output.putInt("FuelLeft", this.fuelLeft);
        output.putInt("FuelItemValue", this.fuelItemValue);
        output.putChild("Energy", this.energy);
        output.putChild("RecipeInventory", this.recipeInventory);
        output.putChild("UpgradeInventory", this.upgradeInventory);
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        super.preRemoveSideEffects(pos, state);

        if (this.level != null) {
            Containers.dropItemStack(this.level, pos.getX(), pos.getY(), pos.getZ(), this.upgradeInventory.getStackCopy());
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CrafterTileEntity tile) {
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
                var inputs = tile.getInputResult(recipe);
                if (inputs.hasAll) {
                    var inventory = tile.toCraftingInput();
                    var result = recipe.assemble(inventory);

                    if (ItemResourceHelper.canCombine(tile.inventory, OUTPUT_SLOT, result)) {
                        try (var tx = Transaction.openRoot()) {
                            tile.isRunning = true;
                            tile.progress++;

                            tile.energy.extract(tile.getFuelUsage(), tx);

                            if (tile.progress >= tile.getOperationTime()) {
                                int[] amounts = inputs.amounts;
                                for (int i = 0; i < amounts.length; i++) {
                                    var amount = amounts[i];
                                    var input = tile.inventory.getResource(INPUT_SLOTS[i]);

                                    if (tile.inventory.extract(INPUT_SLOTS[i], input, amount, tx, true) == tile.inventory.getAmountAsInt(INPUT_SLOTS[i])) {
                                        var remainder = input.toStack().getCraftingRemainder();
                                        if (remainder != null && input.matches(remainder)) {
                                            tile.inventory.insert(INPUT_SLOTS[i], ItemResource.of(remainder), remainder.count(), tx, true);
                                        }
                                    }
                                }

                                var remaining = recipe.getRemainingItems(inventory);
                                for (int i = 0; i < remaining.size(); i++) {
                                    var remainder = remaining.get(i);
                                    if (remainder.isEmpty())
                                        continue;

                                    var recipeStack = tile.recipeInventory.getResource(i);

                                    for (var slot : INPUT_SLOTS) {
                                        if (!recipeStack.matches(ItemUtil.getStack(tile.recipeInventory, slot)))
                                            continue;

                                        remainder = ItemUtil.insertItemReturnRemaining(tile.inventory, i, ItemUtil.getStack(tile.inventory, slot), false, tx);
                                        if (remainder.isEmpty())
                                            break;
                                    }
                                }

                                tile.inventory.insert(OUTPUT_SLOT, ItemResource.of(result), result.count(), tx, true);

                                tile.progress = 0;
                            }

                            tx.commit();
                        }

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
            remaining[i] = this.inventory.getAmountAsInt(INPUT_SLOTS[i]);
        }

        var required = 0;

        var displays = recipe.display();
        if (!displays.isEmpty() && displays.getFirst() instanceof ShapelessCraftingRecipeDisplay display) {
            for (var ingredient : display.ingredients()) {
                required++;

                for (int j = 0; j < INPUT_SLOTS.length; j++) {
                    var slot = INPUT_SLOTS[j];
                    var stack = ItemUtil.getStack(this.inventory, slot);

                    if (remaining[j] > 0 && ingredient.resolveForStacks(ContextMap.EMPTY).stream().anyMatch(s -> ItemStack.isSameItem(s, stack))) {
                        remaining[j]--;
                        amounts[j]++;
                        break;
                    }
                }
            }
        }

        return new InputResult(Arrays.stream(amounts).sum() == required, amounts);
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

    private record InputResult(boolean hasAll, int[] amounts) { }

    public static CItemStacksHandler createInventoryHandler() {
        return createInventoryHandler(createRecipeInventoryHandler(), null, () -> null);
    }

    public static CItemStacksHandler createInventoryHandler(CItemStacksHandler recipeInventory, @Nullable OnContentsChangedFunction onContentsChanged, Supplier<Level> level) {
        return CItemStacksHandler.create(11, onContentsChanged, handler -> {
            handler.setCanInsert((slot, resource) -> {
                if (ArrayUtils.contains(INPUT_SLOTS, slot)) {
                    var recipeStack = ItemUtil.getStack(recipeInventory, slot);
                    return resource.matches(recipeStack);
                }

                return true;
            });

            handler.setOutputSlots(OUTPUT_SLOT);
            handler.setCanExtract(slot ->
                    slot == OUTPUT_SLOT || (slot == FUEL_SLOT && level.get() == null || level.get() != null && level.get().fuelValues().isFuel(handler.getResource(slot).toStack()))
                            || (ArrayUtils.contains(INPUT_SLOTS, slot) && !recipeInventory.isValid(slot, handler.getResource(slot)))
            );
        });
    }

    public static CItemStacksHandler createRecipeInventoryHandler() {
        return createRecipeInventoryHandler(null);
    }

    public static CItemStacksHandler createRecipeInventoryHandler(@Nullable OnContentsChangedFunction onContentsChanged) {
        return CItemStacksHandler.create(9, onContentsChanged, _ -> {});
    }
}

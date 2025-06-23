package com.blakebr0.mysticalautomation.init;

import com.blakebr0.mysticalautomation.MysticalAutomation;
import com.blakebr0.mysticalautomation.tileentity.AwakeningAltarnatorTileEntity;
import com.blakebr0.mysticalautomation.tileentity.CrafterTileEntity;
import com.blakebr0.mysticalautomation.tileentity.EnchanternatorTileEntity;
import com.blakebr0.mysticalautomation.tileentity.FarmerTileEntity;
import com.blakebr0.mysticalautomation.tileentity.FertilizerTileEntity;
import com.blakebr0.mysticalautomation.tileentity.InfuserTileEntity;
import com.blakebr0.mysticalautomation.tileentity.InfusionAltarnatorTileEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class ModTileEntities {
    public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MysticalAutomation.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<InfuserTileEntity>> INFUSER = register("infuser", InfuserTileEntity::new, () -> new Block[] { ModBlocks.INFUSER.get() });
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CrafterTileEntity>> CRAFTER = register("crafter", CrafterTileEntity::new, () -> new Block[] { ModBlocks.CRAFTER.get() });
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FarmerTileEntity>> FARMER = register("farmer", FarmerTileEntity::new, () -> new Block[] { ModBlocks.FARMER.get() });
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FertilizerTileEntity>> FERTILIZER = register("fertilizer", FertilizerTileEntity::new, () -> new Block[] { ModBlocks.FERTILIZER.get() });
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<InfusionAltarnatorTileEntity>> INFUSION_ALTARNATOR = register("infusion_altarnator", InfusionAltarnatorTileEntity::new, () -> new Block[] { ModBlocks.INFUSION_ALTARNATOR.get() });
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AwakeningAltarnatorTileEntity>> AWAKENING_ALTARNATOR = register("awakening_altarnator", AwakeningAltarnatorTileEntity::new,  () -> new Block[] { ModBlocks.AWAKENING_ALTARNATOR.get() });
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnchanternatorTileEntity>> ENCHANTERNATOR = register("enchanternator", EnchanternatorTileEntity::new, () -> new Block[] { ModBlocks.ENCHANTERNATOR.get() });

    private static <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> register(String name, BlockEntityType.BlockEntitySupplier<T> tile, Supplier<Block[]> blocks) {
        return REGISTRY.register(name, () -> BlockEntityType.Builder.of(tile, blocks.get()).build(null));
    }
}

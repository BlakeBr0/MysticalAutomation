package com.blakebr0.mysticalautomation.init;

import com.blakebr0.mysticalautomation.MysticalAutomation;
import com.blakebr0.mysticalautomation.tilentity.CrafterTileEntity;
import com.blakebr0.mysticalautomation.tilentity.InfuserTileEntity;
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

    private static <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> register(String name, BlockEntityType.BlockEntitySupplier<T> tile, Supplier<Block[]> blocks) {
        return REGISTRY.register(name, () -> BlockEntityType.Builder.of(tile, blocks.get()).build(null));
    }
}

package com.blakebr0.mysticalautomation.init;

import com.blakebr0.cucumber.item.BaseBlockItem;
import com.blakebr0.mysticalautomation.MysticalAutomation;
import com.blakebr0.mysticalautomation.block.InfuserBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public final class ModBlocks {
    public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(Registries.BLOCK, MysticalAutomation.MOD_ID);
    public static final Map<String, Supplier<BlockItem>> BLOCK_ITEMS = new LinkedHashMap<>();

    public static final DeferredHolder<Block, Block> INFUSER = register("infuser", InfuserBlock::new);

    private static DeferredHolder<Block, Block> register(String name, Supplier<Block> block) {
        return register(name, block, b -> () -> new BaseBlockItem(b.get()));
    }

    private static DeferredHolder<Block, Block> register(String name, Supplier<Block> block, Function<DeferredHolder<Block, Block>, Supplier<? extends BlockItem>> item) {
        var reg = REGISTRY.register(name, block);
        BLOCK_ITEMS.put(name, () -> item.apply(reg).get());
        return reg;
    }
}

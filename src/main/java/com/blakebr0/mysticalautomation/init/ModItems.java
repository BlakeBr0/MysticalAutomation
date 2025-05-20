package com.blakebr0.mysticalautomation.init;

import com.blakebr0.mysticalautomation.MysticalAutomation;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(Registries.ITEM, MysticalAutomation.MOD_ID);

    // register block items here for class load order purposes
    static {
        ModBlocks.BLOCK_ITEMS.forEach(REGISTRY::register);
    }
}

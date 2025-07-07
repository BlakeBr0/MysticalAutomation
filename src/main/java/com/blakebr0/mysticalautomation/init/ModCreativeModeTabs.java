package com.blakebr0.mysticalautomation.init;

import com.blakebr0.cucumber.util.FeatureFlagDisplayItemGenerator;
import com.blakebr0.mysticalautomation.MysticalAutomation;
import com.blakebr0.mysticalautomation.lib.ModFeatureFlags;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MysticalAutomation.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CREATIVE_TAB = REGISTRY.register("creative_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.mysticalautomation"))
            .icon(() -> new ItemStack(ModBlocks.INFUSER.get()))
            .displayItems(FeatureFlagDisplayItemGenerator.create((parameters, output) -> {
                output.accept(ModBlocks.INFUSER, ModFeatureFlags.INFUSER_ENABLED);
                output.accept(ModBlocks.CRAFTER, ModFeatureFlags.CRAFTER_ENABLED);
                output.accept(ModBlocks.FARMER, ModFeatureFlags.FARMER_ENABLED);
                output.accept(ModBlocks.FERTILIZER, ModFeatureFlags.FERTILIZER_ENABLED);
                output.accept(ModBlocks.ENCHANTERNATOR, ModFeatureFlags.ENCHANTERNATOR_ENABLED);
                output.accept(ModBlocks.INFUSION_ALTARNATOR, ModFeatureFlags.INFUSION_ALTARNATOR_ENABLED);
                output.accept(ModBlocks.AWAKENING_ALTARNATOR, ModFeatureFlags.AWAKENING_ALTARNATOR_ENABLED);
            }))
            .build());
}

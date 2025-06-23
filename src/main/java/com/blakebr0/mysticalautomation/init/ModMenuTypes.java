package com.blakebr0.mysticalautomation.init;

import com.blakebr0.mysticalautomation.MysticalAutomation;
import com.blakebr0.mysticalautomation.container.AwakeningAltarnatorContainer;
import com.blakebr0.mysticalautomation.container.CrafterContainer;
import com.blakebr0.mysticalautomation.container.EnchanternatorContainer;
import com.blakebr0.mysticalautomation.container.FarmerContainer;
import com.blakebr0.mysticalautomation.container.FertilizerContainer;
import com.blakebr0.mysticalautomation.container.InfuserContainer;
import com.blakebr0.mysticalautomation.container.InfusionAltarnatorContainer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(Registries.MENU, MysticalAutomation.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<InfuserContainer>> INFUSER = REGISTRY.register("infuser", () -> new MenuType<>((IContainerFactory<InfuserContainer>) InfuserContainer::new, FeatureFlagSet.of()));
    public static final DeferredHolder<MenuType<?>, MenuType<CrafterContainer>> CRAFTER = REGISTRY.register("crafter", () -> new MenuType<>((IContainerFactory<CrafterContainer>) CrafterContainer::new, FeatureFlagSet.of()));
    public static final DeferredHolder<MenuType<?>, MenuType<FarmerContainer>> FARMER = REGISTRY.register("farmer", () -> new MenuType<>((IContainerFactory<FarmerContainer>) FarmerContainer::new, FeatureFlagSet.of()));
    public static final DeferredHolder<MenuType<?>, MenuType<FertilizerContainer>> FERTILIZER = REGISTRY.register("fertilizer", () -> new MenuType<>((IContainerFactory<FertilizerContainer>) FertilizerContainer::new, FeatureFlagSet.of()));
    public static final DeferredHolder<MenuType<?>, MenuType<InfusionAltarnatorContainer>> INFUSION_ALTARNATOR = REGISTRY.register("infusion_altarnator", () -> new MenuType<>((IContainerFactory<InfusionAltarnatorContainer>) InfusionAltarnatorContainer::new, FeatureFlagSet.of()));
    public static final DeferredHolder<MenuType<?>, MenuType<AwakeningAltarnatorContainer>> AWAKENING_ALTARNATOR = REGISTRY.register("awakening_altarnator", () -> new MenuType<>((IContainerFactory<AwakeningAltarnatorContainer>) AwakeningAltarnatorContainer::new, FeatureFlagSet.of()));
    public static final DeferredHolder<MenuType<?>, MenuType<EnchanternatorContainer>> ENCHANTERNATOR = REGISTRY.register("enchanternator", () -> new MenuType<>((IContainerFactory<EnchanternatorContainer>) EnchanternatorContainer::new, FeatureFlagSet.of()));
}

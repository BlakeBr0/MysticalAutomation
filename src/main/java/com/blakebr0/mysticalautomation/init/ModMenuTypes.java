package com.blakebr0.mysticalautomation.init;

import com.blakebr0.mysticalautomation.MysticalAutomation;
import com.blakebr0.mysticalautomation.container.CrafterContainer;
import com.blakebr0.mysticalautomation.container.InfuserContainer;
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
}

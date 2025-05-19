package com.blakebr0.mysticalautomation;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(MysticalAutomation.MOD_ID)
public final class MysticalAutomation {
    public static final String MOD_ID = "mysticalautomation";
    public static final String NAME = "Mystical Automation";
    public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

    public MysticalAutomation(IEventBus bus) {
        bus.register(this);
    }

    @SubscribeEvent
    public void onCommonSetup(FMLCommonSetupEvent event) {

    }

    public static ResourceLocation resource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}

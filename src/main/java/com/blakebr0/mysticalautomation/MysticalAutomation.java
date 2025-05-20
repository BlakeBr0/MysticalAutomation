package com.blakebr0.mysticalautomation;

import com.blakebr0.mysticalautomation.client.ModMenuScreens;
import com.blakebr0.mysticalautomation.init.ModBlocks;
import com.blakebr0.mysticalautomation.init.ModCreativeModeTabs;
import com.blakebr0.mysticalautomation.init.ModItems;
import com.blakebr0.mysticalautomation.init.ModMenuTypes;
import com.blakebr0.mysticalautomation.init.ModTileEntities;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(MysticalAutomation.MOD_ID)
public final class MysticalAutomation {
    public static final String MOD_ID = "mysticalautomation";
    public static final String NAME = "Mystical Automation";
    public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

    public MysticalAutomation(IEventBus bus) {
        bus.register(this);

        ModBlocks.REGISTRY.register(bus);
        ModCreativeModeTabs.REGISTRY.register(bus);
        ModItems.REGISTRY.register(bus);
        ModMenuTypes.REGISTRY.register(bus);
        ModTileEntities.REGISTRY.register(bus);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            bus.register(new ModMenuScreens());
        }
    }

    @SubscribeEvent
    public void onCommonSetup(FMLCommonSetupEvent event) {

    }

    public static ResourceLocation resource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}

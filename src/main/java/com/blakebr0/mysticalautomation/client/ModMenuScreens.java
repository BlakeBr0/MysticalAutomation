package com.blakebr0.mysticalautomation.client;

import com.blakebr0.mysticalautomation.client.screen.AwakeningAltarnatorScreen;
import com.blakebr0.mysticalautomation.client.screen.CrafterScreen;
import com.blakebr0.mysticalautomation.client.screen.EnchanternatorScreen;
import com.blakebr0.mysticalautomation.client.screen.FarmerScreen;
import com.blakebr0.mysticalautomation.client.screen.FertilizerScreen;
import com.blakebr0.mysticalautomation.client.screen.InfuserScreen;
import com.blakebr0.mysticalautomation.client.screen.InfusionAltarnatorScreen;
import com.blakebr0.mysticalautomation.init.ModMenuTypes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public final class ModMenuScreens {
    @SubscribeEvent
    public void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.INFUSER.get(), InfuserScreen::new);
        event.register(ModMenuTypes.CRAFTER.get(), CrafterScreen::new);
        event.register(ModMenuTypes.FARMER.get(), FarmerScreen::new);
        event.register(ModMenuTypes.FERTILIZER.get(), FertilizerScreen::new);
        event.register(ModMenuTypes.ENCHANTERNATOR.get(), EnchanternatorScreen::new);
        event.register(ModMenuTypes.INFUSION_ALTARNATOR.get(), InfusionAltarnatorScreen::new);
        event.register(ModMenuTypes.AWAKENING_ALTARNATOR.get(), AwakeningAltarnatorScreen::new);
    }
}

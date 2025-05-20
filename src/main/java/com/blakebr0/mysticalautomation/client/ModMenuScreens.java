package com.blakebr0.mysticalautomation.client;

import com.blakebr0.mysticalautomation.client.screen.InfuserScreen;
import com.blakebr0.mysticalautomation.init.ModMenuTypes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public final class ModMenuScreens {
    @SubscribeEvent
    public void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.INFUSER.get(), InfuserScreen::new);
    }
}

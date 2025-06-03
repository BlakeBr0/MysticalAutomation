package com.blakebr0.mysticalautomation.network;

import com.blakebr0.mysticalautomation.network.payload.InfuserSelectIndexPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class NetworkHandler {
    @SubscribeEvent
    public void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("1");

        registrar.playToServer(InfuserSelectIndexPayload.TYPE, InfuserSelectIndexPayload.STREAM_CODEC, InfuserSelectIndexPayload::handleServer);
    }
}
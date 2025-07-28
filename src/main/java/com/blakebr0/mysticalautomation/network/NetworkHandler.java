package com.blakebr0.mysticalautomation.network;

import com.blakebr0.mysticalautomation.network.payload.EnchanternatorSelectLevelPayload;
import com.blakebr0.mysticalautomation.network.payload.InfuserSelectIndexPayload;
import com.blakebr0.mysticalautomation.network.payload.ReloadIngredientCachePayload;
import com.blakebr0.mysticalautomation.network.payload.SetFakeRecipeSlotPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class NetworkHandler {
    @SubscribeEvent
    public void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("1");

        registrar.playToClient(ReloadIngredientCachePayload.TYPE, ReloadIngredientCachePayload.STREAM_CODEC, ReloadIngredientCachePayload::handleClient);
        registrar.playToServer(InfuserSelectIndexPayload.TYPE, InfuserSelectIndexPayload.STREAM_CODEC, InfuserSelectIndexPayload::handleServer);
        registrar.playToServer(EnchanternatorSelectLevelPayload.TYPE, EnchanternatorSelectLevelPayload.STREAM_CODEC, EnchanternatorSelectLevelPayload::handleServer);
        registrar.playToServer(SetFakeRecipeSlotPayload.TYPE, SetFakeRecipeSlotPayload.STREAM_CODEC, SetFakeRecipeSlotPayload::handleServer);
    }
}
package com.blakebr0.mysticalautomation.handler;

import com.blakebr0.mysticalautomation.init.ModTileEntities;
import com.blakebr0.mysticalautomation.tilentity.InfuserTileEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public final class RegisterCapabilityHandler {
    @SubscribeEvent
    public void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModTileEntities.INFUSER.get(), InfuserTileEntity::getSidedInventory);
    }
}

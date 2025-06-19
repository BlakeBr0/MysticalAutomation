package com.blakebr0.mysticalautomation.handler;

import com.blakebr0.mysticalautomation.init.ModTileEntities;
import com.blakebr0.mysticalautomation.tilentity.CrafterTileEntity;
import com.blakebr0.mysticalautomation.tilentity.InfuserTileEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public final class RegisterCapabilityHandler {
    @SubscribeEvent
    public void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModTileEntities.INFUSER.get(), InfuserTileEntity::getSidedInventory);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModTileEntities.CRAFTER.get(), CrafterTileEntity::getSidedInventory);

        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModTileEntities.INFUSER.get(), (block, direction) -> block.getEnergy());
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModTileEntities.CRAFTER.get(), (block, direction) -> block.getEnergy());
    }
}

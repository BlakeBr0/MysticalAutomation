package com.blakebr0.mysticalautomation.handler;

import com.blakebr0.mysticalautomation.init.ModTileEntities;
import com.blakebr0.mysticalautomation.tileentity.AwakeningAltarnatorTileEntity;
import com.blakebr0.mysticalautomation.tileentity.CrafterTileEntity;
import com.blakebr0.mysticalautomation.tileentity.EnchanternatorTileEntity;
import com.blakebr0.mysticalautomation.tileentity.FarmerTileEntity;
import com.blakebr0.mysticalautomation.tileentity.FertilizerTileEntity;
import com.blakebr0.mysticalautomation.tileentity.InfuserTileEntity;
import com.blakebr0.mysticalautomation.tileentity.InfusionAltarnatorTileEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public final class RegisterCapabilityHandler {
    @SubscribeEvent
    public void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModTileEntities.INFUSER.get(), InfuserTileEntity::getSidedInventory);
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModTileEntities.CRAFTER.get(), CrafterTileEntity::getSidedInventory);
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModTileEntities.FARMER.get(), FarmerTileEntity::getSidedInventory);
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModTileEntities.FERTILIZER.get(), FertilizerTileEntity::getSidedInventory);
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModTileEntities.INFUSION_ALTARNATOR.get(), InfusionAltarnatorTileEntity::getSidedInventory);
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModTileEntities.AWAKENING_ALTARNATOR.get(), AwakeningAltarnatorTileEntity::getSidedInventory);
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModTileEntities.ENCHANTERNATOR.get(), EnchanternatorTileEntity::getSidedInventory);

        event.registerBlockEntity(Capabilities.Energy.BLOCK, ModTileEntities.INFUSER.get(), (block, _) -> block.getEnergy());
        event.registerBlockEntity(Capabilities.Energy.BLOCK, ModTileEntities.CRAFTER.get(), (block, _) -> block.getEnergy());
        event.registerBlockEntity(Capabilities.Energy.BLOCK, ModTileEntities.FARMER.get(), (block, _) -> block.getEnergy());
        event.registerBlockEntity(Capabilities.Energy.BLOCK, ModTileEntities.FERTILIZER.get(), (block, _) -> block.getEnergy());
        event.registerBlockEntity(Capabilities.Energy.BLOCK, ModTileEntities.INFUSION_ALTARNATOR.get(), (block, _) -> block.getEnergy());
        event.registerBlockEntity(Capabilities.Energy.BLOCK, ModTileEntities.AWAKENING_ALTARNATOR.get(), (block, _) -> block.getEnergy());
        event.registerBlockEntity(Capabilities.Energy.BLOCK, ModTileEntities.ENCHANTERNATOR.get(), (block, _) -> block.getEnergy());
    }
}

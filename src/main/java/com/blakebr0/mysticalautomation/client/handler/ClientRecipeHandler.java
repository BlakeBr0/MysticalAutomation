package com.blakebr0.mysticalautomation.client.handler;

import com.blakebr0.mysticalautomation.api.crafting.IFarmerRecipe;
import com.blakebr0.mysticalautomation.init.ModRecipeTypes;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public final class ClientRecipeHandler {
    public static final List<RecipeHolder<IFarmerRecipe>> FARMER_RECIPES = new ArrayList<>();

    @SubscribeEvent
    public void onRecipesReceived(RecipesReceivedEvent event) {
        var recipes = event.getRecipeMap();

        FARMER_RECIPES.addAll(recipes.byType(ModRecipeTypes.FARMER.get()));
    }

    @SubscribeEvent
    public void onClientPlayerLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        FARMER_RECIPES.clear();
    }
}

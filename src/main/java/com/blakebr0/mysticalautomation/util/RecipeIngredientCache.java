package com.blakebr0.mysticalautomation.util;

import com.blakebr0.cucumber.event.RecipeManagerLoadedEvent;
import com.blakebr0.mysticalautomation.MysticalAutomation;
import com.blakebr0.mysticalautomation.init.ModRecipeTypes;
import com.blakebr0.mysticalautomation.network.payload.ReloadIngredientCachePayload;
import com.google.common.base.Stopwatch;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class RecipeIngredientCache {
    public static final RecipeIngredientCache INSTANCE = new RecipeIngredientCache();

    private final Map<Key, Map<Item, List<Ingredient>>> caches;

    private RecipeIngredientCache() {
        this.caches = new HashMap<>();
    }

    @SubscribeEvent
    public void onDatapackSyncEvent(OnDatapackSyncEvent event) {
        var payload = new ReloadIngredientCachePayload(this.caches);
        var player = event.getPlayer();

        // send the new caches to the client
        if (player != null) {
            PacketDistributor.sendToPlayer(player, payload);
        } else {
            PacketDistributor.sendToAllPlayers(payload);
        }
    }

    @SubscribeEvent
    public void onRecipeManagerLoaded(RecipeManagerLoadedEvent event) {
        var stopwatch = Stopwatch.createStarted();
        var manager = event.getRecipeManager();

        this.caches.clear();
        this.cacheFarmerRecipes(manager);

        MysticalAutomation.LOGGER.info("Recipe ingredient caching done in {} ms", stopwatch.stop().elapsed(TimeUnit.MILLISECONDS));
    }

    // called on the client by ReloadIngredientCacheMessage
    public void setCaches(Map<Key, Map<Item, List<Ingredient>>> caches) {
        this.caches.clear();
        this.caches.putAll(caches);
    }

    public boolean isValidInput(Key key, ItemStack stack) {
        var cache = this.caches.getOrDefault(key, Collections.emptyMap()).get(stack.getItem());
        return cache != null && cache.stream().anyMatch(i -> i.test(stack));
    }

    private void cacheFarmerRecipes(RecipeManager manager) {
        this.caches.put(Key.FARMER_SEEDS, new HashMap<>());
        this.caches.put(Key.FARMER_SOIL, new HashMap<>());
        this.caches.put(Key.FARMER_CRUX, new HashMap<>());

        var recipes = manager.recipeMap().byType(ModRecipeTypes.FARMER.get());
        for (var recipe : recipes) {
            var seeds = recipe.value().getSeedsIngredient();
            {
                var items = new HashSet<>();
                for (var item : seeds.items().toList()) {
                    if (items.contains(item.value()))
                        continue;

                    items.add(item);
                    this.caches.get(Key.FARMER_SEEDS).computeIfAbsent(item.value(), _ -> new ArrayList<>()).add(seeds);
                }
            }

            var soil = recipe.value().getSoilIngredient();
            {
                var items = new HashSet<>();
                for (var item : soil.items().toList()) {
                    if (items.contains(item.value()))
                        continue;

                    items.add(item);
                    this.caches.get(Key.FARMER_SOIL).computeIfAbsent(item.value(), _ -> new ArrayList<>()).add(seeds);
                }
            }

            var crux = recipe.value().getCruxIngredient();
            if (crux.isPresent()) {
                var items = new HashSet<>();
                for (var item : crux.get().items().toList()) {
                    if (items.contains(item.value()))
                        continue;

                    items.add(item);
                    this.caches.get(Key.FARMER_CRUX).computeIfAbsent(item.value(), _ -> new ArrayList<>()).add(seeds);
                }
            }
        }
    }

    public enum Key implements StringRepresentable {
        FARMER_SEEDS("farmer_seeds"),
        FARMER_SOIL("farmer_soil"),
        FARMER_CRUX("farmer_crux");

        public final String name;

        Key(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}

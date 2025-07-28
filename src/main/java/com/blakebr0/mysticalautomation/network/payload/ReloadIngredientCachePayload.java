package com.blakebr0.mysticalautomation.network.payload;

import com.blakebr0.mysticalautomation.MysticalAutomation;
import com.blakebr0.mysticalautomation.util.RecipeIngredientCache;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record ReloadIngredientCachePayload(Map<RecipeIngredientCache.Key, Map<Item, List<Ingredient>>> caches) implements CustomPacketPayload {
    public static final Type<ReloadIngredientCachePayload> TYPE = new Type<>(MysticalAutomation.resource("reload_ingredient_cache"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ReloadIngredientCachePayload> STREAM_CODEC = StreamCodec.of(
            ReloadIngredientCachePayload::toNetwork, ReloadIngredientCachePayload::fromNetwork
    );

    private static ReloadIngredientCachePayload fromNetwork(RegistryFriendlyByteBuf buffer) {
        var caches = new HashMap<RecipeIngredientCache.Key, Map<Item, List<Ingredient>>>();
        var types = buffer.readVarInt();

        for (var i = 0; i < types; i++) {
            var key = buffer.readEnum(RecipeIngredientCache.Key.class);
            var items = buffer.readVarInt();

            caches.put(key, new HashMap<>());

            for (var j = 0; j < items; j++) {
                var item = BuiltInRegistries.ITEM.get(buffer.readResourceLocation());
                var ingredients = buffer.readVarInt();

                for (var k = 0; k < ingredients; k++) {
                    var cache = caches.get(key).computeIfAbsent(item, l -> new ArrayList<>());
                    var ingredient = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);

                    cache.add(ingredient);
                }
            }
        }

        return new ReloadIngredientCachePayload(caches);
    }

    private static void toNetwork(RegistryFriendlyByteBuf buffer, ReloadIngredientCachePayload payload) {
        buffer.writeVarInt(payload.caches.size());

        for (var entry : payload.caches.entrySet()) {
            var key = entry.getKey();
            var caches = entry.getValue();

            buffer.writeEnum(key);
            buffer.writeVarInt(caches.size());

            for (var cache : caches.entrySet()) {
                var item = BuiltInRegistries.ITEM.getKey(cache.getKey());
                var ingredients = cache.getValue();

                buffer.writeResourceLocation(item);
                buffer.writeVarInt(ingredients.size());

                for (var ingredient : ingredients) {
                    Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
                }
            }
        }
    }

    @Override
    public Type<ReloadIngredientCachePayload> type() {
        return TYPE;
    }

    public static void handleClient(ReloadIngredientCachePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            RecipeIngredientCache.INSTANCE.setCaches(payload.caches);
        });
    }
}

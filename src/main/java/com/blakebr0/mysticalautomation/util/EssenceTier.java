package com.blakebr0.mysticalautomation.util;

import com.blakebr0.mysticalautomation.compat.MysticalCompat;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

public enum EssenceTier {
    INFERIUM(MysticalCompat.Items.INFERIUM_ESSENCE),
    PRUDENTIUM(MysticalCompat.Items.PRUDENTIUM_ESSENCE),
    TERTIUM(MysticalCompat.Items.TERTIUM_ESSENCE),
    IMPERIUM(MysticalCompat.Items.IMPERIUM_ESSENCE),
    SUPREMIUM(MysticalCompat.Items.SUPREMIUM_ESSENCE),
    INSANIUM(MysticalCompat.Items.INSANIUM_ESSENCE);

    private static final EssenceTier[] VALUES = values();

    private final DeferredHolder<Item, Item> item;

    EssenceTier(DeferredHolder<Item, Item> item) {
        this.item = item;
    }

    @Nullable
    public Item getItem() {
        return this.item.isBound() ? this.item.value() : null;
    }

    @Nullable
    public EssenceTier getNextTier() {
        return VALUES[(this.ordinal() + 1) % VALUES.length];
    }

    @Nullable
    public static EssenceTier fromIndex(int index) {
        if (index > VALUES.length - 1 || index < 0)
            return null;
        return VALUES[index];
    }
}

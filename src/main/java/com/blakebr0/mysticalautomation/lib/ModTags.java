package com.blakebr0.mysticalautomation.lib;

import com.blakebr0.mysticalautomation.MysticalAutomation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class ModTags {
    public static final class Items {
        public static final TagKey<Item> FERTILIZERS = ItemTags.create(MysticalAutomation.resource("fertilizers"));
    }
}

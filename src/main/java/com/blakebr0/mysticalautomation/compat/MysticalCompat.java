package com.blakebr0.mysticalautomation.compat;

import com.blakebr0.cucumber.util.Tooltip;
import com.blakebr0.mysticalagriculture.api.crafting.IAwakeningRecipe;
import com.blakebr0.mysticalagriculture.api.crafting.IEnchanterRecipe;
import com.blakebr0.mysticalagriculture.api.crafting.IInfusionRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class MysticalCompat {
    public static final class Items {
        public static final DeferredHolder<Item, Item> INFERIUM_ESSENCE = DeferredHolder.create(Registries.ITEM.location(), ResourceLocation.parse("mysticalagriculture:inferium_essence"));
        public static final DeferredHolder<Item, Item> PRUDENTIUM_ESSENCE = DeferredHolder.create(Registries.ITEM.location(), ResourceLocation.parse("mysticalagriculture:prudentium_essence"));
        public static final DeferredHolder<Item, Item> TERTIUM_ESSENCE = DeferredHolder.create(Registries.ITEM.location(), ResourceLocation.parse("mysticalagriculture:tertium_essence"));
        public static final DeferredHolder<Item, Item> IMPERIUM_ESSENCE = DeferredHolder.create(Registries.ITEM.location(), ResourceLocation.parse("mysticalagriculture:imperium_essence"));
        public static final DeferredHolder<Item, Item> SUPREMIUM_ESSENCE = DeferredHolder.create(Registries.ITEM.location(), ResourceLocation.parse("mysticalagriculture:supremium_essence"));

        public static final DeferredHolder<Item, Item> INFUSION_CRYSTAL = DeferredHolder.create(Registries.ITEM.location(), ResourceLocation.parse("mysticalagriculture:infusion_crystal"));
        public static final DeferredHolder<Item, Item> MASTER_INFUSION_CRYSTAL = DeferredHolder.create(Registries.ITEM.location(), ResourceLocation.parse("mysticalagriculture:master_infusion_crystal"));

        public static final DeferredHolder<Item, Item> INSANIUM_ESSENCE = DeferredHolder.create(Registries.ITEM.location(), ResourceLocation.parse("mysticalagradditions:insanium_essence"));
    }

    public static final class RecipeTypes {
        public static final DeferredHolder<RecipeType<?>, RecipeType<IInfusionRecipe>> INFUSION = DeferredHolder.create(Registries.RECIPE_TYPE.location(), ResourceLocation.parse("mysticalagriculture:infusion"));
        public static final DeferredHolder<RecipeType<?>, RecipeType<IAwakeningRecipe>> AWAKENING = DeferredHolder.create(Registries.RECIPE_TYPE.location(), ResourceLocation.parse("mysticalagriculture:awakening"));
        public static final DeferredHolder<RecipeType<?>, RecipeType<IEnchanterRecipe>> ENCHANTER = DeferredHolder.create(Registries.RECIPE_TYPE.location(), ResourceLocation.parse("mysticalagriculture:enchanter"));
    }

    public static final class Tooltips {
        public static final Tooltip MACHINE_AREA = new Tooltip("tooltip.mysticalagriculture.machine_area");
        public static final Tooltip MACHINE_SPEED = new Tooltip("tooltip.mysticalagriculture.machine_speed");
        public static final Tooltip MACHINE_FUEL_RATE = new Tooltip("tooltip.mysticalagriculture.machine_fuel_rate");
        public static final Tooltip MACHINE_FUEL_CAPACITY = new Tooltip("tooltip.mysticalagriculture.machine_fuel_capacity");
    }

    public static boolean isEssence(ItemStack stack) {
        return stack.is(Items.INFERIUM_ESSENCE)
                || stack.is(Items.PRUDENTIUM_ESSENCE)
                || stack.is(Items.TERTIUM_ESSENCE)
                || stack.is(Items.IMPERIUM_ESSENCE)
                || stack.is(Items.SUPREMIUM_ESSENCE)
                || (Items.INSANIUM_ESSENCE.isBound() && stack.is(Items.INSANIUM_ESSENCE));
    }

    public static boolean isInfusionCrystal(ItemStack stack) {
        return stack.is(Items.INFUSION_CRYSTAL) || stack.is(Items.MASTER_INFUSION_CRYSTAL);
    }
}

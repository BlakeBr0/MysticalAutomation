package com.blakebr0.mysticalautomation.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class ModConfigs {
    public static final ModConfigSpec COMMON;

    public static final ModConfigSpec.BooleanValue INFUSER_ENABLED;
    public static final ModConfigSpec.BooleanValue CRAFTER_ENABLED;
    public static final ModConfigSpec.BooleanValue FARMER_ENABLED;
    public static final ModConfigSpec.BooleanValue FERTILIZER_ENABLED;
    public static final ModConfigSpec.BooleanValue ENCHANTERNATOR_ENABLED;
    public static final ModConfigSpec.BooleanValue INFUSION_ALTARNATOR_ENABLED;
    public static final ModConfigSpec.BooleanValue AWAKENING_ALTARNATOR_ENABLED;

    // common
    static {
        final var common = new ModConfigSpec.Builder();

        common.comment("Machine configuration options").push("Machines");

        {
            common.push("Infuser");
            INFUSER_ENABLED = common
                    .comment("Should the Infuser be enabled?")
                    .define("enabled", true);
            common.pop();

            common.push("Crafter");
            CRAFTER_ENABLED = common
                    .comment("Should the Crafter be enabled?")
                    .define("enabled", true);
            common.pop();

            common.push("Farmer");
            FARMER_ENABLED = common
                    .comment("Should the Farmer be enabled?")
                    .define("enabled", true);
            common.pop();

            common.push("Fertilizer");
            FERTILIZER_ENABLED = common
                    .comment("Should the Fertilizer be enabled?")
                    .define("enabled", true);
            common.pop();

            common.push("Enchanternator");
            ENCHANTERNATOR_ENABLED = common
                    .comment("Should the Enchanternator be enabled?")
                    .define("enabled", true);
            common.pop();

            common.push("Infusion Altarnator");
            INFUSION_ALTARNATOR_ENABLED = common
                    .comment("Should the Infusion Altarnator be enabled?")
                    .define("enabled", true);
            common.pop();

            common.push("Awakening Altarnator");
            AWAKENING_ALTARNATOR_ENABLED = common
                    .comment("Should the Awakening Altar be enabled?")
                    .define("enabled", true);
            common.pop();
        }

        common.pop();

        COMMON = common.build();
    }
}

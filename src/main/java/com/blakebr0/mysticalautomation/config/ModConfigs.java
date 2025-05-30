package com.blakebr0.mysticalautomation.config;

import net.neoforged.fml.ModList;

public final class ModConfigs {
    public static boolean isMysticalAgradditionsInstalled() {
        return ModList.get().isLoaded("mysticalagradditions");
    }
}

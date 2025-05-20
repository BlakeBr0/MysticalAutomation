package com.blakebr0.mysticalautomation.compat;

import com.blakebr0.mysticalautomation.MysticalAutomation;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public final class JeiCompat implements IModPlugin {
    public static final ResourceLocation UID = MysticalAutomation.resource("jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }
}

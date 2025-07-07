package com.blakebr0.mysticalautomation.lib;

import com.blakebr0.cucumber.util.FeatureFlag;
import com.blakebr0.cucumber.util.FeatureFlags;
import com.blakebr0.mysticalautomation.MysticalAutomation;
import com.blakebr0.mysticalautomation.config.ModConfigs;

@FeatureFlags
public final class ModFeatureFlags {
    public static final FeatureFlag INFUSER_ENABLED = FeatureFlag.create(MysticalAutomation.resource("infuser_enabled"), ModConfigs.INFUSER_ENABLED);
    public static final FeatureFlag CRAFTER_ENABLED = FeatureFlag.create(MysticalAutomation.resource("crafter_enabled"), ModConfigs.CRAFTER_ENABLED);
    public static final FeatureFlag FARMER_ENABLED = FeatureFlag.create(MysticalAutomation.resource("farmer_enabled"), ModConfigs.FARMER_ENABLED);
    public static final FeatureFlag FERTILIZER_ENABLED = FeatureFlag.create(MysticalAutomation.resource("fertilizer_enabled"), ModConfigs.FERTILIZER_ENABLED);
    public static final FeatureFlag ENCHANTERNATOR_ENABLED = FeatureFlag.create(MysticalAutomation.resource("enchanternator_enabled"), ModConfigs.ENCHANTERNATOR_ENABLED);
    public static final FeatureFlag INFUSION_ALTARNATOR_ENABLED = FeatureFlag.create(MysticalAutomation.resource("infusion_altarnator_enabled"), ModConfigs.INFUSION_ALTARNATOR_ENABLED);
    public static final FeatureFlag AWAKENING_ALTARNATOR_ENABLED = FeatureFlag.create(MysticalAutomation.resource("awakening_altarnator_enabled"), ModConfigs.AWAKENING_ALTARNATOR_ENABLED);
}

package com.blakebr0.mysticalautomation.lib;

import com.blakebr0.cucumber.util.FeatureFlag;
import com.blakebr0.cucumber.util.FeatureFlags;
import com.blakebr0.mysticalautomation.MysticalAutomation;
import com.blakebr0.mysticalautomation.config.ModConfigs;

@FeatureFlags
public final class ModFeatureFlags {
    public static final FeatureFlag INFUSER_ENABLED = FeatureFlag.create(MysticalAutomation.resource("infuser"), ModConfigs.INFUSER_ENABLED);
    public static final FeatureFlag CRAFTER_ENABLED = FeatureFlag.create(MysticalAutomation.resource("crafter"), ModConfigs.CRAFTER_ENABLED);
    public static final FeatureFlag FARMER_ENABLED = FeatureFlag.create(MysticalAutomation.resource("farmer"), ModConfigs.FARMER_ENABLED);
    public static final FeatureFlag FARMER_DYNAMIC_MYSTICAL_AGRICULTURE_RECIPES = FeatureFlag.create(MysticalAutomation.resource("dynamic_mystical_agriculture_recipes"), ModConfigs.FARMER_DYNAMIC_MYSTICAL_AGRICULTURE_RECIPES);
    public static final FeatureFlag FERTILIZER_ENABLED = FeatureFlag.create(MysticalAutomation.resource("fertilizer"), ModConfigs.FERTILIZER_ENABLED);
    public static final FeatureFlag ENCHANTERNATOR_ENABLED = FeatureFlag.create(MysticalAutomation.resource("enchanternator"), ModConfigs.ENCHANTERNATOR_ENABLED);
    public static final FeatureFlag INFUSION_ALTARNATOR_ENABLED = FeatureFlag.create(MysticalAutomation.resource("infusion_altarnator"), ModConfigs.INFUSION_ALTARNATOR_ENABLED);
    public static final FeatureFlag AWAKENING_ALTARNATOR_ENABLED = FeatureFlag.create(MysticalAutomation.resource("awakening_altarnator"), ModConfigs.AWAKENING_ALTARNATOR_ENABLED);
}

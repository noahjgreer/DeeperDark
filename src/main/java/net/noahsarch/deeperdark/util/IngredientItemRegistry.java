package net.noahsarch.deeperdark.util;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class IngredientItemRegistry {

    public static Set<Item> ingredientItems = Collections.emptySet();

    public static void buildIngredientSet(HolderLookup.Provider registryAccess) {
        CreativeModeTab tab = BuiltInRegistries.CREATIVE_MODE_TAB.getValue(CreativeModeTabs.INGREDIENTS);
        if (tab == null) return;
        try {
            CreativeModeTab.ItemDisplayParameters params = new CreativeModeTab.ItemDisplayParameters(
                    FeatureFlags.REGISTRY.allFlags(), true, registryAccess);
            tab.buildContents(params);
            Set<Item> items = new HashSet<>();
            for (ItemStack stack : tab.getDisplayItems()) {
                items.add(stack.getItem());
            }
            ingredientItems = Collections.unmodifiableSet(items);
        } catch (Exception e) {
            net.noahsarch.deeperdark.Deeperdark.LOGGER.warn(
                    "[Deeper Dark] Could not build ingredient tab contents: {}", e.getMessage());
        }
    }
}

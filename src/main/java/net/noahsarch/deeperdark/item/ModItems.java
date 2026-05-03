package net.noahsarch.deeperdark.item;

import java.util.function.Function;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.noahsarch.deeperdark.Deeperdark;

public class ModItems {
    public static <T extends Item> T register(String name, Function<Item.Properties, T> itemFactory, Item.Properties settings) {
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Deeperdark.MOD_ID, name));

        T item = itemFactory.apply(settings.setId(itemKey));
        Registry.register(BuiltInRegistries.ITEM, itemKey, item);
        return item;
    }

    public static void initialize() {
        Deeperdark.LOGGER.info("Registering ModItems for deeperdark");

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
            .register(creativeTab -> creativeTab.accept(ModItems.LEATHER_SCRAP));

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)
            .register(creativeTab -> {
                creativeTab.accept(COPPER_ITEM_MAGNET);
                creativeTab.accept(IRON_ITEM_MAGNET);
                creativeTab.accept(GOLDEN_ITEM_MAGNET);
                creativeTab.accept(DIAMOND_ITEM_MAGNET);
                creativeTab.accept(NETHERITE_ITEM_MAGNET);
            });
    }

    public static final Item LEATHER_SCRAP = register(
        "leather_scrap",
        Item::new,
        new Item.Properties());

    public static final ItemMagnetItem COPPER_ITEM_MAGNET = register(
        "copper_item_magnet",
        props -> new ItemMagnetItem(ItemMagnetItem.MagnetType.COPPER, props),
        new Item.Properties());

    public static final ItemMagnetItem IRON_ITEM_MAGNET = register(
        "iron_item_magnet",
        props -> new ItemMagnetItem(ItemMagnetItem.MagnetType.IRON, props),
        new Item.Properties());

    public static final ItemMagnetItem GOLDEN_ITEM_MAGNET = register(
        "golden_item_magnet",
        props -> new ItemMagnetItem(ItemMagnetItem.MagnetType.GOLDEN, props),
        new Item.Properties());

    public static final ItemMagnetItem DIAMOND_ITEM_MAGNET = register(
        "diamond_item_magnet",
        props -> new ItemMagnetItem(ItemMagnetItem.MagnetType.DIAMOND, props),
        new Item.Properties());

    public static final ItemMagnetItem NETHERITE_ITEM_MAGNET = register(
        "netherite_item_magnet",
        props -> new ItemMagnetItem(ItemMagnetItem.MagnetType.NETHERITE, props),
        new Item.Properties());
}

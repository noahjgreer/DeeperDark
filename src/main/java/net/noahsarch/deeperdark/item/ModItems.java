package net.noahsarch.deeperdark.item;

import java.util.List;
import java.util.function.Function;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.consume_effects.ClearAllStatusEffectsConsumeEffect;
import net.noahsarch.deeperdark.Deeperdark;

public class ModItems {

    // Six false flags for trinket layer visibility: sponge, gold, bell, magnet, blaze_rod, glow_berries
    private static final CustomModelData COLLAR_DEFAULT_CMD = new CustomModelData(
        List.of(), List.of(false, false, false, false, false, false), List.of(), List.of());

    public static final CollarItem LEATHER_COLLAR = register(
        "leather_collar",
        props -> new CollarItem(net.noahsarch.deeperdark.item.CollarTier.LEATHER, props),
        new Item.Properties().stacksTo(1).component(DataComponents.CUSTOM_MODEL_DATA, COLLAR_DEFAULT_CMD));

    public static final CollarItem COPPER_COLLAR = register(
        "copper_collar",
        props -> new CollarItem(net.noahsarch.deeperdark.item.CollarTier.COPPER, props),
        new Item.Properties().stacksTo(1).component(DataComponents.CUSTOM_MODEL_DATA, COLLAR_DEFAULT_CMD));

    public static final CollarItem IRON_COLLAR = register(
        "iron_collar",
        props -> new CollarItem(net.noahsarch.deeperdark.item.CollarTier.IRON, props),
        new Item.Properties().stacksTo(1).component(DataComponents.CUSTOM_MODEL_DATA, COLLAR_DEFAULT_CMD));

    public static final CollarItem DIAMOND_COLLAR = register(
        "diamond_collar",
        props -> new CollarItem(net.noahsarch.deeperdark.item.CollarTier.DIAMOND, props),
        new Item.Properties().stacksTo(1).component(DataComponents.CUSTOM_MODEL_DATA, COLLAR_DEFAULT_CMD));

    public static final CollarItem NETHERITE_COLLAR = register(
        "netherite_collar",
        props -> new CollarItem(net.noahsarch.deeperdark.item.CollarTier.NETHERITE, props),
        new Item.Properties().stacksTo(1).fireResistant().component(DataComponents.CUSTOM_MODEL_DATA, COLLAR_DEFAULT_CMD));
    public static <T extends Item> T register(String name, Function<Item.Properties, T> itemFactory, Item.Properties settings) {
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Deeperdark.MOD_ID, name));

        T item = itemFactory.apply(settings.setId(itemKey));
        Registry.register(BuiltInRegistries.ITEM, itemKey, item);
        return item;
    }

    public static void initialize() {
        Deeperdark.LOGGER.info("Registering ModItems for deeperdark");

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
            .register(creativeTab -> {
                creativeTab.accept(ModItems.LEATHER_SCRAP);
                creativeTab.accept(ModItems.GUNPOWDER_DUST);
            });

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)
            .register(creativeTab -> {
                creativeTab.accept(COPPER_ITEM_MAGNET);
                creativeTab.accept(IRON_ITEM_MAGNET);
                creativeTab.accept(GOLDEN_ITEM_MAGNET);
                creativeTab.accept(DIAMOND_ITEM_MAGNET);
                creativeTab.accept(NETHERITE_ITEM_MAGNET);
            });

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
            .register(creativeTab -> {
                creativeTab.accept(LEATHER_COLLAR);
                creativeTab.accept(COPPER_COLLAR);
                creativeTab.accept(IRON_COLLAR);
                creativeTab.accept(DIAMOND_COLLAR);
                creativeTab.accept(NETHERITE_COLLAR);
            });

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FOOD_AND_DRINKS)
            .register(creativeTab -> creativeTab.accept(MILK_BOTTLE));

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
            .register(creativeTab -> creativeTab.accept(SPLASH_MILK_BOTTLE));
    }

    public static final Item LEATHER_SCRAP = register(
        "leather_scrap",
        Item::new,
        new Item.Properties());

    public static final Item GUNPOWDER_DUST = register(
        "gunpowder_dust",
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
        new Item.Properties().fireResistant());

    public static final Item MILK_BOTTLE = register(
        "milk_bottle",
        Item::new,
        new Item.Properties()
            .stacksTo(16)
            .usingConvertsTo(Items.GLASS_BOTTLE)
            .component(DataComponents.CONSUMABLE,
                Consumable.builder()
                    .consumeSeconds(1.6F)
                    .onConsume(ClearAllStatusEffectsConsumeEffect.INSTANCE)
                    .build()));

    public static final Item SPLASH_MILK_BOTTLE = register(
        "splash_milk_bottle",
        SplashMilkBottleItem::new,
        new Item.Properties().stacksTo(16));
}

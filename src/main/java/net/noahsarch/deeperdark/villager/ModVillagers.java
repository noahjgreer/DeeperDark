package net.noahsarch.deeperdark.villager;

import com.google.common.collect.ImmutableSet;
import net.fabricmc.fabric.api.object.builder.v1.villager.VillagerProfessionBuilder;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;
import net.minecraft.world.poi.PointOfInterestTypes;
import net.noahsarch.deeperdark.Deeperdark;

public class ModVillagers {

    public static final VillagerProfession POTION_MASTER = registerProfession("potion_master", PointOfInterestTypes.CLERIC);

    private static VillagerProfession registerProfession(String name, RegistryKey<PointOfInterestType> type) {
        return Registry.register(Registries.VILLAGER_PROFESSION, new Identifier(Deeperdark.MOD_ID, name),
                VillagerProfessionBuilder.create()
                        .id(new Identifier(Deeperdark.MOD_ID, name))
                        .workstation(type)
                        .workSound(SoundEvents.ENTITY_VILLAGER_WORK_CLERIC)
                        .build());
    }

    public static void registerTrades() {
        TradeOfferHelper.registerVillagerOffers(POTION_MASTER, 1, factories -> {
            factories.add(new TradeOffers.BuyForOneEmeraldFactory(Items.GLASS_BOTTLE, 4, 12, 5));
            factories.add(new TradeOffers.SellItemFactory(Items.REDSTONE, 2, 4, 12, 1));
        });

        TradeOfferHelper.registerVillagerOffers(POTION_MASTER, 2, factories -> {
            factories.add(new TradeOffers.BuyForOneEmeraldFactory(Items.GLOWSTONE_DUST, 2, 12, 10));
            factories.add(new TradeOffers.SellItemFactory(Items.SUGAR, 1, 4, 12, 5));
            factories.add(new TradeOffers.SellItemFactory(Items.SPIDER_EYE, 2, 2, 12, 5));
        });

        TradeOfferHelper.registerVillagerOffers(POTION_MASTER, 3, factories -> {
            factories.add(new TradeOffers.BuyForOneEmeraldFactory(Items.GUNPOWDER, 1, 12, 20));
            factories.add(new TradeOffers.SellItemFactory(Items.GLISTERING_MELON_SLICE, 4, 1, 12, 10));
            factories.add(new TradeOffers.SellItemFactory(Items.FERMENTED_SPIDER_EYE, 3, 1, 12, 10));
        });

        TradeOfferHelper.registerVillagerOffers(POTION_MASTER, 4, factories -> {
            factories.add(new TradeOffers.BuyForOneEmeraldFactory(Items.GHAST_TEAR, 1, 12, 30));
            factories.add(new TradeOffers.SellItemFactory(Items.PHANTOM_MEMBRANE, 4, 1, 12, 15));
            factories.add(new TradeOffers.SellItemFactory(Items.DRAGON_BREATH, 4, 1, 12, 15));
        });

        TradeOfferHelper.registerVillagerOffers(POTION_MASTER, 5, factories -> {
            factories.add(new TradeOffers.SellItemFactory(Items.EXPERIENCE_BOTTLE, 3, 1, 12, 30));
        });
    }

    public static void registerVillagers() {
        Deeperdark.LOGGER.info("Registering Villagers for " + Deeperdark.MOD_ID);
        registerTrades();
    }
}

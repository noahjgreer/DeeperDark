package net.noahsarch.deeperdark.villager;

import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;

public class ModTradeOffers {
    public static void registerTrades() {
        // Level 1: Novice
        TradeOfferHelper.registerVillagerOffers(ModVillagers.POTIONMASTER_KEY, 1, factories -> {
            // Buy Glass Bottles (Placeholder: using SellItemFactory for compilation safety)
            // factories.add(new TradeOffers.BuyItemFactory(Items.GLASS_BOTTLE, 4, 1, 12, 1));
            factories.add(new TradeOffers.SellItemFactory(Items.GLASS_BOTTLE, 1, 4, 12, 1));
            factories.add(new TradeOffers.SellItemFactory(Items.REDSTONE, 1, 2, 12, 1));
        });

        // Level 2: Apprentice
        TradeOfferHelper.registerVillagerOffers(ModVillagers.POTIONMASTER_KEY, 2, factories -> {
            factories.add(new TradeOffers.SellItemFactory(Items.GLOWSTONE_DUST, 1, 2, 12, 5));
            factories.add(new TradeOffers.SellItemFactory(Items.SUGAR, 1, 4, 12, 5));
        });

        // Level 3: Journeyman
        TradeOfferHelper.registerVillagerOffers(ModVillagers.POTIONMASTER_KEY, 3, factories -> {
            factories.add(new TradeOffers.SellItemFactory(Items.GUNPOWDER, 1, 1, 12, 10));
            factories.add(new TradeOffers.SellItemFactory(Items.SPIDER_EYE, 1, 2, 12, 10));
        });

        // Level 4: Expert
        TradeOfferHelper.registerVillagerOffers(ModVillagers.POTIONMASTER_KEY, 4, factories -> {
            factories.add(new TradeOffers.SellItemFactory(Items.GHAST_TEAR, 4, 1, 12, 15));
            factories.add(new TradeOffers.SellItemFactory(Items.PHANTOM_MEMBRANE, 4, 1, 12, 15));
        });

        // Level 5: Master
        TradeOfferHelper.registerVillagerOffers(ModVillagers.POTIONMASTER_KEY, 5, factories -> {
            factories.add(new TradeOffers.SellItemFactory(Items.DRAGON_BREATH, 8, 1, 12, 30));
        });
    }
}


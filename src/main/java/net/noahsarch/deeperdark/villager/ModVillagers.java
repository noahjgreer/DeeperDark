package net.noahsarch.deeperdark.villager;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.item.trading.ItemCost;
import net.noahsarch.deeperdark.Deeperdark;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.registries.Registries;

public class ModVillagers {

    @FunctionalInterface
    public interface TradeFactory {
        net.minecraft.world.item.trading.MerchantOffer create(ServerLevel world, net.minecraft.world.entity.Entity entity, RandomSource random);
    }

    public static final Int2ObjectMap<TradeFactory[]> POTION_MASTER_TRADES = new Int2ObjectOpenHashMap<>();

    public static void registerTrades() {
        // Level 1
        List<TradeFactory> level1 = new ArrayList<>();
        level1.add(new RandomBuyFactory(Items.GLASS_BOTTLE, 3, 6, 12, 5));
        level1.add(new RandomSellFactory(Items.REDSTONE, 1, 1, 2, 4, 12, 1)); // 1 Emerald for 2-4 Redstone
        level1.add(new RandomBuyFactory(Items.NETHER_WART, 16, 24, 12, 2));
        level1.add(new RandomBuyFactory(Items.STICK, 32, 64, 12, 2));
        POTION_MASTER_TRADES.put(1, level1.toArray(new TradeFactory[0]));

        // Level 2
        List<TradeFactory> level2 = new ArrayList<>();
        level2.add(new RandomBuyFactory(Items.GLOWSTONE_DUST, 1, 3, 12, 10));
        level2.add(new RandomSellFactory(Items.SUGAR, 1, 1, 2, 4, 12, 5));
        level2.add(new RandomSellFactory(Items.SPIDER_EYE, 1, 1, 1, 2, 12, 5));
        level2.add(new RandomBuyFactory(Items.QUARTZ, 8, 16, 12, 10));
        level2.add(new RandomSellFactory(Items.LAPIS_LAZULI, 1, 1, 1, 3, 12, 5));
        level2.add(new RandomBuyFactory(Items.GOLD_NUGGET, 16, 32, 12, 5));
        POTION_MASTER_TRADES.put(2, level2.toArray(new TradeFactory[0]));

        // Level 3
        List<TradeFactory> level3 = new ArrayList<>();
        level3.add(new RandomBuyFactory(Items.GUNPOWDER, 1, 3, 12, 20));
        level3.add(new RandomSellFactory(Items.GLISTERING_MELON_SLICE, 2, 4, 2, 4, 12, 10)); // Higher price?
        level3.add(new RandomSellFactory(Items.FERMENTED_SPIDER_EYE, 1, 1, 1, 2, 12, 10));
        level3.add(new RandomBuyFactory(Items.RABBIT_FOOT, 1, 2, 12, 20));
        level3.add(new RandomSellFactory(Items.HONEY_BOTTLE, 2, 3, 1, 1, 12, 10));
        POTION_MASTER_TRADES.put(3, level3.toArray(new TradeFactory[0]));

        // Level 4
        List<TradeFactory> level4 = new ArrayList<>();
        level4.add(new RandomBuyFactory(Items.GHAST_TEAR, 1, 1, 12, 30));
        level4.add(new RandomSellFactory(Items.PHANTOM_MEMBRANE, 4, 6, 1, 1, 12, 15));
        level4.add(new RandomSellFactory(Items.DRAGON_BREATH, 4, 6, 1, 1, 12, 15));
        level4.add(new RandomBuyFactory(Items.BLAZE_ROD, 1, 2, 12, 30));
        level4.add(new RandomSellFactory(Items.TURTLE_SCUTE, 5, 8, 1, 1, 12, 15));
        level4.add(new MultiEffectPotionFactory(12, Items.SPLASH_POTION));
        level4.add(new MultiEffectPotionFactory(16, Items.SPLASH_POTION));
        POTION_MASTER_TRADES.put(4, level4.toArray(new TradeFactory[0]));

        // Level 5
        List<TradeFactory> level5 = new ArrayList<>();
        level5.add(new RandomSellFactory(Items.EXPERIENCE_BOTTLE, 3, 6, 1, 3, 12, 30));
        level5.add(new MultiEffectPotionFactory(6));
        level5.add(new MultiEffectPotionFactory(8));
        POTION_MASTER_TRADES.put(5, level5.toArray(new TradeFactory[0]));
    }

    public static void registerVillagers() {
        Deeperdark.LOGGER.info("Registering Villagers for " + Deeperdark.MOD_ID);
        registerTrades();
    }

    private static class MultiEffectPotionFactory implements TradeFactory {
        private final int price;
        private final net.minecraft.world.item.Item item;

        public MultiEffectPotionFactory(int price) {
            this(price, Items.POTION);
        }

        public MultiEffectPotionFactory(int price, net.minecraft.world.item.Item item) {
            this.price = price;
            this.item = item;
        }

        @Override
        public MerchantOffer create(ServerLevel world, Entity entity, RandomSource random) {
            List<Holder<MobEffect>> possibleEffects = new ArrayList<>();
            BuiltInRegistries.MOB_EFFECT.listElements().forEach(possibleEffects::add);

            ItemStack potionStack = new ItemStack(this.item);
            List<MobEffectInstance> effects = new ArrayList<>();

            // 1 to 3 effects
            int numEffects = 1 + random.nextInt(3);

            for(int i = 0; i < numEffects; i++) {
                if (possibleEffects.isEmpty()) break;
                Holder<MobEffect> effectEntry = possibleEffects.get(random.nextInt(possibleEffects.size()));
                MobEffect effect = effectEntry.value();

                int duration = effect.isInstantenous() ? 1 : (600 + random.nextInt(1200)); // 30s to 90s
                effects.add(new MobEffectInstance(effectEntry, duration, 0));
            }

            PotionContents contents = new PotionContents(
                Optional.empty(),
                Optional.of(random.nextInt(0xFFFFFF)),
                effects,
                Optional.empty()
            );

            potionStack.set(DataComponents.POTION_CONTENTS, contents);

            String baseKey = this.item == Items.SPLASH_POTION ? "item.deeperdark.mystery_splash_potion" : "item.deeperdark.mystery_potion";
            String[] suffixes = {
                "", // Perplexing
                ".uninteresting", ".bland", ".clear", ".milky", ".diffuse", ".artless", ".thin", ".flat", ".bulky", ".bungling", ".buttered",
                ".smooth", ".suave", ".debonair", ".elegant", ".fancy", ".charming", ".dashing", ".refined", ".cordial", ".sparkling",
                ".potent", ".foul", ".odorless", ".rank", ".harsh", ".acrid", ".gross", ".stinky"
            };

            String suffix = suffixes[random.nextInt(suffixes.length)];
            String translationKey = baseKey + suffix;

            potionStack.set(DataComponents.CUSTOM_NAME, Component.translatable(translationKey).withStyle(style -> style.withItalic(false)));

            return new MerchantOffer(
                    new ItemCost(Items.EMERALD, this.price),
                    potionStack,
                    3,
                    30,
                    0.05f
            );
        }
    }

    private static class RandomBuyFactory implements TradeFactory {
        private final net.minecraft.world.item.Item item;
        private final int price;
        private final int minCount;
        private final int maxCount;
        private final int maxUses;
        private final int experience;

        public RandomBuyFactory(net.minecraft.world.item.Item item, int minCount, int maxCount, int maxUses, int experience) {
            this(item, 1, minCount, maxCount, maxUses, experience);
        }

        public RandomBuyFactory(net.minecraft.world.item.Item item, int price, int minCount, int maxCount, int maxUses, int experience) {
            this.item = item;
            this.price = price;
            this.minCount = minCount;
            this.maxCount = maxCount;
            this.maxUses = maxUses;
            this.experience = experience;
        }

        @Override
        public MerchantOffer create(ServerLevel world, Entity entity, RandomSource random) {
            int count = net.minecraft.util.Mth.nextInt(random, this.minCount, this.maxCount);
            return new MerchantOffer(new ItemCost(this.item, count), new ItemStack(Items.EMERALD, this.price), this.maxUses, this.experience, 0.05F);
        }
    }

    private static class RandomSellFactory implements TradeFactory {
        private final ItemStack sell;
        private final int minPrice;
        private final int maxPrice;
        private final int minCount;
        private final int maxCount;
        private final int maxUses;
        private final int experience;
        private final float multiplier;

        public RandomSellFactory(net.minecraft.world.item.Item item, int minPrice, int maxPrice, int minCount, int maxCount, int maxUses, int experience) {
            this(new ItemStack(item), minPrice, maxPrice, minCount, maxCount, maxUses, experience, 0.05F);
        }

        public RandomSellFactory(ItemStack stack, int minPrice, int maxPrice, int minCount, int maxCount, int maxUses, int experience, float multiplier) {
            this.sell = stack;
            this.minPrice = minPrice;
            this.maxPrice = maxPrice;
            this.minCount = minCount;
            this.maxCount = maxCount;
            this.maxUses = maxUses;
            this.experience = experience;
            this.multiplier = multiplier;
        }

        @Override
        public MerchantOffer create(ServerLevel world, Entity entity, RandomSource random) {
            int price = net.minecraft.util.Mth.nextInt(random, this.minPrice, this.maxPrice);
            int count = net.minecraft.util.Mth.nextInt(random, this.minCount, this.maxCount);
            ItemStack stack = this.sell.copy();
            stack.setCount(count);
            return new MerchantOffer(new ItemCost(Items.EMERALD, price), stack, this.maxUses, this.experience, this.multiplier);
        }
    }
}


package net.noahsarch.deeperdark.villager;

import com.google.common.collect.ImmutableSet;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.TradedItem;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;
import net.noahsarch.deeperdark.Deeperdark;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class ModVillagers {

    public static final RegistryKey<VillagerProfession> POTION_MASTER_KEY = RegistryKey.of(RegistryKeys.VILLAGER_PROFESSION, Identifier.of(Deeperdark.MOD_ID, "potion_master"));
    public static final VillagerProfession POTION_MASTER = registerProfession("potion_master", PointOfInterestType.NONE);

    private static VillagerProfession registerProfession(String name, Predicate<RegistryEntry<PointOfInterestType>> workstationPredicate) {
        Identifier id = Identifier.of(Deeperdark.MOD_ID, name);

        return Registry.register(Registries.VILLAGER_PROFESSION, id,
                new VillagerProfession(
                        Text.translatable("entity." + Deeperdark.MOD_ID + ".villager." + name),
                        workstationPredicate,
                        workstationPredicate,
                        ImmutableSet.of(),
                        ImmutableSet.of(),
                        SoundEvents.ENTITY_VILLAGER_WORK_CLERIC
                ));
    }

    public static void registerTrades() {
        TradeOfferHelper.registerVillagerOffers(POTION_MASTER_KEY, 1, factories -> {
            factories.add(new RandomBuyFactory(Items.GLASS_BOTTLE, 3, 6, 12, 5));
            factories.add(new RandomSellFactory(Items.REDSTONE, 1, 1, 2, 4, 12, 1)); // 1 Emerald for 2-4 Redstone
            factories.add(new RandomBuyFactory(Items.NETHER_WART, 16, 24, 12, 2));
            factories.add(new RandomBuyFactory(Items.STICK, 32, 64, 12, 2));
        });

        TradeOfferHelper.registerVillagerOffers(POTION_MASTER_KEY, 2, factories -> {
            factories.add(new RandomBuyFactory(Items.GLOWSTONE_DUST, 1, 3, 12, 10));
            factories.add(new RandomSellFactory(Items.SUGAR, 1, 1, 2, 4, 12, 5));
            factories.add(new RandomSellFactory(Items.SPIDER_EYE, 1, 1, 1, 2, 12, 5));
            factories.add(new RandomBuyFactory(Items.QUARTZ, 8, 16, 12, 10));
            factories.add(new RandomSellFactory(Items.LAPIS_LAZULI, 1, 1, 1, 3, 12, 5));
            factories.add(new RandomBuyFactory(Items.GOLD_NUGGET, 16, 32, 12, 5));
        });

        TradeOfferHelper.registerVillagerOffers(POTION_MASTER_KEY, 3, factories -> {
            factories.add(new RandomBuyFactory(Items.GUNPOWDER, 1, 3, 12, 20));
            factories.add(new RandomSellFactory(Items.GLISTERING_MELON_SLICE, 2, 4, 2, 4, 12, 10)); // Higher price?
            factories.add(new RandomSellFactory(Items.FERMENTED_SPIDER_EYE, 1, 1, 1, 2, 12, 10));
            factories.add(new RandomBuyFactory(Items.RABBIT_FOOT, 1, 2, 12, 20));
            factories.add(new RandomSellFactory(Items.HONEY_BOTTLE, 2, 3, 1, 1, 12, 10));
        });

        TradeOfferHelper.registerVillagerOffers(POTION_MASTER_KEY, 4, factories -> {
            factories.add(new RandomBuyFactory(Items.GHAST_TEAR, 1, 1, 12, 30));
            factories.add(new RandomSellFactory(Items.PHANTOM_MEMBRANE, 4, 6, 1, 1, 12, 15));
            factories.add(new RandomSellFactory(Items.DRAGON_BREATH, 4, 6, 1, 1, 12, 15));
            factories.add(new RandomBuyFactory(Items.BLAZE_ROD, 1, 2, 12, 30));
            factories.add(new RandomSellFactory(Items.TURTLE_SCUTE, 5, 8, 1, 1, 12, 15));
            factories.add(new MultiEffectPotionFactory(12, Items.SPLASH_POTION));
            factories.add(new MultiEffectPotionFactory(16, Items.SPLASH_POTION));
        });

        TradeOfferHelper.registerVillagerOffers(POTION_MASTER_KEY, 5, factories -> {
            factories.add(new RandomSellFactory(Items.EXPERIENCE_BOTTLE, 3, 6, 1, 3, 12, 30));
            factories.add(new MultiEffectPotionFactory(6));
             // Adding another mystery potion factory simulates "possibility to instead be another different mystery potion"
             // if the villager picks this trade instead of the XP bottle or first mystery potion.
            factories.add(new MultiEffectPotionFactory(8)); // Slightly more expensive variant?
        });
    }

    private static class MultiEffectPotionFactory implements TradeOffers.Factory {
        private final int price;
        private final net.minecraft.item.Item item;

        public MultiEffectPotionFactory(int price) {
            this(price, Items.POTION);
        }

        public MultiEffectPotionFactory(int price, net.minecraft.item.Item item) {
            this.price = price;
            this.item = item;
        }

        @Override
        public TradeOffer create(Entity entity, Random random) {
            List<RegistryEntry<StatusEffect>> possibleEffects = new ArrayList<>();
            Registries.STATUS_EFFECT.streamEntries().forEach(possibleEffects::add);

            ItemStack potionStack = new ItemStack(this.item);
            List<StatusEffectInstance> effects = new ArrayList<>();

            // 1 to 3 effects
            int numEffects = 1 + random.nextInt(3);

            for(int i = 0; i < numEffects; i++) {
                if (possibleEffects.isEmpty()) break;
                RegistryEntry<StatusEffect> effectEntry = possibleEffects.get(random.nextInt(possibleEffects.size()));
                StatusEffect effect = effectEntry.value();

                int duration = effect.isInstant() ? 1 : (600 + random.nextInt(1200)); // 30s to 90s
                effects.add(new StatusEffectInstance(effectEntry, duration, 0));
            }

            PotionContentsComponent contents = new PotionContentsComponent(
                Optional.empty(),
                Optional.of(random.nextInt(0xFFFFFF)),
                effects,
                Optional.empty()
            );

            potionStack.set(DataComponentTypes.POTION_CONTENTS, contents);
            potionStack.set(DataComponentTypes.CUSTOM_NAME, Text.translatable("item.deeperdark.mystery_potion").styled(style -> style.withItalic(false)));

            return new TradeOffer(
                    new TradedItem(Items.EMERALD, this.price),
                    potionStack,
                    3,
                    30,
                    0.05f
            );
        }
    }

    private static class RandomBuyFactory implements TradeOffers.Factory {
        private final net.minecraft.item.Item item;
        private final int price;
        private final int minCount;
        private final int maxCount;
        private final int maxUses;
        private final int experience;

        public RandomBuyFactory(net.minecraft.item.Item item, int minCount, int maxCount, int maxUses, int experience) {
            this(item, 1, minCount, maxCount, maxUses, experience);
        }

        public RandomBuyFactory(net.minecraft.item.Item item, int price, int minCount, int maxCount, int maxUses, int experience) {
            this.item = item;
            this.price = price;
            this.minCount = minCount;
            this.maxCount = maxCount;
            this.maxUses = maxUses;
            this.experience = experience;
        }

        @Override
        public TradeOffer create(Entity entity, Random random) {
            int count = net.minecraft.util.math.MathHelper.nextInt(random, this.minCount, this.maxCount);
            return new TradeOffer(new TradedItem(this.item, count), new ItemStack(Items.EMERALD, this.price), this.maxUses, this.experience, 0.05F);
        }
    }

    private static class RandomSellFactory implements TradeOffers.Factory {
        private final ItemStack sell;
        private final int minPrice;
        private final int maxPrice;
        private final int minCount;
        private final int maxCount;
        private final int maxUses;
        private final int experience;
        private final float multiplier;

        public RandomSellFactory(net.minecraft.item.Item item, int minPrice, int maxPrice, int minCount, int maxCount, int maxUses, int experience) {
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
        public TradeOffer create(Entity entity, Random random) {
            int price = net.minecraft.util.math.MathHelper.nextInt(random, this.minPrice, this.maxPrice);
            int count = net.minecraft.util.math.MathHelper.nextInt(random, this.minCount, this.maxCount);
            ItemStack stack = this.sell.copy();
            stack.setCount(count);
            return new TradeOffer(new TradedItem(Items.EMERALD, price), stack, this.maxUses, this.experience, this.multiplier);
        }
    }

    public static void registerVillagers() {
        Deeperdark.LOGGER.info("Registering Villagers for " + Deeperdark.MOD_ID);
        registerTrades();
    }
}

/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.DecoratedPotPattern
 *  net.minecraft.block.DecoratedPotPatterns
 *  net.minecraft.item.Item
 *  net.minecraft.item.Items
 *  net.minecraft.registry.Registry
 *  net.minecraft.registry.RegistryKey
 *  net.minecraft.registry.RegistryKeys
 *  net.minecraft.util.Identifier
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import java.util.Map;
import net.minecraft.block.DecoratedPotPattern;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public class DecoratedPotPatterns {
    public static final RegistryKey<DecoratedPotPattern> BLANK = DecoratedPotPatterns.of((String)"blank");
    public static final RegistryKey<DecoratedPotPattern> ANGLER = DecoratedPotPatterns.of((String)"angler");
    public static final RegistryKey<DecoratedPotPattern> ARCHER = DecoratedPotPatterns.of((String)"archer");
    public static final RegistryKey<DecoratedPotPattern> ARMS_UP = DecoratedPotPatterns.of((String)"arms_up");
    public static final RegistryKey<DecoratedPotPattern> BLADE = DecoratedPotPatterns.of((String)"blade");
    public static final RegistryKey<DecoratedPotPattern> BREWER = DecoratedPotPatterns.of((String)"brewer");
    public static final RegistryKey<DecoratedPotPattern> BURN = DecoratedPotPatterns.of((String)"burn");
    public static final RegistryKey<DecoratedPotPattern> DANGER = DecoratedPotPatterns.of((String)"danger");
    public static final RegistryKey<DecoratedPotPattern> EXPLORER = DecoratedPotPatterns.of((String)"explorer");
    public static final RegistryKey<DecoratedPotPattern> FLOW = DecoratedPotPatterns.of((String)"flow");
    public static final RegistryKey<DecoratedPotPattern> FRIEND = DecoratedPotPatterns.of((String)"friend");
    public static final RegistryKey<DecoratedPotPattern> GUSTER = DecoratedPotPatterns.of((String)"guster");
    public static final RegistryKey<DecoratedPotPattern> HEART = DecoratedPotPatterns.of((String)"heart");
    public static final RegistryKey<DecoratedPotPattern> HEARTBREAK = DecoratedPotPatterns.of((String)"heartbreak");
    public static final RegistryKey<DecoratedPotPattern> HOWL = DecoratedPotPatterns.of((String)"howl");
    public static final RegistryKey<DecoratedPotPattern> MINER = DecoratedPotPatterns.of((String)"miner");
    public static final RegistryKey<DecoratedPotPattern> MOURNER = DecoratedPotPatterns.of((String)"mourner");
    public static final RegistryKey<DecoratedPotPattern> PLENTY = DecoratedPotPatterns.of((String)"plenty");
    public static final RegistryKey<DecoratedPotPattern> PRIZE = DecoratedPotPatterns.of((String)"prize");
    public static final RegistryKey<DecoratedPotPattern> SCRAPE = DecoratedPotPatterns.of((String)"scrape");
    public static final RegistryKey<DecoratedPotPattern> SHEAF = DecoratedPotPatterns.of((String)"sheaf");
    public static final RegistryKey<DecoratedPotPattern> SHELTER = DecoratedPotPatterns.of((String)"shelter");
    public static final RegistryKey<DecoratedPotPattern> SKULL = DecoratedPotPatterns.of((String)"skull");
    public static final RegistryKey<DecoratedPotPattern> SNORT = DecoratedPotPatterns.of((String)"snort");
    private static final Map<Item, RegistryKey<DecoratedPotPattern>> SHERD_TO_PATTERN = Map.ofEntries(Map.entry(Items.BRICK, BLANK), Map.entry(Items.ANGLER_POTTERY_SHERD, ANGLER), Map.entry(Items.ARCHER_POTTERY_SHERD, ARCHER), Map.entry(Items.ARMS_UP_POTTERY_SHERD, ARMS_UP), Map.entry(Items.BLADE_POTTERY_SHERD, BLADE), Map.entry(Items.BREWER_POTTERY_SHERD, BREWER), Map.entry(Items.BURN_POTTERY_SHERD, BURN), Map.entry(Items.DANGER_POTTERY_SHERD, DANGER), Map.entry(Items.EXPLORER_POTTERY_SHERD, EXPLORER), Map.entry(Items.FLOW_POTTERY_SHERD, FLOW), Map.entry(Items.FRIEND_POTTERY_SHERD, FRIEND), Map.entry(Items.GUSTER_POTTERY_SHERD, GUSTER), Map.entry(Items.HEART_POTTERY_SHERD, HEART), Map.entry(Items.HEARTBREAK_POTTERY_SHERD, HEARTBREAK), Map.entry(Items.HOWL_POTTERY_SHERD, HOWL), Map.entry(Items.MINER_POTTERY_SHERD, MINER), Map.entry(Items.MOURNER_POTTERY_SHERD, MOURNER), Map.entry(Items.PLENTY_POTTERY_SHERD, PLENTY), Map.entry(Items.PRIZE_POTTERY_SHERD, PRIZE), Map.entry(Items.SCRAPE_POTTERY_SHERD, SCRAPE), Map.entry(Items.SHEAF_POTTERY_SHERD, SHEAF), Map.entry(Items.SHELTER_POTTERY_SHERD, SHELTER), Map.entry(Items.SKULL_POTTERY_SHERD, SKULL), Map.entry(Items.SNORT_POTTERY_SHERD, SNORT));

    public static @Nullable RegistryKey<DecoratedPotPattern> fromSherd(Item sherd) {
        return (RegistryKey)SHERD_TO_PATTERN.get(sherd);
    }

    private static RegistryKey<DecoratedPotPattern> of(String path) {
        return RegistryKey.of((RegistryKey)RegistryKeys.DECORATED_POT_PATTERN, (Identifier)Identifier.ofVanilla((String)path));
    }

    public static DecoratedPotPattern registerAndGetDefault(Registry<DecoratedPotPattern> registry) {
        DecoratedPotPatterns.register(registry, (RegistryKey)ANGLER, (String)"angler_pottery_pattern");
        DecoratedPotPatterns.register(registry, (RegistryKey)ARCHER, (String)"archer_pottery_pattern");
        DecoratedPotPatterns.register(registry, (RegistryKey)ARMS_UP, (String)"arms_up_pottery_pattern");
        DecoratedPotPatterns.register(registry, (RegistryKey)BLADE, (String)"blade_pottery_pattern");
        DecoratedPotPatterns.register(registry, (RegistryKey)BREWER, (String)"brewer_pottery_pattern");
        DecoratedPotPatterns.register(registry, (RegistryKey)BURN, (String)"burn_pottery_pattern");
        DecoratedPotPatterns.register(registry, (RegistryKey)DANGER, (String)"danger_pottery_pattern");
        DecoratedPotPatterns.register(registry, (RegistryKey)EXPLORER, (String)"explorer_pottery_pattern");
        DecoratedPotPatterns.register(registry, (RegistryKey)FLOW, (String)"flow_pottery_pattern");
        DecoratedPotPatterns.register(registry, (RegistryKey)FRIEND, (String)"friend_pottery_pattern");
        DecoratedPotPatterns.register(registry, (RegistryKey)GUSTER, (String)"guster_pottery_pattern");
        DecoratedPotPatterns.register(registry, (RegistryKey)HEART, (String)"heart_pottery_pattern");
        DecoratedPotPatterns.register(registry, (RegistryKey)HEARTBREAK, (String)"heartbreak_pottery_pattern");
        DecoratedPotPatterns.register(registry, (RegistryKey)HOWL, (String)"howl_pottery_pattern");
        DecoratedPotPatterns.register(registry, (RegistryKey)MINER, (String)"miner_pottery_pattern");
        DecoratedPotPatterns.register(registry, (RegistryKey)MOURNER, (String)"mourner_pottery_pattern");
        DecoratedPotPatterns.register(registry, (RegistryKey)PLENTY, (String)"plenty_pottery_pattern");
        DecoratedPotPatterns.register(registry, (RegistryKey)PRIZE, (String)"prize_pottery_pattern");
        DecoratedPotPatterns.register(registry, (RegistryKey)SCRAPE, (String)"scrape_pottery_pattern");
        DecoratedPotPatterns.register(registry, (RegistryKey)SHEAF, (String)"sheaf_pottery_pattern");
        DecoratedPotPatterns.register(registry, (RegistryKey)SHELTER, (String)"shelter_pottery_pattern");
        DecoratedPotPatterns.register(registry, (RegistryKey)SKULL, (String)"skull_pottery_pattern");
        DecoratedPotPatterns.register(registry, (RegistryKey)SNORT, (String)"snort_pottery_pattern");
        return DecoratedPotPatterns.register(registry, (RegistryKey)BLANK, (String)"decorated_pot_side");
    }

    private static DecoratedPotPattern register(Registry<DecoratedPotPattern> registry, RegistryKey<DecoratedPotPattern> key, String id) {
        return (DecoratedPotPattern)Registry.register(registry, key, (Object)new DecoratedPotPattern(Identifier.ofVanilla((String)id)));
    }
}


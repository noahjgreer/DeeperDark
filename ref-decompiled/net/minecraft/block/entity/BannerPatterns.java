/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.entity.BannerPattern
 *  net.minecraft.block.entity.BannerPatterns
 *  net.minecraft.registry.Registerable
 *  net.minecraft.registry.RegistryKey
 *  net.minecraft.registry.RegistryKeys
 *  net.minecraft.util.Identifier
 */
package net.minecraft.block.entity;

import net.minecraft.block.entity.BannerPattern;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

/*
 * Exception performing whole class analysis ignored.
 */
public class BannerPatterns {
    public static final RegistryKey<BannerPattern> BASE = BannerPatterns.of((String)"base");
    public static final RegistryKey<BannerPattern> SQUARE_BOTTOM_LEFT = BannerPatterns.of((String)"square_bottom_left");
    public static final RegistryKey<BannerPattern> SQUARE_BOTTOM_RIGHT = BannerPatterns.of((String)"square_bottom_right");
    public static final RegistryKey<BannerPattern> SQUARE_TOP_LEFT = BannerPatterns.of((String)"square_top_left");
    public static final RegistryKey<BannerPattern> SQUARE_TOP_RIGHT = BannerPatterns.of((String)"square_top_right");
    public static final RegistryKey<BannerPattern> STRIPE_BOTTOM = BannerPatterns.of((String)"stripe_bottom");
    public static final RegistryKey<BannerPattern> STRIPE_TOP = BannerPatterns.of((String)"stripe_top");
    public static final RegistryKey<BannerPattern> STRIPE_LEFT = BannerPatterns.of((String)"stripe_left");
    public static final RegistryKey<BannerPattern> STRIPE_RIGHT = BannerPatterns.of((String)"stripe_right");
    public static final RegistryKey<BannerPattern> STRIPE_CENTER = BannerPatterns.of((String)"stripe_center");
    public static final RegistryKey<BannerPattern> STRIPE_MIDDLE = BannerPatterns.of((String)"stripe_middle");
    public static final RegistryKey<BannerPattern> STRIPE_DOWNRIGHT = BannerPatterns.of((String)"stripe_downright");
    public static final RegistryKey<BannerPattern> STRIPE_DOWNLEFT = BannerPatterns.of((String)"stripe_downleft");
    public static final RegistryKey<BannerPattern> SMALL_STRIPES = BannerPatterns.of((String)"small_stripes");
    public static final RegistryKey<BannerPattern> CROSS = BannerPatterns.of((String)"cross");
    public static final RegistryKey<BannerPattern> STRAIGHT_CROSS = BannerPatterns.of((String)"straight_cross");
    public static final RegistryKey<BannerPattern> TRIANGLE_BOTTOM = BannerPatterns.of((String)"triangle_bottom");
    public static final RegistryKey<BannerPattern> TRIANGLE_TOP = BannerPatterns.of((String)"triangle_top");
    public static final RegistryKey<BannerPattern> TRIANGLES_BOTTOM = BannerPatterns.of((String)"triangles_bottom");
    public static final RegistryKey<BannerPattern> TRIANGLES_TOP = BannerPatterns.of((String)"triangles_top");
    public static final RegistryKey<BannerPattern> DIAGONAL_LEFT = BannerPatterns.of((String)"diagonal_left");
    public static final RegistryKey<BannerPattern> DIAGONAL_UP_RIGHT = BannerPatterns.of((String)"diagonal_up_right");
    public static final RegistryKey<BannerPattern> DIAGONAL_UP_LEFT = BannerPatterns.of((String)"diagonal_up_left");
    public static final RegistryKey<BannerPattern> DIAGONAL_RIGHT = BannerPatterns.of((String)"diagonal_right");
    public static final RegistryKey<BannerPattern> CIRCLE = BannerPatterns.of((String)"circle");
    public static final RegistryKey<BannerPattern> RHOMBUS = BannerPatterns.of((String)"rhombus");
    public static final RegistryKey<BannerPattern> HALF_VERTICAL = BannerPatterns.of((String)"half_vertical");
    public static final RegistryKey<BannerPattern> HALF_HORIZONTAL = BannerPatterns.of((String)"half_horizontal");
    public static final RegistryKey<BannerPattern> HALF_VERTICAL_RIGHT = BannerPatterns.of((String)"half_vertical_right");
    public static final RegistryKey<BannerPattern> HALF_HORIZONTAL_BOTTOM = BannerPatterns.of((String)"half_horizontal_bottom");
    public static final RegistryKey<BannerPattern> BORDER = BannerPatterns.of((String)"border");
    public static final RegistryKey<BannerPattern> CURLY_BORDER = BannerPatterns.of((String)"curly_border");
    public static final RegistryKey<BannerPattern> GRADIENT = BannerPatterns.of((String)"gradient");
    public static final RegistryKey<BannerPattern> GRADIENT_UP = BannerPatterns.of((String)"gradient_up");
    public static final RegistryKey<BannerPattern> BRICKS = BannerPatterns.of((String)"bricks");
    public static final RegistryKey<BannerPattern> GLOBE = BannerPatterns.of((String)"globe");
    public static final RegistryKey<BannerPattern> CREEPER = BannerPatterns.of((String)"creeper");
    public static final RegistryKey<BannerPattern> SKULL = BannerPatterns.of((String)"skull");
    public static final RegistryKey<BannerPattern> FLOWER = BannerPatterns.of((String)"flower");
    public static final RegistryKey<BannerPattern> MOJANG = BannerPatterns.of((String)"mojang");
    public static final RegistryKey<BannerPattern> PIGLIN = BannerPatterns.of((String)"piglin");
    public static final RegistryKey<BannerPattern> FLOW = BannerPatterns.of((String)"flow");
    public static final RegistryKey<BannerPattern> GUSTER = BannerPatterns.of((String)"guster");

    private static RegistryKey<BannerPattern> of(String id) {
        return RegistryKey.of((RegistryKey)RegistryKeys.BANNER_PATTERN, (Identifier)Identifier.ofVanilla((String)id));
    }

    public static void bootstrap(Registerable<BannerPattern> registry) {
        BannerPatterns.register(registry, (RegistryKey)BASE);
        BannerPatterns.register(registry, (RegistryKey)SQUARE_BOTTOM_LEFT);
        BannerPatterns.register(registry, (RegistryKey)SQUARE_BOTTOM_RIGHT);
        BannerPatterns.register(registry, (RegistryKey)SQUARE_TOP_LEFT);
        BannerPatterns.register(registry, (RegistryKey)SQUARE_TOP_RIGHT);
        BannerPatterns.register(registry, (RegistryKey)STRIPE_BOTTOM);
        BannerPatterns.register(registry, (RegistryKey)STRIPE_TOP);
        BannerPatterns.register(registry, (RegistryKey)STRIPE_LEFT);
        BannerPatterns.register(registry, (RegistryKey)STRIPE_RIGHT);
        BannerPatterns.register(registry, (RegistryKey)STRIPE_CENTER);
        BannerPatterns.register(registry, (RegistryKey)STRIPE_MIDDLE);
        BannerPatterns.register(registry, (RegistryKey)STRIPE_DOWNRIGHT);
        BannerPatterns.register(registry, (RegistryKey)STRIPE_DOWNLEFT);
        BannerPatterns.register(registry, (RegistryKey)SMALL_STRIPES);
        BannerPatterns.register(registry, (RegistryKey)CROSS);
        BannerPatterns.register(registry, (RegistryKey)STRAIGHT_CROSS);
        BannerPatterns.register(registry, (RegistryKey)TRIANGLE_BOTTOM);
        BannerPatterns.register(registry, (RegistryKey)TRIANGLE_TOP);
        BannerPatterns.register(registry, (RegistryKey)TRIANGLES_BOTTOM);
        BannerPatterns.register(registry, (RegistryKey)TRIANGLES_TOP);
        BannerPatterns.register(registry, (RegistryKey)DIAGONAL_LEFT);
        BannerPatterns.register(registry, (RegistryKey)DIAGONAL_UP_RIGHT);
        BannerPatterns.register(registry, (RegistryKey)DIAGONAL_UP_LEFT);
        BannerPatterns.register(registry, (RegistryKey)DIAGONAL_RIGHT);
        BannerPatterns.register(registry, (RegistryKey)CIRCLE);
        BannerPatterns.register(registry, (RegistryKey)RHOMBUS);
        BannerPatterns.register(registry, (RegistryKey)HALF_VERTICAL);
        BannerPatterns.register(registry, (RegistryKey)HALF_HORIZONTAL);
        BannerPatterns.register(registry, (RegistryKey)HALF_VERTICAL_RIGHT);
        BannerPatterns.register(registry, (RegistryKey)HALF_HORIZONTAL_BOTTOM);
        BannerPatterns.register(registry, (RegistryKey)BORDER);
        BannerPatterns.register(registry, (RegistryKey)GRADIENT);
        BannerPatterns.register(registry, (RegistryKey)GRADIENT_UP);
        BannerPatterns.register(registry, (RegistryKey)BRICKS);
        BannerPatterns.register(registry, (RegistryKey)CURLY_BORDER);
        BannerPatterns.register(registry, (RegistryKey)GLOBE);
        BannerPatterns.register(registry, (RegistryKey)CREEPER);
        BannerPatterns.register(registry, (RegistryKey)SKULL);
        BannerPatterns.register(registry, (RegistryKey)FLOWER);
        BannerPatterns.register(registry, (RegistryKey)MOJANG);
        BannerPatterns.register(registry, (RegistryKey)PIGLIN);
        BannerPatterns.register(registry, (RegistryKey)FLOW);
        BannerPatterns.register(registry, (RegistryKey)GUSTER);
    }

    public static void register(Registerable<BannerPattern> registry, RegistryKey<BannerPattern> key) {
        registry.register(key, (Object)new BannerPattern(key.getValue(), "block.minecraft.banner." + key.getValue().toShortTranslationKey()));
    }
}


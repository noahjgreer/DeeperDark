/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.data.TextureKey
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.data;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public final class TextureKey {
    public static final TextureKey ALL = TextureKey.of((String)"all");
    public static final TextureKey TEXTURE = TextureKey.of((String)"texture", (TextureKey)ALL);
    public static final TextureKey PARTICLE = TextureKey.of((String)"particle", (TextureKey)TEXTURE);
    public static final TextureKey END = TextureKey.of((String)"end", (TextureKey)ALL);
    public static final TextureKey BOTTOM = TextureKey.of((String)"bottom", (TextureKey)END);
    public static final TextureKey TOP = TextureKey.of((String)"top", (TextureKey)END);
    public static final TextureKey FRONT = TextureKey.of((String)"front", (TextureKey)ALL);
    public static final TextureKey BACK = TextureKey.of((String)"back", (TextureKey)ALL);
    public static final TextureKey SIDE = TextureKey.of((String)"side", (TextureKey)ALL);
    public static final TextureKey NORTH = TextureKey.of((String)"north", (TextureKey)SIDE);
    public static final TextureKey SOUTH = TextureKey.of((String)"south", (TextureKey)SIDE);
    public static final TextureKey EAST = TextureKey.of((String)"east", (TextureKey)SIDE);
    public static final TextureKey WEST = TextureKey.of((String)"west", (TextureKey)SIDE);
    public static final TextureKey UP = TextureKey.of((String)"up");
    public static final TextureKey DOWN = TextureKey.of((String)"down");
    public static final TextureKey CROSS = TextureKey.of((String)"cross");
    public static final TextureKey CROSS_EMISSIVE = TextureKey.of((String)"cross_emissive");
    public static final TextureKey PLANT = TextureKey.of((String)"plant");
    public static final TextureKey WALL = TextureKey.of((String)"wall", (TextureKey)ALL);
    public static final TextureKey RAIL = TextureKey.of((String)"rail");
    public static final TextureKey WOOL = TextureKey.of((String)"wool");
    public static final TextureKey PATTERN = TextureKey.of((String)"pattern");
    public static final TextureKey PANE = TextureKey.of((String)"pane");
    public static final TextureKey EDGE = TextureKey.of((String)"edge");
    public static final TextureKey FAN = TextureKey.of((String)"fan");
    public static final TextureKey STEM = TextureKey.of((String)"stem");
    public static final TextureKey UPPERSTEM = TextureKey.of((String)"upperstem");
    public static final TextureKey CROP = TextureKey.of((String)"crop");
    public static final TextureKey DIRT = TextureKey.of((String)"dirt");
    public static final TextureKey FIRE = TextureKey.of((String)"fire");
    public static final TextureKey LANTERN = TextureKey.of((String)"lantern");
    public static final TextureKey PLATFORM = TextureKey.of((String)"platform");
    public static final TextureKey UNSTICKY = TextureKey.of((String)"unsticky");
    public static final TextureKey TORCH = TextureKey.of((String)"torch");
    public static final TextureKey LAYER0 = TextureKey.of((String)"layer0");
    public static final TextureKey LAYER1 = TextureKey.of((String)"layer1");
    public static final TextureKey LAYER2 = TextureKey.of((String)"layer2");
    public static final TextureKey LIT_LOG = TextureKey.of((String)"lit_log");
    public static final TextureKey CANDLE = TextureKey.of((String)"candle");
    public static final TextureKey INSIDE = TextureKey.of((String)"inside");
    public static final TextureKey CONTENT = TextureKey.of((String)"content");
    public static final TextureKey INNER_TOP = TextureKey.of((String)"inner_top");
    public static final TextureKey FLOWERBED = TextureKey.of((String)"flowerbed");
    public static final TextureKey TENTACLES = TextureKey.of((String)"tentacles");
    public static final TextureKey BARS = TextureKey.of((String)"bars");
    private final String name;
    private final @Nullable TextureKey parent;

    public static TextureKey of(String name) {
        return new TextureKey(name, null);
    }

    public static TextureKey of(String name, TextureKey parent) {
        return new TextureKey(name, parent);
    }

    private TextureKey(String name, @Nullable TextureKey parent) {
        this.name = name;
        this.parent = parent;
    }

    public String getName() {
        return this.name;
    }

    public @Nullable TextureKey getParent() {
        return this.parent;
    }

    public String toString() {
        return "#" + this.name;
    }
}


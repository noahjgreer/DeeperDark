/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.enchantment;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record Enchantment.Cost(int base, int perLevelAboveFirst) {
    public static final Codec<Enchantment.Cost> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.INT.fieldOf("base").forGetter(Enchantment.Cost::base), (App)Codec.INT.fieldOf("per_level_above_first").forGetter(Enchantment.Cost::perLevelAboveFirst)).apply((Applicative)instance, Enchantment.Cost::new));

    public int forLevel(int level) {
        return this.base + this.perLevelAboveFirst * (level - 1);
    }
}

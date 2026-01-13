/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
 */
package net.minecraft.block;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.Map;
import net.minecraft.util.StringIdentifiable;

public static interface SkullBlock.SkullType
extends StringIdentifiable {
    public static final Map<String, SkullBlock.SkullType> TYPES = new Object2ObjectArrayMap();
    public static final Codec<SkullBlock.SkullType> CODEC = Codec.stringResolver(StringIdentifiable::asString, TYPES::get);
}

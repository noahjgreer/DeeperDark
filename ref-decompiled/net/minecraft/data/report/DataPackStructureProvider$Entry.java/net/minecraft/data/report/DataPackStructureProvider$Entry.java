/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.data.report;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

record DataPackStructureProvider.Entry(boolean elements, boolean tags, boolean stable) {
    public static final MapCodec<DataPackStructureProvider.Entry> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codec.BOOL.fieldOf("elements").forGetter(DataPackStructureProvider.Entry::elements), (App)Codec.BOOL.fieldOf("tags").forGetter(DataPackStructureProvider.Entry::tags), (App)Codec.BOOL.fieldOf("stable").forGetter(DataPackStructureProvider.Entry::stable)).apply((Applicative)instance, DataPackStructureProvider.Entry::new));
    public static final Codec<DataPackStructureProvider.Entry> CODEC = MAP_CODEC.codec();
}

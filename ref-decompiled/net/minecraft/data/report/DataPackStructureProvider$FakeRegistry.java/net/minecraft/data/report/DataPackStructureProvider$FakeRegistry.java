/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.data.report;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.data.report.DataPackStructureProvider;

record DataPackStructureProvider.FakeRegistry(DataPackStructureProvider.Format format, DataPackStructureProvider.Entry entry) {
    public static final Codec<DataPackStructureProvider.FakeRegistry> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)DataPackStructureProvider.Format.CODEC.fieldOf("format").forGetter(DataPackStructureProvider.FakeRegistry::format), (App)DataPackStructureProvider.Entry.MAP_CODEC.forGetter(DataPackStructureProvider.FakeRegistry::entry)).apply((Applicative)instance, DataPackStructureProvider.FakeRegistry::new));
}

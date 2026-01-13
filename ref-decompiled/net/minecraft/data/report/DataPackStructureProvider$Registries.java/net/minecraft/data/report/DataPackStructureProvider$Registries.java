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
import java.util.Map;
import net.minecraft.data.report.DataPackStructureProvider;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

record DataPackStructureProvider.Registries(Map<RegistryKey<? extends Registry<?>>, DataPackStructureProvider.Entry> registries, Map<String, DataPackStructureProvider.FakeRegistry> others) {
    public static final Codec<DataPackStructureProvider.Registries> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.unboundedMap(REGISTRY_KEY_CODEC, DataPackStructureProvider.Entry.CODEC).fieldOf("registries").forGetter(DataPackStructureProvider.Registries::registries), (App)Codec.unboundedMap((Codec)Codec.STRING, DataPackStructureProvider.FakeRegistry.CODEC).fieldOf("others").forGetter(DataPackStructureProvider.Registries::others)).apply((Applicative)instance, DataPackStructureProvider.Registries::new));
}

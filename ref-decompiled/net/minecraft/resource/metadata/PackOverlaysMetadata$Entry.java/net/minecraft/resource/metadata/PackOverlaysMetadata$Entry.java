/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.resource.metadata;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.resource.PackVersion;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.PackOverlaysMetadata;
import net.minecraft.util.dynamic.Range;

public record PackOverlaysMetadata.Entry(Range<PackVersion> format, String overlay) {
    static Codec<List<PackOverlaysMetadata.Entry>> createCodec(ResourceType type) {
        int i = PackVersion.getLastOldPackVersion(type);
        return Holder.CODEC.listOf().flatXmap(holders -> PackVersion.validate(holders, i, (holder, versionRange) -> new PackOverlaysMetadata.Entry((Range<PackVersion>)versionRange, holder.overlay())), entries -> DataResult.success(entries.stream().map(entry -> new Holder(PackVersion.Format.ofRange(entry.format(), i), entry.overlay())).toList()));
    }

    public boolean isValid(PackVersion version) {
        return this.format.contains(version);
    }

    record Holder(PackVersion.Format format, String overlay) implements PackVersion.FormatHolder
    {
        static final Codec<Holder> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)PackVersion.Format.OVERLAY_CODEC.forGetter(Holder::format), (App)Codec.STRING.validate(PackOverlaysMetadata::validate).fieldOf("directory").forGetter(Holder::overlay)).apply((Applicative)instance, Holder::new));

        @Override
        public String toString() {
            return this.overlay;
        }
    }
}

/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.resource.metadata;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resource.PackVersion;
import net.minecraft.resource.metadata.PackOverlaysMetadata;

record PackOverlaysMetadata.Entry.Holder(PackVersion.Format format, String overlay) implements PackVersion.FormatHolder
{
    static final Codec<PackOverlaysMetadata.Entry.Holder> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)PackVersion.Format.OVERLAY_CODEC.forGetter(PackOverlaysMetadata.Entry.Holder::format), (App)Codec.STRING.validate(PackOverlaysMetadata::validate).fieldOf("directory").forGetter(PackOverlaysMetadata.Entry.Holder::overlay)).apply((Applicative)instance, PackOverlaysMetadata.Entry.Holder::new));

    @Override
    public String toString() {
        return this.overlay;
    }
}

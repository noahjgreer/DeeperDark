/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.resource.metadata.TextureResourceMetadata
 *  net.minecraft.client.texture.MipmapStrategy
 *  net.minecraft.resource.metadata.ResourceMetadataSerializer
 */
package net.minecraft.client.resource.metadata;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.MipmapStrategy;
import net.minecraft.resource.metadata.ResourceMetadataSerializer;

@Environment(value=EnvType.CLIENT)
public record TextureResourceMetadata(boolean blur, boolean clamp, MipmapStrategy mipmapStrategy, float alphaCutoffBias) {
    private final boolean blur;
    private final boolean clamp;
    private final MipmapStrategy mipmapStrategy;
    private final float alphaCutoffBias;
    public static final boolean DEFAULT_BLUR = false;
    public static final boolean DEFAULT_CLAMP = false;
    public static final float DEFAULT_ALPHA_CUTOFF_BIAS = 0.0f;
    public static final Codec<TextureResourceMetadata> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.BOOL.optionalFieldOf("blur", (Object)false).forGetter(TextureResourceMetadata::blur), (App)Codec.BOOL.optionalFieldOf("clamp", (Object)false).forGetter(TextureResourceMetadata::clamp), (App)MipmapStrategy.CODEC.optionalFieldOf("mipmap_strategy", (Object)MipmapStrategy.AUTO).forGetter(TextureResourceMetadata::mipmapStrategy), (App)Codec.FLOAT.optionalFieldOf("alpha_cutoff_bias", (Object)Float.valueOf(0.0f)).forGetter(TextureResourceMetadata::alphaCutoffBias)).apply((Applicative)instance, TextureResourceMetadata::new));
    public static final ResourceMetadataSerializer<TextureResourceMetadata> SERIALIZER = new ResourceMetadataSerializer("texture", CODEC);

    public TextureResourceMetadata(boolean blur, boolean clamp, MipmapStrategy mipmapStrategy, float alphaCutoffBias) {
        this.blur = blur;
        this.clamp = clamp;
        this.mipmapStrategy = mipmapStrategy;
        this.alphaCutoffBias = alphaCutoffBias;
    }

    public boolean blur() {
        return this.blur;
    }

    public boolean clamp() {
        return this.clamp;
    }

    public MipmapStrategy mipmapStrategy() {
        return this.mipmapStrategy;
    }

    public float alphaCutoffBias() {
        return this.alphaCutoffBias;
    }
}


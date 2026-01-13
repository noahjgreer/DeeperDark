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
 */
package net.minecraft.client.gl;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.PostEffectPipeline;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

@Environment(value=EnvType.CLIENT)
public record PostEffectPipeline.TextureSampler(String samplerName, Identifier location, int width, int height, boolean bilinear) implements PostEffectPipeline.Input
{
    public static final Codec<PostEffectPipeline.TextureSampler> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.STRING.fieldOf("sampler_name").forGetter(PostEffectPipeline.TextureSampler::samplerName), (App)Identifier.CODEC.fieldOf("location").forGetter(PostEffectPipeline.TextureSampler::location), (App)Codecs.POSITIVE_INT.fieldOf("width").forGetter(PostEffectPipeline.TextureSampler::width), (App)Codecs.POSITIVE_INT.fieldOf("height").forGetter(PostEffectPipeline.TextureSampler::height), (App)Codec.BOOL.optionalFieldOf("bilinear", (Object)false).forGetter(PostEffectPipeline.TextureSampler::bilinear)).apply((Applicative)instance, PostEffectPipeline.TextureSampler::new));

    @Override
    public Set<Identifier> getTargetId() {
        return Set.of();
    }
}

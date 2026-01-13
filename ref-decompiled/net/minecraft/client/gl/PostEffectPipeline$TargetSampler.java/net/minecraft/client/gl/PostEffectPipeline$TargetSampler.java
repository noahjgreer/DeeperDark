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

@Environment(value=EnvType.CLIENT)
public record PostEffectPipeline.TargetSampler(String samplerName, Identifier targetId, boolean useDepthBuffer, boolean bilinear) implements PostEffectPipeline.Input
{
    public static final Codec<PostEffectPipeline.TargetSampler> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.STRING.fieldOf("sampler_name").forGetter(PostEffectPipeline.TargetSampler::samplerName), (App)Identifier.CODEC.fieldOf("target").forGetter(PostEffectPipeline.TargetSampler::targetId), (App)Codec.BOOL.optionalFieldOf("use_depth_buffer", (Object)false).forGetter(PostEffectPipeline.TargetSampler::useDepthBuffer), (App)Codec.BOOL.optionalFieldOf("bilinear", (Object)false).forGetter(PostEffectPipeline.TargetSampler::bilinear)).apply((Applicative)instance, PostEffectPipeline.TargetSampler::new));

    @Override
    public Set<Identifier> getTargetId() {
        return Set.of(this.targetId);
    }
}

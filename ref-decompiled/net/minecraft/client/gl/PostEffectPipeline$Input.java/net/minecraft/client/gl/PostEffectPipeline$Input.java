/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gl;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import java.lang.runtime.SwitchBootstraps;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.PostEffectPipeline;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public static sealed interface PostEffectPipeline.Input
permits PostEffectPipeline.TextureSampler, PostEffectPipeline.TargetSampler {
    public static final Codec<PostEffectPipeline.Input> CODEC = Codec.xor(PostEffectPipeline.TextureSampler.CODEC, PostEffectPipeline.TargetSampler.CODEC).xmap(either -> (PostEffectPipeline.Input)either.map(Function.identity(), Function.identity()), sampler -> {
        PostEffectPipeline.Input input = sampler;
        Objects.requireNonNull(input);
        PostEffectPipeline.Input input2 = input;
        int i = 0;
        return switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{PostEffectPipeline.TextureSampler.class, PostEffectPipeline.TargetSampler.class}, (Object)input2, i)) {
            default -> throw new MatchException(null, null);
            case 0 -> {
                PostEffectPipeline.TextureSampler textureSampler = (PostEffectPipeline.TextureSampler)input2;
                yield Either.left((Object)textureSampler);
            }
            case 1 -> {
                PostEffectPipeline.TargetSampler targetSampler = (PostEffectPipeline.TargetSampler)input2;
                yield Either.right((Object)targetSampler);
            }
        };
    });

    public String samplerName();

    public Set<Identifier> getTargetId();
}

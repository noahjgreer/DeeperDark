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
 *  net.minecraft.client.gl.PostEffectPipeline
 *  net.minecraft.client.gl.PostEffectPipeline$Pass
 *  net.minecraft.client.gl.PostEffectPipeline$Targets
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.gl;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.PostEffectPipeline;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public record PostEffectPipeline(Map<Identifier, Targets> internalTargets, List<Pass> passes) {
    private final Map<Identifier, Targets> internalTargets;
    private final List<Pass> passes;
    public static final Codec<PostEffectPipeline> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.unboundedMap((Codec)Identifier.CODEC, (Codec)Targets.CODEC).optionalFieldOf("targets", Map.of()).forGetter(PostEffectPipeline::internalTargets), (App)Pass.CODEC.listOf().optionalFieldOf("passes", List.of()).forGetter(PostEffectPipeline::passes)).apply((Applicative)instance, PostEffectPipeline::new));

    public PostEffectPipeline(Map<Identifier, Targets> internalTargets, List<Pass> passes) {
        this.internalTargets = internalTargets;
        this.passes = passes;
    }

    public Map<Identifier, Targets> internalTargets() {
        return this.internalTargets;
    }

    public List<Pass> passes() {
        return this.passes;
    }
}


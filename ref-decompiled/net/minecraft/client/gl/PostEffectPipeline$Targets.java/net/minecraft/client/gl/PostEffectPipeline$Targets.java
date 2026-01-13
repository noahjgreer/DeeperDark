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
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.dynamic.Codecs;

@Environment(value=EnvType.CLIENT)
public record PostEffectPipeline.Targets(Optional<Integer> width, Optional<Integer> height, boolean persistent, int clearColor) {
    public static final Codec<PostEffectPipeline.Targets> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codecs.POSITIVE_INT.optionalFieldOf("width").forGetter(PostEffectPipeline.Targets::width), (App)Codecs.POSITIVE_INT.optionalFieldOf("height").forGetter(PostEffectPipeline.Targets::height), (App)Codec.BOOL.optionalFieldOf("persistent", (Object)false).forGetter(PostEffectPipeline.Targets::persistent), (App)Codecs.ARGB.optionalFieldOf("clear_color", (Object)0).forGetter(PostEffectPipeline.Targets::clearColor)).apply((Applicative)instance, PostEffectPipeline.Targets::new));
}

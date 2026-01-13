/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.objects.ObjectArraySet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gl;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.PostEffectPipeline;
import net.minecraft.client.gl.UniformValue;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public record PostEffectPipeline.Pass(Identifier vertexShaderId, Identifier fragmentShaderId, List<PostEffectPipeline.Input> inputs, Identifier outputTarget, Map<String, List<UniformValue>> uniforms) {
    private static final Codec<List<PostEffectPipeline.Input>> INPUTS_CODEC = PostEffectPipeline.Input.CODEC.listOf().validate(inputs -> {
        ObjectArraySet set = new ObjectArraySet(inputs.size());
        for (PostEffectPipeline.Input input : inputs) {
            if (set.add(input.samplerName())) continue;
            return DataResult.error(() -> "Encountered repeated sampler name: " + input.samplerName());
        }
        return DataResult.success((Object)inputs);
    });
    private static final Codec<Map<String, List<UniformValue>>> UNIFORMS_CODEC = Codec.unboundedMap((Codec)Codec.STRING, (Codec)UniformValue.CODEC.listOf());
    public static final Codec<PostEffectPipeline.Pass> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Identifier.CODEC.fieldOf("vertex_shader").forGetter(PostEffectPipeline.Pass::vertexShaderId), (App)Identifier.CODEC.fieldOf("fragment_shader").forGetter(PostEffectPipeline.Pass::fragmentShaderId), (App)INPUTS_CODEC.optionalFieldOf("inputs", List.of()).forGetter(PostEffectPipeline.Pass::inputs), (App)Identifier.CODEC.fieldOf("output").forGetter(PostEffectPipeline.Pass::outputTarget), (App)UNIFORMS_CODEC.optionalFieldOf("uniforms", Map.of()).forGetter(PostEffectPipeline.Pass::uniforms)).apply((Applicative)instance, PostEffectPipeline.Pass::new));

    public Stream<Identifier> streamTargets() {
        Stream stream = this.inputs.stream().flatMap(input -> input.getTargetId().stream());
        return Stream.concat(stream, Stream.of(this.outputTarget));
    }
}

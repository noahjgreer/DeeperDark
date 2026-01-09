package net.minecraft.client.gl;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

@Environment(EnvType.CLIENT)
public record PostEffectPipeline(Map internalTargets, List passes) {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(Codec.unboundedMap(Identifier.CODEC, PostEffectPipeline.Targets.CODEC).optionalFieldOf("targets", Map.of()).forGetter(PostEffectPipeline::internalTargets), PostEffectPipeline.Pass.CODEC.listOf().optionalFieldOf("passes", List.of()).forGetter(PostEffectPipeline::passes)).apply(instance, PostEffectPipeline::new);
   });

   public PostEffectPipeline(Map map, List list) {
      this.internalTargets = map;
      this.passes = list;
   }

   public Map internalTargets() {
      return this.internalTargets;
   }

   public List passes() {
      return this.passes;
   }

   @Environment(EnvType.CLIENT)
   public static record Targets(Optional width, Optional height, boolean persistent, int clearColor) {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(Codecs.POSITIVE_INT.optionalFieldOf("width").forGetter(Targets::width), Codecs.POSITIVE_INT.optionalFieldOf("height").forGetter(Targets::height), Codec.BOOL.optionalFieldOf("persistent", false).forGetter(Targets::persistent), Codecs.ARGB.optionalFieldOf("clear_color", 0).forGetter(Targets::clearColor)).apply(instance, Targets::new);
      });

      public Targets(Optional optional, Optional optional2, boolean bl, int i) {
         this.width = optional;
         this.height = optional2;
         this.persistent = bl;
         this.clearColor = i;
      }

      public Optional width() {
         return this.width;
      }

      public Optional height() {
         return this.height;
      }

      public boolean persistent() {
         return this.persistent;
      }

      public int clearColor() {
         return this.clearColor;
      }
   }

   @Environment(EnvType.CLIENT)
   public static record Pass(Identifier vertexShaderId, Identifier fragmentShaderId, List inputs, Identifier outputTarget, Map uniforms) {
      private static final Codec INPUTS_CODEC;
      private static final Codec UNIFORMS_CODEC;
      public static final Codec CODEC;

      public Pass(Identifier identifier, Identifier identifier2, List list, Identifier identifier3, Map map) {
         this.vertexShaderId = identifier;
         this.fragmentShaderId = identifier2;
         this.inputs = list;
         this.outputTarget = identifier3;
         this.uniforms = map;
      }

      public Stream streamTargets() {
         Stream stream = this.inputs.stream().flatMap((input) -> {
            return input.getTargetId().stream();
         });
         return Stream.concat(stream, Stream.of(this.outputTarget));
      }

      public Identifier vertexShaderId() {
         return this.vertexShaderId;
      }

      public Identifier fragmentShaderId() {
         return this.fragmentShaderId;
      }

      public List inputs() {
         return this.inputs;
      }

      public Identifier outputTarget() {
         return this.outputTarget;
      }

      public Map uniforms() {
         return this.uniforms;
      }

      static {
         INPUTS_CODEC = PostEffectPipeline.Input.CODEC.listOf().validate((inputs) -> {
            Set set = new ObjectArraySet(inputs.size());
            Iterator var2 = inputs.iterator();

            Input input;
            do {
               if (!var2.hasNext()) {
                  return DataResult.success(inputs);
               }

               input = (Input)var2.next();
            } while(set.add(input.samplerName()));

            return DataResult.error(() -> {
               return "Encountered repeated sampler name: " + input.samplerName();
            });
         });
         UNIFORMS_CODEC = Codec.unboundedMap(Codec.STRING, UniformValue.CODEC.listOf());
         CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(Identifier.CODEC.fieldOf("vertex_shader").forGetter(Pass::vertexShaderId), Identifier.CODEC.fieldOf("fragment_shader").forGetter(Pass::fragmentShaderId), INPUTS_CODEC.optionalFieldOf("inputs", List.of()).forGetter(Pass::inputs), Identifier.CODEC.fieldOf("output").forGetter(Pass::outputTarget), UNIFORMS_CODEC.optionalFieldOf("uniforms", Map.of()).forGetter(Pass::uniforms)).apply(instance, Pass::new);
         });
      }
   }

   @Environment(EnvType.CLIENT)
   public static record TargetSampler(String samplerName, Identifier targetId, boolean useDepthBuffer, boolean bilinear) implements Input {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(Codec.STRING.fieldOf("sampler_name").forGetter(TargetSampler::samplerName), Identifier.CODEC.fieldOf("target").forGetter(TargetSampler::targetId), Codec.BOOL.optionalFieldOf("use_depth_buffer", false).forGetter(TargetSampler::useDepthBuffer), Codec.BOOL.optionalFieldOf("bilinear", false).forGetter(TargetSampler::bilinear)).apply(instance, TargetSampler::new);
      });

      public TargetSampler(String string, Identifier identifier, boolean bl, boolean bl2) {
         this.samplerName = string;
         this.targetId = identifier;
         this.useDepthBuffer = bl;
         this.bilinear = bl2;
      }

      public Set getTargetId() {
         return Set.of(this.targetId);
      }

      public String samplerName() {
         return this.samplerName;
      }

      public Identifier targetId() {
         return this.targetId;
      }

      public boolean useDepthBuffer() {
         return this.useDepthBuffer;
      }

      public boolean bilinear() {
         return this.bilinear;
      }
   }

   @Environment(EnvType.CLIENT)
   public static record TextureSampler(String samplerName, Identifier location, int width, int height, boolean bilinear) implements Input {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(Codec.STRING.fieldOf("sampler_name").forGetter(TextureSampler::samplerName), Identifier.CODEC.fieldOf("location").forGetter(TextureSampler::location), Codecs.POSITIVE_INT.fieldOf("width").forGetter(TextureSampler::width), Codecs.POSITIVE_INT.fieldOf("height").forGetter(TextureSampler::height), Codec.BOOL.optionalFieldOf("bilinear", false).forGetter(TextureSampler::bilinear)).apply(instance, TextureSampler::new);
      });

      public TextureSampler(String string, Identifier identifier, int i, int j, boolean bl) {
         this.samplerName = string;
         this.location = identifier;
         this.width = i;
         this.height = j;
         this.bilinear = bl;
      }

      public Set getTargetId() {
         return Set.of();
      }

      public String samplerName() {
         return this.samplerName;
      }

      public Identifier location() {
         return this.location;
      }

      public int width() {
         return this.width;
      }

      public int height() {
         return this.height;
      }

      public boolean bilinear() {
         return this.bilinear;
      }
   }

   @Environment(EnvType.CLIENT)
   public sealed interface Input permits PostEffectPipeline.TextureSampler, PostEffectPipeline.TargetSampler {
      Codec CODEC = Codec.xor(PostEffectPipeline.TextureSampler.CODEC, PostEffectPipeline.TargetSampler.CODEC).xmap((either) -> {
         return (Input)either.map(Function.identity(), Function.identity());
      }, (sampler) -> {
         Objects.requireNonNull(sampler);
         int i = 0;
         Either var10000;
         switch (sampler.typeSwitch<invokedynamic>(sampler, i)) {
            case 0:
               TextureSampler textureSampler = (TextureSampler)sampler;
               var10000 = Either.left(textureSampler);
               break;
            case 1:
               TargetSampler targetSampler = (TargetSampler)sampler;
               var10000 = Either.right(targetSampler);
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      });

      String samplerName();

      Set getTargetId();
   }
}

package net.minecraft.client.gl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.render.ProjectionMatrix2;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.Handle;
import net.minecraft.client.util.ObjectAllocator;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class PostEffectProcessor implements AutoCloseable {
   public static final Identifier MAIN = Identifier.ofVanilla("main");
   private final List passes;
   private final Map internalTargets;
   private final Set externalTargets;
   private final Map framebuffers = new HashMap();
   private final ProjectionMatrix2 projectionMatrix;

   private PostEffectProcessor(List passes, Map internalTargets, Set externalTargets, ProjectionMatrix2 projectionMatrix) {
      this.passes = passes;
      this.internalTargets = internalTargets;
      this.externalTargets = externalTargets;
      this.projectionMatrix = projectionMatrix;
   }

   public static PostEffectProcessor parseEffect(PostEffectPipeline pipeline, TextureManager textureManager, Set availableExternalTargets, Identifier id, ProjectionMatrix2 projectionMatrix) throws ShaderLoader.LoadException {
      Stream stream = pipeline.passes().stream().flatMap(PostEffectPipeline.Pass::streamTargets);
      Set set = (Set)stream.filter((target) -> {
         return !pipeline.internalTargets().containsKey(target);
      }).collect(Collectors.toSet());
      Set set2 = Sets.difference(set, availableExternalTargets);
      if (!set2.isEmpty()) {
         throw new ShaderLoader.LoadException("Referenced external targets are not available in this context: " + String.valueOf(set2));
      } else {
         ImmutableList.Builder builder = ImmutableList.builder();

         for(int i = 0; i < pipeline.passes().size(); ++i) {
            PostEffectPipeline.Pass pass = (PostEffectPipeline.Pass)pipeline.passes().get(i);
            builder.add(parsePass(textureManager, pass, id.withSuffixedPath("/" + i)));
         }

         return new PostEffectProcessor(builder.build(), pipeline.internalTargets(), set, projectionMatrix);
      }
   }

   private static PostEffectPass parsePass(TextureManager textureManager, PostEffectPipeline.Pass pass, Identifier id) throws ShaderLoader.LoadException {
      RenderPipeline.Builder builder = RenderPipeline.builder(RenderPipelines.POST_EFFECT_PROCESSOR_SNIPPET).withFragmentShader(pass.fragmentShaderId()).withVertexShader(pass.vertexShaderId()).withLocation(id);
      Iterator var4 = pass.inputs().iterator();

      while(var4.hasNext()) {
         PostEffectPipeline.Input input = (PostEffectPipeline.Input)var4.next();
         builder.withSampler(input.samplerName() + "Sampler");
      }

      builder.withUniform("SamplerInfo", UniformType.UNIFORM_BUFFER);
      var4 = pass.uniforms().keySet().iterator();

      while(var4.hasNext()) {
         String string = (String)var4.next();
         builder.withUniform(string, UniformType.UNIFORM_BUFFER);
      }

      RenderPipeline renderPipeline = builder.build();
      List list = new ArrayList();
      Iterator var6 = pass.inputs().iterator();

      while(var6.hasNext()) {
         PostEffectPipeline.Input input2 = (PostEffectPipeline.Input)var6.next();
         Objects.requireNonNull(input2);
         byte var9 = 0;
         boolean var10001;
         Throwable var35;
         String var37;
         Identifier var40;
         boolean var43;
         switch (input2.typeSwitch<invokedynamic>(input2, var9)) {
            case 0:
               PostEffectPipeline.TextureSampler var10 = (PostEffectPipeline.TextureSampler)input2;
               PostEffectPipeline.TextureSampler var44 = var10;

               try {
                  var37 = var44.samplerName();
               } catch (Throwable var30) {
                  var35 = var30;
                  var10001 = false;
                  break;
               }

               String var36 = var37;
               String string2 = var36;
               var44 = var10;

               try {
                  var40 = var44.location();
               } catch (Throwable var29) {
                  var35 = var29;
                  var10001 = false;
                  break;
               }

               Identifier var38 = var40;
               Identifier identifier = var38;
               var44 = var10;

               int var47;
               try {
                  var47 = var44.width();
               } catch (Throwable var28) {
                  var35 = var28;
                  var10001 = false;
                  break;
               }

               int var39 = var47;
               int i = var39;
               var44 = var10;

               try {
                  var47 = var44.height();
               } catch (Throwable var27) {
                  var35 = var27;
                  var10001 = false;
                  break;
               }

               var39 = var47;
               int j = var39;
               var44 = var10;

               try {
                  var43 = var44.bilinear();
               } catch (Throwable var26) {
                  var35 = var26;
                  var10001 = false;
                  break;
               }

               boolean var41 = var43;
               boolean bl = var41;
               AbstractTexture abstractTexture = textureManager.getTexture(identifier.withPath((name) -> {
                  return "textures/effect/" + name + ".png";
               }));
               abstractTexture.setFilter(bl, false);
               list.add(new PostEffectPass.TextureSampler(string2, abstractTexture, i, j));
               continue;
            case 1:
               PostEffectPipeline.TargetSampler var16 = (PostEffectPipeline.TargetSampler)input2;
               PostEffectPipeline.TargetSampler var10000 = var16;

               try {
                  var37 = var10000.samplerName();
               } catch (Throwable var25) {
                  var35 = var25;
                  var10001 = false;
                  break;
               }

               String var21 = var37;
               String string3 = var21;
               var10000 = var16;

               try {
                  var40 = var10000.targetId();
               } catch (Throwable var24) {
                  var35 = var24;
                  var10001 = false;
                  break;
               }

               Identifier var45 = var40;
               Identifier identifier2 = var45;
               var10000 = var16;

               try {
                  var43 = var10000.useDepthBuffer();
               } catch (Throwable var23) {
                  var35 = var23;
                  var10001 = false;
                  break;
               }

               boolean var46 = var43;
               boolean bl2 = var46;
               var10000 = var16;

               try {
                  var43 = var10000.bilinear();
               } catch (Throwable var22) {
                  var35 = var22;
                  var10001 = false;
                  break;
               }

               var46 = var43;
               list.add(new PostEffectPass.TargetSampler(string3, identifier2, bl2, var46));
               continue;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         Throwable var34 = var35;
         throw new MatchException(var34.toString(), var34);
      }

      return new PostEffectPass(renderPipeline, pass.outputTarget(), pass.uniforms(), list);
   }

   public void render(FrameGraphBuilder builder, int textureWidth, int textureHeight, FramebufferSet framebufferSet) {
      GpuBufferSlice gpuBufferSlice = this.projectionMatrix.set((float)textureWidth, (float)textureHeight);
      Map map = new HashMap(this.internalTargets.size() + this.externalTargets.size());
      Iterator var7 = this.externalTargets.iterator();

      Identifier identifier;
      while(var7.hasNext()) {
         identifier = (Identifier)var7.next();
         map.put(identifier, framebufferSet.getOrThrow(identifier));
      }

      var7 = this.internalTargets.entrySet().iterator();

      while(var7.hasNext()) {
         Map.Entry entry = (Map.Entry)var7.next();
         Identifier identifier2 = (Identifier)entry.getKey();
         PostEffectPipeline.Targets targets = (PostEffectPipeline.Targets)entry.getValue();
         SimpleFramebufferFactory simpleFramebufferFactory = new SimpleFramebufferFactory((Integer)targets.width().orElse(textureWidth), (Integer)targets.height().orElse(textureHeight), true, targets.clearColor());
         if (targets.persistent()) {
            Framebuffer framebuffer = this.createFramebuffer(identifier2, simpleFramebufferFactory);
            map.put(identifier2, builder.createObjectNode(identifier2.toString(), framebuffer));
         } else {
            map.put(identifier2, builder.createResourceHandle(identifier2.toString(), simpleFramebufferFactory));
         }
      }

      var7 = this.passes.iterator();

      while(var7.hasNext()) {
         PostEffectPass postEffectPass = (PostEffectPass)var7.next();
         postEffectPass.render(builder, map, gpuBufferSlice);
      }

      var7 = this.externalTargets.iterator();

      while(var7.hasNext()) {
         identifier = (Identifier)var7.next();
         framebufferSet.set(identifier, (Handle)map.get(identifier));
      }

   }

   /** @deprecated */
   @Deprecated
   public void render(Framebuffer framebuffer, ObjectAllocator objectAllocator) {
      FrameGraphBuilder frameGraphBuilder = new FrameGraphBuilder();
      FramebufferSet framebufferSet = PostEffectProcessor.FramebufferSet.singleton(MAIN, frameGraphBuilder.createObjectNode("main", framebuffer));
      this.render(frameGraphBuilder, framebuffer.textureWidth, framebuffer.textureHeight, framebufferSet);
      frameGraphBuilder.run(objectAllocator);
   }

   private Framebuffer createFramebuffer(Identifier id, SimpleFramebufferFactory factory) {
      Framebuffer framebuffer = (Framebuffer)this.framebuffers.get(id);
      if (framebuffer == null || framebuffer.textureWidth != factory.width() || framebuffer.textureHeight != factory.height()) {
         if (framebuffer != null) {
            framebuffer.delete();
         }

         framebuffer = factory.create();
         factory.prepare(framebuffer);
         this.framebuffers.put(id, framebuffer);
      }

      return framebuffer;
   }

   public void close() {
      this.framebuffers.values().forEach(Framebuffer::delete);
      this.framebuffers.clear();
      Iterator var1 = this.passes.iterator();

      while(var1.hasNext()) {
         PostEffectPass postEffectPass = (PostEffectPass)var1.next();
         postEffectPass.close();
      }

   }

   @Environment(EnvType.CLIENT)
   public interface FramebufferSet {
      static FramebufferSet singleton(final Identifier id, final Handle framebuffer) {
         return new FramebufferSet() {
            private Handle framebuffer = framebuffer;

            public void set(Identifier idx, Handle framebufferx) {
               if (idx.equals(id)) {
                  this.framebuffer = framebufferx;
               } else {
                  throw new IllegalArgumentException("No target with id " + String.valueOf(idx));
               }
            }

            @Nullable
            public Handle get(Identifier idx) {
               return idx.equals(id) ? this.framebuffer : null;
            }
         };
      }

      void set(Identifier id, Handle framebuffer);

      @Nullable
      Handle get(Identifier id);

      default Handle getOrThrow(Identifier id) {
         Handle handle = this.get(id);
         if (handle == null) {
            throw new IllegalArgumentException("Missing target with id " + String.valueOf(id));
         } else {
            return handle;
         }
      }
   }
}

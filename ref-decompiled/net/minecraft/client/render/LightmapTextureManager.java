package net.minecraft.client.render;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.OptionalInt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.MappableRingBuffer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.world.dimension.DimensionType;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class LightmapTextureManager implements AutoCloseable {
   public static final int MAX_LIGHT_COORDINATE = 15728880;
   public static final int MAX_SKY_LIGHT_COORDINATE = 15728640;
   public static final int MAX_BLOCK_LIGHT_COORDINATE = 240;
   private static final int field_53098 = 16;
   private static final int UBO_SIZE = (new Std140SizeCalculator()).putFloat().putFloat().putFloat().putInt().putFloat().putFloat().putFloat().putFloat().putVec3().get();
   private final GpuTexture glTexture;
   private final GpuTextureView glTextureView;
   private boolean dirty;
   private float flickerIntensity;
   private final GameRenderer renderer;
   private final MinecraftClient client;
   private final MappableRingBuffer buffer;

   public LightmapTextureManager(GameRenderer renderer, MinecraftClient client) {
      this.renderer = renderer;
      this.client = client;
      GpuDevice gpuDevice = RenderSystem.getDevice();
      this.glTexture = gpuDevice.createTexture((String)"Light Texture", 12, TextureFormat.RGBA8, 16, 16, 1, 1);
      this.glTexture.setTextureFilter(FilterMode.LINEAR, false);
      this.glTextureView = gpuDevice.createTextureView(this.glTexture);
      gpuDevice.createCommandEncoder().clearColorTexture(this.glTexture, -1);
      this.buffer = new MappableRingBuffer(() -> {
         return "Lightmap UBO";
      }, 130, UBO_SIZE);
   }

   public GpuTextureView getGlTextureView() {
      return this.glTextureView;
   }

   public void close() {
      this.glTexture.close();
      this.glTextureView.close();
      this.buffer.close();
   }

   public void tick() {
      this.flickerIntensity += (float)((Math.random() - Math.random()) * Math.random() * Math.random() * 0.1);
      this.flickerIntensity *= 0.9F;
      this.dirty = true;
   }

   public void disable() {
      RenderSystem.setShaderTexture(2, (GpuTextureView)null);
   }

   public void enable() {
      RenderSystem.setShaderTexture(2, this.glTextureView);
   }

   private float getDarkness(LivingEntity entity, float factor, float tickProgress) {
      float f = 0.45F * factor;
      return Math.max(0.0F, MathHelper.cos(((float)entity.age - tickProgress) * 3.1415927F * 0.025F) * f);
   }

   public void update(float tickProgress) {
      if (this.dirty) {
         this.dirty = false;
         Profiler profiler = Profilers.get();
         profiler.push("lightTex");
         ClientWorld clientWorld = this.client.world;
         if (clientWorld != null) {
            float f = clientWorld.getSkyBrightness(1.0F);
            float g;
            if (clientWorld.getLightningTicksLeft() > 0) {
               g = 1.0F;
            } else {
               g = f * 0.95F + 0.05F;
            }

            float h = ((Double)this.client.options.getDarknessEffectScale().getValue()).floatValue();
            float i = this.client.player.getEffectFadeFactor(StatusEffects.DARKNESS, tickProgress) * h;
            float j = this.getDarkness(this.client.player, i, tickProgress) * h;
            float k = this.client.player.getUnderwaterVisibility();
            float l;
            if (this.client.player.hasStatusEffect(StatusEffects.NIGHT_VISION)) {
               l = GameRenderer.getNightVisionStrength(this.client.player, tickProgress);
            } else if (k > 0.0F && this.client.player.hasStatusEffect(StatusEffects.CONDUIT_POWER)) {
               l = k;
            } else {
               l = 0.0F;
            }

            Vector3f vector3f = (new Vector3f(f, f, 1.0F)).lerp(new Vector3f(1.0F, 1.0F, 1.0F), 0.35F);
            float m = this.flickerIntensity + 1.5F;
            float n = clientWorld.getDimension().ambientLight();
            boolean bl = clientWorld.getDimensionEffects().shouldBrightenLighting();
            float o = ((Double)this.client.options.getGamma().getValue()).floatValue();
            RenderSystem.ShapeIndexBuffer shapeIndexBuffer = RenderSystem.getSequentialBuffer(VertexFormat.DrawMode.QUADS);
            GpuBuffer gpuBuffer = shapeIndexBuffer.getIndexBuffer(6);
            CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
            GpuBuffer.MappedView mappedView = commandEncoder.mapBuffer(this.buffer.getBlocking(), false, true);

            try {
               Std140Builder.intoBuffer(mappedView.data()).putFloat(n).putFloat(g).putFloat(m).putInt(bl ? 1 : 0).putFloat(l).putFloat(j).putFloat(this.renderer.getSkyDarkness(tickProgress)).putFloat(Math.max(0.0F, o - i)).putVec3(vector3f);
            } catch (Throwable var25) {
               if (mappedView != null) {
                  try {
                     mappedView.close();
                  } catch (Throwable var22) {
                     var25.addSuppressed(var22);
                  }
               }

               throw var25;
            }

            if (mappedView != null) {
               mappedView.close();
            }

            RenderPass renderPass = commandEncoder.createRenderPass(() -> {
               return "Update light";
            }, this.glTextureView, OptionalInt.empty());

            try {
               renderPass.setPipeline(RenderPipelines.BILT_SCREEN_LIGHTMAP);
               RenderSystem.bindDefaultUniforms(renderPass);
               renderPass.setUniform("LightmapInfo", this.buffer.getBlocking());
               renderPass.setVertexBuffer(0, RenderSystem.getQuadVertexBuffer());
               renderPass.setIndexBuffer(gpuBuffer, shapeIndexBuffer.getIndexType());
               renderPass.drawIndexed(0, 0, 6, 1);
            } catch (Throwable var24) {
               if (renderPass != null) {
                  try {
                     renderPass.close();
                  } catch (Throwable var23) {
                     var24.addSuppressed(var23);
                  }
               }

               throw var24;
            }

            if (renderPass != null) {
               renderPass.close();
            }

            this.buffer.rotate();
            profiler.pop();
         }
      }
   }

   public static float getBrightness(DimensionType type, int lightLevel) {
      return getBrightness(type.ambientLight(), lightLevel);
   }

   public static float getBrightness(float ambientLight, int lightLevel) {
      float f = (float)lightLevel / 15.0F;
      float g = f / (4.0F - 3.0F * f);
      return MathHelper.lerp(ambientLight, g, 1.0F);
   }

   public static int pack(int block, int sky) {
      return block << 4 | sky << 20;
   }

   public static int getBlockLightCoordinates(int light) {
      return light >>> 4 & 15;
   }

   public static int getSkyLightCoordinates(int light) {
      return light >>> 20 & 15;
   }

   public static int applyEmission(int light, int lightEmission) {
      if (lightEmission == 0) {
         return light;
      } else {
         int i = Math.max(getSkyLightCoordinates(light), lightEmission);
         int j = Math.max(getBlockLightCoordinates(light), lightEmission);
         return pack(j, i);
      }
   }
}

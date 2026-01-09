package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.ElderGuardianEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.GuardianEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class ElderGuardianAppearanceParticle extends Particle {
   private final Model model;
   private final RenderLayer layer;

   ElderGuardianAppearanceParticle(ClientWorld clientWorld, double d, double e, double f) {
      super(clientWorld, d, e, f);
      this.layer = RenderLayer.getEntityTranslucent(ElderGuardianEntityRenderer.TEXTURE);
      this.model = new GuardianEntityModel(MinecraftClient.getInstance().getLoadedEntityModels().getModelPart(EntityModelLayers.ELDER_GUARDIAN));
      this.gravityStrength = 0.0F;
      this.maxAge = 30;
   }

   public ParticleTextureSheet getType() {
      return ParticleTextureSheet.CUSTOM;
   }

   public void renderCustom(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Camera camera, float tickProgress) {
      float f = ((float)this.age + tickProgress) / (float)this.maxAge;
      float g = 0.05F + 0.5F * MathHelper.sin(f * 3.1415927F);
      int i = ColorHelper.fromFloats(g, 1.0F, 1.0F, 1.0F);
      matrices.push();
      matrices.multiply(camera.getRotation());
      matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(60.0F - 150.0F * f));
      float h = 0.42553192F;
      matrices.scale(0.42553192F, -0.42553192F, -0.42553192F);
      matrices.translate(0.0F, -0.56F, 3.5F);
      VertexConsumer vertexConsumer = vertexConsumers.getBuffer(this.layer);
      this.model.render(matrices, vertexConsumer, 15728880, OverlayTexture.DEFAULT_UV, i);
      matrices.pop();
   }

   public void render(VertexConsumer vertexConsumer, Camera camera, float tickProgress) {
   }

   @Environment(EnvType.CLIENT)
   public static class Factory implements ParticleFactory {
      public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
         return new ElderGuardianAppearanceParticle(clientWorld, d, e, f);
      }

      // $FF: synthetic method
      public Particle createParticle(final ParticleEffect particleEffect, final ClientWorld clientWorld, final double d, final double e, final double f, final double g, final double h, final double i) {
         return this.createParticle((SimpleParticleType)particleEffect, clientWorld, d, e, f, g, h, i);
      }
   }
}

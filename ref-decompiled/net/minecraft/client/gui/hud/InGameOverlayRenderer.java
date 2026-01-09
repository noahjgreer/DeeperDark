package net.minecraft.client.gui.hud;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.ModelBaker;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class InGameOverlayRenderer {
   private static final Identifier UNDERWATER_TEXTURE = Identifier.ofVanilla("textures/misc/underwater.png");
   private final MinecraftClient client;
   private final VertexConsumerProvider vertexConsumers;
   public static final int field_59969 = 40;
   @Nullable
   private ItemStack floatingItem;
   private int floatingItemTimer;
   private float floatingItemOffsetX;
   private float floatingItemOffsetY;

   public InGameOverlayRenderer(MinecraftClient client, VertexConsumerProvider vertexConsumers) {
      this.client = client;
      this.vertexConsumers = vertexConsumers;
   }

   public void tickFloatingItemTimer() {
      if (this.floatingItemTimer > 0) {
         --this.floatingItemTimer;
         if (this.floatingItemTimer == 0) {
            this.floatingItem = null;
         }
      }

   }

   public void renderOverlays(boolean sleeping, float tickProgress) {
      MatrixStack matrixStack = new MatrixStack();
      PlayerEntity playerEntity = this.client.player;
      if (this.client.options.getPerspective().isFirstPerson() && !sleeping) {
         if (!playerEntity.noClip) {
            BlockState blockState = getInWallBlockState(playerEntity);
            if (blockState != null) {
               renderInWallOverlay(this.client.getBlockRenderManager().getModels().getModelParticleSprite(blockState), matrixStack, this.vertexConsumers);
            }
         }

         if (!this.client.player.isSpectator()) {
            if (this.client.player.isSubmergedIn(FluidTags.WATER)) {
               renderUnderwaterOverlay(this.client, matrixStack, this.vertexConsumers);
            }

            if (this.client.player.isOnFire()) {
               renderFireOverlay(matrixStack, this.vertexConsumers);
            }
         }
      }

      if (!this.client.options.hudHidden) {
         this.renderFloatingItem(matrixStack, tickProgress);
      }

   }

   private void renderFloatingItem(MatrixStack matrices, float tickProgress) {
      if (this.floatingItem != null && this.floatingItemTimer > 0) {
         int i = 40 - this.floatingItemTimer;
         float f = ((float)i + tickProgress) / 40.0F;
         float g = f * f;
         float h = f * g;
         float j = 10.25F * h * g - 24.95F * g * g + 25.5F * h - 13.8F * g + 4.0F * f;
         float k = j * 3.1415927F;
         float l = (float)this.client.getWindow().getFramebufferWidth() / (float)this.client.getWindow().getFramebufferHeight();
         float m = this.floatingItemOffsetX * 0.3F * l;
         float n = this.floatingItemOffsetY * 0.3F;
         matrices.push();
         matrices.translate(m * MathHelper.abs(MathHelper.sin(k * 2.0F)), n * MathHelper.abs(MathHelper.sin(k * 2.0F)), -10.0F + 9.0F * MathHelper.sin(k));
         float o = 0.8F;
         matrices.scale(0.8F, 0.8F, 0.8F);
         matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(900.0F * MathHelper.abs(MathHelper.sin(k))));
         matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(6.0F * MathHelper.cos(f * 8.0F)));
         matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(6.0F * MathHelper.cos(f * 8.0F)));
         this.client.gameRenderer.getDiffuseLighting().setShaderLights(DiffuseLighting.Type.ITEMS_3D);
         this.client.getItemRenderer().renderItem(this.floatingItem, ItemDisplayContext.FIXED, 15728880, OverlayTexture.DEFAULT_UV, matrices, this.vertexConsumers, this.client.world, 0);
         matrices.pop();
      }
   }

   public void clearFloatingItem() {
      this.floatingItem = null;
   }

   public void setFloatingItem(ItemStack stack, Random random) {
      this.floatingItem = stack;
      this.floatingItemTimer = 40;
      this.floatingItemOffsetX = random.nextFloat() * 2.0F - 1.0F;
      this.floatingItemOffsetY = random.nextFloat() * 2.0F - 1.0F;
   }

   @Nullable
   private static BlockState getInWallBlockState(PlayerEntity player) {
      BlockPos.Mutable mutable = new BlockPos.Mutable();

      for(int i = 0; i < 8; ++i) {
         double d = player.getX() + (double)(((float)((i >> 0) % 2) - 0.5F) * player.getWidth() * 0.8F);
         double e = player.getEyeY() + (double)(((float)((i >> 1) % 2) - 0.5F) * 0.1F * player.getScale());
         double f = player.getZ() + (double)(((float)((i >> 2) % 2) - 0.5F) * player.getWidth() * 0.8F);
         mutable.set(d, e, f);
         BlockState blockState = player.getWorld().getBlockState(mutable);
         if (blockState.getRenderType() != BlockRenderType.INVISIBLE && blockState.shouldBlockVision(player.getWorld(), mutable)) {
            return blockState;
         }
      }

      return null;
   }

   private static void renderInWallOverlay(Sprite sprite, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
      float f = 0.1F;
      int i = ColorHelper.fromFloats(1.0F, 0.1F, 0.1F, 0.1F);
      float g = -1.0F;
      float h = 1.0F;
      float j = -1.0F;
      float k = 1.0F;
      float l = -0.5F;
      float m = sprite.getMinU();
      float n = sprite.getMaxU();
      float o = sprite.getMinV();
      float p = sprite.getMaxV();
      Matrix4f matrix4f = matrices.peek().getPositionMatrix();
      VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getBlockScreenEffect(sprite.getAtlasId()));
      vertexConsumer.vertex(matrix4f, -1.0F, -1.0F, -0.5F).texture(n, p).color(i);
      vertexConsumer.vertex(matrix4f, 1.0F, -1.0F, -0.5F).texture(m, p).color(i);
      vertexConsumer.vertex(matrix4f, 1.0F, 1.0F, -0.5F).texture(m, o).color(i);
      vertexConsumer.vertex(matrix4f, -1.0F, 1.0F, -0.5F).texture(n, o).color(i);
   }

   private static void renderUnderwaterOverlay(MinecraftClient client, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
      BlockPos blockPos = BlockPos.ofFloored(client.player.getX(), client.player.getEyeY(), client.player.getZ());
      float f = LightmapTextureManager.getBrightness(client.player.getWorld().getDimension(), client.player.getWorld().getLightLevel(blockPos));
      int i = ColorHelper.fromFloats(0.1F, f, f, f);
      float g = 4.0F;
      float h = -1.0F;
      float j = 1.0F;
      float k = -1.0F;
      float l = 1.0F;
      float m = -0.5F;
      float n = -client.player.getYaw() / 64.0F;
      float o = client.player.getPitch() / 64.0F;
      Matrix4f matrix4f = matrices.peek().getPositionMatrix();
      VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getBlockScreenEffect(UNDERWATER_TEXTURE));
      vertexConsumer.vertex(matrix4f, -1.0F, -1.0F, -0.5F).texture(4.0F + n, 4.0F + o).color(i);
      vertexConsumer.vertex(matrix4f, 1.0F, -1.0F, -0.5F).texture(0.0F + n, 4.0F + o).color(i);
      vertexConsumer.vertex(matrix4f, 1.0F, 1.0F, -0.5F).texture(0.0F + n, 0.0F + o).color(i);
      vertexConsumer.vertex(matrix4f, -1.0F, 1.0F, -0.5F).texture(4.0F + n, 0.0F + o).color(i);
   }

   private static void renderFireOverlay(MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
      Sprite sprite = ModelBaker.FIRE_1.getSprite();
      VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getFireScreenEffect(sprite.getAtlasId()));
      float f = sprite.getMinU();
      float g = sprite.getMaxU();
      float h = (f + g) / 2.0F;
      float i = sprite.getMinV();
      float j = sprite.getMaxV();
      float k = (i + j) / 2.0F;
      float l = sprite.getUvScaleDelta();
      float m = MathHelper.lerp(l, f, h);
      float n = MathHelper.lerp(l, g, h);
      float o = MathHelper.lerp(l, i, k);
      float p = MathHelper.lerp(l, j, k);
      float q = 1.0F;

      for(int r = 0; r < 2; ++r) {
         matrices.push();
         float s = -0.5F;
         float t = 0.5F;
         float u = -0.5F;
         float v = 0.5F;
         float w = -0.5F;
         matrices.translate((float)(-(r * 2 - 1)) * 0.24F, -0.3F, 0.0F);
         matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)(r * 2 - 1) * 10.0F));
         Matrix4f matrix4f = matrices.peek().getPositionMatrix();
         vertexConsumer.vertex(matrix4f, -0.5F, -0.5F, -0.5F).texture(n, p).color(1.0F, 1.0F, 1.0F, 0.9F);
         vertexConsumer.vertex(matrix4f, 0.5F, -0.5F, -0.5F).texture(m, p).color(1.0F, 1.0F, 1.0F, 0.9F);
         vertexConsumer.vertex(matrix4f, 0.5F, 0.5F, -0.5F).texture(m, o).color(1.0F, 1.0F, 1.0F, 0.9F);
         vertexConsumer.vertex(matrix4f, -0.5F, 0.5F, -0.5F).texture(n, o).color(1.0F, 1.0F, 1.0F, 0.9F);
         matrices.pop();
      }

   }
}

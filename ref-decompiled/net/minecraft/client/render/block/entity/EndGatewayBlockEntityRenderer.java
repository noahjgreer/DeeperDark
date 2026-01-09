package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.block.entity.EndPortalBlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class EndGatewayBlockEntityRenderer extends EndPortalBlockEntityRenderer {
   private static final Identifier BEAM_TEXTURE = Identifier.ofVanilla("textures/entity/end_gateway_beam.png");

   public EndGatewayBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
      super(context);
   }

   public void render(EndGatewayBlockEntity endGatewayBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, Vec3d vec3d) {
      if (endGatewayBlockEntity.isRecentlyGenerated() || endGatewayBlockEntity.needsCooldownBeforeTeleporting()) {
         float g = endGatewayBlockEntity.isRecentlyGenerated() ? endGatewayBlockEntity.getRecentlyGeneratedBeamHeight(f) : endGatewayBlockEntity.getCooldownBeamHeight(f);
         double d = endGatewayBlockEntity.isRecentlyGenerated() ? (double)endGatewayBlockEntity.getWorld().getTopYInclusive() : 50.0;
         g = MathHelper.sin(g * 3.1415927F);
         int k = MathHelper.floor((double)g * d);
         int l = endGatewayBlockEntity.isRecentlyGenerated() ? DyeColor.MAGENTA.getEntityColor() : DyeColor.PURPLE.getEntityColor();
         long m = endGatewayBlockEntity.getWorld().getTime();
         BeaconBlockEntityRenderer.renderBeam(matrixStack, vertexConsumerProvider, BEAM_TEXTURE, f, g, m, -k, k * 2, l, 0.15F, 0.175F);
      }

      super.render((EndPortalBlockEntity)endGatewayBlockEntity, f, matrixStack, vertexConsumerProvider, i, j, vec3d);
   }

   protected float getTopYOffset() {
      return 1.0F;
   }

   protected float getBottomYOffset() {
      return 0.0F;
   }

   protected RenderLayer getLayer() {
      return RenderLayer.getEndGateway();
   }

   public int getRenderDistance() {
      return 256;
   }
}

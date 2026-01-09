package net.minecraft.client.gui.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.render.state.special.EntityGuiElementRenderState;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class EntityGuiElementRenderer extends SpecialGuiElementRenderer {
   private final EntityRenderDispatcher entityRenderDispatcher;

   public EntityGuiElementRenderer(VertexConsumerProvider.Immediate vertexConsumers, EntityRenderDispatcher entityRenderDispatcher) {
      super(vertexConsumers);
      this.entityRenderDispatcher = entityRenderDispatcher;
   }

   public Class getElementClass() {
      return EntityGuiElementRenderState.class;
   }

   protected void render(EntityGuiElementRenderState entityGuiElementRenderState, MatrixStack matrixStack) {
      MinecraftClient.getInstance().gameRenderer.getDiffuseLighting().setShaderLights(DiffuseLighting.Type.ENTITY_IN_UI);
      Vector3f vector3f = entityGuiElementRenderState.translation();
      matrixStack.translate(vector3f.x, vector3f.y, vector3f.z);
      matrixStack.multiply(entityGuiElementRenderState.rotation());
      Quaternionf quaternionf = entityGuiElementRenderState.overrideCameraAngle();
      if (quaternionf != null) {
         this.entityRenderDispatcher.setRotation(quaternionf.conjugate(new Quaternionf()).rotateY(3.1415927F));
      }

      this.entityRenderDispatcher.setRenderShadows(false);
      this.entityRenderDispatcher.render(entityGuiElementRenderState.renderState(), 0.0, 0.0, 0.0, matrixStack, this.vertexConsumers, 15728880);
      this.entityRenderDispatcher.setRenderShadows(true);
   }

   protected float getYOffset(int height, int windowScaleFactor) {
      return (float)height / 2.0F;
   }

   protected String getName() {
      return "entity";
   }
}

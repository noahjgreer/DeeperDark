package net.minecraft.client.render.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.state.BlockDisplayEntityRenderState;
import net.minecraft.client.render.entity.state.DisplayEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.ItemDisplayEntityRenderState;
import net.minecraft.client.render.entity.state.TextDisplayEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

@Environment(EnvType.CLIENT)
public abstract class DisplayEntityRenderer extends EntityRenderer {
   private final EntityRenderDispatcher renderDispatcher;

   protected DisplayEntityRenderer(EntityRendererFactory.Context context) {
      super(context);
      this.renderDispatcher = context.getRenderDispatcher();
   }

   protected Box getBoundingBox(DisplayEntity displayEntity) {
      return displayEntity.getVisibilityBoundingBox();
   }

   protected boolean canBeCulled(DisplayEntity displayEntity) {
      return displayEntity.shouldRender();
   }

   private static int getBrightnessOverride(DisplayEntity entity) {
      DisplayEntity.RenderState renderState = entity.getRenderState();
      return renderState != null ? renderState.brightnessOverride() : -1;
   }

   protected int getSkyLight(DisplayEntity displayEntity, BlockPos blockPos) {
      int i = getBrightnessOverride(displayEntity);
      return i != -1 ? LightmapTextureManager.getSkyLightCoordinates(i) : super.getSkyLight(displayEntity, blockPos);
   }

   protected int getBlockLight(DisplayEntity displayEntity, BlockPos blockPos) {
      int i = getBrightnessOverride(displayEntity);
      return i != -1 ? LightmapTextureManager.getBlockLightCoordinates(i) : super.getBlockLight(displayEntity, blockPos);
   }

   protected float getShadowRadius(DisplayEntityRenderState displayEntityRenderState) {
      DisplayEntity.RenderState renderState = displayEntityRenderState.displayRenderState;
      return renderState == null ? 0.0F : renderState.shadowRadius().lerp(displayEntityRenderState.lerpProgress);
   }

   protected float getShadowOpacity(DisplayEntityRenderState displayEntityRenderState) {
      DisplayEntity.RenderState renderState = displayEntityRenderState.displayRenderState;
      return renderState == null ? 0.0F : renderState.shadowStrength().lerp(displayEntityRenderState.lerpProgress);
   }

   public void render(DisplayEntityRenderState displayEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
      DisplayEntity.RenderState renderState = displayEntityRenderState.displayRenderState;
      if (renderState != null && displayEntityRenderState.canRender()) {
         float f = displayEntityRenderState.lerpProgress;
         super.render(displayEntityRenderState, matrixStack, vertexConsumerProvider, i);
         matrixStack.push();
         matrixStack.multiply(this.getBillboardRotation(renderState, displayEntityRenderState, new Quaternionf()));
         AffineTransformation affineTransformation = (AffineTransformation)renderState.transformation().interpolate(f);
         matrixStack.multiplyPositionMatrix(affineTransformation.getMatrix());
         this.render(displayEntityRenderState, matrixStack, vertexConsumerProvider, i, f);
         matrixStack.pop();
      }
   }

   private Quaternionf getBillboardRotation(DisplayEntity.RenderState renderState, DisplayEntityRenderState state, Quaternionf quaternionf) {
      Camera camera = this.renderDispatcher.camera;
      Quaternionf var10000;
      switch (renderState.billboardConstraints()) {
         case FIXED:
            var10000 = quaternionf.rotationYXZ(-0.017453292F * state.yaw, 0.017453292F * state.pitch, 0.0F);
            break;
         case HORIZONTAL:
            var10000 = quaternionf.rotationYXZ(-0.017453292F * state.yaw, 0.017453292F * getNegatedPitch(camera), 0.0F);
            break;
         case VERTICAL:
            var10000 = quaternionf.rotationYXZ(-0.017453292F * getBackwardsYaw(camera), 0.017453292F * state.pitch, 0.0F);
            break;
         case CENTER:
            var10000 = quaternionf.rotationYXZ(-0.017453292F * getBackwardsYaw(camera), 0.017453292F * getNegatedPitch(camera), 0.0F);
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   private static float getBackwardsYaw(Camera camera) {
      return camera.getYaw() - 180.0F;
   }

   private static float getNegatedPitch(Camera camera) {
      return -camera.getPitch();
   }

   private static float lerpYaw(DisplayEntity entity, float deltaTicks) {
      return entity.getLerpedYaw(deltaTicks);
   }

   private static float lerpPitch(DisplayEntity entity, float deltaTicks) {
      return entity.getLerpedPitch(deltaTicks);
   }

   protected abstract void render(DisplayEntityRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float tickProgress);

   public void updateRenderState(DisplayEntity displayEntity, DisplayEntityRenderState displayEntityRenderState, float f) {
      super.updateRenderState(displayEntity, displayEntityRenderState, f);
      displayEntityRenderState.displayRenderState = displayEntity.getRenderState();
      displayEntityRenderState.lerpProgress = displayEntity.getLerpProgress(f);
      displayEntityRenderState.yaw = lerpYaw(displayEntity, f);
      displayEntityRenderState.pitch = lerpPitch(displayEntity, f);
   }

   // $FF: synthetic method
   protected float getShadowRadius(final EntityRenderState state) {
      return this.getShadowRadius((DisplayEntityRenderState)state);
   }

   // $FF: synthetic method
   protected int getBlockLight(final Entity entity, final BlockPos pos) {
      return this.getBlockLight((DisplayEntity)entity, pos);
   }

   // $FF: synthetic method
   protected int getSkyLight(final Entity entity, final BlockPos pos) {
      return this.getSkyLight((DisplayEntity)entity, pos);
   }

   @Environment(EnvType.CLIENT)
   public static class TextDisplayEntityRenderer extends DisplayEntityRenderer {
      private final TextRenderer displayTextRenderer;

      protected TextDisplayEntityRenderer(EntityRendererFactory.Context context) {
         super(context);
         this.displayTextRenderer = context.getTextRenderer();
      }

      public TextDisplayEntityRenderState createRenderState() {
         return new TextDisplayEntityRenderState();
      }

      public void updateRenderState(DisplayEntity.TextDisplayEntity textDisplayEntity, TextDisplayEntityRenderState textDisplayEntityRenderState, float f) {
         super.updateRenderState((DisplayEntity)textDisplayEntity, (DisplayEntityRenderState)textDisplayEntityRenderState, f);
         textDisplayEntityRenderState.data = textDisplayEntity.getData();
         textDisplayEntityRenderState.textLines = textDisplayEntity.splitLines(this::getLines);
      }

      private DisplayEntity.TextDisplayEntity.TextLines getLines(Text text, int width) {
         List list = this.displayTextRenderer.wrapLines(text, width);
         List list2 = new ArrayList(list.size());
         int i = 0;
         Iterator var6 = list.iterator();

         while(var6.hasNext()) {
            OrderedText orderedText = (OrderedText)var6.next();
            int j = this.displayTextRenderer.getWidth(orderedText);
            i = Math.max(i, j);
            list2.add(new DisplayEntity.TextDisplayEntity.TextLine(orderedText, j));
         }

         return new DisplayEntity.TextDisplayEntity.TextLines(list2, i);
      }

      public void render(TextDisplayEntityRenderState textDisplayEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, float f) {
         DisplayEntity.TextDisplayEntity.Data data = textDisplayEntityRenderState.data;
         byte b = data.flags();
         boolean bl = (b & 2) != 0;
         boolean bl2 = (b & 4) != 0;
         boolean bl3 = (b & 1) != 0;
         DisplayEntity.TextDisplayEntity.TextAlignment textAlignment = DisplayEntity.TextDisplayEntity.getAlignment(b);
         byte c = (byte)data.textOpacity().lerp(f);
         int j;
         float g;
         if (bl2) {
            g = MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25F);
            j = (int)(g * 255.0F) << 24;
         } else {
            j = data.backgroundColor().lerp(f);
         }

         g = 0.0F;
         Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
         matrix4f.rotate(3.1415927F, 0.0F, 1.0F, 0.0F);
         matrix4f.scale(-0.025F, -0.025F, -0.025F);
         DisplayEntity.TextDisplayEntity.TextLines textLines = textDisplayEntityRenderState.textLines;
         int k = true;
         Objects.requireNonNull(this.displayTextRenderer);
         int l = 9 + 1;
         int m = textLines.width();
         int n = textLines.lines().size() * l - 1;
         matrix4f.translate(1.0F - (float)m / 2.0F, (float)(-n), 0.0F);
         if (j != 0) {
            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(bl ? RenderLayer.getTextBackgroundSeeThrough() : RenderLayer.getTextBackground());
            vertexConsumer.vertex(matrix4f, -1.0F, -1.0F, 0.0F).color(j).light(i);
            vertexConsumer.vertex(matrix4f, -1.0F, (float)n, 0.0F).color(j).light(i);
            vertexConsumer.vertex(matrix4f, (float)m, (float)n, 0.0F).color(j).light(i);
            vertexConsumer.vertex(matrix4f, (float)m, -1.0F, 0.0F).color(j).light(i);
         }

         for(Iterator var24 = textLines.lines().iterator(); var24.hasNext(); g += (float)l) {
            DisplayEntity.TextDisplayEntity.TextLine textLine = (DisplayEntity.TextDisplayEntity.TextLine)var24.next();
            float var10000;
            switch (textAlignment) {
               case LEFT:
                  var10000 = 0.0F;
                  break;
               case RIGHT:
                  var10000 = (float)(m - textLine.width());
                  break;
               case CENTER:
                  var10000 = (float)m / 2.0F - (float)textLine.width() / 2.0F;
                  break;
               default:
                  throw new MatchException((String)null, (Throwable)null);
            }

            float h = var10000;
            this.displayTextRenderer.draw((OrderedText)textLine.contents(), h, g, c << 24 | 16777215, bl3, matrix4f, vertexConsumerProvider, bl ? TextRenderer.TextLayerType.SEE_THROUGH : TextRenderer.TextLayerType.POLYGON_OFFSET, 0, i);
         }

      }

      // $FF: synthetic method
      public EntityRenderState createRenderState() {
         return this.createRenderState();
      }

      // $FF: synthetic method
      protected float getShadowRadius(final EntityRenderState state) {
         return super.getShadowRadius((DisplayEntityRenderState)state);
      }

      // $FF: synthetic method
      protected int getBlockLight(final Entity entity, final BlockPos pos) {
         return super.getBlockLight((DisplayEntity)entity, pos);
      }

      // $FF: synthetic method
      protected int getSkyLight(final Entity entity, final BlockPos pos) {
         return super.getSkyLight((DisplayEntity)entity, pos);
      }
   }

   @Environment(EnvType.CLIENT)
   public static class ItemDisplayEntityRenderer extends DisplayEntityRenderer {
      private final ItemModelManager itemModelManager;

      protected ItemDisplayEntityRenderer(EntityRendererFactory.Context context) {
         super(context);
         this.itemModelManager = context.getItemModelManager();
      }

      public ItemDisplayEntityRenderState createRenderState() {
         return new ItemDisplayEntityRenderState();
      }

      public void updateRenderState(DisplayEntity.ItemDisplayEntity itemDisplayEntity, ItemDisplayEntityRenderState itemDisplayEntityRenderState, float f) {
         super.updateRenderState((DisplayEntity)itemDisplayEntity, (DisplayEntityRenderState)itemDisplayEntityRenderState, f);
         DisplayEntity.ItemDisplayEntity.Data data = itemDisplayEntity.getData();
         if (data != null) {
            this.itemModelManager.updateForNonLivingEntity(itemDisplayEntityRenderState.itemRenderState, data.itemStack(), data.itemTransform(), itemDisplayEntity);
         } else {
            itemDisplayEntityRenderState.itemRenderState.clear();
         }

      }

      public void render(ItemDisplayEntityRenderState itemDisplayEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, float f) {
         if (!itemDisplayEntityRenderState.itemRenderState.isEmpty()) {
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotation(3.1415927F));
            itemDisplayEntityRenderState.itemRenderState.render(matrixStack, vertexConsumerProvider, i, OverlayTexture.DEFAULT_UV);
         }
      }

      // $FF: synthetic method
      public EntityRenderState createRenderState() {
         return this.createRenderState();
      }

      // $FF: synthetic method
      protected float getShadowRadius(final EntityRenderState state) {
         return super.getShadowRadius((DisplayEntityRenderState)state);
      }

      // $FF: synthetic method
      protected int getBlockLight(final Entity entity, final BlockPos pos) {
         return super.getBlockLight((DisplayEntity)entity, pos);
      }

      // $FF: synthetic method
      protected int getSkyLight(final Entity entity, final BlockPos pos) {
         return super.getSkyLight((DisplayEntity)entity, pos);
      }
   }

   @Environment(EnvType.CLIENT)
   public static class BlockDisplayEntityRenderer extends DisplayEntityRenderer {
      private final BlockRenderManager blockRenderManager;

      protected BlockDisplayEntityRenderer(EntityRendererFactory.Context context) {
         super(context);
         this.blockRenderManager = context.getBlockRenderManager();
      }

      public BlockDisplayEntityRenderState createRenderState() {
         return new BlockDisplayEntityRenderState();
      }

      public void updateRenderState(DisplayEntity.BlockDisplayEntity blockDisplayEntity, BlockDisplayEntityRenderState blockDisplayEntityRenderState, float f) {
         super.updateRenderState((DisplayEntity)blockDisplayEntity, (DisplayEntityRenderState)blockDisplayEntityRenderState, f);
         blockDisplayEntityRenderState.data = blockDisplayEntity.getData();
      }

      public void render(BlockDisplayEntityRenderState blockDisplayEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, float f) {
         this.blockRenderManager.renderBlockAsEntity(blockDisplayEntityRenderState.data.blockState(), matrixStack, vertexConsumerProvider, i, OverlayTexture.DEFAULT_UV);
      }

      // $FF: synthetic method
      public EntityRenderState createRenderState() {
         return this.createRenderState();
      }

      // $FF: synthetic method
      protected float getShadowRadius(final EntityRenderState state) {
         return super.getShadowRadius((DisplayEntityRenderState)state);
      }

      // $FF: synthetic method
      protected int getBlockLight(final Entity entity, final BlockPos pos) {
         return super.getBlockLight((DisplayEntity)entity, pos);
      }

      // $FF: synthetic method
      protected int getSkyLight(final Entity entity, final BlockPos pos) {
         return super.getSkyLight((DisplayEntity)entity, pos);
      }
   }
}

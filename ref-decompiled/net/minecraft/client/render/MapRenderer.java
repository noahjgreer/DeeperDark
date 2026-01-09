package net.minecraft.client.render;

import java.util.Iterator;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.texture.MapDecorationsAtlasManager;
import net.minecraft.client.texture.MapTextureManager;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.item.map.MapDecoration;
import net.minecraft.item.map.MapState;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class MapRenderer {
   private static final float field_53102 = -0.01F;
   private static final float field_53103 = -0.001F;
   public static final int DEFAULT_IMAGE_WIDTH = 128;
   public static final int DEFAULT_IMAGE_HEIGHT = 128;
   private final MapTextureManager textureManager;
   private final MapDecorationsAtlasManager decorationsAtlasManager;

   public MapRenderer(MapDecorationsAtlasManager decorationsAtlasManager, MapTextureManager textureManager) {
      this.decorationsAtlasManager = decorationsAtlasManager;
      this.textureManager = textureManager;
   }

   public void draw(MapRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, boolean bl, int light) {
      Matrix4f matrix4f = matrices.peek().getPositionMatrix();
      VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getText(state.texture));
      vertexConsumer.vertex(matrix4f, 0.0F, 128.0F, -0.01F).color(-1).texture(0.0F, 1.0F).light(light);
      vertexConsumer.vertex(matrix4f, 128.0F, 128.0F, -0.01F).color(-1).texture(1.0F, 1.0F).light(light);
      vertexConsumer.vertex(matrix4f, 128.0F, 0.0F, -0.01F).color(-1).texture(1.0F, 0.0F).light(light);
      vertexConsumer.vertex(matrix4f, 0.0F, 0.0F, -0.01F).color(-1).texture(0.0F, 0.0F).light(light);
      int i = 0;
      Iterator var9 = state.decorations.iterator();

      while(true) {
         MapRenderState.Decoration decoration;
         do {
            if (!var9.hasNext()) {
               return;
            }

            decoration = (MapRenderState.Decoration)var9.next();
         } while(bl && !decoration.alwaysRendered);

         matrices.push();
         matrices.translate((float)decoration.x / 2.0F + 64.0F, (float)decoration.z / 2.0F + 64.0F, -0.02F);
         matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float)(decoration.rotation * 360) / 16.0F));
         matrices.scale(4.0F, 4.0F, 3.0F);
         matrices.translate(-0.125F, 0.125F, 0.0F);
         Matrix4f matrix4f2 = matrices.peek().getPositionMatrix();
         Sprite sprite = decoration.sprite;
         if (sprite != null) {
            VertexConsumer vertexConsumer2 = vertexConsumers.getBuffer(RenderLayer.getText(sprite.getAtlasId()));
            vertexConsumer2.vertex(matrix4f2, -1.0F, 1.0F, (float)i * -0.001F).color(-1).texture(sprite.getMinU(), sprite.getMinV()).light(light);
            vertexConsumer2.vertex(matrix4f2, 1.0F, 1.0F, (float)i * -0.001F).color(-1).texture(sprite.getMaxU(), sprite.getMinV()).light(light);
            vertexConsumer2.vertex(matrix4f2, 1.0F, -1.0F, (float)i * -0.001F).color(-1).texture(sprite.getMaxU(), sprite.getMaxV()).light(light);
            vertexConsumer2.vertex(matrix4f2, -1.0F, -1.0F, (float)i * -0.001F).color(-1).texture(sprite.getMinU(), sprite.getMaxV()).light(light);
            matrices.pop();
         }

         if (decoration.name != null) {
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            float f = (float)textRenderer.getWidth((StringVisitable)decoration.name);
            float var10000 = 25.0F / f;
            Objects.requireNonNull(textRenderer);
            float g = MathHelper.clamp(var10000, 0.0F, 6.0F / 9.0F);
            matrices.push();
            matrices.translate((float)decoration.x / 2.0F + 64.0F - f * g / 2.0F, (float)decoration.z / 2.0F + 64.0F + 4.0F, -0.025F);
            matrices.scale(g, g, -1.0F);
            matrices.translate(0.0F, 0.0F, 0.1F);
            textRenderer.draw((Text)decoration.name, 0.0F, 0.0F, -1, false, matrices.peek().getPositionMatrix(), vertexConsumers, TextRenderer.TextLayerType.NORMAL, Integer.MIN_VALUE, light);
            matrices.pop();
         }

         ++i;
      }
   }

   public void update(MapIdComponent mapId, MapState mapState, MapRenderState renderState) {
      renderState.texture = this.textureManager.getTextureId(mapId, mapState);
      renderState.decorations.clear();
      Iterator var4 = mapState.getDecorations().iterator();

      while(var4.hasNext()) {
         MapDecoration mapDecoration = (MapDecoration)var4.next();
         renderState.decorations.add(this.createDecoration(mapDecoration));
      }

   }

   private MapRenderState.Decoration createDecoration(MapDecoration decoration) {
      MapRenderState.Decoration decoration2 = new MapRenderState.Decoration();
      decoration2.sprite = this.decorationsAtlasManager.getSprite(decoration);
      decoration2.x = decoration.x();
      decoration2.z = decoration.z();
      decoration2.rotation = decoration.rotation();
      decoration2.name = (Text)decoration.name().orElse((Object)null);
      decoration2.alwaysRendered = decoration.isAlwaysRendered();
      return decoration2;
   }
}

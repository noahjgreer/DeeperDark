/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.font.TextRenderer$TextLayerType
 *  net.minecraft.client.render.MapRenderState
 *  net.minecraft.client.render.MapRenderState$Decoration
 *  net.minecraft.client.render.MapRenderer
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.texture.AtlasManager
 *  net.minecraft.client.texture.MapTextureManager
 *  net.minecraft.client.texture.Sprite
 *  net.minecraft.client.texture.SpriteAtlasTexture
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.component.type.MapIdComponent
 *  net.minecraft.item.map.MapDecoration
 *  net.minecraft.item.map.MapState
 *  net.minecraft.text.StringVisitable
 *  net.minecraft.util.Atlases
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.RotationAxis
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.render;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.MapRenderState;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.texture.AtlasManager;
import net.minecraft.client.texture.MapTextureManager;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.item.map.MapDecoration;
import net.minecraft.item.map.MapState;
import net.minecraft.text.StringVisitable;
import net.minecraft.util.Atlases;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class MapRenderer {
    private static final float field_53102 = -0.01f;
    private static final float field_53103 = -0.001f;
    public static final int DEFAULT_IMAGE_WIDTH = 128;
    public static final int DEFAULT_IMAGE_HEIGHT = 128;
    private final SpriteAtlasTexture decorationsAtlasManager;
    private final MapTextureManager textureManager;

    public MapRenderer(AtlasManager decorationsAtlasManager, MapTextureManager textureManager) {
        this.decorationsAtlasManager = decorationsAtlasManager.getAtlasTexture(Atlases.MAP_DECORATIONS);
        this.textureManager = textureManager;
    }

    public void draw(MapRenderState state, MatrixStack matrices, OrderedRenderCommandQueue queue, boolean renderDecorations, int light) {
        queue.submitCustom(matrices, RenderLayers.text((Identifier)state.texture), (matrix, vertexConsumer) -> {
            vertexConsumer.vertex(matrix, 0.0f, 128.0f, -0.01f).color(-1).texture(0.0f, 1.0f).light(light);
            vertexConsumer.vertex(matrix, 128.0f, 128.0f, -0.01f).color(-1).texture(1.0f, 1.0f).light(light);
            vertexConsumer.vertex(matrix, 128.0f, 0.0f, -0.01f).color(-1).texture(1.0f, 0.0f).light(light);
            vertexConsumer.vertex(matrix, 0.0f, 0.0f, -0.01f).color(-1).texture(0.0f, 0.0f).light(light);
        });
        int i = 0;
        for (MapRenderState.Decoration decoration : state.decorations) {
            if (renderDecorations && !decoration.alwaysRendered) continue;
            matrices.push();
            matrices.translate((float)decoration.x / 2.0f + 64.0f, (float)decoration.z / 2.0f + 64.0f, -0.02f);
            matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees((float)(decoration.rotation * 360) / 16.0f));
            matrices.scale(4.0f, 4.0f, 3.0f);
            matrices.translate(-0.125f, 0.125f, 0.0f);
            Sprite sprite = decoration.sprite;
            if (sprite != null) {
                float f = (float)i * -0.001f;
                queue.submitCustom(matrices, RenderLayers.text((Identifier)sprite.getAtlasId()), (matrix, vertexConsumer) -> {
                    vertexConsumer.vertex(matrix, -1.0f, 1.0f, f).color(-1).texture(sprite.getMinU(), sprite.getMinV()).light(light);
                    vertexConsumer.vertex(matrix, 1.0f, 1.0f, f).color(-1).texture(sprite.getMaxU(), sprite.getMinV()).light(light);
                    vertexConsumer.vertex(matrix, 1.0f, -1.0f, f).color(-1).texture(sprite.getMaxU(), sprite.getMaxV()).light(light);
                    vertexConsumer.vertex(matrix, -1.0f, -1.0f, f).color(-1).texture(sprite.getMinU(), sprite.getMaxV()).light(light);
                });
                matrices.pop();
            }
            if (decoration.name != null) {
                TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
                float g = textRenderer.getWidth((StringVisitable)decoration.name);
                float f = 25.0f / g;
                Objects.requireNonNull(textRenderer);
                float h = MathHelper.clamp((float)f, (float)0.0f, (float)(6.0f / 9.0f));
                matrices.push();
                matrices.translate((float)decoration.x / 2.0f + 64.0f - g * h / 2.0f, (float)decoration.z / 2.0f + 64.0f + 4.0f, -0.025f);
                matrices.scale(h, h, -1.0f);
                matrices.translate(0.0f, 0.0f, 0.1f);
                queue.getBatchingQueue(1).submitText(matrices, 0.0f, 0.0f, decoration.name.asOrderedText(), false, TextRenderer.TextLayerType.NORMAL, light, -1, Integer.MIN_VALUE, 0);
                matrices.pop();
            }
            ++i;
        }
    }

    public void update(MapIdComponent mapId, MapState mapState, MapRenderState renderState) {
        renderState.texture = this.textureManager.getTextureId(mapId, mapState);
        renderState.decorations.clear();
        for (MapDecoration mapDecoration : mapState.getDecorations()) {
            renderState.decorations.add(this.createDecoration(mapDecoration));
        }
    }

    private MapRenderState.Decoration createDecoration(MapDecoration decoration) {
        MapRenderState.Decoration decoration2 = new MapRenderState.Decoration();
        decoration2.sprite = this.decorationsAtlasManager.getSprite(decoration.getAssetId());
        decoration2.x = decoration.x();
        decoration2.z = decoration.z();
        decoration2.rotation = decoration.rotation();
        decoration2.name = decoration.name().orElse(null);
        decoration2.alwaysRendered = decoration.isAlwaysRendered();
        return decoration2;
    }
}


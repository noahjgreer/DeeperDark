/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.render.OverlayVertexConsumer
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.client.render.TexturedRenderLayers
 *  net.minecraft.client.render.VertexConsumer
 *  net.minecraft.client.render.VertexConsumerProvider
 *  net.minecraft.client.render.VertexConsumers
 *  net.minecraft.client.render.item.ItemRenderState$Glint
 *  net.minecraft.client.render.item.ItemRenderer
 *  net.minecraft.client.render.model.BakedQuad
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.util.math.MatrixStack$Entry
 *  net.minecraft.item.ItemDisplayContext
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.ColorHelper
 *  net.minecraft.util.math.MatrixUtil
 *  org.joml.Matrix4f
 */
package net.minecraft.client.render.item;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayVertexConsumer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumers;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MatrixUtil;
import org.joml.Matrix4f;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class ItemRenderer {
    public static final Identifier ENTITY_ENCHANTMENT_GLINT = Identifier.ofVanilla((String)"textures/misc/enchanted_glint_armor.png");
    public static final Identifier ITEM_ENCHANTMENT_GLINT = Identifier.ofVanilla((String)"textures/misc/enchanted_glint_item.png");
    public static final float field_60154 = 0.5f;
    public static final float field_60155 = 0.75f;
    public static final float field_60156 = 0.0078125f;
    public static final int NO_TINT = -1;

    public static void renderItem(ItemDisplayContext displayContext, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, int[] tints, List<BakedQuad> quads, RenderLayer layer, ItemRenderState.Glint glint) {
        VertexConsumer vertexConsumer;
        if (glint == ItemRenderState.Glint.SPECIAL) {
            MatrixStack.Entry entry = matrices.peek().copy();
            if (displayContext == ItemDisplayContext.GUI) {
                MatrixUtil.scale((Matrix4f)entry.getPositionMatrix(), (float)0.5f);
            } else if (displayContext.isFirstPerson()) {
                MatrixUtil.scale((Matrix4f)entry.getPositionMatrix(), (float)0.75f);
            }
            vertexConsumer = ItemRenderer.getSpecialItemGlintConsumer((VertexConsumerProvider)vertexConsumers, (RenderLayer)layer, (MatrixStack.Entry)entry);
        } else {
            vertexConsumer = ItemRenderer.getItemGlintConsumer((VertexConsumerProvider)vertexConsumers, (RenderLayer)layer, (boolean)true, (glint != ItemRenderState.Glint.NONE ? 1 : 0) != 0);
        }
        ItemRenderer.renderBakedItemQuads((MatrixStack)matrices, (VertexConsumer)vertexConsumer, quads, (int[])tints, (int)light, (int)overlay);
    }

    private static VertexConsumer getSpecialItemGlintConsumer(VertexConsumerProvider consumers, RenderLayer layer, MatrixStack.Entry matrix) {
        return VertexConsumers.union((VertexConsumer)new OverlayVertexConsumer(consumers.getBuffer(ItemRenderer.useTransparentGlint((RenderLayer)layer) ? RenderLayers.glintTranslucent() : RenderLayers.glint()), matrix, 0.0078125f), (VertexConsumer)consumers.getBuffer(layer));
    }

    public static VertexConsumer getItemGlintConsumer(VertexConsumerProvider vertexConsumers, RenderLayer layer, boolean solid, boolean glint) {
        if (glint) {
            if (ItemRenderer.useTransparentGlint((RenderLayer)layer)) {
                return VertexConsumers.union((VertexConsumer)vertexConsumers.getBuffer(RenderLayers.glintTranslucent()), (VertexConsumer)vertexConsumers.getBuffer(layer));
            }
            return VertexConsumers.union((VertexConsumer)vertexConsumers.getBuffer(solid ? RenderLayers.glint() : RenderLayers.entityGlint()), (VertexConsumer)vertexConsumers.getBuffer(layer));
        }
        return vertexConsumers.getBuffer(layer);
    }

    public static List<RenderLayer> getGlintRenderLayers(RenderLayer renderLayer, boolean solid, boolean glint) {
        if (glint) {
            if (ItemRenderer.useTransparentGlint((RenderLayer)renderLayer)) {
                return List.of(renderLayer, RenderLayers.glintTranslucent());
            }
            return List.of(renderLayer, solid ? RenderLayers.glint() : RenderLayers.entityGlint());
        }
        return List.of(renderLayer);
    }

    private static boolean useTransparentGlint(RenderLayer renderLayer) {
        return MinecraftClient.usesImprovedTransparency() && (renderLayer == TexturedRenderLayers.getItemTranslucentCull() || renderLayer == TexturedRenderLayers.getBlockTranslucentCull());
    }

    private static int getTint(int[] tints, int index) {
        if (index < 0 || index >= tints.length) {
            return -1;
        }
        return tints[index];
    }

    private static void renderBakedItemQuads(MatrixStack matrices, VertexConsumer vertexConsumer, List<BakedQuad> quads, int[] tints, int light, int overlay) {
        MatrixStack.Entry entry = matrices.peek();
        for (BakedQuad bakedQuad : quads) {
            float j;
            float h;
            float g;
            float f;
            if (bakedQuad.hasTint()) {
                int i = ItemRenderer.getTint((int[])tints, (int)bakedQuad.tintIndex());
                f = (float)ColorHelper.getAlpha((int)i) / 255.0f;
                g = (float)ColorHelper.getRed((int)i) / 255.0f;
                h = (float)ColorHelper.getGreen((int)i) / 255.0f;
                j = (float)ColorHelper.getBlue((int)i) / 255.0f;
            } else {
                f = 1.0f;
                g = 1.0f;
                h = 1.0f;
                j = 1.0f;
            }
            vertexConsumer.quad(entry, bakedQuad, g, h, j, f, light, overlay);
        }
    }
}


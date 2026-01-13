/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.render.BannerResultGuiElementRenderer
 *  net.minecraft.client.gui.render.SpecialGuiElementRenderer
 *  net.minecraft.client.gui.render.state.special.BannerResultGuiElementRenderState
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.render.DiffuseLighting$Type
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.client.render.VertexConsumerProvider$Immediate
 *  net.minecraft.client.render.block.entity.BannerBlockEntityRenderer
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.command.OrderedRenderCommandQueueImpl
 *  net.minecraft.client.render.command.RenderDispatcher
 *  net.minecraft.client.render.model.ModelBaker
 *  net.minecraft.client.texture.SpriteHolder
 *  net.minecraft.client.util.SpriteIdentifier
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.component.type.BannerPatternsComponent
 *  net.minecraft.util.DyeColor
 */
package net.minecraft.client.gui.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.render.SpecialGuiElementRenderer;
import net.minecraft.client.gui.render.state.special.BannerResultGuiElementRenderState;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.command.OrderedRenderCommandQueueImpl;
import net.minecraft.client.render.command.RenderDispatcher;
import net.minecraft.client.render.model.ModelBaker;
import net.minecraft.client.texture.SpriteHolder;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.util.DyeColor;

@Environment(value=EnvType.CLIENT)
public class BannerResultGuiElementRenderer
extends SpecialGuiElementRenderer<BannerResultGuiElementRenderState> {
    private final SpriteHolder sprite;

    public BannerResultGuiElementRenderer(VertexConsumerProvider.Immediate immediate, SpriteHolder sprite) {
        super(immediate);
        this.sprite = sprite;
    }

    public Class<BannerResultGuiElementRenderState> getElementClass() {
        return BannerResultGuiElementRenderState.class;
    }

    protected void render(BannerResultGuiElementRenderState bannerResultGuiElementRenderState, MatrixStack matrixStack) {
        MinecraftClient.getInstance().gameRenderer.getDiffuseLighting().setShaderLights(DiffuseLighting.Type.ITEMS_FLAT);
        matrixStack.translate(0.0f, 0.25f, 0.0f);
        RenderDispatcher renderDispatcher = MinecraftClient.getInstance().gameRenderer.getEntityRenderDispatcher();
        OrderedRenderCommandQueueImpl orderedRenderCommandQueueImpl = renderDispatcher.getQueue();
        BannerBlockEntityRenderer.renderCanvas((SpriteHolder)this.sprite, (MatrixStack)matrixStack, (OrderedRenderCommandQueue)orderedRenderCommandQueueImpl, (int)0xF000F0, (int)OverlayTexture.DEFAULT_UV, (Model)bannerResultGuiElementRenderState.flag(), (Object)Float.valueOf(0.0f), (SpriteIdentifier)ModelBaker.BANNER_BASE, (boolean)true, (DyeColor)bannerResultGuiElementRenderState.baseColor(), (BannerPatternsComponent)bannerResultGuiElementRenderState.resultBannerPatterns(), (boolean)false, null, (int)0);
        renderDispatcher.render();
    }

    protected String getName() {
        return "banner result";
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.WoodType
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.render.SignGuiElementRenderer
 *  net.minecraft.client.gui.render.SpecialGuiElementRenderer
 *  net.minecraft.client.gui.render.state.special.SignGuiElementRenderState
 *  net.minecraft.client.model.Model$SinglePartModel
 *  net.minecraft.client.render.DiffuseLighting$Type
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.client.render.TexturedRenderLayers
 *  net.minecraft.client.render.VertexConsumer
 *  net.minecraft.client.render.VertexConsumerProvider
 *  net.minecraft.client.render.VertexConsumerProvider$Immediate
 *  net.minecraft.client.texture.SpriteHolder
 *  net.minecraft.client.util.SpriteIdentifier
 *  net.minecraft.client.util.math.MatrixStack
 */
package net.minecraft.client.gui.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.WoodType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.render.SpecialGuiElementRenderer;
import net.minecraft.client.gui.render.state.special.SignGuiElementRenderState;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.SpriteHolder;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;

@Environment(value=EnvType.CLIENT)
public class SignGuiElementRenderer
extends SpecialGuiElementRenderer<SignGuiElementRenderState> {
    private final SpriteHolder sprite;

    public SignGuiElementRenderer(VertexConsumerProvider.Immediate immediate, SpriteHolder sprite) {
        super(immediate);
        this.sprite = sprite;
    }

    public Class<SignGuiElementRenderState> getElementClass() {
        return SignGuiElementRenderState.class;
    }

    protected void render(SignGuiElementRenderState signGuiElementRenderState, MatrixStack matrixStack) {
        MinecraftClient.getInstance().gameRenderer.getDiffuseLighting().setShaderLights(DiffuseLighting.Type.ITEMS_FLAT);
        matrixStack.translate(0.0f, -0.75f, 0.0f);
        SpriteIdentifier spriteIdentifier = TexturedRenderLayers.getSignTextureId((WoodType)signGuiElementRenderState.woodType());
        Model.SinglePartModel singlePartModel = signGuiElementRenderState.signModel();
        VertexConsumer vertexConsumer = spriteIdentifier.getVertexConsumer(this.sprite, (VertexConsumerProvider)this.vertexConsumers, arg_0 -> ((Model.SinglePartModel)singlePartModel).getLayer(arg_0));
        singlePartModel.render(matrixStack, vertexConsumer, 0xF000F0, OverlayTexture.DEFAULT_UV);
    }

    protected String getName() {
        return "sign";
    }
}


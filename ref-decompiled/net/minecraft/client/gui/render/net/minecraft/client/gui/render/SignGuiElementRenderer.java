/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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

    @Override
    public Class<SignGuiElementRenderState> getElementClass() {
        return SignGuiElementRenderState.class;
    }

    @Override
    protected void render(SignGuiElementRenderState signGuiElementRenderState, MatrixStack matrixStack) {
        MinecraftClient.getInstance().gameRenderer.getDiffuseLighting().setShaderLights(DiffuseLighting.Type.ITEMS_FLAT);
        matrixStack.translate(0.0f, -0.75f, 0.0f);
        SpriteIdentifier spriteIdentifier = TexturedRenderLayers.getSignTextureId(signGuiElementRenderState.woodType());
        Model.SinglePartModel singlePartModel = signGuiElementRenderState.signModel();
        VertexConsumer vertexConsumer = spriteIdentifier.getVertexConsumer(this.sprite, this.vertexConsumers, singlePartModel::getLayer);
        singlePartModel.render(matrixStack, vertexConsumer, 0xF000F0, OverlayTexture.DEFAULT_UV);
    }

    @Override
    protected String getName() {
        return "sign";
    }
}

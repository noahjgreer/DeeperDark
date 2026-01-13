/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.render.EntityGuiElementRenderer
 *  net.minecraft.client.gui.render.SpecialGuiElementRenderer
 *  net.minecraft.client.gui.render.state.special.EntityGuiElementRenderState
 *  net.minecraft.client.render.DiffuseLighting$Type
 *  net.minecraft.client.render.VertexConsumerProvider$Immediate
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.command.RenderDispatcher
 *  net.minecraft.client.render.entity.EntityRenderManager
 *  net.minecraft.client.render.state.CameraRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 */
package net.minecraft.client.gui.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.render.SpecialGuiElementRenderer;
import net.minecraft.client.gui.render.state.special.EntityGuiElementRenderState;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.command.RenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

@Environment(value=EnvType.CLIENT)
public class EntityGuiElementRenderer
extends SpecialGuiElementRenderer<EntityGuiElementRenderState> {
    private final EntityRenderManager entityRenderDispatcher;

    public EntityGuiElementRenderer(VertexConsumerProvider.Immediate vertexConsumers, EntityRenderManager entityRenderDispatcher) {
        super(vertexConsumers);
        this.entityRenderDispatcher = entityRenderDispatcher;
    }

    public Class<EntityGuiElementRenderState> getElementClass() {
        return EntityGuiElementRenderState.class;
    }

    protected void render(EntityGuiElementRenderState entityGuiElementRenderState, MatrixStack matrixStack) {
        MinecraftClient.getInstance().gameRenderer.getDiffuseLighting().setShaderLights(DiffuseLighting.Type.ENTITY_IN_UI);
        Vector3f vector3f = entityGuiElementRenderState.translation();
        matrixStack.translate(vector3f.x, vector3f.y, vector3f.z);
        matrixStack.multiply((Quaternionfc)entityGuiElementRenderState.rotation());
        Quaternionf quaternionf = entityGuiElementRenderState.overrideCameraAngle();
        RenderDispatcher renderDispatcher = MinecraftClient.getInstance().gameRenderer.getEntityRenderDispatcher();
        CameraRenderState cameraRenderState = new CameraRenderState();
        if (quaternionf != null) {
            cameraRenderState.orientation = quaternionf.conjugate(new Quaternionf()).rotateY((float)Math.PI);
        }
        this.entityRenderDispatcher.render(entityGuiElementRenderState.renderState(), cameraRenderState, 0.0, 0.0, 0.0, matrixStack, (OrderedRenderCommandQueue)renderDispatcher.getQueue());
        renderDispatcher.render();
    }

    protected float getYOffset(int height, int windowScaleFactor) {
        return (float)height / 2.0f;
    }

    protected String getName() {
        return "entity";
    }
}


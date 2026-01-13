/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.block.entity;

import java.util.EnumSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.EndPortalBlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.state.EndPortalBlockEntityRenderState;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public abstract class AbstractEndPortalBlockEntityRenderer<T extends EndPortalBlockEntity, S extends EndPortalBlockEntityRenderState>
implements BlockEntityRenderer<T, S> {
    public static final Identifier SKY_TEXTURE = Identifier.ofVanilla("textures/environment/end_sky.png");
    public static final Identifier PORTAL_TEXTURE = Identifier.ofVanilla("textures/entity/end_portal.png");

    @Override
    public void updateRenderState(T endPortalBlockEntity, S endPortalBlockEntityRenderState, float f, Vec3d vec3d,  @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlayCommand) {
        BlockEntityRenderer.super.updateRenderState(endPortalBlockEntity, endPortalBlockEntityRenderState, f, vec3d, crumblingOverlayCommand);
        ((EndPortalBlockEntityRenderState)endPortalBlockEntityRenderState).sides.clear();
        for (Direction direction : Direction.values()) {
            if (!((EndPortalBlockEntity)endPortalBlockEntity).shouldDrawSide(direction)) continue;
            ((EndPortalBlockEntityRenderState)endPortalBlockEntityRenderState).sides.add(direction);
        }
    }

    @Override
    public void render(S endPortalBlockEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        orderedRenderCommandQueue.submitCustom(matrixStack, this.getLayer(), (matricesEntry, vertexConsumer) -> this.renderSides(endPortalBlockEntityRenderState.sides, matricesEntry.getPositionMatrix(), vertexConsumer));
    }

    private void renderSides(EnumSet<Direction> sides, Matrix4f matrix, VertexConsumer vertexConsumer) {
        float f = this.getBottomYOffset();
        float g = this.getTopYOffset();
        this.renderSide(sides, matrix, vertexConsumer, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, Direction.SOUTH);
        this.renderSide(sides, matrix, vertexConsumer, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, Direction.NORTH);
        this.renderSide(sides, matrix, vertexConsumer, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, Direction.EAST);
        this.renderSide(sides, matrix, vertexConsumer, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, Direction.WEST);
        this.renderSide(sides, matrix, vertexConsumer, 0.0f, 1.0f, f, f, 0.0f, 0.0f, 1.0f, 1.0f, Direction.DOWN);
        this.renderSide(sides, matrix, vertexConsumer, 0.0f, 1.0f, g, g, 1.0f, 1.0f, 0.0f, 0.0f, Direction.UP);
    }

    private void renderSide(EnumSet<Direction> sides, Matrix4f model, VertexConsumer vertices, float x1, float x2, float y1, float y2, float z1, float z2, float z3, float z4, Direction side) {
        if (sides.contains(side)) {
            vertices.vertex((Matrix4fc)model, x1, y1, z1);
            vertices.vertex((Matrix4fc)model, x2, y1, z2);
            vertices.vertex((Matrix4fc)model, x2, y2, z3);
            vertices.vertex((Matrix4fc)model, x1, y2, z4);
        }
    }

    protected float getTopYOffset() {
        return 0.75f;
    }

    protected float getBottomYOffset() {
        return 0.375f;
    }

    protected RenderLayer getLayer() {
        return RenderLayers.endPortal();
    }
}

/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Quaternionfc
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BeamEmitter;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.state.BeaconBlockEntityRenderState;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionfc;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class BeaconBlockEntityRenderer<T extends BlockEntity>
implements BlockEntityRenderer<T, BeaconBlockEntityRenderState> {
    public static final Identifier BEAM_TEXTURE = Identifier.ofVanilla("textures/entity/beacon_beam.png");
    public static final int MAX_BEAM_HEIGHT = 2048;
    private static final float field_56505 = 96.0f;
    public static final float field_56503 = 0.2f;
    public static final float field_56504 = 0.25f;

    @Override
    public BeaconBlockEntityRenderState createRenderState() {
        return new BeaconBlockEntityRenderState();
    }

    @Override
    public void updateRenderState(T blockEntity, BeaconBlockEntityRenderState beaconBlockEntityRenderState, float f, Vec3d vec3d,  @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlayCommand) {
        BlockEntityRenderer.super.updateRenderState(blockEntity, beaconBlockEntityRenderState, f, vec3d, crumblingOverlayCommand);
        BeaconBlockEntityRenderer.updateBeaconRenderState(blockEntity, beaconBlockEntityRenderState, f, vec3d);
    }

    public static <T extends BlockEntity> void updateBeaconRenderState(T blockEntity, BeaconBlockEntityRenderState state, float tickProgress, Vec3d cameraPos) {
        state.beamRotationDegrees = blockEntity.getWorld() != null ? (float)Math.floorMod(blockEntity.getWorld().getTime(), 40) + tickProgress : 0.0f;
        state.beamSegments = ((BeamEmitter)((Object)blockEntity)).getBeamSegments().stream().map(beamSegment -> new BeaconBlockEntityRenderState.BeamSegment(beamSegment.getColor(), beamSegment.getHeight())).toList();
        float f = (float)cameraPos.subtract(state.pos.toCenterPos()).horizontalLength();
        ClientPlayerEntity clientPlayerEntity = MinecraftClient.getInstance().player;
        state.beamScale = clientPlayerEntity != null && clientPlayerEntity.isUsingSpyglass() ? 1.0f : Math.max(1.0f, f / 96.0f);
    }

    @Override
    public void render(BeaconBlockEntityRenderState beaconBlockEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        int i = 0;
        for (int j = 0; j < beaconBlockEntityRenderState.beamSegments.size(); ++j) {
            BeaconBlockEntityRenderState.BeamSegment beamSegment = beaconBlockEntityRenderState.beamSegments.get(j);
            BeaconBlockEntityRenderer.renderBeam(matrixStack, orderedRenderCommandQueue, beaconBlockEntityRenderState.beamScale, beaconBlockEntityRenderState.beamRotationDegrees, i, j == beaconBlockEntityRenderState.beamSegments.size() - 1 ? 2048 : beamSegment.height(), beamSegment.color());
            i += beamSegment.height();
        }
    }

    private static void renderBeam(MatrixStack matrices, OrderedRenderCommandQueue queue, float scale, float rotationDegrees, int minHeight, int maxHeight, int color) {
        BeaconBlockEntityRenderer.renderBeam(matrices, queue, BEAM_TEXTURE, 1.0f, rotationDegrees, minHeight, maxHeight, color, 0.2f * scale, 0.25f * scale);
    }

    public static void renderBeam(MatrixStack matrices, OrderedRenderCommandQueue queue, Identifier textureId, float beamHeight, float beamRotationDegrees, int minHeight, int maxHeight, int color, float innerScale, float outerScale) {
        int i = minHeight + maxHeight;
        matrices.push();
        matrices.translate(0.5, 0.0, 0.5);
        float f = maxHeight < 0 ? beamRotationDegrees : -beamRotationDegrees;
        float g = MathHelper.fractionalPart(f * 0.2f - (float)MathHelper.floor(f * 0.1f));
        matrices.push();
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(beamRotationDegrees * 2.25f - 45.0f));
        float h = 0.0f;
        float j = innerScale;
        float k = innerScale;
        float l = 0.0f;
        float m = -innerScale;
        float n = 0.0f;
        float o = 0.0f;
        float p = -innerScale;
        float q = 0.0f;
        float r = 1.0f;
        float s = -1.0f + g;
        float t = (float)maxHeight * beamHeight * (0.5f / innerScale) + s;
        queue.submitCustom(matrices, RenderLayers.beaconBeam(textureId, false), (matricesEntry, vertexConsumer) -> BeaconBlockEntityRenderer.renderBeamLayer(matricesEntry, vertexConsumer, color, minHeight, i, 0.0f, j, k, 0.0f, m, 0.0f, 0.0f, p, 0.0f, 1.0f, t, s));
        matrices.pop();
        h = -outerScale;
        j = -outerScale;
        k = outerScale;
        l = -outerScale;
        m = -outerScale;
        n = outerScale;
        o = outerScale;
        p = outerScale;
        q = 0.0f;
        r = 1.0f;
        s = -1.0f + g;
        t = (float)maxHeight * beamHeight + s;
        queue.submitCustom(matrices, RenderLayers.beaconBeam(textureId, true), (matricesEntry, vertexConsumer) -> BeaconBlockEntityRenderer.renderBeamLayer(matricesEntry, vertexConsumer, ColorHelper.withAlpha(32, color), minHeight, i, h, j, k, l, m, n, o, p, 0.0f, 1.0f, t, s));
        matrices.pop();
    }

    private static void renderBeamLayer(MatrixStack.Entry matricesEntry, VertexConsumer vertices, int color, int yOffset, int height, float x1, float z1, float x2, float z2, float x3, float z3, float x4, float z4, float u1, float u2, float v1, float v2) {
        BeaconBlockEntityRenderer.renderBeamFace(matricesEntry, vertices, color, yOffset, height, x1, z1, x2, z2, u1, u2, v1, v2);
        BeaconBlockEntityRenderer.renderBeamFace(matricesEntry, vertices, color, yOffset, height, x4, z4, x3, z3, u1, u2, v1, v2);
        BeaconBlockEntityRenderer.renderBeamFace(matricesEntry, vertices, color, yOffset, height, x2, z2, x4, z4, u1, u2, v1, v2);
        BeaconBlockEntityRenderer.renderBeamFace(matricesEntry, vertices, color, yOffset, height, x3, z3, x1, z1, u1, u2, v1, v2);
    }

    private static void renderBeamFace(MatrixStack.Entry matrix, VertexConsumer vertices, int color, int yOffset, int height, float x1, float z1, float x2, float z2, float u1, float u2, float v1, float v2) {
        BeaconBlockEntityRenderer.renderBeamVertex(matrix, vertices, color, height, x1, z1, u2, v1);
        BeaconBlockEntityRenderer.renderBeamVertex(matrix, vertices, color, yOffset, x1, z1, u2, v2);
        BeaconBlockEntityRenderer.renderBeamVertex(matrix, vertices, color, yOffset, x2, z2, u1, v2);
        BeaconBlockEntityRenderer.renderBeamVertex(matrix, vertices, color, height, x2, z2, u1, v1);
    }

    private static void renderBeamVertex(MatrixStack.Entry matrix, VertexConsumer vertices, int color, int y, float x, float z, float u, float v) {
        vertices.vertex(matrix, x, (float)y, z).color(color).texture(u, v).overlay(OverlayTexture.DEFAULT_UV).light(0xF000F0).normal(matrix, 0.0f, 1.0f, 0.0f);
    }

    @Override
    public boolean rendersOutsideBoundingBox() {
        return true;
    }

    @Override
    public int getRenderDistance() {
        return MinecraftClient.getInstance().options.getClampedViewDistance() * 16;
    }

    @Override
    public boolean isInRenderDistance(T blockEntity, Vec3d pos) {
        return Vec3d.ofCenter(((BlockEntity)blockEntity).getPos()).multiply(1.0, 0.0, 1.0).isInRange(pos.multiply(1.0, 0.0, 1.0), this.getRenderDistance());
    }

    @Override
    public /* synthetic */ BlockEntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.entity.BeamEmitter
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.network.ClientPlayerEntity
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.client.render.VertexConsumer
 *  net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer
 *  net.minecraft.client.render.block.entity.BlockEntityRenderer
 *  net.minecraft.client.render.block.entity.state.BeaconBlockEntityRenderState
 *  net.minecraft.client.render.block.entity.state.BeaconBlockEntityRenderState$BeamSegment
 *  net.minecraft.client.render.block.entity.state.BlockEntityRenderState
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.state.CameraRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.util.math.MatrixStack$Entry
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.ColorHelper
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Position
 *  net.minecraft.util.math.RotationAxis
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.Vec3i
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
import net.minecraft.util.math.Position;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.joml.Quaternionfc;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class BeaconBlockEntityRenderer<T extends BlockEntity>
implements BlockEntityRenderer<T, BeaconBlockEntityRenderState> {
    public static final Identifier BEAM_TEXTURE = Identifier.ofVanilla((String)"textures/entity/beacon_beam.png");
    public static final int MAX_BEAM_HEIGHT = 2048;
    private static final float field_56505 = 96.0f;
    public static final float field_56503 = 0.2f;
    public static final float field_56504 = 0.25f;

    public BeaconBlockEntityRenderState createRenderState() {
        return new BeaconBlockEntityRenderState();
    }

    public void updateRenderState(T blockEntity, BeaconBlockEntityRenderState beaconBlockEntityRenderState, float f, Vec3d vec3d, // Could not load outer class - annotation placement on inner may be incorrect
     @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlayCommand) {
        super.updateRenderState(blockEntity, (BlockEntityRenderState)beaconBlockEntityRenderState, f, vec3d, crumblingOverlayCommand);
        BeaconBlockEntityRenderer.updateBeaconRenderState(blockEntity, (BeaconBlockEntityRenderState)beaconBlockEntityRenderState, (float)f, (Vec3d)vec3d);
    }

    public static <T extends BlockEntity> void updateBeaconRenderState(T blockEntity, BeaconBlockEntityRenderState state, float tickProgress, Vec3d cameraPos) {
        state.beamRotationDegrees = blockEntity.getWorld() != null ? (float)Math.floorMod(blockEntity.getWorld().getTime(), 40) + tickProgress : 0.0f;
        state.beamSegments = ((BeamEmitter)blockEntity).getBeamSegments().stream().map(beamSegment -> new BeaconBlockEntityRenderState.BeamSegment(beamSegment.getColor(), beamSegment.getHeight())).toList();
        float f = (float)cameraPos.subtract(state.pos.toCenterPos()).horizontalLength();
        ClientPlayerEntity clientPlayerEntity = MinecraftClient.getInstance().player;
        state.beamScale = clientPlayerEntity != null && clientPlayerEntity.isUsingSpyglass() ? 1.0f : Math.max(1.0f, f / 96.0f);
    }

    public void render(BeaconBlockEntityRenderState beaconBlockEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        int i = 0;
        for (int j = 0; j < beaconBlockEntityRenderState.beamSegments.size(); ++j) {
            BeaconBlockEntityRenderState.BeamSegment beamSegment = (BeaconBlockEntityRenderState.BeamSegment)beaconBlockEntityRenderState.beamSegments.get(j);
            BeaconBlockEntityRenderer.renderBeam((MatrixStack)matrixStack, (OrderedRenderCommandQueue)orderedRenderCommandQueue, (float)beaconBlockEntityRenderState.beamScale, (float)beaconBlockEntityRenderState.beamRotationDegrees, (int)i, (int)(j == beaconBlockEntityRenderState.beamSegments.size() - 1 ? 2048 : beamSegment.height()), (int)beamSegment.color());
            i += beamSegment.height();
        }
    }

    private static void renderBeam(MatrixStack matrices, OrderedRenderCommandQueue queue, float scale, float rotationDegrees, int minHeight, int maxHeight, int color) {
        BeaconBlockEntityRenderer.renderBeam((MatrixStack)matrices, (OrderedRenderCommandQueue)queue, (Identifier)BEAM_TEXTURE, (float)1.0f, (float)rotationDegrees, (int)minHeight, (int)maxHeight, (int)color, (float)(0.2f * scale), (float)(0.25f * scale));
    }

    public static void renderBeam(MatrixStack matrices, OrderedRenderCommandQueue queue, Identifier textureId, float beamHeight, float beamRotationDegrees, int minHeight, int maxHeight, int color, float innerScale, float outerScale) {
        int i = minHeight + maxHeight;
        matrices.push();
        matrices.translate(0.5, 0.0, 0.5);
        float f = maxHeight < 0 ? beamRotationDegrees : -beamRotationDegrees;
        float g = MathHelper.fractionalPart((float)(f * 0.2f - (float)MathHelper.floor((float)(f * 0.1f))));
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
        queue.submitCustom(matrices, RenderLayers.beaconBeam((Identifier)textureId, (boolean)false), (matricesEntry, vertexConsumer) -> BeaconBlockEntityRenderer.renderBeamLayer((MatrixStack.Entry)matricesEntry, (VertexConsumer)vertexConsumer, (int)color, (int)minHeight, (int)i, (float)0.0f, (float)j, (float)k, (float)0.0f, (float)m, (float)0.0f, (float)0.0f, (float)p, (float)0.0f, (float)1.0f, (float)t, (float)s));
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
        queue.submitCustom(matrices, RenderLayers.beaconBeam((Identifier)textureId, (boolean)true), (matricesEntry, vertexConsumer) -> BeaconBlockEntityRenderer.renderBeamLayer((MatrixStack.Entry)matricesEntry, (VertexConsumer)vertexConsumer, (int)ColorHelper.withAlpha((int)32, (int)color), (int)minHeight, (int)i, (float)h, (float)j, (float)k, (float)l, (float)m, (float)n, (float)o, (float)p, (float)0.0f, (float)1.0f, (float)t, (float)s));
        matrices.pop();
    }

    private static void renderBeamLayer(MatrixStack.Entry matricesEntry, VertexConsumer vertices, int color, int yOffset, int height, float x1, float z1, float x2, float z2, float x3, float z3, float x4, float z4, float u1, float u2, float v1, float v2) {
        BeaconBlockEntityRenderer.renderBeamFace((MatrixStack.Entry)matricesEntry, (VertexConsumer)vertices, (int)color, (int)yOffset, (int)height, (float)x1, (float)z1, (float)x2, (float)z2, (float)u1, (float)u2, (float)v1, (float)v2);
        BeaconBlockEntityRenderer.renderBeamFace((MatrixStack.Entry)matricesEntry, (VertexConsumer)vertices, (int)color, (int)yOffset, (int)height, (float)x4, (float)z4, (float)x3, (float)z3, (float)u1, (float)u2, (float)v1, (float)v2);
        BeaconBlockEntityRenderer.renderBeamFace((MatrixStack.Entry)matricesEntry, (VertexConsumer)vertices, (int)color, (int)yOffset, (int)height, (float)x2, (float)z2, (float)x4, (float)z4, (float)u1, (float)u2, (float)v1, (float)v2);
        BeaconBlockEntityRenderer.renderBeamFace((MatrixStack.Entry)matricesEntry, (VertexConsumer)vertices, (int)color, (int)yOffset, (int)height, (float)x3, (float)z3, (float)x1, (float)z1, (float)u1, (float)u2, (float)v1, (float)v2);
    }

    private static void renderBeamFace(MatrixStack.Entry matrix, VertexConsumer vertices, int color, int yOffset, int height, float x1, float z1, float x2, float z2, float u1, float u2, float v1, float v2) {
        BeaconBlockEntityRenderer.renderBeamVertex((MatrixStack.Entry)matrix, (VertexConsumer)vertices, (int)color, (int)height, (float)x1, (float)z1, (float)u2, (float)v1);
        BeaconBlockEntityRenderer.renderBeamVertex((MatrixStack.Entry)matrix, (VertexConsumer)vertices, (int)color, (int)yOffset, (float)x1, (float)z1, (float)u2, (float)v2);
        BeaconBlockEntityRenderer.renderBeamVertex((MatrixStack.Entry)matrix, (VertexConsumer)vertices, (int)color, (int)yOffset, (float)x2, (float)z2, (float)u1, (float)v2);
        BeaconBlockEntityRenderer.renderBeamVertex((MatrixStack.Entry)matrix, (VertexConsumer)vertices, (int)color, (int)height, (float)x2, (float)z2, (float)u1, (float)v1);
    }

    private static void renderBeamVertex(MatrixStack.Entry matrix, VertexConsumer vertices, int color, int y, float x, float z, float u, float v) {
        vertices.vertex(matrix, x, (float)y, z).color(color).texture(u, v).overlay(OverlayTexture.DEFAULT_UV).light(0xF000F0).normal(matrix, 0.0f, 1.0f, 0.0f);
    }

    public boolean rendersOutsideBoundingBox() {
        return true;
    }

    public int getRenderDistance() {
        return MinecraftClient.getInstance().options.getClampedViewDistance() * 16;
    }

    public boolean isInRenderDistance(T blockEntity, Vec3d pos) {
        return Vec3d.ofCenter((Vec3i)blockEntity.getPos()).multiply(1.0, 0.0, 1.0).isInRange((Position)pos.multiply(1.0, 0.0, 1.0), (double)this.getRenderDistance());
    }

    public /* synthetic */ BlockEntityRenderState createRenderState() {
        return this.createRenderState();
    }
}


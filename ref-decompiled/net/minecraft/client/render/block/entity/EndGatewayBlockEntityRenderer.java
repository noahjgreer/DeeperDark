/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.entity.EndGatewayBlockEntity
 *  net.minecraft.block.entity.EndPortalBlockEntity
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.client.render.block.entity.AbstractEndPortalBlockEntityRenderer
 *  net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer
 *  net.minecraft.client.render.block.entity.EndGatewayBlockEntityRenderer
 *  net.minecraft.client.render.block.entity.state.BlockEntityRenderState
 *  net.minecraft.client.render.block.entity.state.EndGatewayBlockEntityRenderState
 *  net.minecraft.client.render.block.entity.state.EndPortalBlockEntityRenderState
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.state.CameraRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.util.DyeColor
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.World
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.block.entity.EndPortalBlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.block.entity.AbstractEndPortalBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.block.entity.state.EndGatewayBlockEntityRenderState;
import net.minecraft.client.render.block.entity.state.EndPortalBlockEntityRenderState;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class EndGatewayBlockEntityRenderer
extends AbstractEndPortalBlockEntityRenderer<EndGatewayBlockEntity, EndGatewayBlockEntityRenderState> {
    private static final Identifier BEAM_TEXTURE = Identifier.ofVanilla((String)"textures/entity/end_gateway_beam.png");

    public EndGatewayBlockEntityRenderState createRenderState() {
        return new EndGatewayBlockEntityRenderState();
    }

    public void updateRenderState(EndGatewayBlockEntity endGatewayBlockEntity, EndGatewayBlockEntityRenderState endGatewayBlockEntityRenderState, float f, Vec3d vec3d, // Could not load outer class - annotation placement on inner may be incorrect
     @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlayCommand) {
        super.updateRenderState((EndPortalBlockEntity)endGatewayBlockEntity, (EndPortalBlockEntityRenderState)endGatewayBlockEntityRenderState, f, vec3d, crumblingOverlayCommand);
        World world = endGatewayBlockEntity.getWorld();
        if (endGatewayBlockEntity.isRecentlyGenerated() || endGatewayBlockEntity.needsCooldownBeforeTeleporting() && world != null) {
            endGatewayBlockEntityRenderState.beamHeight = endGatewayBlockEntity.isRecentlyGenerated() ? endGatewayBlockEntity.getRecentlyGeneratedBeamHeight(f) : endGatewayBlockEntity.getCooldownBeamHeight(f);
            double d = endGatewayBlockEntity.isRecentlyGenerated() ? (double)endGatewayBlockEntity.getWorld().getTopYInclusive() : 50.0;
            endGatewayBlockEntityRenderState.beamHeight = MathHelper.sin((double)(endGatewayBlockEntityRenderState.beamHeight * (float)Math.PI));
            endGatewayBlockEntityRenderState.beamSpan = MathHelper.floor((double)((double)endGatewayBlockEntityRenderState.beamHeight * d));
            endGatewayBlockEntityRenderState.beamColor = endGatewayBlockEntity.isRecentlyGenerated() ? DyeColor.MAGENTA.getEntityColor() : DyeColor.PURPLE.getEntityColor();
            endGatewayBlockEntityRenderState.beamRotationDegrees = endGatewayBlockEntity.getWorld() != null ? (float)Math.floorMod(endGatewayBlockEntity.getWorld().getTime(), 40) + f : 0.0f;
        } else {
            endGatewayBlockEntityRenderState.beamSpan = 0;
        }
    }

    public void render(EndGatewayBlockEntityRenderState endGatewayBlockEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        if (endGatewayBlockEntityRenderState.beamSpan > 0) {
            BeaconBlockEntityRenderer.renderBeam((MatrixStack)matrixStack, (OrderedRenderCommandQueue)orderedRenderCommandQueue, (Identifier)BEAM_TEXTURE, (float)endGatewayBlockEntityRenderState.beamHeight, (float)endGatewayBlockEntityRenderState.beamRotationDegrees, (int)(-endGatewayBlockEntityRenderState.beamSpan), (int)(endGatewayBlockEntityRenderState.beamSpan * 2), (int)endGatewayBlockEntityRenderState.beamColor, (float)0.15f, (float)0.175f);
        }
        super.render((EndPortalBlockEntityRenderState)endGatewayBlockEntityRenderState, matrixStack, orderedRenderCommandQueue, cameraRenderState);
    }

    protected float getTopYOffset() {
        return 1.0f;
    }

    protected float getBottomYOffset() {
        return 0.0f;
    }

    protected RenderLayer getLayer() {
        return RenderLayers.endGateway();
    }

    public int getRenderDistance() {
        return 256;
    }

    public /* synthetic */ BlockEntityRenderState createRenderState() {
        return this.createRenderState();
    }
}


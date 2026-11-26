package net.noahsarch.deeperdark.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.noahsarch.deeperdark.block.CrateBlock;
import net.noahsarch.deeperdark.block.CrateBlockEntity;

/** A blank map with the crate's best item on it, on every face that isn't against a solid block. */
public class CrateBlockEntityRenderer implements BlockEntityRenderer<CrateBlockEntity, CrateRenderState> {

    // The held map's background, as vanilla draws it in first person.
    private static final RenderType MAP = RenderTypes.text(Identifier.withDefaultNamespace("textures/map/map_background.png"));
    private static final float MAP_HALF_SIZE = 5 / 16F;
    private static final float ITEM_SCALE = 0.45F;
    private static final float MAP_OUT = 0.002F, ITEM_OUT = 0.03F;

    private final ItemModelResolver itemModelResolver;

    public CrateBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public CrateRenderState createRenderState() {
        return new CrateRenderState();
    }

    @Override
    public void extractRenderState(CrateBlockEntity crate, CrateRenderState state, float partialTick,
                                   Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay overlay) {
        BlockEntityRenderState.extractBase(crate, state, overlay);
        Level level = crate.getLevel();
        BlockPos pos = crate.getBlockPos();
        state.facing = crate.getBlockState().getValue(CrateBlock.FACING);
        itemModelResolver.updateForTopItem(state.item, crate.getShown(), ItemDisplayContext.FIXED, level, null, (int) pos.asLong());
        for (Direction face : Direction.values()) {
            BlockPos front = pos.relative(face);
            state.faceLight[face.ordinal()] = level.getBlockState(front).isSolidRender() ? -1 : lightAt(level, front);
        }
    }

    // The crate itself is opaque, so each label is lit by the block in front of it.
    private static int lightAt(Level level, BlockPos pos) {
        //? if >=26.3 {
        /*return net.minecraft.util.LightCoordsUtil.getLightCoords(level, pos);
        *///?} else {
        return net.minecraft.client.renderer.LevelRenderer.getLightCoords(level, pos);
        //?}
    }

    @Override
    public void submit(CrateRenderState state, PoseStack poseStack, SubmitNodeCollector nodes, CameraRenderState camera) {
        if (state.item.isEmpty()) return;
        for (Direction face : Direction.values()) {
            int light = state.faceLight[face.ordinal()];
            if (light < 0) continue;
            poseStack.pushPose();
            poseStack.translate(0.5F, 0.5F, 0.5F);
            if (face.getAxis().isHorizontal()) {
                poseStack.mulPose(Axis.YP.rotationDegrees(-face.toYRot()));
            } else {
                // Top and bottom labels read upright from the crate's front.
                poseStack.mulPose(Axis.YP.rotationDegrees(-state.facing.toYRot()));
                poseStack.mulPose(Axis.XP.rotationDegrees(face == Direction.UP ? -90 : 90));
            }
            poseStack.translate(0F, 0F, 0.5F + MAP_OUT);
            nodes.submitCustomGeometry(poseStack, MAP, (pose, buffer) -> {
                buffer.addVertex(pose, -MAP_HALF_SIZE, -MAP_HALF_SIZE, 0F).setColor(-1).setUv(0F, 1F).setLight(light);
                buffer.addVertex(pose, MAP_HALF_SIZE, -MAP_HALF_SIZE, 0F).setColor(-1).setUv(1F, 1F).setLight(light);
                buffer.addVertex(pose, MAP_HALF_SIZE, MAP_HALF_SIZE, 0F).setColor(-1).setUv(1F, 0F).setLight(light);
                buffer.addVertex(pose, -MAP_HALF_SIZE, MAP_HALF_SIZE, 0F).setColor(-1).setUv(0F, 0F).setLight(light);
            });
            // FIXED items face local -Z, as in an item frame.
            poseStack.translate(0F, 0F, ITEM_OUT);
            poseStack.mulPose(Axis.YP.rotationDegrees(180F));
            // Flat, so block items lie on the map instead of sinking into the crate.
            poseStack.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE / 16);
            state.item.submit(poseStack, nodes, light, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }
    }
}

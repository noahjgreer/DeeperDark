/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Quaternionfc
 *  org.joml.Vector3fc
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.block.entity;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BannerBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.WallBannerBlock;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.model.BannerBlockModel;
import net.minecraft.client.render.block.entity.model.BannerFlagBlockModel;
import net.minecraft.client.render.block.entity.state.BannerBlockEntityRenderState;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.render.model.ModelBaker;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.texture.SpriteHolder;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.RotationPropertyHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionfc;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class BannerBlockEntityRenderer
implements BlockEntityRenderer<BannerBlockEntity, BannerBlockEntityRenderState> {
    private static final int ROTATIONS = 16;
    private static final float field_55282 = 0.6666667f;
    private final SpriteHolder materials;
    private final BannerBlockModel standingModel;
    private final BannerBlockModel wallModel;
    private final BannerFlagBlockModel standingFlagModel;
    private final BannerFlagBlockModel wallFlagModel;

    public BannerBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this(context.loadedEntityModels(), context.spriteHolder());
    }

    public BannerBlockEntityRenderer(SpecialModelRenderer.BakeContext context) {
        this(context.entityModelSet(), context.spriteHolder());
    }

    public BannerBlockEntityRenderer(LoadedEntityModels models, SpriteHolder materials) {
        this.materials = materials;
        this.standingModel = new BannerBlockModel(models.getModelPart(EntityModelLayers.STANDING_BANNER));
        this.wallModel = new BannerBlockModel(models.getModelPart(EntityModelLayers.WALL_BANNER));
        this.standingFlagModel = new BannerFlagBlockModel(models.getModelPart(EntityModelLayers.STANDING_BANNER_FLAG));
        this.wallFlagModel = new BannerFlagBlockModel(models.getModelPart(EntityModelLayers.WALL_BANNER_FLAG));
    }

    @Override
    public BannerBlockEntityRenderState createRenderState() {
        return new BannerBlockEntityRenderState();
    }

    @Override
    public void updateRenderState(BannerBlockEntity bannerBlockEntity, BannerBlockEntityRenderState bannerBlockEntityRenderState, float f, Vec3d vec3d,  @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlayCommand) {
        BlockEntityRenderer.super.updateRenderState(bannerBlockEntity, bannerBlockEntityRenderState, f, vec3d, crumblingOverlayCommand);
        bannerBlockEntityRenderState.dyeColor = bannerBlockEntity.getColorForState();
        bannerBlockEntityRenderState.bannerPatterns = bannerBlockEntity.getPatterns();
        BlockState blockState = bannerBlockEntity.getCachedState();
        if (blockState.getBlock() instanceof BannerBlock) {
            bannerBlockEntityRenderState.yaw = -RotationPropertyHelper.toDegrees(blockState.get(BannerBlock.ROTATION));
            bannerBlockEntityRenderState.standing = true;
        } else {
            bannerBlockEntityRenderState.yaw = -blockState.get(WallBannerBlock.FACING).getPositiveHorizontalDegrees();
            bannerBlockEntityRenderState.standing = false;
        }
        long l = bannerBlockEntity.getWorld() != null ? bannerBlockEntity.getWorld().getTime() : 0L;
        BlockPos blockPos = bannerBlockEntity.getPos();
        bannerBlockEntityRenderState.pitch = ((float)Math.floorMod((long)(blockPos.getX() * 7 + blockPos.getY() * 9 + blockPos.getZ() * 13) + l, 100L) + f) / 100.0f;
    }

    @Override
    public void render(BannerBlockEntityRenderState bannerBlockEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        BannerFlagBlockModel bannerFlagBlockModel;
        BannerBlockModel bannerBlockModel;
        if (bannerBlockEntityRenderState.standing) {
            bannerBlockModel = this.standingModel;
            bannerFlagBlockModel = this.standingFlagModel;
        } else {
            bannerBlockModel = this.wallModel;
            bannerFlagBlockModel = this.wallFlagModel;
        }
        BannerBlockEntityRenderer.render(this.materials, matrixStack, orderedRenderCommandQueue, bannerBlockEntityRenderState.lightmapCoordinates, OverlayTexture.DEFAULT_UV, bannerBlockEntityRenderState.yaw, bannerBlockModel, bannerFlagBlockModel, bannerBlockEntityRenderState.pitch, bannerBlockEntityRenderState.dyeColor, bannerBlockEntityRenderState.bannerPatterns, bannerBlockEntityRenderState.crumblingOverlay, 0);
    }

    public void renderAsItem(MatrixStack matrices, OrderedRenderCommandQueue queue, int light, int overlay, DyeColor baseColor, BannerPatternsComponent patterns, int i) {
        BannerBlockEntityRenderer.render(this.materials, matrices, queue, light, overlay, 0.0f, this.standingModel, this.standingFlagModel, 0.0f, baseColor, patterns, null, i);
    }

    private static void render(SpriteHolder materials, MatrixStack matrices, OrderedRenderCommandQueue queue, int light, int overlay, float yaw, BannerBlockModel model, BannerFlagBlockModel flagModel, float pitch, DyeColor dyeColor, BannerPatternsComponent bannerPatterns,  @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay, int i) {
        matrices.push();
        matrices.translate(0.5f, 0.0f, 0.5f);
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(yaw));
        matrices.scale(0.6666667f, -0.6666667f, -0.6666667f);
        SpriteIdentifier spriteIdentifier = ModelBaker.BANNER_BASE;
        queue.submitModel(model, Unit.INSTANCE, matrices, spriteIdentifier.getRenderLayer(RenderLayers::entitySolid), light, overlay, -1, materials.getSprite(spriteIdentifier), i, crumblingOverlay);
        BannerBlockEntityRenderer.renderCanvas(materials, matrices, queue, light, overlay, flagModel, Float.valueOf(pitch), spriteIdentifier, true, dyeColor, bannerPatterns, false, crumblingOverlay, i);
        matrices.pop();
    }

    public static <S> void renderCanvas(SpriteHolder materials, MatrixStack matrices, OrderedRenderCommandQueue queue, int light, int overlay, Model<S> model, S state, SpriteIdentifier spriteId, boolean useBannerLayer, DyeColor color, BannerPatternsComponent patterns, boolean bl,  @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlayCommand, int i) {
        queue.submitModel(model, state, matrices, spriteId.getRenderLayer(RenderLayers::entitySolid), light, overlay, -1, materials.getSprite(spriteId), i, crumblingOverlayCommand);
        if (bl) {
            queue.submitModel(model, state, matrices, RenderLayers.entityGlint(), light, overlay, -1, materials.getSprite(spriteId), 0, crumblingOverlayCommand);
        }
        BannerBlockEntityRenderer.renderLayer(materials, matrices, queue, light, overlay, model, state, useBannerLayer ? TexturedRenderLayers.BANNER_BASE : TexturedRenderLayers.SHIELD_BASE, color, crumblingOverlayCommand);
        for (int j = 0; j < 16 && j < patterns.layers().size(); ++j) {
            BannerPatternsComponent.Layer layer = patterns.layers().get(j);
            SpriteIdentifier spriteIdentifier = useBannerLayer ? TexturedRenderLayers.getBannerPatternTextureId(layer.pattern()) : TexturedRenderLayers.getShieldPatternTextureId(layer.pattern());
            BannerBlockEntityRenderer.renderLayer(materials, matrices, queue, light, overlay, model, state, spriteIdentifier, layer.color(), null);
        }
    }

    private static <S> void renderLayer(SpriteHolder materials, MatrixStack matrices, OrderedRenderCommandQueue queue, int light, int overlay, Model<S> model, S state, SpriteIdentifier spriteId, DyeColor color,  @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay) {
        int i = color.getEntityColor();
        queue.submitModel(model, state, matrices, spriteId.getRenderLayer(RenderLayers::entityNoOutline), light, overlay, i, materials.getSprite(spriteId), 0, crumblingOverlay);
    }

    public void collectVertices(Consumer<Vector3fc> consumer) {
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.translate(0.5f, 0.0f, 0.5f);
        matrixStack.scale(0.6666667f, -0.6666667f, -0.6666667f);
        this.standingModel.getRootPart().collectVertices(matrixStack, consumer);
        this.standingFlagModel.setAngles(Float.valueOf(0.0f));
        this.standingFlagModel.getRootPart().collectVertices(matrixStack, consumer);
    }

    @Override
    public /* synthetic */ BlockEntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.BannerBlock
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.WallBannerBlock
 *  net.minecraft.block.entity.BannerBlockEntity
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.client.render.TexturedRenderLayers
 *  net.minecraft.client.render.block.entity.BannerBlockEntityRenderer
 *  net.minecraft.client.render.block.entity.BlockEntityRenderer
 *  net.minecraft.client.render.block.entity.BlockEntityRendererFactory$Context
 *  net.minecraft.client.render.block.entity.model.BannerBlockModel
 *  net.minecraft.client.render.block.entity.model.BannerFlagBlockModel
 *  net.minecraft.client.render.block.entity.state.BannerBlockEntityRenderState
 *  net.minecraft.client.render.block.entity.state.BlockEntityRenderState
 *  net.minecraft.client.render.command.ModelCommandRenderer$CrumblingOverlayCommand
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.LoadedEntityModels
 *  net.minecraft.client.render.item.model.special.SpecialModelRenderer$BakeContext
 *  net.minecraft.client.render.model.ModelBaker
 *  net.minecraft.client.render.state.CameraRenderState
 *  net.minecraft.client.texture.SpriteHolder
 *  net.minecraft.client.util.SpriteIdentifier
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.component.type.BannerPatternsComponent
 *  net.minecraft.component.type.BannerPatternsComponent$Layer
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.DyeColor
 *  net.minecraft.util.Unit
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.RotationAxis
 *  net.minecraft.util.math.RotationPropertyHelper
 *  net.minecraft.util.math.Vec3d
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
import net.minecraft.block.entity.BlockEntity;
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
import net.minecraft.client.render.command.ModelCommandRenderer;
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
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.state.property.Property;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.RotationPropertyHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionfc;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
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

    public BannerBlockEntityRenderState createRenderState() {
        return new BannerBlockEntityRenderState();
    }

    public void updateRenderState(BannerBlockEntity bannerBlockEntity, BannerBlockEntityRenderState bannerBlockEntityRenderState, float f, Vec3d vec3d, // Could not load outer class - annotation placement on inner may be incorrect
     @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlayCommand) {
        super.updateRenderState((BlockEntity)bannerBlockEntity, (BlockEntityRenderState)bannerBlockEntityRenderState, f, vec3d, crumblingOverlayCommand);
        bannerBlockEntityRenderState.dyeColor = bannerBlockEntity.getColorForState();
        bannerBlockEntityRenderState.bannerPatterns = bannerBlockEntity.getPatterns();
        BlockState blockState = bannerBlockEntity.getCachedState();
        if (blockState.getBlock() instanceof BannerBlock) {
            bannerBlockEntityRenderState.yaw = -RotationPropertyHelper.toDegrees((int)((Integer)blockState.get((Property)BannerBlock.ROTATION)));
            bannerBlockEntityRenderState.standing = true;
        } else {
            bannerBlockEntityRenderState.yaw = -((Direction)blockState.get((Property)WallBannerBlock.FACING)).getPositiveHorizontalDegrees();
            bannerBlockEntityRenderState.standing = false;
        }
        long l = bannerBlockEntity.getWorld() != null ? bannerBlockEntity.getWorld().getTime() : 0L;
        BlockPos blockPos = bannerBlockEntity.getPos();
        bannerBlockEntityRenderState.pitch = ((float)Math.floorMod((long)(blockPos.getX() * 7 + blockPos.getY() * 9 + blockPos.getZ() * 13) + l, 100L) + f) / 100.0f;
    }

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
        BannerBlockEntityRenderer.render((SpriteHolder)this.materials, (MatrixStack)matrixStack, (OrderedRenderCommandQueue)orderedRenderCommandQueue, (int)bannerBlockEntityRenderState.lightmapCoordinates, (int)OverlayTexture.DEFAULT_UV, (float)bannerBlockEntityRenderState.yaw, (BannerBlockModel)bannerBlockModel, (BannerFlagBlockModel)bannerFlagBlockModel, (float)bannerBlockEntityRenderState.pitch, (DyeColor)bannerBlockEntityRenderState.dyeColor, (BannerPatternsComponent)bannerBlockEntityRenderState.bannerPatterns, (ModelCommandRenderer.CrumblingOverlayCommand)bannerBlockEntityRenderState.crumblingOverlay, (int)0);
    }

    public void renderAsItem(MatrixStack matrices, OrderedRenderCommandQueue queue, int light, int overlay, DyeColor baseColor, BannerPatternsComponent patterns, int i) {
        BannerBlockEntityRenderer.render((SpriteHolder)this.materials, (MatrixStack)matrices, (OrderedRenderCommandQueue)queue, (int)light, (int)overlay, (float)0.0f, (BannerBlockModel)this.standingModel, (BannerFlagBlockModel)this.standingFlagModel, (float)0.0f, (DyeColor)baseColor, (BannerPatternsComponent)patterns, null, (int)i);
    }

    private static void render(SpriteHolder materials, MatrixStack matrices, OrderedRenderCommandQueue queue, int light, int overlay, float yaw, BannerBlockModel model, BannerFlagBlockModel flagModel, float pitch, DyeColor dyeColor, BannerPatternsComponent bannerPatterns, // Could not load outer class - annotation placement on inner may be incorrect
     @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay, int i) {
        matrices.push();
        matrices.translate(0.5f, 0.0f, 0.5f);
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(yaw));
        matrices.scale(0.6666667f, -0.6666667f, -0.6666667f);
        SpriteIdentifier spriteIdentifier = ModelBaker.BANNER_BASE;
        queue.submitModel((Model)model, (Object)Unit.INSTANCE, matrices, spriteIdentifier.getRenderLayer(RenderLayers::entitySolid), light, overlay, -1, materials.getSprite(spriteIdentifier), i, crumblingOverlay);
        BannerBlockEntityRenderer.renderCanvas((SpriteHolder)materials, (MatrixStack)matrices, (OrderedRenderCommandQueue)queue, (int)light, (int)overlay, (Model)flagModel, (Object)Float.valueOf(pitch), (SpriteIdentifier)spriteIdentifier, (boolean)true, (DyeColor)dyeColor, (BannerPatternsComponent)bannerPatterns, (boolean)false, (ModelCommandRenderer.CrumblingOverlayCommand)crumblingOverlay, (int)i);
        matrices.pop();
    }

    public static <S> void renderCanvas(SpriteHolder materials, MatrixStack matrices, OrderedRenderCommandQueue queue, int light, int overlay, Model<S> model, S state, SpriteIdentifier spriteId, boolean useBannerLayer, DyeColor color, BannerPatternsComponent patterns, boolean bl, // Could not load outer class - annotation placement on inner may be incorrect
     @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlayCommand, int i) {
        queue.submitModel(model, state, matrices, spriteId.getRenderLayer(RenderLayers::entitySolid), light, overlay, -1, materials.getSprite(spriteId), i, crumblingOverlayCommand);
        if (bl) {
            queue.submitModel(model, state, matrices, RenderLayers.entityGlint(), light, overlay, -1, materials.getSprite(spriteId), 0, crumblingOverlayCommand);
        }
        BannerBlockEntityRenderer.renderLayer((SpriteHolder)materials, (MatrixStack)matrices, (OrderedRenderCommandQueue)queue, (int)light, (int)overlay, model, state, (SpriteIdentifier)(useBannerLayer ? TexturedRenderLayers.BANNER_BASE : TexturedRenderLayers.SHIELD_BASE), (DyeColor)color, (ModelCommandRenderer.CrumblingOverlayCommand)crumblingOverlayCommand);
        for (int j = 0; j < 16 && j < patterns.layers().size(); ++j) {
            BannerPatternsComponent.Layer layer = (BannerPatternsComponent.Layer)patterns.layers().get(j);
            SpriteIdentifier spriteIdentifier = useBannerLayer ? TexturedRenderLayers.getBannerPatternTextureId((RegistryEntry)layer.pattern()) : TexturedRenderLayers.getShieldPatternTextureId((RegistryEntry)layer.pattern());
            BannerBlockEntityRenderer.renderLayer((SpriteHolder)materials, (MatrixStack)matrices, (OrderedRenderCommandQueue)queue, (int)light, (int)overlay, model, state, (SpriteIdentifier)spriteIdentifier, (DyeColor)layer.color(), null);
        }
    }

    private static <S> void renderLayer(SpriteHolder materials, MatrixStack matrices, OrderedRenderCommandQueue queue, int light, int overlay, Model<S> model, S state, SpriteIdentifier spriteId, DyeColor color, // Could not load outer class - annotation placement on inner may be incorrect
     @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay) {
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

    public /* synthetic */ BlockEntityRenderState createRenderState() {
        return this.createRenderState();
    }
}


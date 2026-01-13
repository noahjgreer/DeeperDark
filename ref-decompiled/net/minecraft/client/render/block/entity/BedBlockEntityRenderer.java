/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2IntFunction
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.BedBlock
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.ChestBlock
 *  net.minecraft.block.DoubleBlockProperties
 *  net.minecraft.block.DoubleBlockProperties$PropertyRetriever
 *  net.minecraft.block.DoubleBlockProperties$PropertySource
 *  net.minecraft.block.entity.BedBlockEntity
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.enums.BedPart
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.model.Model$SinglePartModel
 *  net.minecraft.client.model.ModelData
 *  net.minecraft.client.model.ModelPartBuilder
 *  net.minecraft.client.model.ModelPartData
 *  net.minecraft.client.model.ModelTransform
 *  net.minecraft.client.model.TexturedModelData
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.client.render.TexturedRenderLayers
 *  net.minecraft.client.render.block.entity.BedBlockEntityRenderer
 *  net.minecraft.client.render.block.entity.BlockEntityRenderer
 *  net.minecraft.client.render.block.entity.BlockEntityRendererFactory$Context
 *  net.minecraft.client.render.block.entity.LightmapCoordinatesRetriever
 *  net.minecraft.client.render.block.entity.state.BedBlockEntityRenderState
 *  net.minecraft.client.render.block.entity.state.BlockEntityRenderState
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.LoadedEntityModels
 *  net.minecraft.client.render.item.model.special.SpecialModelRenderer$BakeContext
 *  net.minecraft.client.render.state.CameraRenderState
 *  net.minecraft.client.texture.SpriteHolder
 *  net.minecraft.client.util.SpriteIdentifier
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.DyeColor
 *  net.minecraft.util.Unit
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.RotationAxis
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.WorldAccess
 *  org.joml.Quaternionfc
 *  org.joml.Vector3fc
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.block.entity;

import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.DoubleBlockProperties;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.BedPart;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.LightmapCoordinatesRetriever;
import net.minecraft.client.render.block.entity.state.BedBlockEntityRenderState;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.texture.SpriteHolder;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.state.property.Property;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldAccess;
import org.joml.Quaternionfc;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class BedBlockEntityRenderer
implements BlockEntityRenderer<BedBlockEntity, BedBlockEntityRenderState> {
    private final SpriteHolder materials;
    private final Model.SinglePartModel bedHead;
    private final Model.SinglePartModel bedFoot;

    public BedBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this(ctx.spriteHolder(), ctx.loadedEntityModels());
    }

    public BedBlockEntityRenderer(SpecialModelRenderer.BakeContext context) {
        this(context.spriteHolder(), context.entityModelSet());
    }

    public BedBlockEntityRenderer(SpriteHolder materials, LoadedEntityModels entityModelSet) {
        this.materials = materials;
        this.bedHead = new Model.SinglePartModel(entityModelSet.getModelPart(EntityModelLayers.BED_HEAD), RenderLayers::entitySolid);
        this.bedFoot = new Model.SinglePartModel(entityModelSet.getModelPart(EntityModelLayers.BED_FOOT), RenderLayers::entitySolid);
    }

    public static TexturedModelData getHeadTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("main", ModelPartBuilder.create().uv(0, 0).cuboid(0.0f, 0.0f, 0.0f, 16.0f, 16.0f, 6.0f), ModelTransform.NONE);
        modelPartData.addChild("left_leg", ModelPartBuilder.create().uv(50, 6).cuboid(0.0f, 6.0f, 0.0f, 3.0f, 3.0f, 3.0f), ModelTransform.rotation((float)1.5707964f, (float)0.0f, (float)1.5707964f));
        modelPartData.addChild("right_leg", ModelPartBuilder.create().uv(50, 18).cuboid(-16.0f, 6.0f, 0.0f, 3.0f, 3.0f, 3.0f), ModelTransform.rotation((float)1.5707964f, (float)0.0f, (float)((float)Math.PI)));
        return TexturedModelData.of((ModelData)modelData, (int)64, (int)64);
    }

    public static TexturedModelData getFootTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("main", ModelPartBuilder.create().uv(0, 22).cuboid(0.0f, 0.0f, 0.0f, 16.0f, 16.0f, 6.0f), ModelTransform.NONE);
        modelPartData.addChild("left_leg", ModelPartBuilder.create().uv(50, 0).cuboid(0.0f, 6.0f, -16.0f, 3.0f, 3.0f, 3.0f), ModelTransform.rotation((float)1.5707964f, (float)0.0f, (float)0.0f));
        modelPartData.addChild("right_leg", ModelPartBuilder.create().uv(50, 12).cuboid(-16.0f, 6.0f, -16.0f, 3.0f, 3.0f, 3.0f), ModelTransform.rotation((float)1.5707964f, (float)0.0f, (float)4.712389f));
        return TexturedModelData.of((ModelData)modelData, (int)64, (int)64);
    }

    public BedBlockEntityRenderState createRenderState() {
        return new BedBlockEntityRenderState();
    }

    public void updateRenderState(BedBlockEntity bedBlockEntity, BedBlockEntityRenderState bedBlockEntityRenderState, float f, Vec3d vec3d, // Could not load outer class - annotation placement on inner may be incorrect
     @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlayCommand) {
        super.updateRenderState((BlockEntity)bedBlockEntity, (BlockEntityRenderState)bedBlockEntityRenderState, f, vec3d, crumblingOverlayCommand);
        bedBlockEntityRenderState.dyeColor = bedBlockEntity.getColor();
        bedBlockEntityRenderState.facing = (Direction)bedBlockEntity.getCachedState().get((Property)BedBlock.FACING);
        boolean bl = bedBlockEntityRenderState.headPart = bedBlockEntity.getCachedState().get((Property)BedBlock.PART) == BedPart.HEAD;
        if (bedBlockEntity.getWorld() != null) {
            DoubleBlockProperties.PropertySource propertySource = DoubleBlockProperties.toPropertySource((BlockEntityType)BlockEntityType.BED, BedBlock::getBedPart, BedBlock::getOppositePartDirection, (Property)ChestBlock.FACING, (BlockState)bedBlockEntity.getCachedState(), (WorldAccess)bedBlockEntity.getWorld(), (BlockPos)bedBlockEntity.getPos(), (world, pos) -> false);
            bedBlockEntityRenderState.lightmapCoordinates = ((Int2IntFunction)propertySource.apply((DoubleBlockProperties.PropertyRetriever)new LightmapCoordinatesRetriever())).get(bedBlockEntityRenderState.lightmapCoordinates);
        }
    }

    public void render(BedBlockEntityRenderState bedBlockEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        SpriteIdentifier spriteIdentifier = TexturedRenderLayers.getBedTextureId((DyeColor)bedBlockEntityRenderState.dyeColor);
        this.renderPart(matrixStack, orderedRenderCommandQueue, bedBlockEntityRenderState.headPart ? this.bedHead : this.bedFoot, bedBlockEntityRenderState.facing, spriteIdentifier, bedBlockEntityRenderState.lightmapCoordinates, OverlayTexture.DEFAULT_UV, false, bedBlockEntityRenderState.crumblingOverlay, 0);
    }

    public void renderAsItem(MatrixStack matrices, OrderedRenderCommandQueue queue, int light, int overlay, SpriteIdentifier textureId, int i) {
        this.renderPart(matrices, queue, this.bedHead, Direction.SOUTH, textureId, light, overlay, false, null, i);
        this.renderPart(matrices, queue, this.bedFoot, Direction.SOUTH, textureId, light, overlay, true, null, i);
    }

    private void renderPart(MatrixStack matrices, OrderedRenderCommandQueue queue, Model.SinglePartModel model, Direction direction, SpriteIdentifier spriteId, int light, int overlay, boolean isFoot, // Could not load outer class - annotation placement on inner may be incorrect
     @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay, int i) {
        matrices.push();
        BedBlockEntityRenderer.setTransforms((MatrixStack)matrices, (boolean)isFoot, (Direction)direction);
        queue.submitModel((Model)model, (Object)Unit.INSTANCE, matrices, spriteId.getRenderLayer(RenderLayers::entitySolid), light, overlay, -1, this.materials.getSprite(spriteId), i, crumblingOverlay);
        matrices.pop();
    }

    private static void setTransforms(MatrixStack matrices, boolean isFoot, Direction direction) {
        matrices.translate(0.0f, 0.5625f, isFoot ? -1.0f : 0.0f);
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(90.0f));
        matrices.translate(0.5f, 0.5f, 0.5f);
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees(180.0f + direction.getPositiveHorizontalDegrees()));
        matrices.translate(-0.5f, -0.5f, -0.5f);
    }

    public void collectVertices(Consumer<Vector3fc> consumer) {
        MatrixStack matrixStack = new MatrixStack();
        BedBlockEntityRenderer.setTransforms((MatrixStack)matrixStack, (boolean)false, (Direction)Direction.SOUTH);
        this.bedHead.getRootPart().collectVertices(matrixStack, consumer);
        matrixStack.loadIdentity();
        BedBlockEntityRenderer.setTransforms((MatrixStack)matrixStack, (boolean)true, (Direction)Direction.SOUTH);
        this.bedFoot.getRootPart().collectVertices(matrixStack, consumer);
    }

    public /* synthetic */ BlockEntityRenderState createRenderState() {
        return this.createRenderState();
    }
}


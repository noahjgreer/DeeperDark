/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.DecoratedPotPatterns
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.DecoratedPotBlockEntity
 *  net.minecraft.block.entity.DecoratedPotBlockEntity$WobbleType
 *  net.minecraft.block.entity.Sherds
 *  net.minecraft.client.model.Dilation
 *  net.minecraft.client.model.ModelData
 *  net.minecraft.client.model.ModelPart
 *  net.minecraft.client.model.ModelPartBuilder
 *  net.minecraft.client.model.ModelPartData
 *  net.minecraft.client.model.ModelTransform
 *  net.minecraft.client.model.TexturedModelData
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.client.render.TexturedRenderLayers
 *  net.minecraft.client.render.block.entity.BlockEntityRenderer
 *  net.minecraft.client.render.block.entity.BlockEntityRendererFactory$Context
 *  net.minecraft.client.render.block.entity.DecoratedPotBlockEntityRenderer
 *  net.minecraft.client.render.block.entity.state.BlockEntityRenderState
 *  net.minecraft.client.render.block.entity.state.DecoratedPotBlockEntityRenderState
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.LoadedEntityModels
 *  net.minecraft.client.render.item.model.special.SpecialModelRenderer$BakeContext
 *  net.minecraft.client.render.state.CameraRenderState
 *  net.minecraft.client.texture.Sprite
 *  net.minecraft.client.texture.SpriteHolder
 *  net.minecraft.client.util.SpriteIdentifier
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.item.Item
 *  net.minecraft.registry.RegistryKey
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.RotationAxis
 *  net.minecraft.util.math.Vec3d
 *  org.joml.Quaternionfc
 *  org.joml.Vector3fc
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.block.entity;

import java.util.EnumSet;
import java.util.Optional;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.DecoratedPotPatterns;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.DecoratedPotBlockEntity;
import net.minecraft.block.entity.Sherds;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.block.entity.state.DecoratedPotBlockEntityRenderState;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteHolder;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionfc;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class DecoratedPotBlockEntityRenderer
implements BlockEntityRenderer<DecoratedPotBlockEntity, DecoratedPotBlockEntityRenderState> {
    private final SpriteHolder materials;
    private static final String NECK = "neck";
    private static final String FRONT = "front";
    private static final String BACK = "back";
    private static final String LEFT = "left";
    private static final String RIGHT = "right";
    private static final String TOP = "top";
    private static final String BOTTOM = "bottom";
    private final ModelPart neck;
    private final ModelPart front;
    private final ModelPart back;
    private final ModelPart left;
    private final ModelPart right;
    private final ModelPart top;
    private final ModelPart bottom;
    private static final float field_46728 = 0.125f;

    public DecoratedPotBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this(context.loadedEntityModels(), context.spriteHolder());
    }

    public DecoratedPotBlockEntityRenderer(SpecialModelRenderer.BakeContext context) {
        this(context.entityModelSet(), context.spriteHolder());
    }

    public DecoratedPotBlockEntityRenderer(LoadedEntityModels entityModelSet, SpriteHolder materials) {
        this.materials = materials;
        ModelPart modelPart = entityModelSet.getModelPart(EntityModelLayers.DECORATED_POT_BASE);
        this.neck = modelPart.getChild("neck");
        this.top = modelPart.getChild("top");
        this.bottom = modelPart.getChild("bottom");
        ModelPart modelPart2 = entityModelSet.getModelPart(EntityModelLayers.DECORATED_POT_SIDES);
        this.front = modelPart2.getChild("front");
        this.back = modelPart2.getChild("back");
        this.left = modelPart2.getChild("left");
        this.right = modelPart2.getChild("right");
    }

    public static TexturedModelData getTopBottomNeckTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        Dilation dilation = new Dilation(0.2f);
        Dilation dilation2 = new Dilation(-0.1f);
        modelPartData.addChild("neck", ModelPartBuilder.create().uv(0, 0).cuboid(4.0f, 17.0f, 4.0f, 8.0f, 3.0f, 8.0f, dilation2).uv(0, 5).cuboid(5.0f, 20.0f, 5.0f, 6.0f, 1.0f, 6.0f, dilation), ModelTransform.of((float)0.0f, (float)37.0f, (float)16.0f, (float)((float)Math.PI), (float)0.0f, (float)0.0f));
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(-14, 13).cuboid(0.0f, 0.0f, 0.0f, 14.0f, 0.0f, 14.0f);
        modelPartData.addChild("top", modelPartBuilder, ModelTransform.of((float)1.0f, (float)16.0f, (float)1.0f, (float)0.0f, (float)0.0f, (float)0.0f));
        modelPartData.addChild("bottom", modelPartBuilder, ModelTransform.of((float)1.0f, (float)0.0f, (float)1.0f, (float)0.0f, (float)0.0f, (float)0.0f));
        return TexturedModelData.of((ModelData)modelData, (int)32, (int)32);
    }

    public static TexturedModelData getSidesTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(1, 0).cuboid(0.0f, 0.0f, 0.0f, 14.0f, 16.0f, 0.0f, EnumSet.of(Direction.NORTH));
        modelPartData.addChild("back", modelPartBuilder, ModelTransform.of((float)15.0f, (float)16.0f, (float)1.0f, (float)0.0f, (float)0.0f, (float)((float)Math.PI)));
        modelPartData.addChild("left", modelPartBuilder, ModelTransform.of((float)1.0f, (float)16.0f, (float)1.0f, (float)0.0f, (float)-1.5707964f, (float)((float)Math.PI)));
        modelPartData.addChild("right", modelPartBuilder, ModelTransform.of((float)15.0f, (float)16.0f, (float)15.0f, (float)0.0f, (float)1.5707964f, (float)((float)Math.PI)));
        modelPartData.addChild("front", modelPartBuilder, ModelTransform.of((float)1.0f, (float)16.0f, (float)15.0f, (float)((float)Math.PI), (float)0.0f, (float)0.0f));
        return TexturedModelData.of((ModelData)modelData, (int)16, (int)16);
    }

    private static SpriteIdentifier getTextureIdFromSherd(Optional<Item> sherd) {
        SpriteIdentifier spriteIdentifier;
        if (sherd.isPresent() && (spriteIdentifier = TexturedRenderLayers.getDecoratedPotPatternTextureId((RegistryKey)DecoratedPotPatterns.fromSherd((Item)sherd.get()))) != null) {
            return spriteIdentifier;
        }
        return TexturedRenderLayers.DECORATED_POT_SIDE;
    }

    public DecoratedPotBlockEntityRenderState createRenderState() {
        return new DecoratedPotBlockEntityRenderState();
    }

    public void updateRenderState(DecoratedPotBlockEntity decoratedPotBlockEntity, DecoratedPotBlockEntityRenderState decoratedPotBlockEntityRenderState, float f, Vec3d vec3d, // Could not load outer class - annotation placement on inner may be incorrect
     @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlayCommand) {
        super.updateRenderState((BlockEntity)decoratedPotBlockEntity, (BlockEntityRenderState)decoratedPotBlockEntityRenderState, f, vec3d, crumblingOverlayCommand);
        decoratedPotBlockEntityRenderState.sherds = decoratedPotBlockEntity.getSherds();
        decoratedPotBlockEntityRenderState.facing = decoratedPotBlockEntity.getHorizontalFacing();
        DecoratedPotBlockEntity.WobbleType wobbleType = decoratedPotBlockEntity.lastWobbleType;
        decoratedPotBlockEntityRenderState.wobbleAnimationProgress = wobbleType != null && decoratedPotBlockEntity.getWorld() != null ? ((float)(decoratedPotBlockEntity.getWorld().getTime() - decoratedPotBlockEntity.lastWobbleTime) + f) / (float)wobbleType.lengthInTicks : 0.0f;
    }

    public void render(DecoratedPotBlockEntityRenderState decoratedPotBlockEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        matrixStack.push();
        Direction direction = decoratedPotBlockEntityRenderState.facing;
        matrixStack.translate(0.5, 0.0, 0.5);
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(180.0f - direction.getPositiveHorizontalDegrees()));
        matrixStack.translate(-0.5, 0.0, -0.5);
        if (decoratedPotBlockEntityRenderState.wobbleAnimationProgress >= 0.0f && decoratedPotBlockEntityRenderState.wobbleAnimationProgress <= 1.0f) {
            if (decoratedPotBlockEntityRenderState.wobbleType == DecoratedPotBlockEntity.WobbleType.POSITIVE) {
                float f = 0.015625f;
                float g = decoratedPotBlockEntityRenderState.wobbleAnimationProgress * ((float)Math.PI * 2);
                float h = -1.5f * (MathHelper.cos((double)g) + 0.5f) * MathHelper.sin((double)(g / 2.0f));
                matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotation(h * 0.015625f), 0.5f, 0.0f, 0.5f);
                float i = MathHelper.sin((double)g);
                matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotation(i * 0.015625f), 0.5f, 0.0f, 0.5f);
            } else {
                float f = MathHelper.sin((double)(-decoratedPotBlockEntityRenderState.wobbleAnimationProgress * 3.0f * (float)Math.PI)) * 0.125f;
                float g = 1.0f - decoratedPotBlockEntityRenderState.wobbleAnimationProgress;
                matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotation(f * g), 0.5f, 0.0f, 0.5f);
            }
        }
        this.render(matrixStack, orderedRenderCommandQueue, decoratedPotBlockEntityRenderState.lightmapCoordinates, OverlayTexture.DEFAULT_UV, decoratedPotBlockEntityRenderState.sherds, 0);
        matrixStack.pop();
    }

    public void render(MatrixStack matrices, OrderedRenderCommandQueue queue, int light, int overlay, Sherds sherds, int i) {
        RenderLayer renderLayer = TexturedRenderLayers.DECORATED_POT_BASE.getRenderLayer(RenderLayers::entitySolid);
        Sprite sprite = this.materials.getSprite(TexturedRenderLayers.DECORATED_POT_BASE);
        queue.submitModelPart(this.neck, matrices, renderLayer, light, overlay, sprite, false, false, -1, null, i);
        queue.submitModelPart(this.top, matrices, renderLayer, light, overlay, sprite, false, false, -1, null, i);
        queue.submitModelPart(this.bottom, matrices, renderLayer, light, overlay, sprite, false, false, -1, null, i);
        SpriteIdentifier spriteIdentifier = DecoratedPotBlockEntityRenderer.getTextureIdFromSherd((Optional)sherds.front());
        queue.submitModelPart(this.front, matrices, spriteIdentifier.getRenderLayer(RenderLayers::entitySolid), light, overlay, this.materials.getSprite(spriteIdentifier), false, false, -1, null, i);
        SpriteIdentifier spriteIdentifier2 = DecoratedPotBlockEntityRenderer.getTextureIdFromSherd((Optional)sherds.back());
        queue.submitModelPart(this.back, matrices, spriteIdentifier2.getRenderLayer(RenderLayers::entitySolid), light, overlay, this.materials.getSprite(spriteIdentifier2), false, false, -1, null, i);
        SpriteIdentifier spriteIdentifier3 = DecoratedPotBlockEntityRenderer.getTextureIdFromSherd((Optional)sherds.left());
        queue.submitModelPart(this.left, matrices, spriteIdentifier3.getRenderLayer(RenderLayers::entitySolid), light, overlay, this.materials.getSprite(spriteIdentifier3), false, false, -1, null, i);
        SpriteIdentifier spriteIdentifier4 = DecoratedPotBlockEntityRenderer.getTextureIdFromSherd((Optional)sherds.right());
        queue.submitModelPart(this.right, matrices, spriteIdentifier4.getRenderLayer(RenderLayers::entitySolid), light, overlay, this.materials.getSprite(spriteIdentifier4), false, false, -1, null, i);
    }

    public void collectVertices(Consumer<Vector3fc> consumer) {
        MatrixStack matrixStack = new MatrixStack();
        this.neck.collectVertices(matrixStack, consumer);
        this.top.collectVertices(matrixStack, consumer);
        this.bottom.collectVertices(matrixStack, consumer);
    }

    public /* synthetic */ BlockEntityRenderState createRenderState() {
        return this.createRenderState();
    }
}


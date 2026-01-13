/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.floats.Float2FloatFunction
 *  it.unimi.dsi.fastutil.ints.Int2IntFunction
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.ChestBlock
 *  net.minecraft.block.CopperChestBlock
 *  net.minecraft.block.DoubleBlockProperties$PropertyRetriever
 *  net.minecraft.block.DoubleBlockProperties$PropertySource
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.EnderChestBlockEntity
 *  net.minecraft.block.entity.LidOpenable
 *  net.minecraft.block.entity.TrappedChestBlockEntity
 *  net.minecraft.block.enums.ChestType
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.client.render.TexturedRenderLayers
 *  net.minecraft.client.render.block.entity.BlockEntityRenderer
 *  net.minecraft.client.render.block.entity.BlockEntityRendererFactory$Context
 *  net.minecraft.client.render.block.entity.ChestBlockEntityRenderer
 *  net.minecraft.client.render.block.entity.ChestBlockEntityRenderer$1
 *  net.minecraft.client.render.block.entity.LightmapCoordinatesRetriever
 *  net.minecraft.client.render.block.entity.model.ChestBlockModel
 *  net.minecraft.client.render.block.entity.state.BlockEntityRenderState
 *  net.minecraft.client.render.block.entity.state.ChestBlockEntityRenderState
 *  net.minecraft.client.render.block.entity.state.ChestBlockEntityRenderState$Variant
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.state.CameraRenderState
 *  net.minecraft.client.texture.Sprite
 *  net.minecraft.client.texture.SpriteHolder
 *  net.minecraft.client.util.SpriteIdentifier
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.Holidays
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.RotationAxis
 *  net.minecraft.util.math.Vec3d
 *  org.joml.Quaternionfc
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.block.entity;

import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.CopperChestBlock;
import net.minecraft.block.DoubleBlockProperties;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.block.entity.LidOpenable;
import net.minecraft.block.entity.TrappedChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.ChestBlockEntityRenderer;
import net.minecraft.client.render.block.entity.LightmapCoordinatesRetriever;
import net.minecraft.client.render.block.entity.model.ChestBlockModel;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.block.entity.state.ChestBlockEntityRenderState;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteHolder;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.state.property.Property;
import net.minecraft.util.Holidays;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionfc;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class ChestBlockEntityRenderer<T extends BlockEntity>
implements BlockEntityRenderer<T, ChestBlockEntityRenderState> {
    private final SpriteHolder materials;
    private final ChestBlockModel singleChest;
    private final ChestBlockModel doubleChestLeft;
    private final ChestBlockModel doubleChestRight;
    private final boolean christmas;

    public ChestBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.materials = context.spriteHolder();
        this.christmas = ChestBlockEntityRenderer.isAroundChristmas();
        this.singleChest = new ChestBlockModel(context.getLayerModelPart(EntityModelLayers.CHEST));
        this.doubleChestLeft = new ChestBlockModel(context.getLayerModelPart(EntityModelLayers.DOUBLE_CHEST_LEFT));
        this.doubleChestRight = new ChestBlockModel(context.getLayerModelPart(EntityModelLayers.DOUBLE_CHEST_RIGHT));
    }

    public static boolean isAroundChristmas() {
        return Holidays.isAroundChristmas();
    }

    public ChestBlockEntityRenderState createRenderState() {
        return new ChestBlockEntityRenderState();
    }

    public void updateRenderState(T blockEntity, ChestBlockEntityRenderState chestBlockEntityRenderState, float f, Vec3d vec3d, // Could not load outer class - annotation placement on inner may be incorrect
     @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlayCommand) {
        DoubleBlockProperties.PropertySource propertySource;
        Block block;
        super.updateRenderState(blockEntity, (BlockEntityRenderState)chestBlockEntityRenderState, f, vec3d, crumblingOverlayCommand);
        boolean bl = blockEntity.getWorld() != null;
        BlockState blockState = bl ? blockEntity.getCachedState() : (BlockState)Blocks.CHEST.getDefaultState().with((Property)ChestBlock.FACING, (Comparable)Direction.SOUTH);
        chestBlockEntityRenderState.chestType = blockState.contains((Property)ChestBlock.CHEST_TYPE) ? (ChestType)blockState.get((Property)ChestBlock.CHEST_TYPE) : ChestType.SINGLE;
        chestBlockEntityRenderState.yaw = ((Direction)blockState.get((Property)ChestBlock.FACING)).getPositiveHorizontalDegrees();
        chestBlockEntityRenderState.variant = this.getVariant(blockEntity, this.christmas);
        if (bl && (block = blockState.getBlock()) instanceof ChestBlock) {
            ChestBlock chestBlock = (ChestBlock)block;
            propertySource = chestBlock.getBlockEntitySource(blockState, blockEntity.getWorld(), blockEntity.getPos(), true);
        } else {
            propertySource = DoubleBlockProperties.PropertyRetriever::getFallback;
        }
        chestBlockEntityRenderState.lidAnimationProgress = ((Float2FloatFunction)propertySource.apply(ChestBlock.getAnimationProgressRetriever((LidOpenable)((LidOpenable)blockEntity)))).get(f);
        if (chestBlockEntityRenderState.chestType != ChestType.SINGLE) {
            chestBlockEntityRenderState.lightmapCoordinates = ((Int2IntFunction)propertySource.apply((DoubleBlockProperties.PropertyRetriever)new LightmapCoordinatesRetriever())).applyAsInt(chestBlockEntityRenderState.lightmapCoordinates);
        }
    }

    public void render(ChestBlockEntityRenderState chestBlockEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        matrixStack.push();
        matrixStack.translate(0.5f, 0.5f, 0.5f);
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(-chestBlockEntityRenderState.yaw));
        matrixStack.translate(-0.5f, -0.5f, -0.5f);
        float f = chestBlockEntityRenderState.lidAnimationProgress;
        f = 1.0f - f;
        f = 1.0f - f * f * f;
        SpriteIdentifier spriteIdentifier = TexturedRenderLayers.getChestTextureId((ChestBlockEntityRenderState.Variant)chestBlockEntityRenderState.variant, (ChestType)chestBlockEntityRenderState.chestType);
        RenderLayer renderLayer = spriteIdentifier.getRenderLayer(RenderLayers::entityCutout);
        Sprite sprite = this.materials.getSprite(spriteIdentifier);
        if (chestBlockEntityRenderState.chestType != ChestType.SINGLE) {
            if (chestBlockEntityRenderState.chestType == ChestType.LEFT) {
                orderedRenderCommandQueue.submitModel((Model)this.doubleChestLeft, (Object)Float.valueOf(f), matrixStack, renderLayer, chestBlockEntityRenderState.lightmapCoordinates, OverlayTexture.DEFAULT_UV, -1, sprite, 0, chestBlockEntityRenderState.crumblingOverlay);
            } else {
                orderedRenderCommandQueue.submitModel((Model)this.doubleChestRight, (Object)Float.valueOf(f), matrixStack, renderLayer, chestBlockEntityRenderState.lightmapCoordinates, OverlayTexture.DEFAULT_UV, -1, sprite, 0, chestBlockEntityRenderState.crumblingOverlay);
            }
        } else {
            orderedRenderCommandQueue.submitModel((Model)this.singleChest, (Object)Float.valueOf(f), matrixStack, renderLayer, chestBlockEntityRenderState.lightmapCoordinates, OverlayTexture.DEFAULT_UV, -1, sprite, 0, chestBlockEntityRenderState.crumblingOverlay);
        }
        matrixStack.pop();
    }

    private ChestBlockEntityRenderState.Variant getVariant(BlockEntity blockEntity, boolean christmas) {
        if (blockEntity instanceof EnderChestBlockEntity) {
            return ChestBlockEntityRenderState.Variant.ENDER_CHEST;
        }
        if (christmas) {
            return ChestBlockEntityRenderState.Variant.CHRISTMAS;
        }
        if (blockEntity instanceof TrappedChestBlockEntity) {
            return ChestBlockEntityRenderState.Variant.TRAPPED;
        }
        Block block = blockEntity.getCachedState().getBlock();
        if (block instanceof CopperChestBlock) {
            CopperChestBlock copperChestBlock = (CopperChestBlock)block;
            return switch (1.field_62658[copperChestBlock.getOxidationLevel().ordinal()]) {
                default -> throw new MatchException(null, null);
                case 1 -> ChestBlockEntityRenderState.Variant.COPPER_UNAFFECTED;
                case 2 -> ChestBlockEntityRenderState.Variant.COPPER_EXPOSED;
                case 3 -> ChestBlockEntityRenderState.Variant.COPPER_WEATHERED;
                case 4 -> ChestBlockEntityRenderState.Variant.COPPER_OXIDIZED;
            };
        }
        return ChestBlockEntityRenderState.Variant.REGULAR;
    }

    public /* synthetic */ BlockEntityRenderState createRenderState() {
        return this.createRenderState();
    }
}


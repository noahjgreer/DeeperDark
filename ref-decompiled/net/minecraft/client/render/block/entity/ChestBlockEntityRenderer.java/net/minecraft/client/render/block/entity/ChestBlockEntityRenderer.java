/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2IntFunction
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Quaternionfc
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.block.entity;

import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.CopperChestBlock;
import net.minecraft.block.DoubleBlockProperties;
import net.minecraft.block.Oxidizable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.block.entity.LidOpenable;
import net.minecraft.block.entity.TrappedChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
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
import net.minecraft.util.Holidays;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionfc;
import org.jspecify.annotations.Nullable;

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

    @Override
    public ChestBlockEntityRenderState createRenderState() {
        return new ChestBlockEntityRenderState();
    }

    @Override
    public void updateRenderState(T blockEntity, ChestBlockEntityRenderState chestBlockEntityRenderState, float f, Vec3d vec3d,  @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlayCommand) {
        DoubleBlockProperties.PropertySource<Object> propertySource;
        Block block;
        BlockEntityRenderer.super.updateRenderState(blockEntity, chestBlockEntityRenderState, f, vec3d, crumblingOverlayCommand);
        boolean bl = ((BlockEntity)blockEntity).getWorld() != null;
        BlockState blockState = bl ? ((BlockEntity)blockEntity).getCachedState() : (BlockState)Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, Direction.SOUTH);
        chestBlockEntityRenderState.chestType = blockState.contains(ChestBlock.CHEST_TYPE) ? blockState.get(ChestBlock.CHEST_TYPE) : ChestType.SINGLE;
        chestBlockEntityRenderState.yaw = blockState.get(ChestBlock.FACING).getPositiveHorizontalDegrees();
        chestBlockEntityRenderState.variant = this.getVariant((BlockEntity)blockEntity, this.christmas);
        if (bl && (block = blockState.getBlock()) instanceof ChestBlock) {
            ChestBlock chestBlock = (ChestBlock)block;
            propertySource = chestBlock.getBlockEntitySource(blockState, ((BlockEntity)blockEntity).getWorld(), ((BlockEntity)blockEntity).getPos(), true);
        } else {
            propertySource = DoubleBlockProperties.PropertyRetriever::getFallback;
        }
        chestBlockEntityRenderState.lidAnimationProgress = propertySource.apply(ChestBlock.getAnimationProgressRetriever((LidOpenable)blockEntity)).get(f);
        if (chestBlockEntityRenderState.chestType != ChestType.SINGLE) {
            chestBlockEntityRenderState.lightmapCoordinates = ((Int2IntFunction)propertySource.apply(new LightmapCoordinatesRetriever())).applyAsInt(chestBlockEntityRenderState.lightmapCoordinates);
        }
    }

    @Override
    public void render(ChestBlockEntityRenderState chestBlockEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        matrixStack.push();
        matrixStack.translate(0.5f, 0.5f, 0.5f);
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(-chestBlockEntityRenderState.yaw));
        matrixStack.translate(-0.5f, -0.5f, -0.5f);
        float f = chestBlockEntityRenderState.lidAnimationProgress;
        f = 1.0f - f;
        f = 1.0f - f * f * f;
        SpriteIdentifier spriteIdentifier = TexturedRenderLayers.getChestTextureId(chestBlockEntityRenderState.variant, chestBlockEntityRenderState.chestType);
        RenderLayer renderLayer = spriteIdentifier.getRenderLayer(RenderLayers::entityCutout);
        Sprite sprite = this.materials.getSprite(spriteIdentifier);
        if (chestBlockEntityRenderState.chestType != ChestType.SINGLE) {
            if (chestBlockEntityRenderState.chestType == ChestType.LEFT) {
                orderedRenderCommandQueue.submitModel(this.doubleChestLeft, Float.valueOf(f), matrixStack, renderLayer, chestBlockEntityRenderState.lightmapCoordinates, OverlayTexture.DEFAULT_UV, -1, sprite, 0, chestBlockEntityRenderState.crumblingOverlay);
            } else {
                orderedRenderCommandQueue.submitModel(this.doubleChestRight, Float.valueOf(f), matrixStack, renderLayer, chestBlockEntityRenderState.lightmapCoordinates, OverlayTexture.DEFAULT_UV, -1, sprite, 0, chestBlockEntityRenderState.crumblingOverlay);
            }
        } else {
            orderedRenderCommandQueue.submitModel(this.singleChest, Float.valueOf(f), matrixStack, renderLayer, chestBlockEntityRenderState.lightmapCoordinates, OverlayTexture.DEFAULT_UV, -1, sprite, 0, chestBlockEntityRenderState.crumblingOverlay);
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
            return switch (copperChestBlock.getOxidationLevel()) {
                default -> throw new MatchException(null, null);
                case Oxidizable.OxidationLevel.UNAFFECTED -> ChestBlockEntityRenderState.Variant.COPPER_UNAFFECTED;
                case Oxidizable.OxidationLevel.EXPOSED -> ChestBlockEntityRenderState.Variant.COPPER_EXPOSED;
                case Oxidizable.OxidationLevel.WEATHERED -> ChestBlockEntityRenderState.Variant.COPPER_WEATHERED;
                case Oxidizable.OxidationLevel.OXIDIZED -> ChestBlockEntityRenderState.Variant.COPPER_OXIDIZED;
            };
        }
        return ChestBlockEntityRenderState.Variant.REGULAR;
    }

    @Override
    public /* synthetic */ BlockEntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

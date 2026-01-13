/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.AbstractSkullBlock
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.SkullBlock
 *  net.minecraft.block.SkullBlock$SkullType
 *  net.minecraft.block.SkullBlock$Type
 *  net.minecraft.block.WallSkullBlock
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.SkullBlockEntity
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.client.render.block.entity.BlockEntityRenderer
 *  net.minecraft.client.render.block.entity.BlockEntityRendererFactory$Context
 *  net.minecraft.client.render.block.entity.SkullBlockEntityModel
 *  net.minecraft.client.render.block.entity.SkullBlockEntityModel$SkullModelState
 *  net.minecraft.client.render.block.entity.SkullBlockEntityRenderer
 *  net.minecraft.client.render.block.entity.SkullBlockEntityRenderer$1
 *  net.minecraft.client.render.block.entity.state.BlockEntityRenderState
 *  net.minecraft.client.render.block.entity.state.SkullBlockEntityRenderState
 *  net.minecraft.client.render.command.ModelCommandRenderer$CrumblingOverlayCommand
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.model.DragonHeadEntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.LoadedEntityModels
 *  net.minecraft.client.render.entity.model.PiglinHeadEntityModel
 *  net.minecraft.client.render.entity.model.SkullEntityModel
 *  net.minecraft.client.render.state.CameraRenderState
 *  net.minecraft.client.texture.PlayerSkinCache
 *  net.minecraft.client.util.DefaultSkinHelper
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.component.type.ProfileComponent
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.Util
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.RotationPropertyHelper
 *  net.minecraft.util.math.Vec3d
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.block.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.WallSkullBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.SkullBlockEntityModel;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.block.entity.state.SkullBlockEntityRenderState;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.model.DragonHeadEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.model.PiglinHeadEntityModel;
import net.minecraft.client.render.entity.model.SkullEntityModel;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.texture.PlayerSkinCache;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationPropertyHelper;
import net.minecraft.util.math.Vec3d;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class SkullBlockEntityRenderer
implements BlockEntityRenderer<SkullBlockEntity, SkullBlockEntityRenderState> {
    private final Function<SkullBlock.SkullType, SkullBlockEntityModel> models;
    private static final Map<SkullBlock.SkullType, Identifier> TEXTURES = (Map)Util.make((Object)Maps.newHashMap(), map -> {
        map.put(SkullBlock.Type.SKELETON, Identifier.ofVanilla((String)"textures/entity/skeleton/skeleton.png"));
        map.put(SkullBlock.Type.WITHER_SKELETON, Identifier.ofVanilla((String)"textures/entity/skeleton/wither_skeleton.png"));
        map.put(SkullBlock.Type.ZOMBIE, Identifier.ofVanilla((String)"textures/entity/zombie/zombie.png"));
        map.put(SkullBlock.Type.CREEPER, Identifier.ofVanilla((String)"textures/entity/creeper/creeper.png"));
        map.put(SkullBlock.Type.DRAGON, Identifier.ofVanilla((String)"textures/entity/enderdragon/dragon.png"));
        map.put(SkullBlock.Type.PIGLIN, Identifier.ofVanilla((String)"textures/entity/piglin/piglin.png"));
        map.put(SkullBlock.Type.PLAYER, DefaultSkinHelper.getTexture());
    });
    private final PlayerSkinCache skinCache;

    public static @Nullable SkullBlockEntityModel getModels(LoadedEntityModels models, SkullBlock.SkullType type) {
        if (type instanceof SkullBlock.Type) {
            SkullBlock.Type type2 = (SkullBlock.Type)type;
            return switch (1.field_55285[type2.ordinal()]) {
                default -> throw new MatchException(null, null);
                case 1 -> new SkullEntityModel(models.getModelPart(EntityModelLayers.SKELETON_SKULL));
                case 2 -> new SkullEntityModel(models.getModelPart(EntityModelLayers.WITHER_SKELETON_SKULL));
                case 3 -> new SkullEntityModel(models.getModelPart(EntityModelLayers.PLAYER_HEAD));
                case 4 -> new SkullEntityModel(models.getModelPart(EntityModelLayers.ZOMBIE_HEAD));
                case 5 -> new SkullEntityModel(models.getModelPart(EntityModelLayers.CREEPER_HEAD));
                case 6 -> new DragonHeadEntityModel(models.getModelPart(EntityModelLayers.DRAGON_SKULL));
                case 7 -> new PiglinHeadEntityModel(models.getModelPart(EntityModelLayers.PIGLIN_HEAD));
            };
        }
        return null;
    }

    public SkullBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        LoadedEntityModels loadedEntityModels = context.loadedEntityModels();
        this.skinCache = context.playerSkinRenderCache();
        this.models = Util.memoize(type -> SkullBlockEntityRenderer.getModels((LoadedEntityModels)loadedEntityModels, (SkullBlock.SkullType)type));
    }

    public SkullBlockEntityRenderState createRenderState() {
        return new SkullBlockEntityRenderState();
    }

    public void updateRenderState(SkullBlockEntity skullBlockEntity, SkullBlockEntityRenderState skullBlockEntityRenderState, float f, Vec3d vec3d, // Could not load outer class - annotation placement on inner may be incorrect
     @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlayCommand) {
        super.updateRenderState((BlockEntity)skullBlockEntity, (BlockEntityRenderState)skullBlockEntityRenderState, f, vec3d, crumblingOverlayCommand);
        skullBlockEntityRenderState.poweredTicks = skullBlockEntity.getPoweredTicks(f);
        BlockState blockState = skullBlockEntity.getCachedState();
        boolean bl = blockState.getBlock() instanceof WallSkullBlock;
        skullBlockEntityRenderState.facing = bl ? (Direction)blockState.get((Property)WallSkullBlock.FACING) : null;
        int i = bl ? RotationPropertyHelper.fromDirection((Direction)skullBlockEntityRenderState.facing.getOpposite()) : (Integer)blockState.get((Property)SkullBlock.ROTATION);
        skullBlockEntityRenderState.yaw = RotationPropertyHelper.toDegrees((int)i);
        skullBlockEntityRenderState.skullType = ((AbstractSkullBlock)blockState.getBlock()).getSkullType();
        skullBlockEntityRenderState.renderLayer = this.renderSkull(skullBlockEntityRenderState.skullType, skullBlockEntity);
    }

    public void render(SkullBlockEntityRenderState skullBlockEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        SkullBlockEntityModel skullBlockEntityModel = (SkullBlockEntityModel)this.models.apply(skullBlockEntityRenderState.skullType);
        SkullBlockEntityRenderer.render((Direction)skullBlockEntityRenderState.facing, (float)skullBlockEntityRenderState.yaw, (float)skullBlockEntityRenderState.poweredTicks, (MatrixStack)matrixStack, (OrderedRenderCommandQueue)orderedRenderCommandQueue, (int)skullBlockEntityRenderState.lightmapCoordinates, (SkullBlockEntityModel)skullBlockEntityModel, (RenderLayer)skullBlockEntityRenderState.renderLayer, (int)0, (ModelCommandRenderer.CrumblingOverlayCommand)skullBlockEntityRenderState.crumblingOverlay);
    }

    public static void render(@Nullable Direction facing, float yaw, float poweredTicks, MatrixStack matrices, OrderedRenderCommandQueue queue, int light, SkullBlockEntityModel model, RenderLayer renderLayer, int outlineColor, // Could not load outer class - annotation placement on inner may be incorrect
     @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay) {
        matrices.push();
        if (facing == null) {
            matrices.translate(0.5f, 0.0f, 0.5f);
        } else {
            float f = 0.25f;
            matrices.translate(0.5f - (float)facing.getOffsetX() * 0.25f, 0.25f, 0.5f - (float)facing.getOffsetZ() * 0.25f);
        }
        matrices.scale(-1.0f, -1.0f, 1.0f);
        SkullBlockEntityModel.SkullModelState skullModelState = new SkullBlockEntityModel.SkullModelState();
        skullModelState.poweredTicks = poweredTicks;
        skullModelState.yaw = yaw;
        queue.submitModel((Model)model, (Object)skullModelState, matrices, renderLayer, light, OverlayTexture.DEFAULT_UV, outlineColor, crumblingOverlay);
        matrices.pop();
    }

    private RenderLayer renderSkull(SkullBlock.SkullType skullType, SkullBlockEntity blockEntity) {
        ProfileComponent profileComponent;
        if (skullType == SkullBlock.Type.PLAYER && (profileComponent = blockEntity.getOwner()) != null) {
            return this.skinCache.get(profileComponent).getRenderLayer();
        }
        return SkullBlockEntityRenderer.getCutoutRenderLayer((SkullBlock.SkullType)skullType, null);
    }

    public static RenderLayer getCutoutRenderLayer(SkullBlock.SkullType type, @Nullable Identifier texture) {
        return RenderLayers.entityCutoutNoCullZOffset((Identifier)(texture != null ? texture : (Identifier)TEXTURES.get(type)));
    }

    public static RenderLayer getTranslucentRenderLayer(Identifier texture) {
        return RenderLayers.entityTranslucent((Identifier)texture);
    }

    public /* synthetic */ BlockEntityRenderState createRenderState() {
        return this.createRenderState();
    }
}


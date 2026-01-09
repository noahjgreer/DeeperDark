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
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.DragonHeadEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.model.PiglinHeadEntityModel;
import net.minecraft.client.render.entity.model.SkullEntityModel;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationPropertyHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class SkullBlockEntityRenderer implements BlockEntityRenderer {
   private final Function models;
   private static final Map TEXTURES = (Map)Util.make(Maps.newHashMap(), (map) -> {
      map.put(SkullBlock.Type.SKELETON, Identifier.ofVanilla("textures/entity/skeleton/skeleton.png"));
      map.put(SkullBlock.Type.WITHER_SKELETON, Identifier.ofVanilla("textures/entity/skeleton/wither_skeleton.png"));
      map.put(SkullBlock.Type.ZOMBIE, Identifier.ofVanilla("textures/entity/zombie/zombie.png"));
      map.put(SkullBlock.Type.CREEPER, Identifier.ofVanilla("textures/entity/creeper/creeper.png"));
      map.put(SkullBlock.Type.DRAGON, Identifier.ofVanilla("textures/entity/enderdragon/dragon.png"));
      map.put(SkullBlock.Type.PIGLIN, Identifier.ofVanilla("textures/entity/piglin/piglin.png"));
      map.put(SkullBlock.Type.PLAYER, DefaultSkinHelper.getTexture());
   });

   @Nullable
   public static SkullBlockEntityModel getModels(LoadedEntityModels models, SkullBlock.SkullType type) {
      if (type instanceof SkullBlock.Type) {
         SkullBlock.Type type2 = (SkullBlock.Type)type;
         Object var10000;
         switch (type2) {
            case SKELETON:
               var10000 = new SkullEntityModel(models.getModelPart(EntityModelLayers.SKELETON_SKULL));
               break;
            case WITHER_SKELETON:
               var10000 = new SkullEntityModel(models.getModelPart(EntityModelLayers.WITHER_SKELETON_SKULL));
               break;
            case PLAYER:
               var10000 = new SkullEntityModel(models.getModelPart(EntityModelLayers.PLAYER_HEAD));
               break;
            case ZOMBIE:
               var10000 = new SkullEntityModel(models.getModelPart(EntityModelLayers.ZOMBIE_HEAD));
               break;
            case CREEPER:
               var10000 = new SkullEntityModel(models.getModelPart(EntityModelLayers.CREEPER_HEAD));
               break;
            case DRAGON:
               var10000 = new DragonHeadEntityModel(models.getModelPart(EntityModelLayers.DRAGON_SKULL));
               break;
            case PIGLIN:
               var10000 = new PiglinHeadEntityModel(models.getModelPart(EntityModelLayers.PIGLIN_HEAD));
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         return (SkullBlockEntityModel)var10000;
      } else {
         return null;
      }
   }

   public SkullBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
      LoadedEntityModels loadedEntityModels = context.getLoadedEntityModels();
      this.models = Util.memoize((type) -> {
         return getModels(loadedEntityModels, type);
      });
   }

   public void render(SkullBlockEntity skullBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, Vec3d vec3d) {
      float g = skullBlockEntity.getPoweredTicks(f);
      BlockState blockState = skullBlockEntity.getCachedState();
      boolean bl = blockState.getBlock() instanceof WallSkullBlock;
      Direction direction = bl ? (Direction)blockState.get(WallSkullBlock.FACING) : null;
      int k = bl ? RotationPropertyHelper.fromDirection(direction.getOpposite()) : (Integer)blockState.get(SkullBlock.ROTATION);
      float h = RotationPropertyHelper.toDegrees(k);
      SkullBlock.SkullType skullType = ((AbstractSkullBlock)blockState.getBlock()).getSkullType();
      SkullBlockEntityModel skullBlockEntityModel = (SkullBlockEntityModel)this.models.apply(skullType);
      RenderLayer renderLayer = getRenderLayer(skullType, skullBlockEntity.getOwner());
      renderSkull(direction, h, g, matrixStack, vertexConsumerProvider, i, skullBlockEntityModel, renderLayer);
   }

   public static void renderSkull(@Nullable Direction direction, float yaw, float animationProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, SkullBlockEntityModel model, RenderLayer renderLayer) {
      matrices.push();
      if (direction == null) {
         matrices.translate(0.5F, 0.0F, 0.5F);
      } else {
         float f = 0.25F;
         matrices.translate(0.5F - (float)direction.getOffsetX() * 0.25F, 0.25F, 0.5F - (float)direction.getOffsetZ() * 0.25F);
      }

      matrices.scale(-1.0F, -1.0F, 1.0F);
      VertexConsumer vertexConsumer = vertexConsumers.getBuffer(renderLayer);
      model.setHeadRotation(animationProgress, yaw, 0.0F);
      model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
      matrices.pop();
   }

   public static RenderLayer getRenderLayer(SkullBlock.SkullType type, @Nullable ProfileComponent profile) {
      return type == SkullBlock.Type.PLAYER && profile != null ? getTranslucentRenderLayer(MinecraftClient.getInstance().getSkinProvider().getSkinTextures(profile.gameProfile()).texture()) : getCutoutRenderLayer(type, (Identifier)null);
   }

   public static RenderLayer getCutoutRenderLayer(SkullBlock.SkullType type, @Nullable Identifier texture) {
      return RenderLayer.getEntityCutoutNoCullZOffset(texture != null ? texture : (Identifier)TEXTURES.get(type));
   }

   public static RenderLayer getTranslucentRenderLayer(Identifier texture) {
      return RenderLayer.getEntityTranslucent(texture);
   }
}

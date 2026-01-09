package net.minecraft.client.render.item.model.special;

import com.mojang.serialization.MapCodec;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.SkullBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.SkullBlockEntityModel;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.texture.PlayerSkinProvider;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class PlayerHeadModelRenderer implements SpecialModelRenderer {
   private final Map profileCache = new HashMap();
   private final PlayerSkinProvider playerSkinProvider;
   private final SkullBlockEntityModel model;
   private final Data data;

   PlayerHeadModelRenderer(PlayerSkinProvider playerSkinProvider, SkullBlockEntityModel model, Data data) {
      this.playerSkinProvider = playerSkinProvider;
      this.model = model;
      this.data = data;
   }

   public void render(@Nullable Data data, ItemDisplayContext itemDisplayContext, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, boolean bl) {
      Data data2 = (Data)Objects.requireNonNullElse(data, this.data);
      RenderLayer renderLayer = data2.layer();
      SkullBlockEntityRenderer.renderSkull((Direction)null, 180.0F, 0.0F, matrixStack, vertexConsumerProvider, i, this.model, renderLayer);
   }

   public void collectVertices(Set vertices) {
      MatrixStack matrixStack = new MatrixStack();
      matrixStack.translate(0.5F, 0.0F, 0.5F);
      matrixStack.scale(-1.0F, -1.0F, 1.0F);
      this.model.getRootPart().collectVertices(matrixStack, vertices);
   }

   @Nullable
   public Data getData(ItemStack itemStack) {
      ProfileComponent profileComponent = (ProfileComponent)itemStack.get(DataComponentTypes.PROFILE);
      if (profileComponent == null) {
         return null;
      } else {
         Data data = (Data)this.profileCache.get(profileComponent);
         if (data != null) {
            return data;
         } else {
            ProfileComponent profileComponent2 = profileComponent.resolve();
            return profileComponent2 != null ? this.createData(profileComponent2) : null;
         }
      }
   }

   @Nullable
   private Data createData(ProfileComponent profileComponent) {
      SkinTextures skinTextures = this.playerSkinProvider.getSkinTextures(profileComponent.gameProfile(), (SkinTextures)null);
      if (skinTextures != null) {
         Data data = PlayerHeadModelRenderer.Data.of(skinTextures);
         this.profileCache.put(profileComponent, data);
         return data;
      } else {
         return null;
      }
   }

   // $FF: synthetic method
   @Nullable
   public Object getData(final ItemStack stack) {
      return this.getData(stack);
   }

   @Environment(EnvType.CLIENT)
   public static record Data(RenderLayer layer) {
      public Data(RenderLayer renderLayer) {
         this.layer = renderLayer;
      }

      static Data of(SkinTextures textures) {
         return new Data(SkullBlockEntityRenderer.getTranslucentRenderLayer(textures.texture()));
      }

      public RenderLayer layer() {
         return this.layer;
      }
   }

   @Environment(EnvType.CLIENT)
   public static record Unbaked() implements SpecialModelRenderer.Unbaked {
      public static final MapCodec CODEC = MapCodec.unit(Unbaked::new);

      public MapCodec getCodec() {
         return CODEC;
      }

      @Nullable
      public SpecialModelRenderer bake(LoadedEntityModels entityModels) {
         SkullBlockEntityModel skullBlockEntityModel = SkullBlockEntityRenderer.getModels(entityModels, SkullBlock.Type.PLAYER);
         return skullBlockEntityModel == null ? null : new PlayerHeadModelRenderer(MinecraftClient.getInstance().getSkinProvider(), skullBlockEntityModel, PlayerHeadModelRenderer.Data.of(DefaultSkinHelper.getSteve()));
      }
   }
}

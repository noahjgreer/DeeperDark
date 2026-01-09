package net.minecraft.client.render.item.model.special;

import com.mojang.serialization.MapCodec;
import java.util.Objects;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.model.ShieldEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.ModelBaker;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class ShieldModelRenderer implements SpecialModelRenderer {
   private final ShieldEntityModel model;

   public ShieldModelRenderer(ShieldEntityModel model) {
      this.model = model;
   }

   @Nullable
   public ComponentMap getData(ItemStack itemStack) {
      return itemStack.getImmutableComponents();
   }

   public void render(@Nullable ComponentMap componentMap, ItemDisplayContext itemDisplayContext, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, boolean bl) {
      BannerPatternsComponent bannerPatternsComponent = componentMap != null ? (BannerPatternsComponent)componentMap.getOrDefault(DataComponentTypes.BANNER_PATTERNS, BannerPatternsComponent.DEFAULT) : BannerPatternsComponent.DEFAULT;
      DyeColor dyeColor = componentMap != null ? (DyeColor)componentMap.get(DataComponentTypes.BASE_COLOR) : null;
      boolean bl2 = !bannerPatternsComponent.layers().isEmpty() || dyeColor != null;
      matrixStack.push();
      matrixStack.scale(1.0F, -1.0F, -1.0F);
      SpriteIdentifier spriteIdentifier = bl2 ? ModelBaker.SHIELD_BASE : ModelBaker.SHIELD_BASE_NO_PATTERN;
      VertexConsumer vertexConsumer = spriteIdentifier.getSprite().getTextureSpecificVertexConsumer(ItemRenderer.getItemGlintConsumer(vertexConsumerProvider, this.model.getLayer(spriteIdentifier.getAtlasId()), itemDisplayContext == ItemDisplayContext.GUI, bl));
      this.model.getHandle().render(matrixStack, vertexConsumer, i, j);
      if (bl2) {
         BannerBlockEntityRenderer.renderCanvas(matrixStack, vertexConsumerProvider, i, j, this.model.getPlate(), spriteIdentifier, false, (DyeColor)Objects.requireNonNullElse(dyeColor, DyeColor.WHITE), bannerPatternsComponent, bl, false);
      } else {
         this.model.getPlate().render(matrixStack, vertexConsumer, i, j);
      }

      matrixStack.pop();
   }

   public void collectVertices(Set vertices) {
      MatrixStack matrixStack = new MatrixStack();
      matrixStack.scale(1.0F, -1.0F, -1.0F);
      this.model.getRootPart().collectVertices(matrixStack, vertices);
   }

   // $FF: synthetic method
   @Nullable
   public Object getData(final ItemStack stack) {
      return this.getData(stack);
   }

   @Environment(EnvType.CLIENT)
   public static record Unbaked() implements SpecialModelRenderer.Unbaked {
      public static final Unbaked INSTANCE = new Unbaked();
      public static final MapCodec CODEC;

      public MapCodec getCodec() {
         return CODEC;
      }

      public SpecialModelRenderer bake(LoadedEntityModels entityModels) {
         return new ShieldModelRenderer(new ShieldEntityModel(entityModels.getModelPart(EntityModelLayers.SHIELD)));
      }

      static {
         CODEC = MapCodec.unit(INSTANCE);
      }
   }
}

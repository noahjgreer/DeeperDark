package net.minecraft.client.render.item.model.special;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class BannerModelRenderer implements SpecialModelRenderer {
   private final BannerBlockEntityRenderer blockEntityRenderer;
   private final DyeColor baseColor;

   public BannerModelRenderer(DyeColor baseColor, BannerBlockEntityRenderer blockEntityRenderer) {
      this.blockEntityRenderer = blockEntityRenderer;
      this.baseColor = baseColor;
   }

   @Nullable
   public BannerPatternsComponent getData(ItemStack itemStack) {
      return (BannerPatternsComponent)itemStack.get(DataComponentTypes.BANNER_PATTERNS);
   }

   public void render(@Nullable BannerPatternsComponent bannerPatternsComponent, ItemDisplayContext itemDisplayContext, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, boolean bl) {
      this.blockEntityRenderer.renderAsItem(matrixStack, vertexConsumerProvider, i, j, this.baseColor, (BannerPatternsComponent)Objects.requireNonNullElse(bannerPatternsComponent, BannerPatternsComponent.DEFAULT));
   }

   public void collectVertices(Set vertices) {
      this.blockEntityRenderer.collectVertices(vertices);
   }

   // $FF: synthetic method
   @Nullable
   public Object getData(final ItemStack stack) {
      return this.getData(stack);
   }

   @Environment(EnvType.CLIENT)
   public static record Unbaked(DyeColor baseColor) implements SpecialModelRenderer.Unbaked {
      public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(DyeColor.CODEC.fieldOf("color").forGetter(Unbaked::baseColor)).apply(instance, Unbaked::new);
      });

      public Unbaked(DyeColor dyeColor) {
         this.baseColor = dyeColor;
      }

      public MapCodec getCodec() {
         return CODEC;
      }

      public SpecialModelRenderer bake(LoadedEntityModels entityModels) {
         return new BannerModelRenderer(this.baseColor, new BannerBlockEntityRenderer(entityModels));
      }

      public DyeColor baseColor() {
         return this.baseColor;
      }
   }
}

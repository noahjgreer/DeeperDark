package net.minecraft.client.render.item.model.special;

import com.mojang.serialization.MapCodec;
import java.util.Objects;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.Sherds;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.DecoratedPotBlockEntityRenderer;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class DecoratedPotModelRenderer implements SpecialModelRenderer {
   private final DecoratedPotBlockEntityRenderer blockEntityRenderer;

   public DecoratedPotModelRenderer(DecoratedPotBlockEntityRenderer blockEntityRenderer) {
      this.blockEntityRenderer = blockEntityRenderer;
   }

   @Nullable
   public Sherds getData(ItemStack itemStack) {
      return (Sherds)itemStack.get(DataComponentTypes.POT_DECORATIONS);
   }

   public void render(@Nullable Sherds sherds, ItemDisplayContext itemDisplayContext, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, boolean bl) {
      this.blockEntityRenderer.renderAsItem(matrixStack, vertexConsumerProvider, i, j, (Sherds)Objects.requireNonNullElse(sherds, Sherds.DEFAULT));
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
   public static record Unbaked() implements SpecialModelRenderer.Unbaked {
      public static final MapCodec CODEC = MapCodec.unit(new Unbaked());

      public MapCodec getCodec() {
         return CODEC;
      }

      public SpecialModelRenderer bake(LoadedEntityModels entityModels) {
         return new DecoratedPotModelRenderer(new DecoratedPotBlockEntityRenderer(entityModels));
      }
   }
}

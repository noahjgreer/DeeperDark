package net.minecraft.client.render.item;

import java.util.Iterator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.OverlayVertexConsumer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumers;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MatrixUtil;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class ItemRenderer {
   public static final Identifier ENTITY_ENCHANTMENT_GLINT = Identifier.ofVanilla("textures/misc/enchanted_glint_armor.png");
   public static final Identifier ITEM_ENCHANTMENT_GLINT = Identifier.ofVanilla("textures/misc/enchanted_glint_item.png");
   public static final float field_60154 = 0.5F;
   public static final float field_60155 = 0.75F;
   public static final float field_60156 = 0.0078125F;
   public static final int field_55295 = -1;
   private final ItemModelManager itemModelManager;
   private final ItemRenderState itemRenderState = new ItemRenderState();

   public ItemRenderer(ItemModelManager itemModelManager) {
      this.itemModelManager = itemModelManager;
   }

   public static void renderItem(ItemDisplayContext displayContext, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, int[] tints, List quads, RenderLayer layer, ItemRenderState.Glint glint) {
      VertexConsumer vertexConsumer;
      if (glint == ItemRenderState.Glint.SPECIAL) {
         MatrixStack.Entry entry = matrices.peek().copy();
         if (displayContext == ItemDisplayContext.GUI) {
            MatrixUtil.scale(entry.getPositionMatrix(), 0.5F);
         } else if (displayContext.isFirstPerson()) {
            MatrixUtil.scale(entry.getPositionMatrix(), 0.75F);
         }

         vertexConsumer = getSpecialItemGlintConsumer(vertexConsumers, layer, entry);
      } else {
         vertexConsumer = getItemGlintConsumer(vertexConsumers, layer, true, glint != ItemRenderState.Glint.NONE);
      }

      renderBakedItemQuads(matrices, vertexConsumer, quads, tints, light, overlay);
   }

   public static VertexConsumer getArmorGlintConsumer(VertexConsumerProvider provider, RenderLayer layer, boolean glint) {
      return glint ? VertexConsumers.union(provider.getBuffer(RenderLayer.getArmorEntityGlint()), provider.getBuffer(layer)) : provider.getBuffer(layer);
   }

   private static VertexConsumer getSpecialItemGlintConsumer(VertexConsumerProvider consumers, RenderLayer layer, MatrixStack.Entry matrix) {
      return VertexConsumers.union(new OverlayVertexConsumer(consumers.getBuffer(useTranslucentGlint(layer) ? RenderLayer.getGlintTranslucent() : RenderLayer.getGlint()), matrix, 0.0078125F), consumers.getBuffer(layer));
   }

   public static VertexConsumer getItemGlintConsumer(VertexConsumerProvider vertexConsumers, RenderLayer layer, boolean solid, boolean glint) {
      if (glint) {
         return useTranslucentGlint(layer) ? VertexConsumers.union(vertexConsumers.getBuffer(RenderLayer.getGlintTranslucent()), vertexConsumers.getBuffer(layer)) : VertexConsumers.union(vertexConsumers.getBuffer(solid ? RenderLayer.getGlint() : RenderLayer.getEntityGlint()), vertexConsumers.getBuffer(layer));
      } else {
         return vertexConsumers.getBuffer(layer);
      }
   }

   private static boolean useTranslucentGlint(RenderLayer renderLayer) {
      return MinecraftClient.isFabulousGraphicsOrBetter() && renderLayer == TexturedRenderLayers.getItemEntityTranslucentCull();
   }

   private static int getTint(int[] tints, int index) {
      return index >= 0 && index < tints.length ? tints[index] : -1;
   }

   private static void renderBakedItemQuads(MatrixStack matrices, VertexConsumer vertexConsumer, List quads, int[] tints, int light, int overlay) {
      MatrixStack.Entry entry = matrices.peek();

      BakedQuad bakedQuad;
      float f;
      float g;
      float h;
      float j;
      for(Iterator var7 = quads.iterator(); var7.hasNext(); vertexConsumer.quad(entry, bakedQuad, g, h, j, f, light, overlay)) {
         bakedQuad = (BakedQuad)var7.next();
         if (bakedQuad.hasTint()) {
            int i = getTint(tints, bakedQuad.tintIndex());
            f = (float)ColorHelper.getAlpha(i) / 255.0F;
            g = (float)ColorHelper.getRed(i) / 255.0F;
            h = (float)ColorHelper.getGreen(i) / 255.0F;
            j = (float)ColorHelper.getBlue(i) / 255.0F;
         } else {
            f = 1.0F;
            g = 1.0F;
            h = 1.0F;
            j = 1.0F;
         }
      }

   }

   public void renderItem(ItemStack stack, ItemDisplayContext displayContext, int light, int overlay, MatrixStack matrices, VertexConsumerProvider vertexConsumers, @Nullable World world, int seed) {
      this.renderItem((LivingEntity)null, stack, displayContext, matrices, vertexConsumers, world, light, overlay, seed);
   }

   public void renderItem(@Nullable LivingEntity entity, ItemStack stack, ItemDisplayContext displayContext, MatrixStack matrices, VertexConsumerProvider vertexConsumers, @Nullable World world, int light, int overlay, int seed) {
      this.itemModelManager.clearAndUpdate(this.itemRenderState, stack, displayContext, world, entity, seed);
      this.itemRenderState.render(matrices, vertexConsumers, light, overlay);
   }
}

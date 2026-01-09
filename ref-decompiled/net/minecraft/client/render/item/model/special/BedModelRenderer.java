package net.minecraft.client.render.item.model.special;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BedBlockEntityRenderer;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class BedModelRenderer implements SimpleSpecialModelRenderer {
   private final BedBlockEntityRenderer blockEntityRenderer;
   private final SpriteIdentifier textureId;

   public BedModelRenderer(BedBlockEntityRenderer blockEntityRenderer, SpriteIdentifier textureId) {
      this.blockEntityRenderer = blockEntityRenderer;
      this.textureId = textureId;
   }

   public void render(ItemDisplayContext displayContext, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, boolean glint) {
      this.blockEntityRenderer.renderAsItem(matrices, vertexConsumers, light, overlay, this.textureId);
   }

   public void collectVertices(Set vertices) {
      this.blockEntityRenderer.collectVertices(vertices);
   }

   @Environment(EnvType.CLIENT)
   public static record Unbaked(Identifier texture) implements SpecialModelRenderer.Unbaked {
      public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(Identifier.CODEC.fieldOf("texture").forGetter(Unbaked::texture)).apply(instance, Unbaked::new);
      });

      public Unbaked(DyeColor color) {
         this(TexturedRenderLayers.createColorId(color));
      }

      public Unbaked(Identifier identifier) {
         this.texture = identifier;
      }

      public MapCodec getCodec() {
         return CODEC;
      }

      public SpecialModelRenderer bake(LoadedEntityModels entityModels) {
         return new BedModelRenderer(new BedBlockEntityRenderer(entityModels), TexturedRenderLayers.BED_SPRITE_MAPPER.map(this.texture));
      }

      public Identifier texture() {
         return this.texture;
      }
   }
}

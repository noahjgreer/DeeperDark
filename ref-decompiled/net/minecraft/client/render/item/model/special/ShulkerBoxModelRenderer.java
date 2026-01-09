package net.minecraft.client.render.item.model.special;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.ShulkerBoxBlockEntityRenderer;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class ShulkerBoxModelRenderer implements SimpleSpecialModelRenderer {
   private final ShulkerBoxBlockEntityRenderer blockEntityRenderer;
   private final float openness;
   private final Direction facing;
   private final SpriteIdentifier textureId;

   public ShulkerBoxModelRenderer(ShulkerBoxBlockEntityRenderer blockEntityRenderer, float openness, Direction facing, SpriteIdentifier textureId) {
      this.blockEntityRenderer = blockEntityRenderer;
      this.openness = openness;
      this.facing = facing;
      this.textureId = textureId;
   }

   public void render(ItemDisplayContext displayContext, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, boolean glint) {
      this.blockEntityRenderer.render(matrices, vertexConsumers, light, overlay, this.facing, this.openness, this.textureId);
   }

   public void collectVertices(Set vertices) {
      this.blockEntityRenderer.collectVertices(this.facing, this.openness, vertices);
   }

   @Environment(EnvType.CLIENT)
   public static record Unbaked(Identifier texture, float openness, Direction facing) implements SpecialModelRenderer.Unbaked {
      public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(Identifier.CODEC.fieldOf("texture").forGetter(Unbaked::texture), Codec.FLOAT.optionalFieldOf("openness", 0.0F).forGetter(Unbaked::openness), Direction.CODEC.optionalFieldOf("orientation", Direction.UP).forGetter(Unbaked::facing)).apply(instance, Unbaked::new);
      });

      public Unbaked() {
         this(Identifier.ofVanilla("shulker"), 0.0F, Direction.UP);
      }

      public Unbaked(DyeColor color) {
         this(TexturedRenderLayers.createShulkerId(color), 0.0F, Direction.UP);
      }

      public Unbaked(Identifier identifier, float f, Direction direction) {
         this.texture = identifier;
         this.openness = f;
         this.facing = direction;
      }

      public MapCodec getCodec() {
         return CODEC;
      }

      public SpecialModelRenderer bake(LoadedEntityModels entityModels) {
         return new ShulkerBoxModelRenderer(new ShulkerBoxBlockEntityRenderer(entityModels), this.openness, this.facing, TexturedRenderLayers.SHULKER_SPRITE_MAPPER.map(this.texture));
      }

      public Identifier texture() {
         return this.texture;
      }

      public float openness() {
         return this.openness;
      }

      public Direction facing() {
         return this.facing;
      }
   }
}

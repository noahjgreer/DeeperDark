package net.minecraft.client.render.item.model.special;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.WoodType;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.SpriteMapper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SignModelRenderer implements SimpleSpecialModelRenderer {
   private final Model model;
   private final SpriteIdentifier texture;

   public SignModelRenderer(Model model, SpriteIdentifier texture) {
      this.model = model;
      this.texture = texture;
   }

   public void render(ItemDisplayContext displayContext, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, boolean glint) {
      SignBlockEntityRenderer.renderAsItem(matrices, vertexConsumers, light, overlay, this.model, this.texture);
   }

   public void collectVertices(Set vertices) {
      MatrixStack matrixStack = new MatrixStack();
      SignBlockEntityRenderer.setTransformsForItem(matrixStack);
      this.model.getRootPart().collectVertices(matrixStack, vertices);
   }

   @Environment(EnvType.CLIENT)
   public static record Unbaked(WoodType woodType, Optional texture) implements SpecialModelRenderer.Unbaked {
      public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(WoodType.CODEC.fieldOf("wood_type").forGetter(Unbaked::woodType), Identifier.CODEC.optionalFieldOf("texture").forGetter(Unbaked::texture)).apply(instance, Unbaked::new);
      });

      public Unbaked(WoodType woodType) {
         this(woodType, Optional.empty());
      }

      public Unbaked(WoodType woodType, Optional optional) {
         this.woodType = woodType;
         this.texture = optional;
      }

      public MapCodec getCodec() {
         return CODEC;
      }

      public SpecialModelRenderer bake(LoadedEntityModels entityModels) {
         Model model = SignBlockEntityRenderer.createSignModel(entityModels, this.woodType, true);
         Optional var10000 = this.texture;
         SpriteMapper var10001 = TexturedRenderLayers.SIGN_SPRITE_MAPPER;
         Objects.requireNonNull(var10001);
         SpriteIdentifier spriteIdentifier = (SpriteIdentifier)var10000.map(var10001::map).orElseGet(() -> {
            return TexturedRenderLayers.getSignTextureId(this.woodType);
         });
         return new SignModelRenderer(model, spriteIdentifier);
      }

      public WoodType woodType() {
         return this.woodType;
      }

      public Optional texture() {
         return this.texture;
      }
   }
}

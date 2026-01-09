package net.minecraft.client.render.item.model.special;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.SkullBlock;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.SkullBlockEntityModel;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class HeadModelRenderer implements SimpleSpecialModelRenderer {
   private final SkullBlockEntityModel model;
   private final float animation;
   private final RenderLayer renderLayer;

   public HeadModelRenderer(SkullBlockEntityModel model, float animation, RenderLayer renderLayer) {
      this.model = model;
      this.animation = animation;
      this.renderLayer = renderLayer;
   }

   public void render(ItemDisplayContext displayContext, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, boolean glint) {
      SkullBlockEntityRenderer.renderSkull((Direction)null, 180.0F, this.animation, matrices, vertexConsumers, light, this.model, this.renderLayer);
   }

   public void collectVertices(Set vertices) {
      MatrixStack matrixStack = new MatrixStack();
      matrixStack.translate(0.5F, 0.0F, 0.5F);
      matrixStack.scale(-1.0F, -1.0F, 1.0F);
      this.model.setHeadRotation(this.animation, 180.0F, 0.0F);
      this.model.getRootPart().collectVertices(matrixStack, vertices);
   }

   @Environment(EnvType.CLIENT)
   public static record Unbaked(SkullBlock.SkullType kind, Optional textureOverride, float animation) implements SpecialModelRenderer.Unbaked {
      public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(SkullBlock.SkullType.CODEC.fieldOf("kind").forGetter(Unbaked::kind), Identifier.CODEC.optionalFieldOf("texture").forGetter(Unbaked::textureOverride), Codec.FLOAT.optionalFieldOf("animation", 0.0F).forGetter(Unbaked::animation)).apply(instance, Unbaked::new);
      });

      public Unbaked(SkullBlock.SkullType kind) {
         this(kind, Optional.empty(), 0.0F);
      }

      public Unbaked(SkullBlock.SkullType skullType, Optional optional, float f) {
         this.kind = skullType;
         this.textureOverride = optional;
         this.animation = f;
      }

      public MapCodec getCodec() {
         return CODEC;
      }

      @Nullable
      public SpecialModelRenderer bake(LoadedEntityModels entityModels) {
         SkullBlockEntityModel skullBlockEntityModel = SkullBlockEntityRenderer.getModels(entityModels, this.kind);
         Identifier identifier = (Identifier)this.textureOverride.map((id) -> {
            return id.withPath((texture) -> {
               return "textures/entity/" + texture + ".png";
            });
         }).orElse((Object)null);
         if (skullBlockEntityModel == null) {
            return null;
         } else {
            RenderLayer renderLayer = SkullBlockEntityRenderer.getCutoutRenderLayer(this.kind, identifier);
            return new HeadModelRenderer(skullBlockEntityModel, this.animation, renderLayer);
         }
      }

      public SkullBlock.SkullType kind() {
         return this.kind;
      }

      public Optional textureOverride() {
         return this.textureOverride;
      }

      public float animation() {
         return this.animation;
      }
   }
}

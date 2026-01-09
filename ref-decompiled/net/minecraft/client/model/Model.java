package net.minecraft.client.model;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public abstract class Model {
   protected final ModelPart root;
   protected final Function layerFactory;
   private final List parts;

   public Model(ModelPart root, Function layerFactory) {
      this.root = root;
      this.layerFactory = layerFactory;
      this.parts = root.traverse();
   }

   public final RenderLayer getLayer(Identifier texture) {
      return (RenderLayer)this.layerFactory.apply(texture);
   }

   public final void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
      this.getRootPart().render(matrices, vertices, light, overlay, color);
   }

   public final void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay) {
      this.render(matrices, vertices, light, overlay, -1);
   }

   public final ModelPart getRootPart() {
      return this.root;
   }

   public final List getParts() {
      return this.parts;
   }

   public final void resetTransforms() {
      Iterator var1 = this.parts.iterator();

      while(var1.hasNext()) {
         ModelPart modelPart = (ModelPart)var1.next();
         modelPart.resetTransform();
      }

   }

   @Environment(EnvType.CLIENT)
   public static class SinglePartModel extends Model {
      public SinglePartModel(ModelPart part, Function layerFactory) {
         super(part, layerFactory);
      }
   }
}

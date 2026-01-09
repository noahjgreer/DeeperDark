package net.minecraft.client.render.model;

import java.util.Iterator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public record UnbakedGeometry(List elements) implements Geometry {
   public UnbakedGeometry(List list) {
      this.elements = list;
   }

   public BakedGeometry bake(ModelTextures modelTextures, Baker baker, ModelBakeSettings modelBakeSettings, SimpleModel simpleModel) {
      return bakeGeometry(this.elements, modelTextures, baker.getSpriteGetter(), modelBakeSettings, simpleModel);
   }

   public static BakedGeometry bakeGeometry(List elements, ModelTextures textures, ErrorCollectingSpriteGetter errorCollectingSpriteGetter, ModelBakeSettings settings, SimpleModel model) {
      BakedGeometry.Builder builder = new BakedGeometry.Builder();
      Iterator var6 = elements.iterator();

      while(var6.hasNext()) {
         ModelElement modelElement = (ModelElement)var6.next();
         modelElement.faces().forEach((direction, modelElementFace) -> {
            Sprite sprite = errorCollectingSpriteGetter.get(textures, modelElementFace.textureId(), model);
            if (modelElementFace.cullFace() == null) {
               builder.add(bakeQuad(modelElement, modelElementFace, sprite, direction, settings));
            } else {
               builder.add(Direction.transform(settings.getRotation().getMatrix(), modelElementFace.cullFace()), bakeQuad(modelElement, modelElementFace, sprite, direction, settings));
            }

         });
      }

      return builder.build();
   }

   private static BakedQuad bakeQuad(ModelElement element, ModelElementFace face, Sprite sprite, Direction facing, ModelBakeSettings settings) {
      return BakedQuadFactory.bake(element.from(), element.to(), face, sprite, facing, settings, element.rotation(), element.shade(), element.lightEmission());
   }

   public List elements() {
      return this.elements;
   }
}

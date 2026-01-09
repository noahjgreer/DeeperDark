package net.minecraft.client.model;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ModelPartData {
   private final List cuboidData;
   private final ModelTransform rotationData;
   private final Map children = Maps.newHashMap();

   ModelPartData(List cuboidData, ModelTransform rotationData) {
      this.cuboidData = cuboidData;
      this.rotationData = rotationData;
   }

   public ModelPartData addChild(String name, ModelPartBuilder builder, ModelTransform rotationData) {
      ModelPartData modelPartData = new ModelPartData(builder.build(), rotationData);
      return this.addChild(name, modelPartData);
   }

   public ModelPartData addChild(String name, ModelPartData data) {
      ModelPartData modelPartData = (ModelPartData)this.children.put(name, data);
      if (modelPartData != null) {
         data.children.putAll(modelPartData.children);
      }

      return data;
   }

   public ModelPartData addChild(String name) {
      ModelPartData modelPartData = (ModelPartData)this.children.get(name);
      if (modelPartData == null) {
         throw new IllegalArgumentException("No child with name: " + name);
      } else {
         return this.addChild(name, ModelPartBuilder.create(), modelPartData.rotationData);
      }
   }

   public ModelPart createPart(int textureWidth, int textureHeight) {
      Object2ObjectArrayMap object2ObjectArrayMap = (Object2ObjectArrayMap)this.children.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, (entry) -> {
         return ((ModelPartData)entry.getValue()).createPart(textureWidth, textureHeight);
      }, (modelPartx, modelPart2) -> {
         return modelPartx;
      }, Object2ObjectArrayMap::new));
      List list = this.cuboidData.stream().map((modelCuboidData) -> {
         return modelCuboidData.createCuboid(textureWidth, textureHeight);
      }).toList();
      ModelPart modelPart = new ModelPart(list, object2ObjectArrayMap);
      modelPart.setDefaultTransform(this.rotationData);
      modelPart.setTransform(this.rotationData);
      return modelPart;
   }

   public ModelPartData getChild(String name) {
      return (ModelPartData)this.children.get(name);
   }

   public Set getChildren() {
      return this.children.entrySet();
   }

   public ModelPartData applyTransformer(UnaryOperator transformer) {
      ModelPartData modelPartData = new ModelPartData(this.cuboidData, (ModelTransform)transformer.apply(this.rotationData));
      modelPartData.children.putAll(this.children);
      return modelPartData;
   }
}

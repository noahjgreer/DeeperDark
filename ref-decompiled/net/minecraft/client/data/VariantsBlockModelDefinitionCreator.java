package net.minecraft.client.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.render.model.json.BlockModelDefinition;
import net.minecraft.client.render.model.json.ModelVariantOperator;
import net.minecraft.client.render.model.json.WeightedVariant;

@Environment(EnvType.CLIENT)
public class VariantsBlockModelDefinitionCreator implements BlockModelDefinitionCreator {
   private final Block block;
   private final List variants;
   private final Set definedProperties;

   VariantsBlockModelDefinitionCreator(Block block, List variants, Set definedProperties) {
      this.block = block;
      this.variants = variants;
      this.definedProperties = definedProperties;
   }

   static Set validateAndAddProperties(Set definedProperties, Block block, BlockStateVariantMap variantMap) {
      List list = variantMap.getProperties();
      list.forEach((property) -> {
         String var10002;
         if (block.getStateManager().getProperty(property.getName()) != property) {
            var10002 = String.valueOf(property);
            throw new IllegalStateException("Property " + var10002 + " is not defined for block " + String.valueOf(block));
         } else if (definedProperties.contains(property)) {
            var10002 = String.valueOf(property);
            throw new IllegalStateException("Values of property " + var10002 + " already defined for block " + String.valueOf(block));
         }
      });
      Set set = new HashSet(definedProperties);
      set.addAll(list);
      return set;
   }

   public VariantsBlockModelDefinitionCreator coordinate(BlockStateVariantMap variantMap) {
      Set set = validateAndAddProperties(this.definedProperties, this.block, variantMap);
      List list = this.variants.stream().flatMap((variant) -> {
         return variant.apply(variantMap);
      }).toList();
      return new VariantsBlockModelDefinitionCreator(this.block, list, set);
   }

   public VariantsBlockModelDefinitionCreator apply(ModelVariantOperator operator) {
      List list = this.variants.stream().flatMap((variant) -> {
         return variant.apply(operator);
      }).toList();
      return new VariantsBlockModelDefinitionCreator(this.block, list, this.definedProperties);
   }

   public BlockModelDefinition createBlockModelDefinition() {
      Map map = new HashMap();
      Iterator var2 = this.variants.iterator();

      while(var2.hasNext()) {
         Entry entry = (Entry)var2.next();
         map.put(entry.properties.asString(), entry.variant.toModel());
      }

      return new BlockModelDefinition(Optional.of(new BlockModelDefinition.Variants(map)), Optional.empty());
   }

   public Block getBlock() {
      return this.block;
   }

   public static Empty of(Block block) {
      return new Empty(block);
   }

   public static VariantsBlockModelDefinitionCreator of(Block block, WeightedVariant model) {
      return new VariantsBlockModelDefinitionCreator(block, List.of(new Entry(PropertiesMap.EMPTY, model)), Set.of());
   }

   @Environment(EnvType.CLIENT)
   static record Entry(PropertiesMap properties, WeightedVariant variant) {
      final PropertiesMap properties;
      final WeightedVariant variant;

      Entry(PropertiesMap propertiesMap, WeightedVariant weightedVariant) {
         this.properties = propertiesMap;
         this.variant = weightedVariant;
      }

      public Stream apply(BlockStateVariantMap operatorMap) {
         return operatorMap.getVariants().entrySet().stream().map((variant) -> {
            PropertiesMap propertiesMap = this.properties.copyOf((PropertiesMap)variant.getKey());
            WeightedVariant weightedVariant = this.variant.apply((ModelVariantOperator)variant.getValue());
            return new Entry(propertiesMap, weightedVariant);
         });
      }

      public Stream apply(ModelVariantOperator operator) {
         return Stream.of(new Entry(this.properties, this.variant.apply(operator)));
      }

      public PropertiesMap properties() {
         return this.properties;
      }

      public WeightedVariant variant() {
         return this.variant;
      }
   }

   @Environment(EnvType.CLIENT)
   public static class Empty {
      private final Block block;

      public Empty(Block block) {
         this.block = block;
      }

      public VariantsBlockModelDefinitionCreator with(BlockStateVariantMap variantMap) {
         Set set = VariantsBlockModelDefinitionCreator.validateAndAddProperties(Set.of(), this.block, variantMap);
         List list = variantMap.getVariants().entrySet().stream().map((entry) -> {
            return new Entry((PropertiesMap)entry.getKey(), (WeightedVariant)entry.getValue());
         }).toList();
         return new VariantsBlockModelDefinitionCreator(this.block, list, set);
      }
   }
}

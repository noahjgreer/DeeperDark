package net.minecraft.client.data;

import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Function4;
import com.mojang.datafixers.util.Function5;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.state.property.Property;

@Environment(EnvType.CLIENT)
public abstract class BlockStateVariantMap {
   private final Map variants = new HashMap();

   protected void register(PropertiesMap properties, Object variant) {
      Object object = this.variants.put(properties, variant);
      if (object != null) {
         throw new IllegalStateException("Value " + String.valueOf(properties) + " is already defined");
      }
   }

   Map getVariants() {
      this.validate();
      return Map.copyOf(this.variants);
   }

   private void validate() {
      List list = this.getProperties();
      Stream stream = Stream.of(PropertiesMap.EMPTY);

      Property property;
      for(Iterator var3 = list.iterator(); var3.hasNext(); stream = stream.flatMap((propertiesMap) -> {
         Stream var10000 = property.stream();
         Objects.requireNonNull(propertiesMap);
         return var10000.map(propertiesMap::withValue);
      })) {
         property = (Property)var3.next();
      }

      List list2 = stream.filter((propertiesMap) -> {
         return !this.variants.containsKey(propertiesMap);
      }).toList();
      if (!list2.isEmpty()) {
         throw new IllegalStateException("Missing definition for properties: " + String.valueOf(list2));
      }
   }

   abstract List getProperties();

   public static SingleProperty models(Property property) {
      return new SingleProperty(property);
   }

   public static DoubleProperty models(Property property1, Property property2) {
      return new DoubleProperty(property1, property2);
   }

   public static TripleProperty models(Property property1, Property property2, Property property3) {
      return new TripleProperty(property1, property2, property3);
   }

   public static QuadrupleProperty models(Property property1, Property property2, Property property3, Property property4) {
      return new QuadrupleProperty(property1, property2, property3, property4);
   }

   public static QuintupleProperty models(Property property1, Property property2, Property property3, Property property4, Property property5) {
      return new QuintupleProperty(property1, property2, property3, property4, property5);
   }

   public static SingleProperty operations(Property property) {
      return new SingleProperty(property);
   }

   public static DoubleProperty operations(Property property1, Property property2) {
      return new DoubleProperty(property1, property2);
   }

   public static TripleProperty operations(Property property1, Property property2, Property property3) {
      return new TripleProperty(property1, property2, property3);
   }

   public static QuadrupleProperty operations(Property property1, Property property2, Property property3, Property property4) {
      return new QuadrupleProperty(property1, property2, property3, property4);
   }

   public static QuintupleProperty operations(Property property1, Property property2, Property property3, Property property4, Property property5) {
      return new QuintupleProperty(property1, property2, property3, property4, property5);
   }

   @Environment(EnvType.CLIENT)
   public static class SingleProperty extends BlockStateVariantMap {
      private final Property property;

      SingleProperty(Property property) {
         this.property = property;
      }

      public List getProperties() {
         return List.of(this.property);
      }

      public SingleProperty register(Comparable property, Object variant) {
         PropertiesMap propertiesMap = PropertiesMap.withValues(this.property.createValue(property));
         this.register(propertiesMap, variant);
         return this;
      }

      public BlockStateVariantMap generate(Function variantFactory) {
         this.property.getValues().forEach((value) -> {
            this.register(value, variantFactory.apply(value));
         });
         return this;
      }
   }

   @Environment(EnvType.CLIENT)
   public static class DoubleProperty extends BlockStateVariantMap {
      private final Property first;
      private final Property second;

      DoubleProperty(Property first, Property second) {
         this.first = first;
         this.second = second;
      }

      public List getProperties() {
         return List.of(this.first, this.second);
      }

      public DoubleProperty register(Comparable firstProperty, Comparable secondProperty, Object variant) {
         PropertiesMap propertiesMap = PropertiesMap.withValues(this.first.createValue(firstProperty), this.second.createValue(secondProperty));
         this.register(propertiesMap, variant);
         return this;
      }

      public BlockStateVariantMap generate(BiFunction variantFactory) {
         this.first.getValues().forEach((firstValue) -> {
            this.second.getValues().forEach((secondValue) -> {
               this.register(firstValue, secondValue, variantFactory.apply(firstValue, secondValue));
            });
         });
         return this;
      }
   }

   @Environment(EnvType.CLIENT)
   public static class TripleProperty extends BlockStateVariantMap {
      private final Property first;
      private final Property second;
      private final Property third;

      TripleProperty(Property first, Property second, Property third) {
         this.first = first;
         this.second = second;
         this.third = third;
      }

      public List getProperties() {
         return List.of(this.first, this.second, this.third);
      }

      public TripleProperty register(Comparable firstProperty, Comparable secondProperty, Comparable thirdProperty, Object variant) {
         PropertiesMap propertiesMap = PropertiesMap.withValues(this.first.createValue(firstProperty), this.second.createValue(secondProperty), this.third.createValue(thirdProperty));
         this.register(propertiesMap, variant);
         return this;
      }

      public BlockStateVariantMap generate(Function3 variantFactory) {
         this.first.getValues().forEach((firstValue) -> {
            this.second.getValues().forEach((secondValue) -> {
               this.third.getValues().forEach((thirdValue) -> {
                  this.register(firstValue, secondValue, thirdValue, variantFactory.apply(firstValue, secondValue, thirdValue));
               });
            });
         });
         return this;
      }
   }

   @Environment(EnvType.CLIENT)
   public static class QuadrupleProperty extends BlockStateVariantMap {
      private final Property first;
      private final Property second;
      private final Property third;
      private final Property fourth;

      QuadrupleProperty(Property first, Property second, Property third, Property fourth) {
         this.first = first;
         this.second = second;
         this.third = third;
         this.fourth = fourth;
      }

      public List getProperties() {
         return List.of(this.first, this.second, this.third, this.fourth);
      }

      public QuadrupleProperty register(Comparable firstProperty, Comparable secondProperty, Comparable thirdProperty, Comparable fourthProperty, Object variant) {
         PropertiesMap propertiesMap = PropertiesMap.withValues(this.first.createValue(firstProperty), this.second.createValue(secondProperty), this.third.createValue(thirdProperty), this.fourth.createValue(fourthProperty));
         this.register(propertiesMap, variant);
         return this;
      }

      public BlockStateVariantMap generate(Function4 variantFactory) {
         this.first.getValues().forEach((firstValue) -> {
            this.second.getValues().forEach((secondValue) -> {
               this.third.getValues().forEach((thirdValue) -> {
                  this.fourth.getValues().forEach((fourthValue) -> {
                     this.register(firstValue, secondValue, thirdValue, fourthValue, variantFactory.apply(firstValue, secondValue, thirdValue, fourthValue));
                  });
               });
            });
         });
         return this;
      }
   }

   @Environment(EnvType.CLIENT)
   public static class QuintupleProperty extends BlockStateVariantMap {
      private final Property first;
      private final Property second;
      private final Property third;
      private final Property fourth;
      private final Property fifth;

      QuintupleProperty(Property first, Property second, Property third, Property fourth, Property fifth) {
         this.first = first;
         this.second = second;
         this.third = third;
         this.fourth = fourth;
         this.fifth = fifth;
      }

      public List getProperties() {
         return List.of(this.first, this.second, this.third, this.fourth, this.fifth);
      }

      public QuintupleProperty register(Comparable firstProperty, Comparable secondProperty, Comparable thirdProperty, Comparable fourthProperty, Comparable fifthProperty, Object variant) {
         PropertiesMap propertiesMap = PropertiesMap.withValues(this.first.createValue(firstProperty), this.second.createValue(secondProperty), this.third.createValue(thirdProperty), this.fourth.createValue(fourthProperty), this.fifth.createValue(fifthProperty));
         this.register(propertiesMap, variant);
         return this;
      }

      public BlockStateVariantMap generate(Function5 variantFactory) {
         this.first.getValues().forEach((firstValue) -> {
            this.second.getValues().forEach((secondValue) -> {
               this.third.getValues().forEach((thirdValue) -> {
                  this.fourth.getValues().forEach((fourthValue) -> {
                     this.fifth.getValues().forEach((fifthValue) -> {
                        this.register(firstValue, secondValue, thirdValue, fourthValue, fifthValue, variantFactory.apply(firstValue, secondValue, thirdValue, fourthValue, fifthValue));
                     });
                  });
               });
            });
         });
         return this;
      }
   }
}

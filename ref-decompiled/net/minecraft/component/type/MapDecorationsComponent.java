package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import net.minecraft.item.map.MapDecorationType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Util;

public record MapDecorationsComponent(Map decorations) {
   public static final MapDecorationsComponent DEFAULT = new MapDecorationsComponent(Map.of());
   public static final Codec CODEC;

   public MapDecorationsComponent(Map map) {
      this.decorations = map;
   }

   public MapDecorationsComponent with(String id, Decoration decoration) {
      return new MapDecorationsComponent(Util.mapWith(this.decorations, id, decoration));
   }

   public Map decorations() {
      return this.decorations;
   }

   static {
      CODEC = Codec.unboundedMap(Codec.STRING, MapDecorationsComponent.Decoration.CODEC).xmap(MapDecorationsComponent::new, MapDecorationsComponent::decorations);
   }

   public static record Decoration(RegistryEntry type, double x, double z, float rotation) {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(MapDecorationType.CODEC.fieldOf("type").forGetter(Decoration::type), Codec.DOUBLE.fieldOf("x").forGetter(Decoration::x), Codec.DOUBLE.fieldOf("z").forGetter(Decoration::z), Codec.FLOAT.fieldOf("rotation").forGetter(Decoration::rotation)).apply(instance, Decoration::new);
      });

      public Decoration(RegistryEntry registryEntry, double d, double e, float f) {
         this.type = registryEntry;
         this.x = d;
         this.z = e;
         this.rotation = f;
      }

      public RegistryEntry type() {
         return this.type;
      }

      public double x() {
         return this.x;
      }

      public double z() {
         return this.z;
      }

      public float rotation() {
         return this.rotation;
      }
   }
}

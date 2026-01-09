package net.minecraft.client.session.telemetry;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class PropertyMap {
   final Map backingMap;

   PropertyMap(Map backingMap) {
      this.backingMap = backingMap;
   }

   public static Builder builder() {
      return new Builder();
   }

   public static MapCodec createCodec(final List properties) {
      return new MapCodec() {
         public RecordBuilder encode(PropertyMap propertyMap, DynamicOps dynamicOps, RecordBuilder recordBuilder) {
            RecordBuilder recordBuilder2 = recordBuilder;

            TelemetryEventProperty telemetryEventProperty;
            for(Iterator var5 = properties.iterator(); var5.hasNext(); recordBuilder2 = this.encode(propertyMap, recordBuilder2, telemetryEventProperty)) {
               telemetryEventProperty = (TelemetryEventProperty)var5.next();
            }

            return recordBuilder2;
         }

         private RecordBuilder encode(PropertyMap map, RecordBuilder builder, TelemetryEventProperty property) {
            Object object = map.get(property);
            return object != null ? builder.add(property.id(), object, property.codec()) : builder;
         }

         public DataResult decode(DynamicOps ops, MapLike map) {
            DataResult dataResult = DataResult.success(new Builder());

            TelemetryEventProperty telemetryEventProperty;
            for(Iterator var4 = properties.iterator(); var4.hasNext(); dataResult = this.decode(dataResult, ops, map, telemetryEventProperty)) {
               telemetryEventProperty = (TelemetryEventProperty)var4.next();
            }

            return dataResult.map(Builder::build);
         }

         private DataResult decode(DataResult result, DynamicOps ops, MapLike map, TelemetryEventProperty property) {
            Object object = map.get(property.id());
            if (object != null) {
               DataResult dataResult = property.codec().parse(ops, object);
               return result.apply2stable((mapBuilder, value) -> {
                  return mapBuilder.put(property, value);
               }, dataResult);
            } else {
               return result;
            }
         }

         public Stream keys(DynamicOps ops) {
            Stream var10000 = properties.stream().map(TelemetryEventProperty::id);
            Objects.requireNonNull(ops);
            return var10000.map(ops::createString);
         }

         // $FF: synthetic method
         public RecordBuilder encode(final Object map, final DynamicOps ops, final RecordBuilder builder) {
            return this.encode((PropertyMap)map, ops, builder);
         }
      };
   }

   @Nullable
   public Object get(TelemetryEventProperty property) {
      return this.backingMap.get(property);
   }

   public String toString() {
      return this.backingMap.toString();
   }

   public Set keySet() {
      return this.backingMap.keySet();
   }

   @Environment(EnvType.CLIENT)
   public static class Builder {
      private final Map backingMap = new Reference2ObjectOpenHashMap();

      Builder() {
      }

      public Builder put(TelemetryEventProperty property, Object value) {
         this.backingMap.put(property, value);
         return this;
      }

      public Builder putIfNonNull(TelemetryEventProperty property, @Nullable Object value) {
         if (value != null) {
            this.backingMap.put(property, value);
         }

         return this;
      }

      public Builder putAll(PropertyMap map) {
         this.backingMap.putAll(map.backingMap);
         return this;
      }

      public PropertyMap build() {
         return new PropertyMap(this.backingMap);
      }
   }
}

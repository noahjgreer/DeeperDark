package net.minecraft.resource.metadata;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import net.minecraft.resource.InputSupplier;
import net.minecraft.util.JsonHelper;

public interface ResourceMetadata {
   ResourceMetadata NONE = new ResourceMetadata() {
      public Optional decode(ResourceMetadataSerializer serializer) {
         return Optional.empty();
      }
   };
   InputSupplier NONE_SUPPLIER = () -> {
      return NONE;
   };

   static ResourceMetadata create(InputStream stream) throws IOException {
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));

      ResourceMetadata var3;
      try {
         final JsonObject jsonObject = JsonHelper.deserialize((Reader)bufferedReader);
         var3 = new ResourceMetadata() {
            public Optional decode(ResourceMetadataSerializer serializer) {
               String string = serializer.name();
               if (jsonObject.has(string)) {
                  Object object = serializer.codec().parse(JsonOps.INSTANCE, jsonObject.get(string)).getOrThrow(JsonParseException::new);
                  return Optional.of(object);
               } else {
                  return Optional.empty();
               }
            }
         };
      } catch (Throwable var5) {
         try {
            bufferedReader.close();
         } catch (Throwable var4) {
            var5.addSuppressed(var4);
         }

         throw var5;
      }

      bufferedReader.close();
      return var3;
   }

   Optional decode(ResourceMetadataSerializer serializer);

   default ResourceMetadata copy(Collection serializers) {
      Builder builder = new Builder();
      Iterator var3 = serializers.iterator();

      while(var3.hasNext()) {
         ResourceMetadataSerializer resourceMetadataSerializer = (ResourceMetadataSerializer)var3.next();
         this.decodeAndAdd(builder, resourceMetadataSerializer);
      }

      return builder.build();
   }

   private void decodeAndAdd(Builder builder, ResourceMetadataSerializer serializer) {
      this.decode(serializer).ifPresent((value) -> {
         builder.add(serializer, value);
      });
   }

   public static class Builder {
      private final ImmutableMap.Builder values = ImmutableMap.builder();

      public Builder add(ResourceMetadataSerializer serializer, Object value) {
         this.values.put(serializer, value);
         return this;
      }

      public ResourceMetadata build() {
         final ImmutableMap immutableMap = this.values.build();
         return immutableMap.isEmpty() ? ResourceMetadata.NONE : new ResourceMetadata(this) {
            public Optional decode(ResourceMetadataSerializer serializer) {
               return Optional.ofNullable(immutableMap.get(serializer));
            }
         };
      }
   }
}

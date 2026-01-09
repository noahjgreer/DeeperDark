package net.minecraft.resource.metadata;

import java.util.Map;

public class ResourceMetadataMap {
   private static final ResourceMetadataMap EMPTY = new ResourceMetadataMap(Map.of());
   private final Map values;

   private ResourceMetadataMap(Map values) {
      this.values = values;
   }

   public Object get(ResourceMetadataSerializer serializer) {
      return this.values.get(serializer);
   }

   public static ResourceMetadataMap of() {
      return EMPTY;
   }

   public static ResourceMetadataMap of(ResourceMetadataSerializer serializer, Object value) {
      return new ResourceMetadataMap(Map.of(serializer, value));
   }

   public static ResourceMetadataMap of(ResourceMetadataSerializer serializer, Object value, ResourceMetadataSerializer serializer2, Object value2) {
      return new ResourceMetadataMap(Map.of(serializer, value, serializer2, value2));
   }
}

package net.minecraft.resource;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.minecraft.resource.metadata.ResourceMetadataSerializer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class OverlayResourcePack implements ResourcePack {
   private final ResourcePack base;
   private final List overlaysAndBase;

   public OverlayResourcePack(ResourcePack base, List overlays) {
      this.base = base;
      List list = new ArrayList(overlays.size() + 1);
      list.addAll(Lists.reverse(overlays));
      list.add(base);
      this.overlaysAndBase = List.copyOf(list);
   }

   @Nullable
   public InputSupplier openRoot(String... segments) {
      return this.base.openRoot(segments);
   }

   @Nullable
   public InputSupplier open(ResourceType type, Identifier id) {
      Iterator var3 = this.overlaysAndBase.iterator();

      InputSupplier inputSupplier;
      do {
         if (!var3.hasNext()) {
            return null;
         }

         ResourcePack resourcePack = (ResourcePack)var3.next();
         inputSupplier = resourcePack.open(type, id);
      } while(inputSupplier == null);

      return inputSupplier;
   }

   public void findResources(ResourceType type, String namespace, String prefix, ResourcePack.ResultConsumer consumer) {
      Map map = new HashMap();
      Iterator var6 = this.overlaysAndBase.iterator();

      while(var6.hasNext()) {
         ResourcePack resourcePack = (ResourcePack)var6.next();
         Objects.requireNonNull(map);
         resourcePack.findResources(type, namespace, prefix, map::putIfAbsent);
      }

      map.forEach(consumer);
   }

   public Set getNamespaces(ResourceType type) {
      Set set = new HashSet();
      Iterator var3 = this.overlaysAndBase.iterator();

      while(var3.hasNext()) {
         ResourcePack resourcePack = (ResourcePack)var3.next();
         set.addAll(resourcePack.getNamespaces(type));
      }

      return set;
   }

   @Nullable
   public Object parseMetadata(ResourceMetadataSerializer metadataSerializer) throws IOException {
      return this.base.parseMetadata(metadataSerializer);
   }

   public ResourcePackInfo getInfo() {
      return this.base.getInfo();
   }

   public void close() {
      this.overlaysAndBase.forEach(ResourcePack::close);
   }
}

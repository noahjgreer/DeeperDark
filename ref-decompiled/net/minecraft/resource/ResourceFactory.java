package net.minecraft.resource;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import net.minecraft.util.Identifier;

@FunctionalInterface
public interface ResourceFactory {
   ResourceFactory MISSING = (id) -> {
      return Optional.empty();
   };

   Optional getResource(Identifier id);

   default Resource getResourceOrThrow(Identifier id) throws FileNotFoundException {
      return (Resource)this.getResource(id).orElseThrow(() -> {
         return new FileNotFoundException(id.toString());
      });
   }

   default InputStream open(Identifier id) throws IOException {
      return this.getResourceOrThrow(id).getInputStream();
   }

   default BufferedReader openAsReader(Identifier id) throws IOException {
      return this.getResourceOrThrow(id).getReader();
   }

   static ResourceFactory fromMap(Map map) {
      return (id) -> {
         return Optional.ofNullable((Resource)map.get(id));
      };
   }
}

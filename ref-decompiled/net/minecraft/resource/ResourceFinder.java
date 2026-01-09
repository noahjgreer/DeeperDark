package net.minecraft.resource;

import java.util.Map;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ResourceFinder {
   private final String directoryName;
   private final String fileExtension;

   public ResourceFinder(String directoryName, String fileExtension) {
      this.directoryName = directoryName;
      this.fileExtension = fileExtension;
   }

   public static ResourceFinder json(String directoryName) {
      return new ResourceFinder(directoryName, ".json");
   }

   public static ResourceFinder json(RegistryKey registryRef) {
      return json(RegistryKeys.getPath(registryRef));
   }

   public Identifier toResourcePath(Identifier id) {
      String var10001 = this.directoryName;
      return id.withPath(var10001 + "/" + id.getPath() + this.fileExtension);
   }

   public Identifier toResourceId(Identifier path) {
      String string = path.getPath();
      return path.withPath(string.substring(this.directoryName.length() + 1, string.length() - this.fileExtension.length()));
   }

   public Map findResources(ResourceManager resourceManager) {
      return resourceManager.findResources(this.directoryName, (path) -> {
         return path.getPath().endsWith(this.fileExtension);
      });
   }

   public Map findAllResources(ResourceManager resourceManager) {
      return resourceManager.findAllResources(this.directoryName, (path) -> {
         return path.getPath().endsWith(this.fileExtension);
      });
   }
}

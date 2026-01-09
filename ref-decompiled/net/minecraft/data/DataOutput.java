package net.minecraft.data;

import java.nio.file.Path;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class DataOutput {
   private final Path path;

   public DataOutput(Path path) {
      this.path = path;
   }

   public Path getPath() {
      return this.path;
   }

   public Path resolvePath(OutputType outputType) {
      return this.getPath().resolve(outputType.path);
   }

   public PathResolver getResolver(OutputType outputType, String directoryName) {
      return new PathResolver(this, outputType, directoryName);
   }

   public PathResolver getResolver(RegistryKey registryRef) {
      return this.getResolver(DataOutput.OutputType.DATA_PACK, RegistryKeys.getPath(registryRef));
   }

   public PathResolver getTagResolver(RegistryKey registryRef) {
      return this.getResolver(DataOutput.OutputType.DATA_PACK, RegistryKeys.getTagPath(registryRef));
   }

   public static enum OutputType {
      DATA_PACK("data"),
      RESOURCE_PACK("assets"),
      REPORTS("reports");

      final String path;

      private OutputType(final String path) {
         this.path = path;
      }

      // $FF: synthetic method
      private static OutputType[] method_44109() {
         return new OutputType[]{DATA_PACK, RESOURCE_PACK, REPORTS};
      }
   }

   public static class PathResolver {
      private final Path rootPath;
      private final String directoryName;

      PathResolver(DataOutput dataGenerator, OutputType outputType, String directoryName) {
         this.rootPath = dataGenerator.resolvePath(outputType);
         this.directoryName = directoryName;
      }

      public Path resolve(Identifier id, String fileExtension) {
         Path var10000 = this.rootPath.resolve(id.getNamespace()).resolve(this.directoryName);
         String var10001 = id.getPath();
         return var10000.resolve(var10001 + "." + fileExtension);
      }

      public Path resolveJson(Identifier id) {
         return this.rootPath.resolve(id.getNamespace()).resolve(this.directoryName).resolve(id.getPath() + ".json");
      }

      public Path resolveJson(RegistryKey key) {
         return this.rootPath.resolve(key.getValue().getNamespace()).resolve(this.directoryName).resolve(key.getValue().getPath() + ".json");
      }
   }
}

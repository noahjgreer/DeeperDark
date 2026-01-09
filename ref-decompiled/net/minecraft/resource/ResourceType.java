package net.minecraft.resource;

public enum ResourceType {
   CLIENT_RESOURCES("assets"),
   SERVER_DATA("data");

   private final String directory;

   private ResourceType(final String directory) {
      this.directory = directory;
   }

   public String getDirectory() {
      return this.directory;
   }

   // $FF: synthetic method
   private static ResourceType[] method_36582() {
      return new ResourceType[]{CLIENT_RESOURCES, SERVER_DATA};
   }
}

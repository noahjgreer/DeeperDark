package net.minecraft.resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import net.minecraft.resource.metadata.ResourceMetadata;
import org.jetbrains.annotations.Nullable;

public class Resource {
   private final ResourcePack pack;
   private final InputSupplier inputSupplier;
   private final InputSupplier metadataSupplier;
   @Nullable
   private ResourceMetadata metadata;

   public Resource(ResourcePack pack, InputSupplier inputSupplier, InputSupplier metadataSupplier) {
      this.pack = pack;
      this.inputSupplier = inputSupplier;
      this.metadataSupplier = metadataSupplier;
   }

   public Resource(ResourcePack pack, InputSupplier inputSupplier) {
      this.pack = pack;
      this.inputSupplier = inputSupplier;
      this.metadataSupplier = ResourceMetadata.NONE_SUPPLIER;
      this.metadata = ResourceMetadata.NONE;
   }

   public ResourcePack getPack() {
      return this.pack;
   }

   public String getPackId() {
      return this.pack.getId();
   }

   public Optional getKnownPackInfo() {
      return this.pack.getKnownPackInfo();
   }

   public InputStream getInputStream() throws IOException {
      return (InputStream)this.inputSupplier.get();
   }

   public BufferedReader getReader() throws IOException {
      return new BufferedReader(new InputStreamReader(this.getInputStream(), StandardCharsets.UTF_8));
   }

   public ResourceMetadata getMetadata() throws IOException {
      if (this.metadata == null) {
         this.metadata = (ResourceMetadata)this.metadataSupplier.get();
      }

      return this.metadata;
   }
}

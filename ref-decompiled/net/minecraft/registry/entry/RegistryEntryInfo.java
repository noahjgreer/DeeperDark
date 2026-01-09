package net.minecraft.registry.entry;

import com.mojang.serialization.Lifecycle;
import java.util.Optional;

public record RegistryEntryInfo(Optional knownPackInfo, Lifecycle lifecycle) {
   public static final RegistryEntryInfo DEFAULT = new RegistryEntryInfo(Optional.empty(), Lifecycle.stable());

   public RegistryEntryInfo(Optional optional, Lifecycle lifecycle) {
      this.knownPackInfo = optional;
      this.lifecycle = lifecycle;
   }

   public Optional knownPackInfo() {
      return this.knownPackInfo;
   }

   public Lifecycle lifecycle() {
      return this.lifecycle;
   }
}

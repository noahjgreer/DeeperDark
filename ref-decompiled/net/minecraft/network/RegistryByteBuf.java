package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import java.util.function.Function;
import net.minecraft.registry.DynamicRegistryManager;

public class RegistryByteBuf extends PacketByteBuf {
   private final DynamicRegistryManager registryManager;

   public RegistryByteBuf(ByteBuf buf, DynamicRegistryManager registryManager) {
      super(buf);
      this.registryManager = registryManager;
   }

   public DynamicRegistryManager getRegistryManager() {
      return this.registryManager;
   }

   public static Function makeFactory(DynamicRegistryManager registryManager) {
      return (buf) -> {
         return new RegistryByteBuf(buf, registryManager);
      };
   }
}

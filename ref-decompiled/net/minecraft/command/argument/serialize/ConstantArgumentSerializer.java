package net.minecraft.command.argument.serialize;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.network.PacketByteBuf;

public class ConstantArgumentSerializer implements ArgumentSerializer {
   private final Properties properties;

   private ConstantArgumentSerializer(Function typeSupplier) {
      this.properties = new Properties(typeSupplier);
   }

   public static ConstantArgumentSerializer of(Supplier typeSupplier) {
      return new ConstantArgumentSerializer((commandRegistryAccess) -> {
         return (ArgumentType)typeSupplier.get();
      });
   }

   public static ConstantArgumentSerializer of(Function typeSupplier) {
      return new ConstantArgumentSerializer(typeSupplier);
   }

   public void writePacket(Properties properties, PacketByteBuf packetByteBuf) {
   }

   public void writeJson(Properties properties, JsonObject jsonObject) {
   }

   public Properties fromPacket(PacketByteBuf packetByteBuf) {
      return this.properties;
   }

   public Properties getArgumentTypeProperties(ArgumentType argumentType) {
      return this.properties;
   }

   // $FF: synthetic method
   public ArgumentSerializer.ArgumentTypeProperties getArgumentTypeProperties(final ArgumentType argumentType) {
      return this.getArgumentTypeProperties(argumentType);
   }

   // $FF: synthetic method
   public ArgumentSerializer.ArgumentTypeProperties fromPacket(final PacketByteBuf buf) {
      return this.fromPacket(buf);
   }

   public final class Properties implements ArgumentSerializer.ArgumentTypeProperties {
      private final Function typeSupplier;

      public Properties(final Function typeSupplier) {
         this.typeSupplier = typeSupplier;
      }

      public ArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
         return (ArgumentType)this.typeSupplier.apply(commandRegistryAccess);
      }

      public ArgumentSerializer getSerializer() {
         return ConstantArgumentSerializer.this;
      }
   }
}

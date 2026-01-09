package net.minecraft.command.argument.serialize;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.ArgumentHelper;
import net.minecraft.network.PacketByteBuf;

public class DoubleArgumentSerializer implements ArgumentSerializer {
   public void writePacket(Properties properties, PacketByteBuf packetByteBuf) {
      boolean bl = properties.min != -1.7976931348623157E308;
      boolean bl2 = properties.max != Double.MAX_VALUE;
      packetByteBuf.writeByte(ArgumentHelper.getMinMaxFlag(bl, bl2));
      if (bl) {
         packetByteBuf.writeDouble(properties.min);
      }

      if (bl2) {
         packetByteBuf.writeDouble(properties.max);
      }

   }

   public Properties fromPacket(PacketByteBuf packetByteBuf) {
      byte b = packetByteBuf.readByte();
      double d = ArgumentHelper.hasMinFlag(b) ? packetByteBuf.readDouble() : -1.7976931348623157E308;
      double e = ArgumentHelper.hasMaxFlag(b) ? packetByteBuf.readDouble() : Double.MAX_VALUE;
      return new Properties(d, e);
   }

   public void writeJson(Properties properties, JsonObject jsonObject) {
      if (properties.min != -1.7976931348623157E308) {
         jsonObject.addProperty("min", properties.min);
      }

      if (properties.max != Double.MAX_VALUE) {
         jsonObject.addProperty("max", properties.max);
      }

   }

   public Properties getArgumentTypeProperties(DoubleArgumentType doubleArgumentType) {
      return new Properties(doubleArgumentType.getMinimum(), doubleArgumentType.getMaximum());
   }

   // $FF: synthetic method
   public ArgumentSerializer.ArgumentTypeProperties fromPacket(final PacketByteBuf buf) {
      return this.fromPacket(buf);
   }

   public final class Properties implements ArgumentSerializer.ArgumentTypeProperties {
      final double min;
      final double max;

      Properties(final double min, final double max) {
         this.min = min;
         this.max = max;
      }

      public DoubleArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
         return DoubleArgumentType.doubleArg(this.min, this.max);
      }

      public ArgumentSerializer getSerializer() {
         return DoubleArgumentSerializer.this;
      }

      // $FF: synthetic method
      public ArgumentType createType(final CommandRegistryAccess commandRegistryAccess) {
         return this.createType(commandRegistryAccess);
      }
   }
}

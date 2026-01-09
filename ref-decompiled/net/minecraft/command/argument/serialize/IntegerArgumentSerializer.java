package net.minecraft.command.argument.serialize;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.ArgumentHelper;
import net.minecraft.network.PacketByteBuf;

public class IntegerArgumentSerializer implements ArgumentSerializer {
   public void writePacket(Properties properties, PacketByteBuf packetByteBuf) {
      boolean bl = properties.min != Integer.MIN_VALUE;
      boolean bl2 = properties.max != Integer.MAX_VALUE;
      packetByteBuf.writeByte(ArgumentHelper.getMinMaxFlag(bl, bl2));
      if (bl) {
         packetByteBuf.writeInt(properties.min);
      }

      if (bl2) {
         packetByteBuf.writeInt(properties.max);
      }

   }

   public Properties fromPacket(PacketByteBuf packetByteBuf) {
      byte b = packetByteBuf.readByte();
      int i = ArgumentHelper.hasMinFlag(b) ? packetByteBuf.readInt() : Integer.MIN_VALUE;
      int j = ArgumentHelper.hasMaxFlag(b) ? packetByteBuf.readInt() : Integer.MAX_VALUE;
      return new Properties(i, j);
   }

   public void writeJson(Properties properties, JsonObject jsonObject) {
      if (properties.min != Integer.MIN_VALUE) {
         jsonObject.addProperty("min", properties.min);
      }

      if (properties.max != Integer.MAX_VALUE) {
         jsonObject.addProperty("max", properties.max);
      }

   }

   public Properties getArgumentTypeProperties(IntegerArgumentType integerArgumentType) {
      return new Properties(integerArgumentType.getMinimum(), integerArgumentType.getMaximum());
   }

   // $FF: synthetic method
   public ArgumentSerializer.ArgumentTypeProperties fromPacket(final PacketByteBuf buf) {
      return this.fromPacket(buf);
   }

   public final class Properties implements ArgumentSerializer.ArgumentTypeProperties {
      final int min;
      final int max;

      Properties(final int min, final int max) {
         this.min = min;
         this.max = max;
      }

      public IntegerArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
         return IntegerArgumentType.integer(this.min, this.max);
      }

      public ArgumentSerializer getSerializer() {
         return IntegerArgumentSerializer.this;
      }

      // $FF: synthetic method
      public ArgumentType createType(final CommandRegistryAccess commandRegistryAccess) {
         return this.createType(commandRegistryAccess);
      }
   }
}

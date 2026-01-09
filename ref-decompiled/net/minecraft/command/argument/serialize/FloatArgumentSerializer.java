package net.minecraft.command.argument.serialize;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.ArgumentHelper;
import net.minecraft.network.PacketByteBuf;

public class FloatArgumentSerializer implements ArgumentSerializer {
   public void writePacket(Properties properties, PacketByteBuf packetByteBuf) {
      boolean bl = properties.min != -3.4028235E38F;
      boolean bl2 = properties.max != Float.MAX_VALUE;
      packetByteBuf.writeByte(ArgumentHelper.getMinMaxFlag(bl, bl2));
      if (bl) {
         packetByteBuf.writeFloat(properties.min);
      }

      if (bl2) {
         packetByteBuf.writeFloat(properties.max);
      }

   }

   public Properties fromPacket(PacketByteBuf packetByteBuf) {
      byte b = packetByteBuf.readByte();
      float f = ArgumentHelper.hasMinFlag(b) ? packetByteBuf.readFloat() : -3.4028235E38F;
      float g = ArgumentHelper.hasMaxFlag(b) ? packetByteBuf.readFloat() : Float.MAX_VALUE;
      return new Properties(f, g);
   }

   public void writeJson(Properties properties, JsonObject jsonObject) {
      if (properties.min != -3.4028235E38F) {
         jsonObject.addProperty("min", properties.min);
      }

      if (properties.max != Float.MAX_VALUE) {
         jsonObject.addProperty("max", properties.max);
      }

   }

   public Properties getArgumentTypeProperties(FloatArgumentType floatArgumentType) {
      return new Properties(floatArgumentType.getMinimum(), floatArgumentType.getMaximum());
   }

   // $FF: synthetic method
   public ArgumentSerializer.ArgumentTypeProperties fromPacket(final PacketByteBuf buf) {
      return this.fromPacket(buf);
   }

   public final class Properties implements ArgumentSerializer.ArgumentTypeProperties {
      final float min;
      final float max;

      Properties(final float min, final float max) {
         this.min = min;
         this.max = max;
      }

      public FloatArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
         return FloatArgumentType.floatArg(this.min, this.max);
      }

      public ArgumentSerializer getSerializer() {
         return FloatArgumentSerializer.this;
      }

      // $FF: synthetic method
      public ArgumentType createType(final CommandRegistryAccess commandRegistryAccess) {
         return this.createType(commandRegistryAccess);
      }
   }
}

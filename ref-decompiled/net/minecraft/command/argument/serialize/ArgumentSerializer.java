package net.minecraft.command.argument.serialize;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.network.PacketByteBuf;

public interface ArgumentSerializer {
   void writePacket(ArgumentTypeProperties properties, PacketByteBuf buf);

   ArgumentTypeProperties fromPacket(PacketByteBuf buf);

   void writeJson(ArgumentTypeProperties properties, JsonObject json);

   ArgumentTypeProperties getArgumentTypeProperties(ArgumentType argumentType);

   public interface ArgumentTypeProperties {
      ArgumentType createType(CommandRegistryAccess commandRegistryAccess);

      ArgumentSerializer getSerializer();
   }
}

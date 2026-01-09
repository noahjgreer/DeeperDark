package net.minecraft.command.argument.serialize;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.network.PacketByteBuf;

public class StringArgumentSerializer implements ArgumentSerializer {
   public void writePacket(Properties properties, PacketByteBuf packetByteBuf) {
      packetByteBuf.writeEnumConstant(properties.type);
   }

   public Properties fromPacket(PacketByteBuf packetByteBuf) {
      StringArgumentType.StringType stringType = (StringArgumentType.StringType)packetByteBuf.readEnumConstant(StringArgumentType.StringType.class);
      return new Properties(stringType);
   }

   public void writeJson(Properties properties, JsonObject jsonObject) {
      String var10002;
      switch (properties.type) {
         case SINGLE_WORD:
            var10002 = "word";
            break;
         case QUOTABLE_PHRASE:
            var10002 = "phrase";
            break;
         case GREEDY_PHRASE:
            var10002 = "greedy";
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      jsonObject.addProperty("type", var10002);
   }

   public Properties getArgumentTypeProperties(StringArgumentType stringArgumentType) {
      return new Properties(stringArgumentType.getType());
   }

   // $FF: synthetic method
   public ArgumentSerializer.ArgumentTypeProperties fromPacket(final PacketByteBuf buf) {
      return this.fromPacket(buf);
   }

   public final class Properties implements ArgumentSerializer.ArgumentTypeProperties {
      final StringArgumentType.StringType type;

      public Properties(final StringArgumentType.StringType type) {
         this.type = type;
      }

      public StringArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
         StringArgumentType var10000;
         switch (this.type) {
            case SINGLE_WORD:
               var10000 = StringArgumentType.word();
               break;
            case QUOTABLE_PHRASE:
               var10000 = StringArgumentType.string();
               break;
            case GREEDY_PHRASE:
               var10000 = StringArgumentType.greedyString();
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      public ArgumentSerializer getSerializer() {
         return StringArgumentSerializer.this;
      }

      // $FF: synthetic method
      public ArgumentType createType(final CommandRegistryAccess commandRegistryAccess) {
         return this.createType(commandRegistryAccess);
      }
   }
}

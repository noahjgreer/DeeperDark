package net.minecraft.client.session.report.log;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.StringIdentifiable;

@Environment(EnvType.CLIENT)
public interface ChatLogEntry {
   Codec CODEC = StringIdentifiable.createCodec(Type::values).dispatch(ChatLogEntry::getType, Type::getCodec);

   Type getType();

   @Environment(EnvType.CLIENT)
   public static enum Type implements StringIdentifiable {
      PLAYER("player", () -> {
         return ReceivedMessage.ChatMessage.CHAT_MESSAGE_CODEC;
      }),
      SYSTEM("system", () -> {
         return ReceivedMessage.GameMessage.GAME_MESSAGE_CODEC;
      });

      private final String id;
      private final Supplier codecSupplier;

      private Type(final String id, final Supplier codecSupplier) {
         this.id = id;
         this.codecSupplier = codecSupplier;
      }

      private MapCodec getCodec() {
         return (MapCodec)this.codecSupplier.get();
      }

      public String asString() {
         return this.id;
      }

      // $FF: synthetic method
      private static Type[] method_46542() {
         return new Type[]{PLAYER, SYSTEM};
      }
   }
}

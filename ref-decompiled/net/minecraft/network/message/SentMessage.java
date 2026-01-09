package net.minecraft.network.message;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public interface SentMessage {
   Text content();

   void send(ServerPlayerEntity sender, boolean filterMaskEnabled, MessageType.Parameters params);

   static SentMessage of(SignedMessage message) {
      return (SentMessage)(message.isSenderMissing() ? new Profileless(message.getContent()) : new Chat(message));
   }

   public static record Profileless(Text content) implements SentMessage {
      public Profileless(Text text) {
         this.content = text;
      }

      public Text content() {
         return this.content;
      }

      public void send(ServerPlayerEntity sender, boolean filterMaskEnabled, MessageType.Parameters params) {
         sender.networkHandler.sendProfilelessChatMessage(this.content, params);
      }
   }

   public static record Chat(SignedMessage message) implements SentMessage {
      public Chat(SignedMessage message) {
         this.message = message;
      }

      public Text content() {
         return this.message.getContent();
      }

      public void send(ServerPlayerEntity sender, boolean filterMaskEnabled, MessageType.Parameters params) {
         SignedMessage signedMessage = this.message.withFilterMaskEnabled(filterMaskEnabled);
         if (!signedMessage.isFullyFiltered()) {
            sender.networkHandler.sendChatMessage(signedMessage, params);
         }

      }

      public SignedMessage message() {
         return this.message;
      }
   }
}

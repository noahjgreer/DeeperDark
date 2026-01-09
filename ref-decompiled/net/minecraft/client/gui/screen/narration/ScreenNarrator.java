package net.minecraft.client.gui.screen.narration;

import com.google.common.collect.Maps;
import java.util.Comparator;
import java.util.Map;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ScreenNarrator {
   int currentMessageIndex;
   final Map narrations = Maps.newTreeMap(Comparator.comparing((partIndex) -> {
      return partIndex.part;
   }).thenComparing((partIndex) -> {
      return partIndex.depth;
   }));

   public void buildNarrations(Consumer builderConsumer) {
      ++this.currentMessageIndex;
      builderConsumer.accept(new MessageBuilder(0));
   }

   public String buildNarratorText(boolean includeUnchanged) {
      final StringBuilder stringBuilder = new StringBuilder();
      Consumer consumer = new Consumer(this) {
         private boolean first = true;

         public void accept(String string) {
            if (!this.first) {
               stringBuilder.append(". ");
            }

            this.first = false;
            stringBuilder.append(string);
         }

         // $FF: synthetic method
         public void accept(final Object sentence) {
            this.accept((String)sentence);
         }
      };
      this.narrations.forEach((partIndex, message) -> {
         if (message.index == this.currentMessageIndex && (includeUnchanged || !message.used)) {
            message.narration.forEachSentence(consumer);
            message.used = true;
         }

      });
      return stringBuilder.toString();
   }

   @Environment(EnvType.CLIENT)
   class MessageBuilder implements NarrationMessageBuilder {
      private final int depth;

      MessageBuilder(final int depth) {
         this.depth = depth;
      }

      public void put(NarrationPart part, Narration narration) {
         ((Message)ScreenNarrator.this.narrations.computeIfAbsent(new PartIndex(part, this.depth), (partIndex) -> {
            return new Message();
         })).setNarration(ScreenNarrator.this.currentMessageIndex, narration);
      }

      public NarrationMessageBuilder nextMessage() {
         return ScreenNarrator.this.new MessageBuilder(this.depth + 1);
      }
   }

   @Environment(EnvType.CLIENT)
   private static class Message {
      Narration narration;
      int index;
      boolean used;

      Message() {
         this.narration = Narration.EMPTY;
         this.index = -1;
      }

      public Message setNarration(int index, Narration narration) {
         if (!this.narration.equals(narration)) {
            this.narration = narration;
            this.used = false;
         } else if (this.index + 1 != index) {
            this.used = false;
         }

         this.index = index;
         return this;
      }
   }

   @Environment(EnvType.CLIENT)
   static class PartIndex {
      final NarrationPart part;
      final int depth;

      PartIndex(NarrationPart part, int depth) {
         this.part = part;
         this.depth = depth;
      }
   }
}

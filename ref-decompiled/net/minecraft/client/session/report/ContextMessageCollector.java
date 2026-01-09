package net.minecraft.client.session.report;

import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.session.report.log.ChatLog;
import net.minecraft.client.session.report.log.ChatLogEntry;
import net.minecraft.client.session.report.log.ReceivedMessage;
import net.minecraft.network.message.SignedMessage;

@Environment(EnvType.CLIENT)
public class ContextMessageCollector {
   final int leadingContextMessageCount;
   private final List contextMessages = new ArrayList();

   public ContextMessageCollector(int leadingContextMessageCount) {
      this.leadingContextMessageCount = leadingContextMessageCount;
   }

   public void add(ChatLog log, IntCollection selections, IndexedMessageConsumer consumer) {
      IntSortedSet intSortedSet = new IntRBTreeSet(selections);

      for(int i = intSortedSet.lastInt(); i >= log.getMinIndex() && (this.hasContextMessage() || !intSortedSet.isEmpty()); --i) {
         ChatLogEntry var7 = log.get(i);
         if (var7 instanceof ReceivedMessage.ChatMessage chatMessage) {
            boolean bl = this.tryLink(chatMessage.message());
            if (intSortedSet.remove(i)) {
               this.add(chatMessage.message());
               consumer.accept(i, chatMessage);
            } else if (bl) {
               consumer.accept(i, chatMessage);
            }
         }
      }

   }

   public void add(SignedMessage message) {
      this.contextMessages.add(new ContextMessage(message));
   }

   public boolean tryLink(SignedMessage message) {
      boolean bl = false;
      Iterator iterator = this.contextMessages.iterator();

      while(iterator.hasNext()) {
         ContextMessage contextMessage = (ContextMessage)iterator.next();
         if (contextMessage.linkTo(message)) {
            bl = true;
            if (contextMessage.isInvalid()) {
               iterator.remove();
            }
         }
      }

      return bl;
   }

   public boolean hasContextMessage() {
      return !this.contextMessages.isEmpty();
   }

   @Environment(EnvType.CLIENT)
   public interface IndexedMessageConsumer {
      void accept(int index, ReceivedMessage.ChatMessage message);
   }

   @Environment(EnvType.CLIENT)
   private class ContextMessage {
      private final Set lastSeenEntries;
      private SignedMessage message;
      private boolean linkSuccessful = true;
      private int count;

      ContextMessage(final SignedMessage message) {
         this.lastSeenEntries = new ObjectOpenHashSet(message.signedBody().lastSeenMessages().entries());
         this.message = message;
      }

      boolean linkTo(SignedMessage message) {
         if (message.equals(this.message)) {
            return false;
         } else {
            boolean bl = this.lastSeenEntries.remove(message.signature());
            if (this.linkSuccessful && this.message.getSender().equals(message.getSender())) {
               if (this.message.link().linksTo(message.link())) {
                  bl = true;
                  this.message = message;
               } else {
                  this.linkSuccessful = false;
               }
            }

            if (bl) {
               ++this.count;
            }

            return bl;
         }
      }

      boolean isInvalid() {
         return this.count >= ContextMessageCollector.this.leadingContextMessageCount || !this.linkSuccessful && this.lastSeenEntries.isEmpty();
      }
   }
}

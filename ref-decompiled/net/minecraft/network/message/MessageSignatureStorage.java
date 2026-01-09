package net.minecraft.network.message;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Set;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

public class MessageSignatureStorage {
   public static final int MISSING = -1;
   private static final int MAX_ENTRIES = 128;
   private final MessageSignatureData[] signatures;

   public MessageSignatureStorage(int maxEntries) {
      this.signatures = new MessageSignatureData[maxEntries];
   }

   public static MessageSignatureStorage create() {
      return new MessageSignatureStorage(128);
   }

   public int indexOf(MessageSignatureData signature) {
      for(int i = 0; i < this.signatures.length; ++i) {
         if (signature.equals(this.signatures[i])) {
            return i;
         }
      }

      return -1;
   }

   @Nullable
   public MessageSignatureData get(int index) {
      return this.signatures[index];
   }

   public void add(MessageBody body, @Nullable MessageSignatureData signature) {
      List list = body.lastSeenMessages().entries();
      ArrayDeque arrayDeque = new ArrayDeque(list.size() + 1);
      arrayDeque.addAll(list);
      if (signature != null) {
         arrayDeque.add(signature);
      }

      this.addFrom(arrayDeque);
   }

   @VisibleForTesting
   void addFrom(List signatures) {
      this.addFrom(new ArrayDeque(signatures));
   }

   private void addFrom(ArrayDeque deque) {
      Set set = new ObjectOpenHashSet(deque);

      for(int i = 0; !deque.isEmpty() && i < this.signatures.length; ++i) {
         MessageSignatureData messageSignatureData = this.signatures[i];
         this.signatures[i] = (MessageSignatureData)deque.removeLast();
         if (messageSignatureData != null && !set.contains(messageSignatureData)) {
            deque.addFirst(messageSignatureData);
         }
      }

   }
}

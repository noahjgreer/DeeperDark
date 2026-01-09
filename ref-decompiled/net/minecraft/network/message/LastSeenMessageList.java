package net.minecraft.network.message;

import com.google.common.primitives.Ints;
import com.mojang.serialization.Codec;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.encryption.SignatureUpdatable;

public record LastSeenMessageList(List entries) {
   public static final Codec CODEC;
   public static LastSeenMessageList EMPTY;
   public static final int MAX_ENTRIES = 20;

   public LastSeenMessageList(List list) {
      this.entries = list;
   }

   public void updateSignatures(SignatureUpdatable.SignatureUpdater updater) throws SignatureException {
      updater.update(Ints.toByteArray(this.entries.size()));
      Iterator var2 = this.entries.iterator();

      while(var2.hasNext()) {
         MessageSignatureData messageSignatureData = (MessageSignatureData)var2.next();
         updater.update(messageSignatureData.data());
      }

   }

   public Indexed pack(MessageSignatureStorage storage) {
      return new Indexed(this.entries.stream().map((signature) -> {
         return signature.pack(storage);
      }).toList());
   }

   public byte calculateChecksum() {
      int i = 1;

      MessageSignatureData messageSignatureData;
      for(Iterator var2 = this.entries.iterator(); var2.hasNext(); i = 31 * i + messageSignatureData.calculateChecksum()) {
         messageSignatureData = (MessageSignatureData)var2.next();
      }

      byte b = (byte)i;
      return b == 0 ? 1 : b;
   }

   public List entries() {
      return this.entries;
   }

   static {
      CODEC = MessageSignatureData.CODEC.listOf().xmap(LastSeenMessageList::new, LastSeenMessageList::entries);
      EMPTY = new LastSeenMessageList(List.of());
   }

   public static record Indexed(List buf) {
      public static final Indexed EMPTY = new Indexed(List.of());

      public Indexed(PacketByteBuf buf) {
         this((List)buf.readCollection(PacketByteBuf.getMaxValidator(ArrayList::new, 20), MessageSignatureData.Indexed::fromBuf));
      }

      public Indexed(List list) {
         this.buf = list;
      }

      public void write(PacketByteBuf buf) {
         buf.writeCollection(this.buf, MessageSignatureData.Indexed::write);
      }

      public Optional unpack(MessageSignatureStorage storage) {
         List list = new ArrayList(this.buf.size());
         Iterator var3 = this.buf.iterator();

         while(var3.hasNext()) {
            MessageSignatureData.Indexed indexed = (MessageSignatureData.Indexed)var3.next();
            Optional optional = indexed.getSignature(storage);
            if (optional.isEmpty()) {
               return Optional.empty();
            }

            list.add((MessageSignatureData)optional.get());
         }

         return Optional.of(new LastSeenMessageList(list));
      }

      public List buf() {
         return this.buf;
      }
   }

   public static record Acknowledgment(int offset, BitSet acknowledged, byte checksum) {
      public static final byte NO_CHECKSUM = 0;

      public Acknowledgment(PacketByteBuf buf) {
         this(buf.readVarInt(), buf.readBitSet(20), buf.readByte());
      }

      public Acknowledgment(int i, BitSet bitSet, byte b) {
         this.offset = i;
         this.acknowledged = bitSet;
         this.checksum = b;
      }

      public void write(PacketByteBuf buf) {
         buf.writeVarInt(this.offset);
         buf.writeBitSet(this.acknowledged, 20);
         buf.writeByte(this.checksum);
      }

      public boolean checksumEquals(LastSeenMessageList lastSeenMessages) {
         return this.checksum == 0 || this.checksum == lastSeenMessages.calculateChecksum();
      }

      public int offset() {
         return this.offset;
      }

      public BitSet acknowledged() {
         return this.acknowledged;
      }

      public byte checksum() {
         return this.checksum;
      }
   }
}

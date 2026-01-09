package net.minecraft.util;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record PlayerInput(boolean forward, boolean backward, boolean left, boolean right, boolean jump, boolean sneak, boolean sprint) {
   private static final byte FORWARD = 1;
   private static final byte BACKWARD = 2;
   private static final byte LEFT = 4;
   private static final byte RIGHT = 8;
   private static final byte JUMP = 16;
   private static final byte SNEAK = 32;
   private static final byte SPRINT = 64;
   public static final PacketCodec PACKET_CODEC = new PacketCodec() {
      public void encode(PacketByteBuf packetByteBuf, PlayerInput playerInput) {
         byte b = 0;
         byte bx = (byte)(b | (playerInput.forward() ? 1 : 0));
         bx = (byte)(bx | (playerInput.backward() ? 2 : 0));
         bx = (byte)(bx | (playerInput.left() ? 4 : 0));
         bx = (byte)(bx | (playerInput.right() ? 8 : 0));
         bx = (byte)(bx | (playerInput.jump() ? 16 : 0));
         bx = (byte)(bx | (playerInput.sneak() ? 32 : 0));
         bx = (byte)(bx | (playerInput.sprint() ? 64 : 0));
         packetByteBuf.writeByte(bx);
      }

      public PlayerInput decode(PacketByteBuf packetByteBuf) {
         byte b = packetByteBuf.readByte();
         boolean bl = (b & 1) != 0;
         boolean bl2 = (b & 2) != 0;
         boolean bl3 = (b & 4) != 0;
         boolean bl4 = (b & 8) != 0;
         boolean bl5 = (b & 16) != 0;
         boolean bl6 = (b & 32) != 0;
         boolean bl7 = (b & 64) != 0;
         return new PlayerInput(bl, bl2, bl3, bl4, bl5, bl6, bl7);
      }

      // $FF: synthetic method
      public void encode(final Object object, final Object object2) {
         this.encode((PacketByteBuf)object, (PlayerInput)object2);
      }

      // $FF: synthetic method
      public Object decode(final Object object) {
         return this.decode((PacketByteBuf)object);
      }
   };
   public static PlayerInput DEFAULT = new PlayerInput(false, false, false, false, false, false, false);

   public PlayerInput(boolean bl, boolean bl2, boolean bl3, boolean bl4, boolean bl5, boolean bl6, boolean bl7) {
      this.forward = bl;
      this.backward = bl2;
      this.left = bl3;
      this.right = bl4;
      this.jump = bl5;
      this.sneak = bl6;
      this.sprint = bl7;
   }

   public boolean forward() {
      return this.forward;
   }

   public boolean backward() {
      return this.backward;
   }

   public boolean left() {
      return this.left;
   }

   public boolean right() {
      return this.right;
   }

   public boolean jump() {
      return this.jump;
   }

   public boolean sneak() {
      return this.sneak;
   }

   public boolean sprint() {
      return this.sprint;
   }
}

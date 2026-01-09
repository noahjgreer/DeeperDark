package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.util.ReferenceCounted;

public record OpaqueByteBufHolder(ByteBuf contents) implements ReferenceCounted {
   public OpaqueByteBufHolder(final ByteBuf buf) {
      this.contents = ByteBufUtil.ensureAccessible(buf);
   }

   public static Object pack(Object buf) {
      if (buf instanceof ByteBuf byteBuf) {
         return new OpaqueByteBufHolder(byteBuf);
      } else {
         return buf;
      }
   }

   public static Object unpack(Object holder) {
      if (holder instanceof OpaqueByteBufHolder opaqueByteBufHolder) {
         return ByteBufUtil.ensureAccessible(opaqueByteBufHolder.contents);
      } else {
         return holder;
      }
   }

   public int refCnt() {
      return this.contents.refCnt();
   }

   public OpaqueByteBufHolder retain() {
      this.contents.retain();
      return this;
   }

   public OpaqueByteBufHolder retain(int i) {
      this.contents.retain(i);
      return this;
   }

   public OpaqueByteBufHolder touch() {
      this.contents.touch();
      return this;
   }

   public OpaqueByteBufHolder touch(Object object) {
      this.contents.touch(object);
      return this;
   }

   public boolean release() {
      return this.contents.release();
   }

   public boolean release(int count) {
      return this.contents.release(count);
   }

   public ByteBuf contents() {
      return this.contents;
   }

   // $FF: synthetic method
   public ReferenceCounted touch(final Object object) {
      return this.touch(object);
   }

   // $FF: synthetic method
   public ReferenceCounted touch() {
      return this.touch();
   }

   // $FF: synthetic method
   public ReferenceCounted retain(final int count) {
      return this.retain(count);
   }

   // $FF: synthetic method
   public ReferenceCounted retain() {
      return this.retain();
   }
}

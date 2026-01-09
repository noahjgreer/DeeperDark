package net.minecraft.network.encryption;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import javax.crypto.Cipher;

public class PacketEncryptor extends MessageToByteEncoder {
   private final PacketEncryptionManager manager;

   public PacketEncryptor(Cipher cipher) {
      this.manager = new PacketEncryptionManager(cipher);
   }

   protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, ByteBuf byteBuf2) throws Exception {
      this.manager.encrypt(byteBuf, byteBuf2);
   }

   // $FF: synthetic method
   protected void encode(final ChannelHandlerContext context, final Object buf, final ByteBuf result) throws Exception {
      this.encode(context, (ByteBuf)buf, result);
   }
}

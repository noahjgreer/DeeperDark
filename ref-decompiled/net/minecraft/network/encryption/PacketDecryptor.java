package net.minecraft.network.encryption;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;
import javax.crypto.Cipher;

public class PacketDecryptor extends MessageToMessageDecoder {
   private final PacketEncryptionManager manager;

   public PacketDecryptor(Cipher cipher) {
      this.manager = new PacketEncryptionManager(cipher);
   }

   protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List list) throws Exception {
      list.add(this.manager.decrypt(channelHandlerContext, byteBuf));
   }

   // $FF: synthetic method
   protected void decode(final ChannelHandlerContext context, final Object buf, final List result) throws Exception {
      this.decode(context, (ByteBuf)buf, result);
   }
}

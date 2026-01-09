package net.minecraft.network.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.util.List;
import java.util.Objects;
import net.minecraft.network.packet.Packet;

public class PacketUnbundler extends MessageToMessageEncoder {
   private final PacketBundleHandler bundleHandler;

   public PacketUnbundler(PacketBundleHandler bundleHandler) {
      this.bundleHandler = bundleHandler;
   }

   protected void encode(ChannelHandlerContext channelHandlerContext, Packet packet, List list) throws Exception {
      PacketBundleHandler var10000 = this.bundleHandler;
      Objects.requireNonNull(list);
      var10000.forEachPacket(packet, list::add);
      if (packet.transitionsNetworkState()) {
         channelHandlerContext.pipeline().remove(channelHandlerContext.name());
      }

   }

   // $FF: synthetic method
   protected void encode(final ChannelHandlerContext context, final Object packet, final List packets) throws Exception {
      this.encode(context, (Packet)packet, packets);
   }
}

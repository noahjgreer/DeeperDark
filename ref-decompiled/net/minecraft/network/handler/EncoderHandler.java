package net.minecraft.network.handler;

import com.mojang.logging.LogUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.state.NetworkState;
import net.minecraft.util.profiling.jfr.FlightProfiler;
import org.slf4j.Logger;

public class EncoderHandler extends MessageToByteEncoder {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final NetworkState state;

   public EncoderHandler(NetworkState state) {
      this.state = state;
   }

   protected void encode(ChannelHandlerContext channelHandlerContext, Packet packet, ByteBuf byteBuf) throws Exception {
      PacketType packetType = packet.getPacketType();

      try {
         this.state.codec().encode(byteBuf, packet);
         int i = byteBuf.readableBytes();
         if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(ClientConnection.PACKET_SENT_MARKER, "OUT: [{}:{}] {} -> {} bytes", new Object[]{this.state.id().getId(), packetType, packet.getClass().getName(), i});
         }

         FlightProfiler.INSTANCE.onPacketSent(this.state.id(), packetType, channelHandlerContext.channel().remoteAddress(), i);
      } catch (Throwable var9) {
         LOGGER.error("Error sending packet {}", packetType, var9);
         if (packet.isWritingErrorSkippable()) {
            throw new PacketEncoderException(var9);
         }

         throw var9;
      } finally {
         NetworkStateTransitionHandler.onEncoded(channelHandlerContext, packet);
      }

   }

   // $FF: synthetic method
   protected void encode(final ChannelHandlerContext context, final Object packet, final ByteBuf out) throws Exception {
      this.encode(context, (Packet)packet, out);
   }
}

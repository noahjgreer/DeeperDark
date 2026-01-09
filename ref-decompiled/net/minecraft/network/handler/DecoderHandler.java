package net.minecraft.network.handler;

import com.mojang.logging.LogUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.io.IOException;
import java.util.List;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.state.NetworkState;
import net.minecraft.util.profiling.jfr.FlightProfiler;
import org.slf4j.Logger;

public class DecoderHandler extends ByteToMessageDecoder implements NetworkStateTransitionHandler {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final NetworkState state;

   public DecoderHandler(NetworkState state) {
      this.state = state;
   }

   protected void decode(ChannelHandlerContext context, ByteBuf buf, List objects) throws Exception {
      int i = buf.readableBytes();
      if (i != 0) {
         Packet packet;
         try {
            packet = (Packet)this.state.codec().decode(buf);
         } catch (Exception var7) {
            if (var7 instanceof PacketException) {
               buf.skipBytes(buf.readableBytes());
            }

            throw var7;
         }

         PacketType packetType = packet.getPacketType();
         FlightProfiler.INSTANCE.onPacketReceived(this.state.id(), packetType, context.channel().remoteAddress(), i);
         if (buf.readableBytes() > 0) {
            String var10002 = this.state.id().getId();
            throw new IOException("Packet " + var10002 + "/" + String.valueOf(packetType) + " (" + packet.getClass().getSimpleName() + ") was larger than I expected, found " + buf.readableBytes() + " bytes extra whilst reading packet " + String.valueOf(packetType));
         } else {
            objects.add(packet);
            if (LOGGER.isDebugEnabled()) {
               LOGGER.debug(ClientConnection.PACKET_RECEIVED_MARKER, " IN: [{}:{}] {} -> {} bytes", new Object[]{this.state.id().getId(), packetType, packet.getClass().getName(), i});
            }

            NetworkStateTransitionHandler.onDecoded(context, packet);
         }
      }
   }
}

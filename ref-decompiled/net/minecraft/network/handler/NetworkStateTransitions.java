package net.minecraft.network.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import io.netty.util.ReferenceCountUtil;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.state.NetworkState;

public class NetworkStateTransitions {
   public static DecoderTransitioner decoderTransitioner(NetworkState newState) {
      return decoderSwapper(new DecoderHandler(newState));
   }

   private static DecoderTransitioner decoderSwapper(ChannelInboundHandler newDecoder) {
      return (context) -> {
         context.pipeline().replace(context.name(), "decoder", newDecoder);
         context.channel().config().setAutoRead(true);
      };
   }

   public static EncoderTransitioner encoderTransitioner(NetworkState newState) {
      return encoderSwapper(new EncoderHandler(newState));
   }

   private static EncoderTransitioner encoderSwapper(ChannelOutboundHandler newEncoder) {
      return (context) -> {
         context.pipeline().replace(context.name(), "encoder", newEncoder);
      };
   }

   @FunctionalInterface
   public interface DecoderTransitioner {
      void run(ChannelHandlerContext context);

      default DecoderTransitioner andThen(DecoderTransitioner decoderTransitioner) {
         return (context) -> {
            this.run(context);
            decoderTransitioner.run(context);
         };
      }
   }

   @FunctionalInterface
   public interface EncoderTransitioner {
      void run(ChannelHandlerContext context);

      default EncoderTransitioner andThen(EncoderTransitioner encoderTransitioner) {
         return (context) -> {
            this.run(context);
            encoderTransitioner.run(context);
         };
      }
   }

   public static class OutboundConfigurer extends ChannelOutboundHandlerAdapter {
      public void write(ChannelHandlerContext context, Object received, ChannelPromise promise) throws Exception {
         if (received instanceof Packet) {
            ReferenceCountUtil.release(received);
            throw new EncoderException("Pipeline has no outbound protocol configured, can't process packet " + String.valueOf(received));
         } else {
            if (received instanceof EncoderTransitioner) {
               EncoderTransitioner encoderTransitioner = (EncoderTransitioner)received;

               try {
                  encoderTransitioner.run(context);
               } finally {
                  ReferenceCountUtil.release(received);
               }

               promise.setSuccess();
            } else {
               context.write(received, promise);
            }

         }
      }
   }

   public static class InboundConfigurer extends ChannelDuplexHandler {
      public void channelRead(ChannelHandlerContext context, Object received) {
         if (!(received instanceof ByteBuf) && !(received instanceof Packet)) {
            context.fireChannelRead(received);
         } else {
            ReferenceCountUtil.release(received);
            throw new DecoderException("Pipeline has no inbound protocol configured, can't process packet " + String.valueOf(received));
         }
      }

      public void write(ChannelHandlerContext context, Object received, ChannelPromise promise) throws Exception {
         if (received instanceof DecoderTransitioner decoderTransitioner) {
            try {
               decoderTransitioner.run(context);
            } finally {
               ReferenceCountUtil.release(received);
            }

            promise.setSuccess();
         } else {
            context.write(received, promise);
         }

      }
   }
}

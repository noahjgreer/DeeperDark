package net.minecraft.network.state;

import net.minecraft.network.NetworkPhase;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.handler.PacketBundleHandler;
import net.minecraft.network.packet.PacketType;
import net.minecraft.util.annotation.Debug;
import org.jetbrains.annotations.Nullable;

public interface NetworkState {
   NetworkPhase id();

   NetworkSide side();

   PacketCodec codec();

   @Nullable
   PacketBundleHandler bundleHandler();

   public interface Factory {
      Unbound buildUnbound();
   }

   public interface Unbound {
      NetworkPhase phase();

      NetworkSide side();

      @Debug
      void forEachPacketType(PacketTypeConsumer callback);

      @FunctionalInterface
      public interface PacketTypeConsumer {
         void accept(PacketType type, int protocolId);
      }
   }
}

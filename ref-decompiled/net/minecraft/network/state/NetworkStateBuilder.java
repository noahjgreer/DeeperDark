package net.minecraft.network.state;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.network.NetworkPhase;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.handler.PacketBundleHandler;
import net.minecraft.network.handler.SideValidatingDispatchingCodecBuilder;
import net.minecraft.network.packet.BundleSplitterPacket;
import net.minecraft.network.packet.PacketCodecModifier;
import net.minecraft.util.Unit;
import org.jetbrains.annotations.Nullable;

public class NetworkStateBuilder {
   final NetworkPhase phase;
   final NetworkSide side;
   private final List packetTypes = new ArrayList();
   @Nullable
   private PacketBundleHandler bundleHandler;

   public NetworkStateBuilder(NetworkPhase phase, NetworkSide side) {
      this.phase = phase;
      this.side = side;
   }

   public NetworkStateBuilder add(net.minecraft.network.packet.PacketType type, PacketCodec codec) {
      this.packetTypes.add(new PacketType(type, codec, (PacketCodecModifier)null));
      return this;
   }

   public NetworkStateBuilder add(net.minecraft.network.packet.PacketType type, PacketCodec codec, PacketCodecModifier modifier) {
      this.packetTypes.add(new PacketType(type, codec, modifier));
      return this;
   }

   public NetworkStateBuilder addBundle(net.minecraft.network.packet.PacketType id, Function bundler, BundleSplitterPacket splitter) {
      PacketCodec packetCodec = PacketCodec.unit(splitter);
      net.minecraft.network.packet.PacketType packetType = splitter.getPacketType();
      this.packetTypes.add(new PacketType(packetType, packetCodec, (PacketCodecModifier)null));
      this.bundleHandler = PacketBundleHandler.create(id, bundler, splitter);
      return this;
   }

   PacketCodec createCodec(Function bufUpgrader, List packetTypes, Object context) {
      SideValidatingDispatchingCodecBuilder sideValidatingDispatchingCodecBuilder = new SideValidatingDispatchingCodecBuilder(this.side);
      Iterator var5 = packetTypes.iterator();

      while(var5.hasNext()) {
         PacketType packetType = (PacketType)var5.next();
         packetType.add(sideValidatingDispatchingCodecBuilder, bufUpgrader, context);
      }

      return sideValidatingDispatchingCodecBuilder.build();
   }

   private static NetworkState.Unbound createState(final NetworkPhase phase, final NetworkSide side, final List types) {
      return new NetworkState.Unbound() {
         public NetworkPhase phase() {
            return phase;
         }

         public NetworkSide side() {
            return side;
         }

         public void forEachPacketType(NetworkState.Unbound.PacketTypeConsumer callback) {
            for(int i = 0; i < types.size(); ++i) {
               PacketType packetType = (PacketType)types.get(i);
               callback.accept(packetType.type, i);
            }

         }
      };
   }

   public NetworkStateFactory buildFactory(final Object context) {
      final List list = List.copyOf(this.packetTypes);
      final PacketBundleHandler packetBundleHandler = this.bundleHandler;
      final NetworkState.Unbound unbound = createState(this.phase, this.side, list);
      return new NetworkStateFactory() {
         public NetworkState bind(Function registryBinder) {
            return new NetworkStateImpl(NetworkStateBuilder.this.phase, NetworkStateBuilder.this.side, NetworkStateBuilder.this.createCodec(registryBinder, list, context), packetBundleHandler);
         }

         public NetworkState.Unbound buildUnbound() {
            return unbound;
         }
      };
   }

   public ContextAwareNetworkStateFactory buildContextAwareFactory() {
      final List list = List.copyOf(this.packetTypes);
      final PacketBundleHandler packetBundleHandler = this.bundleHandler;
      final NetworkState.Unbound unbound = createState(this.phase, this.side, list);
      return new ContextAwareNetworkStateFactory() {
         public NetworkState bind(Function registryBinder, Object context) {
            return new NetworkStateImpl(NetworkStateBuilder.this.phase, NetworkStateBuilder.this.side, NetworkStateBuilder.this.createCodec(registryBinder, list, context), packetBundleHandler);
         }

         public NetworkState.Unbound buildUnbound() {
            return unbound;
         }
      };
   }

   private static NetworkStateFactory build(NetworkPhase type, NetworkSide side, Consumer registrar) {
      NetworkStateBuilder networkStateBuilder = new NetworkStateBuilder(type, side);
      registrar.accept(networkStateBuilder);
      return networkStateBuilder.buildFactory(Unit.INSTANCE);
   }

   public static NetworkStateFactory c2s(NetworkPhase type, Consumer registrar) {
      return build(type, NetworkSide.SERVERBOUND, registrar);
   }

   public static NetworkStateFactory s2c(NetworkPhase type, Consumer registrar) {
      return build(type, NetworkSide.CLIENTBOUND, registrar);
   }

   private static ContextAwareNetworkStateFactory buildContextAware(NetworkPhase type, NetworkSide side, Consumer registrar) {
      NetworkStateBuilder networkStateBuilder = new NetworkStateBuilder(type, side);
      registrar.accept(networkStateBuilder);
      return networkStateBuilder.buildContextAwareFactory();
   }

   public static ContextAwareNetworkStateFactory contextAwareC2S(NetworkPhase type, Consumer registrar) {
      return buildContextAware(type, NetworkSide.SERVERBOUND, registrar);
   }

   public static ContextAwareNetworkStateFactory contextAwareS2C(NetworkPhase type, Consumer registrar) {
      return buildContextAware(type, NetworkSide.CLIENTBOUND, registrar);
   }

   static record PacketType(net.minecraft.network.packet.PacketType type, PacketCodec codec, @Nullable PacketCodecModifier modifier) {
      final net.minecraft.network.packet.PacketType type;

      PacketType(net.minecraft.network.packet.PacketType packetType, PacketCodec packetCodec, @Nullable PacketCodecModifier packetCodecModifier) {
         this.type = packetType;
         this.codec = packetCodec;
         this.modifier = packetCodecModifier;
      }

      public void add(SideValidatingDispatchingCodecBuilder builder, Function bufUpgrader, Object context) {
         PacketCodec packetCodec;
         if (this.modifier != null) {
            packetCodec = this.modifier.apply(this.codec, context);
         } else {
            packetCodec = this.codec;
         }

         PacketCodec packetCodec2 = packetCodec.mapBuf(bufUpgrader);
         builder.add(this.type, packetCodec2);
      }

      public net.minecraft.network.packet.PacketType type() {
         return this.type;
      }

      public PacketCodec codec() {
         return this.codec;
      }

      @Nullable
      public PacketCodecModifier modifier() {
         return this.modifier;
      }
   }

   static record NetworkStateImpl(NetworkPhase id, NetworkSide side, PacketCodec codec, @Nullable PacketBundleHandler bundleHandler) implements NetworkState {
      NetworkStateImpl(NetworkPhase networkPhase, NetworkSide networkSide, PacketCodec packetCodec, @Nullable PacketBundleHandler packetBundleHandler) {
         this.id = networkPhase;
         this.side = networkSide;
         this.codec = packetCodec;
         this.bundleHandler = packetBundleHandler;
      }

      public NetworkPhase id() {
         return this.id;
      }

      public NetworkSide side() {
         return this.side;
      }

      public PacketCodec codec() {
         return this.codec;
      }

      @Nullable
      public PacketBundleHandler bundleHandler() {
         return this.bundleHandler;
      }
   }
}

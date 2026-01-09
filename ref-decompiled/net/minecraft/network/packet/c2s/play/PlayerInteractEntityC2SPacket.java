package net.minecraft.network.packet.c2s.play;

import java.util.function.Function;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class PlayerInteractEntityC2SPacket implements Packet {
   public static final PacketCodec CODEC = Packet.createCodec(PlayerInteractEntityC2SPacket::write, PlayerInteractEntityC2SPacket::new);
   private final int entityId;
   private final InteractTypeHandler type;
   private final boolean playerSneaking;
   static final InteractTypeHandler ATTACK = new InteractTypeHandler() {
      public InteractType getType() {
         return PlayerInteractEntityC2SPacket.InteractType.ATTACK;
      }

      public void handle(Handler handler) {
         handler.attack();
      }

      public void write(PacketByteBuf buf) {
      }
   };

   private PlayerInteractEntityC2SPacket(int entityId, boolean playerSneaking, InteractTypeHandler type) {
      this.entityId = entityId;
      this.type = type;
      this.playerSneaking = playerSneaking;
   }

   public static PlayerInteractEntityC2SPacket attack(Entity entity, boolean playerSneaking) {
      return new PlayerInteractEntityC2SPacket(entity.getId(), playerSneaking, ATTACK);
   }

   public static PlayerInteractEntityC2SPacket interact(Entity entity, boolean playerSneaking, Hand hand) {
      return new PlayerInteractEntityC2SPacket(entity.getId(), playerSneaking, new InteractHandler(hand));
   }

   public static PlayerInteractEntityC2SPacket interactAt(Entity entity, boolean playerSneaking, Hand hand, Vec3d pos) {
      return new PlayerInteractEntityC2SPacket(entity.getId(), playerSneaking, new InteractAtHandler(hand, pos));
   }

   private PlayerInteractEntityC2SPacket(PacketByteBuf buf) {
      this.entityId = buf.readVarInt();
      InteractType interactType = (InteractType)buf.readEnumConstant(InteractType.class);
      this.type = (InteractTypeHandler)interactType.handlerGetter.apply(buf);
      this.playerSneaking = buf.readBoolean();
   }

   private void write(PacketByteBuf buf) {
      buf.writeVarInt(this.entityId);
      buf.writeEnumConstant(this.type.getType());
      this.type.write(buf);
      buf.writeBoolean(this.playerSneaking);
   }

   public PacketType getPacketType() {
      return PlayPackets.INTERACT;
   }

   public void apply(ServerPlayPacketListener serverPlayPacketListener) {
      serverPlayPacketListener.onPlayerInteractEntity(this);
   }

   @Nullable
   public Entity getEntity(ServerWorld world) {
      return world.getEntityOrDragonPart(this.entityId);
   }

   public boolean isPlayerSneaking() {
      return this.playerSneaking;
   }

   public void handle(Handler handler) {
      this.type.handle(handler);
   }

   private interface InteractTypeHandler {
      InteractType getType();

      void handle(Handler handler);

      void write(PacketByteBuf buf);
   }

   private static class InteractHandler implements InteractTypeHandler {
      private final Hand hand;

      InteractHandler(Hand hand) {
         this.hand = hand;
      }

      private InteractHandler(PacketByteBuf buf) {
         this.hand = (Hand)buf.readEnumConstant(Hand.class);
      }

      public InteractType getType() {
         return PlayerInteractEntityC2SPacket.InteractType.INTERACT;
      }

      public void handle(Handler handler) {
         handler.interact(this.hand);
      }

      public void write(PacketByteBuf buf) {
         buf.writeEnumConstant(this.hand);
      }
   }

   static class InteractAtHandler implements InteractTypeHandler {
      private final Hand hand;
      private final Vec3d pos;

      InteractAtHandler(Hand hand, Vec3d pos) {
         this.hand = hand;
         this.pos = pos;
      }

      private InteractAtHandler(PacketByteBuf buf) {
         this.pos = new Vec3d((double)buf.readFloat(), (double)buf.readFloat(), (double)buf.readFloat());
         this.hand = (Hand)buf.readEnumConstant(Hand.class);
      }

      public InteractType getType() {
         return PlayerInteractEntityC2SPacket.InteractType.INTERACT_AT;
      }

      public void handle(Handler handler) {
         handler.interactAt(this.hand, this.pos);
      }

      public void write(PacketByteBuf buf) {
         buf.writeFloat((float)this.pos.x);
         buf.writeFloat((float)this.pos.y);
         buf.writeFloat((float)this.pos.z);
         buf.writeEnumConstant(this.hand);
      }
   }

   private static enum InteractType {
      INTERACT(InteractHandler::new),
      ATTACK((buf) -> {
         return PlayerInteractEntityC2SPacket.ATTACK;
      }),
      INTERACT_AT(InteractAtHandler::new);

      final Function handlerGetter;

      private InteractType(final Function handlerGetter) {
         this.handlerGetter = handlerGetter;
      }

      // $FF: synthetic method
      private static InteractType[] method_36956() {
         return new InteractType[]{INTERACT, ATTACK, INTERACT_AT};
      }
   }

   public interface Handler {
      void interact(Hand hand);

      void interactAt(Hand hand, Vec3d pos);

      void attack();
   }
}

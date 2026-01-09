package net.minecraft.network.packet.c2s.play;

import java.util.Optional;
import java.util.function.IntFunction;
import net.minecraft.block.entity.TestInstanceBlockEntity;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.function.ValueLists;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public record TestInstanceBlockActionC2SPacket(BlockPos pos, Action action, TestInstanceBlockEntity.Data data) implements Packet {
   public static final PacketCodec CODEC;

   public TestInstanceBlockActionC2SPacket(BlockPos pos, Action actin, Optional optional, Vec3i vec3i, BlockRotation blockRotation, boolean bl) {
      this(pos, actin, new TestInstanceBlockEntity.Data(optional, vec3i, blockRotation, bl, TestInstanceBlockEntity.Status.CLEARED, Optional.empty()));
   }

   public TestInstanceBlockActionC2SPacket(BlockPos blockPos, Action action, TestInstanceBlockEntity.Data data) {
      this.pos = blockPos;
      this.action = action;
      this.data = data;
   }

   public PacketType getPacketType() {
      return PlayPackets.TEST_INSTANCE_BLOCK_ACTION;
   }

   public void apply(ServerPlayPacketListener serverPlayPacketListener) {
      serverPlayPacketListener.onTestInstanceBlockAction(this);
   }

   public BlockPos pos() {
      return this.pos;
   }

   public Action action() {
      return this.action;
   }

   public TestInstanceBlockEntity.Data data() {
      return this.data;
   }

   static {
      CODEC = PacketCodec.tuple(BlockPos.PACKET_CODEC, TestInstanceBlockActionC2SPacket::pos, TestInstanceBlockActionC2SPacket.Action.CODEC, TestInstanceBlockActionC2SPacket::action, TestInstanceBlockEntity.Data.PACKET_CODEC, TestInstanceBlockActionC2SPacket::data, TestInstanceBlockActionC2SPacket::new);
   }

   public static enum Action {
      INIT(0),
      QUERY(1),
      SET(2),
      RESET(3),
      SAVE(4),
      EXPORT(5),
      RUN(6);

      private static final IntFunction INDEX_MAPPER = ValueLists.createIndexToValueFunction((action) -> {
         return action.index;
      }, values(), (ValueLists.OutOfBoundsHandling)ValueLists.OutOfBoundsHandling.ZERO);
      public static final PacketCodec CODEC = PacketCodecs.indexed(INDEX_MAPPER, (action) -> {
         return action.index;
      });
      private final int index;

      private Action(final int index) {
         this.index = index;
      }

      // $FF: synthetic method
      private static Action[] method_66585() {
         return new Action[]{INIT, QUERY, SET, RESET, SAVE, EXPORT, RUN};
      }
   }
}

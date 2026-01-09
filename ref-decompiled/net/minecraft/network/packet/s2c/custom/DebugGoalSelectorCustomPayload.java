package net.minecraft.network.packet.s2c.custom;

import java.util.List;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;

public record DebugGoalSelectorCustomPayload(int entityId, BlockPos pos, List goals) implements CustomPayload {
   public static final PacketCodec CODEC = CustomPayload.codecOf(DebugGoalSelectorCustomPayload::write, DebugGoalSelectorCustomPayload::new);
   public static final CustomPayload.Id ID = CustomPayload.id("debug/goal_selector");

   private DebugGoalSelectorCustomPayload(PacketByteBuf buf) {
      this(buf.readInt(), buf.readBlockPos(), buf.readList(Goal::new));
   }

   public DebugGoalSelectorCustomPayload(int i, BlockPos blockPos, List list) {
      this.entityId = i;
      this.pos = blockPos;
      this.goals = list;
   }

   private void write(PacketByteBuf buf) {
      buf.writeInt(this.entityId);
      buf.writeBlockPos(this.pos);
      buf.writeCollection(this.goals, (bufx, goal) -> {
         goal.write(bufx);
      });
   }

   public CustomPayload.Id getId() {
      return ID;
   }

   public int entityId() {
      return this.entityId;
   }

   public BlockPos pos() {
      return this.pos;
   }

   public List goals() {
      return this.goals;
   }

   public static record Goal(int priority, boolean isRunning, String name) {
      public Goal(PacketByteBuf buf) {
         this(buf.readInt(), buf.readBoolean(), buf.readString(255));
      }

      public Goal(int i, boolean bl, String string) {
         this.priority = i;
         this.isRunning = bl;
         this.name = string;
      }

      public void write(PacketByteBuf buf) {
         buf.writeInt(this.priority);
         buf.writeBoolean(this.isRunning);
         buf.writeString(this.name);
      }

      public int priority() {
         return this.priority;
      }

      public boolean isRunning() {
         return this.isRunning;
      }

      public String name() {
         return this.name;
      }
   }
}

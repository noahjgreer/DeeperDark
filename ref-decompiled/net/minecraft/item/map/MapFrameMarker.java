package net.minecraft.item.map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.BlockPos;

public record MapFrameMarker(BlockPos pos, int rotation, int entityId) {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(BlockPos.CODEC.fieldOf("pos").forGetter(MapFrameMarker::pos), Codec.INT.fieldOf("rotation").forGetter(MapFrameMarker::rotation), Codec.INT.fieldOf("entity_id").forGetter(MapFrameMarker::entityId)).apply(instance, MapFrameMarker::new);
   });

   public MapFrameMarker(BlockPos pos, int rotation, int entityId) {
      this.pos = pos;
      this.rotation = rotation;
      this.entityId = entityId;
   }

   public String getKey() {
      return getKey(this.pos);
   }

   public static String getKey(BlockPos pos) {
      int var10000 = pos.getX();
      return "frame-" + var10000 + "," + pos.getY() + "," + pos.getZ();
   }

   public BlockPos pos() {
      return this.pos;
   }

   public int rotation() {
      return this.rotation;
   }

   public int entityId() {
      return this.entityId;
   }
}

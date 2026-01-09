package net.minecraft.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.datafixer.DataFixTypes;

public class IdCountsState extends PersistentState {
   private static final int field_56476 = -1;
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(Codec.INT.optionalFieldOf("map", -1).forGetter((state) -> {
         return state.map;
      })).apply(instance, IdCountsState::new);
   });
   public static final PersistentStateType STATE_TYPE;
   private int map;

   public IdCountsState() {
      this(-1);
   }

   public IdCountsState(int map) {
      this.map = map;
   }

   public MapIdComponent createNextMapId() {
      MapIdComponent mapIdComponent = new MapIdComponent(++this.map);
      this.markDirty();
      return mapIdComponent;
   }

   static {
      STATE_TYPE = new PersistentStateType("idcounts", IdCountsState::new, CODEC, DataFixTypes.SAVED_DATA_MAP_INDEX);
   }
}

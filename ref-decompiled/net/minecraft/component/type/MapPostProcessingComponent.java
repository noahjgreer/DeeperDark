package net.minecraft.component.type;

import java.util.function.IntFunction;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.function.ValueLists;

public enum MapPostProcessingComponent {
   LOCK(0),
   SCALE(1);

   public static final IntFunction ID_TO_VALUE = ValueLists.createIndexToValueFunction(MapPostProcessingComponent::getId, values(), (ValueLists.OutOfBoundsHandling)ValueLists.OutOfBoundsHandling.ZERO);
   public static final PacketCodec PACKET_CODEC = PacketCodecs.indexed(ID_TO_VALUE, MapPostProcessingComponent::getId);
   private final int id;

   private MapPostProcessingComponent(final int id) {
      this.id = id;
   }

   public int getId() {
      return this.id;
   }

   // $FF: synthetic method
   private static MapPostProcessingComponent[] method_57506() {
      return new MapPostProcessingComponent[]{LOCK, SCALE};
   }
}

package net.minecraft.entity.data;

import net.minecraft.network.codec.PacketCodec;

public interface TrackedDataHandler {
   PacketCodec codec();

   default TrackedData create(int id) {
      return new TrackedData(id, this);
   }

   Object copy(Object value);

   static TrackedDataHandler create(PacketCodec codec) {
      return () -> {
         return codec;
      };
   }

   public interface ImmutableHandler extends TrackedDataHandler {
      default Object copy(Object object) {
         return object;
      }
   }
}

package net.minecraft.network.packet.s2c.play;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public enum PositionFlag {
   X(0),
   Y(1),
   Z(2),
   Y_ROT(3),
   X_ROT(4),
   DELTA_X(5),
   DELTA_Y(6),
   DELTA_Z(7),
   ROTATE_DELTA(8);

   public static final Set VALUES = Set.of(values());
   public static final Set ROT = Set.of(X_ROT, Y_ROT);
   public static final Set DELTA = Set.of(DELTA_X, DELTA_Y, DELTA_Z, ROTATE_DELTA);
   public static final PacketCodec PACKET_CODEC = PacketCodecs.INTEGER.xmap(PositionFlag::getFlags, PositionFlag::getBitfield);
   private final int shift;

   @SafeVarargs
   public static Set combine(Set... sets) {
      HashSet hashSet = new HashSet();
      Set[] var2 = sets;
      int var3 = sets.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Set set = var2[var4];
         hashSet.addAll(set);
      }

      return hashSet;
   }

   private PositionFlag(final int shift) {
      this.shift = shift;
   }

   private int getMask() {
      return 1 << this.shift;
   }

   private boolean isSet(int mask) {
      return (mask & this.getMask()) == this.getMask();
   }

   public static Set getFlags(int mask) {
      Set set = EnumSet.noneOf(PositionFlag.class);
      PositionFlag[] var2 = values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         PositionFlag positionFlag = var2[var4];
         if (positionFlag.isSet(mask)) {
            set.add(positionFlag);
         }
      }

      return set;
   }

   public static int getBitfield(Set flags) {
      int i = 0;

      PositionFlag positionFlag;
      for(Iterator var2 = flags.iterator(); var2.hasNext(); i |= positionFlag.getMask()) {
         positionFlag = (PositionFlag)var2.next();
      }

      return i;
   }

   // $FF: synthetic method
   private static PositionFlag[] method_36952() {
      return new PositionFlag[]{X, Y, Z, Y_ROT, X_ROT, DELTA_X, DELTA_Y, DELTA_Z, ROTATE_DELTA};
   }
}

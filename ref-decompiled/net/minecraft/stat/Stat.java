package net.minecraft.stat;

import java.util.Objects;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class Stat extends ScoreboardCriterion {
   public static final PacketCodec PACKET_CODEC;
   private final StatFormatter formatter;
   private final Object value;
   private final StatType type;

   protected Stat(StatType type, Object value, StatFormatter formatter) {
      super(getName(type, value));
      this.type = type;
      this.formatter = formatter;
      this.value = value;
   }

   public static String getName(StatType type, Object value) {
      String var10000 = getName(Registries.STAT_TYPE.getId(type));
      return var10000 + ":" + getName(type.getRegistry().getId(value));
   }

   private static String getName(@Nullable Identifier id) {
      return id.toString().replace(':', '.');
   }

   public StatType getType() {
      return this.type;
   }

   public Object getValue() {
      return this.value;
   }

   public String format(int value) {
      return this.formatter.format(value);
   }

   public boolean equals(Object o) {
      return this == o || o instanceof Stat && Objects.equals(this.getName(), ((Stat)o).getName());
   }

   public int hashCode() {
      return this.getName().hashCode();
   }

   public String toString() {
      String var10000 = this.getName();
      return "Stat{name=" + var10000 + ", formatter=" + String.valueOf(this.formatter) + "}";
   }

   static {
      PACKET_CODEC = PacketCodecs.registryValue(RegistryKeys.STAT_TYPE).dispatch(Stat::getType, StatType::getPacketCodec);
   }
}

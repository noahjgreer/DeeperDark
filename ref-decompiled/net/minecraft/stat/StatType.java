package net.minecraft.stat;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;

public class StatType implements Iterable {
   private final Registry registry;
   private final Map stats = new IdentityHashMap();
   private final Text name;
   private final PacketCodec packetCodec;

   public StatType(Registry registry, Text name) {
      this.registry = registry;
      this.name = name;
      this.packetCodec = PacketCodecs.registryValue(registry.getKey()).xmap(this::getOrCreateStat, Stat::getValue);
   }

   public PacketCodec getPacketCodec() {
      return this.packetCodec;
   }

   public boolean hasStat(Object key) {
      return this.stats.containsKey(key);
   }

   public Stat getOrCreateStat(Object key, StatFormatter formatter) {
      return (Stat)this.stats.computeIfAbsent(key, (value) -> {
         return new Stat(this, value, formatter);
      });
   }

   public Registry getRegistry() {
      return this.registry;
   }

   public Iterator iterator() {
      return this.stats.values().iterator();
   }

   public Stat getOrCreateStat(Object key) {
      return this.getOrCreateStat(key, StatFormatter.DEFAULT);
   }

   public Text getName() {
      return this.name;
   }
}

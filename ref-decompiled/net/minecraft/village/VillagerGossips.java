package net.minecraft.village;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.DoublePredicate;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.util.Uuids;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.random.Random;

public class VillagerGossips {
   public static final Codec CODEC;
   public static final int field_30236 = 2;
   private final Map entityReputation = new HashMap();

   public VillagerGossips() {
   }

   private VillagerGossips(List gossips) {
      gossips.forEach((gossip) -> {
         this.getReputationFor(gossip.target).associatedGossip.put(gossip.type, gossip.value);
      });
   }

   @Debug
   public Map getEntityReputationAssociatedGossips() {
      Map map = Maps.newHashMap();
      this.entityReputation.keySet().forEach((uuid) -> {
         Reputation reputation = (Reputation)this.entityReputation.get(uuid);
         map.put(uuid, reputation.associatedGossip);
      });
      return map;
   }

   public void decay() {
      Iterator iterator = this.entityReputation.values().iterator();

      while(iterator.hasNext()) {
         Reputation reputation = (Reputation)iterator.next();
         reputation.decay();
         if (reputation.isObsolete()) {
            iterator.remove();
         }
      }

   }

   private Stream entries() {
      return this.entityReputation.entrySet().stream().flatMap((entry) -> {
         return ((Reputation)entry.getValue()).entriesFor((UUID)entry.getKey());
      });
   }

   private Collection pickGossips(Random random, int count) {
      List list = this.entries().toList();
      if (list.isEmpty()) {
         return Collections.emptyList();
      } else {
         int[] is = new int[list.size()];
         int i = 0;

         for(int j = 0; j < list.size(); ++j) {
            GossipEntry gossipEntry = (GossipEntry)list.get(j);
            i += Math.abs(gossipEntry.getValue());
            is[j] = i - 1;
         }

         Set set = Sets.newIdentityHashSet();

         for(int k = 0; k < count; ++k) {
            int l = random.nextInt(i);
            int m = Arrays.binarySearch(is, l);
            set.add((GossipEntry)list.get(m < 0 ? -m - 1 : m));
         }

         return set;
      }
   }

   private Reputation getReputationFor(UUID target) {
      return (Reputation)this.entityReputation.computeIfAbsent(target, (uuid) -> {
         return new Reputation();
      });
   }

   public void shareGossipFrom(VillagerGossips from, Random random, int count) {
      Collection collection = from.pickGossips(random, count);
      collection.forEach((gossip) -> {
         int i = gossip.value - gossip.type.shareDecrement;
         if (i >= 2) {
            this.getReputationFor(gossip.target).associatedGossip.mergeInt(gossip.type, i, VillagerGossips::max);
         }

      });
   }

   public int getReputationFor(UUID target, Predicate gossipTypeFilter) {
      Reputation reputation = (Reputation)this.entityReputation.get(target);
      return reputation != null ? reputation.getValueFor(gossipTypeFilter) : 0;
   }

   public long getReputationCount(VillagerGossipType type, DoublePredicate predicate) {
      return this.entityReputation.values().stream().filter((reputation) -> {
         return predicate.test((double)(reputation.associatedGossip.getOrDefault(type, 0) * type.multiplier));
      }).count();
   }

   public void startGossip(UUID target, VillagerGossipType type, int value) {
      Reputation reputation = this.getReputationFor(target);
      reputation.associatedGossip.mergeInt(type, value, (left, right) -> {
         return this.mergeReputation(type, left, right);
      });
      reputation.clamp(type);
      if (reputation.isObsolete()) {
         this.entityReputation.remove(target);
      }

   }

   public void removeGossip(UUID target, VillagerGossipType type, int value) {
      this.startGossip(target, type, -value);
   }

   public void remove(UUID target, VillagerGossipType type) {
      Reputation reputation = (Reputation)this.entityReputation.get(target);
      if (reputation != null) {
         reputation.remove(type);
         if (reputation.isObsolete()) {
            this.entityReputation.remove(target);
         }
      }

   }

   public void remove(VillagerGossipType type) {
      Iterator iterator = this.entityReputation.values().iterator();

      while(iterator.hasNext()) {
         Reputation reputation = (Reputation)iterator.next();
         reputation.remove(type);
         if (reputation.isObsolete()) {
            iterator.remove();
         }
      }

   }

   public void clear() {
      this.entityReputation.clear();
   }

   public void add(VillagerGossips gossips) {
      gossips.entityReputation.forEach((target, reputation) -> {
         this.getReputationFor(target).associatedGossip.putAll(reputation.associatedGossip);
      });
   }

   private static int max(int left, int right) {
      return Math.max(left, right);
   }

   private int mergeReputation(VillagerGossipType type, int left, int right) {
      int i = left + right;
      return i > type.maxValue ? Math.max(type.maxValue, left) : i;
   }

   public VillagerGossips copy() {
      VillagerGossips villagerGossips = new VillagerGossips();
      villagerGossips.add(this);
      return villagerGossips;
   }

   static {
      CODEC = VillagerGossips.GossipEntry.CODEC.listOf().xmap(VillagerGossips::new, (gossips) -> {
         return gossips.entries().toList();
      });
   }

   private static class Reputation {
      final Object2IntMap associatedGossip = new Object2IntOpenHashMap();

      Reputation() {
      }

      public int getValueFor(Predicate gossipTypeFilter) {
         return this.associatedGossip.object2IntEntrySet().stream().filter((entry) -> {
            return gossipTypeFilter.test((VillagerGossipType)entry.getKey());
         }).mapToInt((entry) -> {
            return entry.getIntValue() * ((VillagerGossipType)entry.getKey()).multiplier;
         }).sum();
      }

      public Stream entriesFor(UUID target) {
         return this.associatedGossip.object2IntEntrySet().stream().map((entry) -> {
            return new GossipEntry(target, (VillagerGossipType)entry.getKey(), entry.getIntValue());
         });
      }

      public void decay() {
         ObjectIterator objectIterator = this.associatedGossip.object2IntEntrySet().iterator();

         while(objectIterator.hasNext()) {
            Object2IntMap.Entry entry = (Object2IntMap.Entry)objectIterator.next();
            int i = entry.getIntValue() - ((VillagerGossipType)entry.getKey()).decay;
            if (i < 2) {
               objectIterator.remove();
            } else {
               entry.setValue(i);
            }
         }

      }

      public boolean isObsolete() {
         return this.associatedGossip.isEmpty();
      }

      public void clamp(VillagerGossipType gossipType) {
         int i = this.associatedGossip.getInt(gossipType);
         if (i > gossipType.maxValue) {
            this.associatedGossip.put(gossipType, gossipType.maxValue);
         }

         if (i < 2) {
            this.remove(gossipType);
         }

      }

      public void remove(VillagerGossipType gossipType) {
         this.associatedGossip.removeInt(gossipType);
      }
   }

   private static record GossipEntry(UUID target, VillagerGossipType type, int value) {
      final UUID target;
      final VillagerGossipType type;
      final int value;
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(Uuids.INT_STREAM_CODEC.fieldOf("Target").forGetter(GossipEntry::target), VillagerGossipType.CODEC.fieldOf("Type").forGetter(GossipEntry::type), Codecs.POSITIVE_INT.fieldOf("Value").forGetter(GossipEntry::value)).apply(instance, GossipEntry::new);
      });

      GossipEntry(UUID target, VillagerGossipType type, int value) {
         this.target = target;
         this.type = type;
         this.value = value;
      }

      public int getValue() {
         return this.value * this.type.multiplier;
      }

      public UUID target() {
         return this.target;
      }

      public VillagerGossipType type() {
         return this.type;
      }

      public int value() {
         return this.value;
      }
   }
}

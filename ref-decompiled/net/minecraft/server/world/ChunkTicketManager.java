package net.minecraft.server.world;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class ChunkTicketManager extends PersistentState {
   private static final int DEFAULT_TICKETS_MAP_SIZE = 4;
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Codec TICKET_POS_CODEC;
   public static final Codec CODEC;
   public static final PersistentStateType STATE_TYPE;
   private final Long2ObjectOpenHashMap tickets;
   private final Long2ObjectOpenHashMap savedTickets;
   private LongSet forcedChunks;
   @Nullable
   private LevelUpdater loadingLevelUpdater;
   @Nullable
   private LevelUpdater simulationLevelUpdater;

   private ChunkTicketManager(Long2ObjectOpenHashMap tickets, Long2ObjectOpenHashMap savedTickets) {
      this.forcedChunks = new LongOpenHashSet();
      this.tickets = tickets;
      this.savedTickets = savedTickets;
      this.recomputeForcedChunks();
   }

   public ChunkTicketManager() {
      this(new Long2ObjectOpenHashMap(4), new Long2ObjectOpenHashMap());
   }

   private static ChunkTicketManager create(List tickets) {
      Long2ObjectOpenHashMap long2ObjectOpenHashMap = new Long2ObjectOpenHashMap();
      Iterator var2 = tickets.iterator();

      while(var2.hasNext()) {
         Pair pair = (Pair)var2.next();
         ChunkPos chunkPos = (ChunkPos)pair.getFirst();
         List list = (List)long2ObjectOpenHashMap.computeIfAbsent(chunkPos.toLong(), (l) -> {
            return new ObjectArrayList(4);
         });
         list.add((ChunkTicket)pair.getSecond());
      }

      return new ChunkTicketManager(new Long2ObjectOpenHashMap(4), long2ObjectOpenHashMap);
   }

   private List getTickets() {
      List list = new ArrayList();
      this.forEachTicket((pos, ticket) -> {
         if (ticket.getType().persist()) {
            list.add(new Pair(pos, ticket));
         }

      });
      return list;
   }

   private void forEachTicket(BiConsumer ticketConsumer) {
      forEachTicket(ticketConsumer, this.tickets);
      forEachTicket(ticketConsumer, this.savedTickets);
   }

   private static void forEachTicket(BiConsumer ticketConsumer, Long2ObjectOpenHashMap tickets) {
      ObjectIterator var2 = Long2ObjectMaps.fastIterable(tickets).iterator();

      while(var2.hasNext()) {
         Long2ObjectMap.Entry entry = (Long2ObjectMap.Entry)var2.next();
         ChunkPos chunkPos = new ChunkPos(entry.getLongKey());
         Iterator var5 = ((List)entry.getValue()).iterator();

         while(var5.hasNext()) {
            ChunkTicket chunkTicket = (ChunkTicket)var5.next();
            ticketConsumer.accept(chunkPos, chunkTicket);
         }
      }

   }

   public void promoteToRealTickets() {
      ObjectIterator var1 = Long2ObjectMaps.fastIterable(this.savedTickets).iterator();

      while(var1.hasNext()) {
         Long2ObjectMap.Entry entry = (Long2ObjectMap.Entry)var1.next();
         Iterator var3 = ((List)entry.getValue()).iterator();

         while(var3.hasNext()) {
            ChunkTicket chunkTicket = (ChunkTicket)var3.next();
            this.addTicket(entry.getLongKey(), chunkTicket);
         }
      }

      this.savedTickets.clear();
   }

   public void setLoadingLevelUpdater(@Nullable LevelUpdater loadingLevelUpdater) {
      this.loadingLevelUpdater = loadingLevelUpdater;
   }

   public void setSimulationLevelUpdater(@Nullable LevelUpdater simulationLevelUpdater) {
      this.simulationLevelUpdater = simulationLevelUpdater;
   }

   public boolean hasTickets() {
      return !this.tickets.isEmpty();
   }

   public List getTickets(long pos) {
      return (List)this.tickets.getOrDefault(pos, List.of());
   }

   private List getTicketsMutable(long pos) {
      return (List)this.tickets.computeIfAbsent(pos, (chunkPos) -> {
         return new ObjectArrayList(4);
      });
   }

   public void addTicket(ChunkTicketType type, ChunkPos pos, int radius) {
      ChunkTicket chunkTicket = new ChunkTicket(type, ChunkLevels.getLevelFromType(ChunkLevelType.FULL) - radius);
      this.addTicket(pos.toLong(), chunkTicket);
   }

   public void addTicket(ChunkTicket ticket, ChunkPos pos) {
      this.addTicket(pos.toLong(), ticket);
   }

   public boolean addTicket(long pos, ChunkTicket ticket) {
      List list = this.getTicketsMutable(pos);
      Iterator var5 = list.iterator();

      ChunkTicket chunkTicket;
      do {
         if (!var5.hasNext()) {
            int i = getLevel(list, true);
            int j = getLevel(list, false);
            list.add(ticket);
            if (ticket.getType().isForSimulation() && ticket.getLevel() < i && this.simulationLevelUpdater != null) {
               this.simulationLevelUpdater.update(pos, ticket.getLevel(), true);
            }

            if (ticket.getType().isForLoading() && ticket.getLevel() < j && this.loadingLevelUpdater != null) {
               this.loadingLevelUpdater.update(pos, ticket.getLevel(), true);
            }

            if (ticket.getType().equals(ChunkTicketType.FORCED)) {
               this.forcedChunks.add(pos);
            }

            this.markDirty();
            return true;
         }

         chunkTicket = (ChunkTicket)var5.next();
      } while(!ticketsEqual(ticket, chunkTicket));

      chunkTicket.refreshExpiry();
      this.markDirty();
      return false;
   }

   private static boolean ticketsEqual(ChunkTicket a, ChunkTicket b) {
      return b.getType() == a.getType() && b.getLevel() == a.getLevel();
   }

   public int getLevel(long pos, boolean forSimulation) {
      return getLevel(this.getTickets(pos), forSimulation);
   }

   private static int getLevel(List tickets, boolean forSimulation) {
      ChunkTicket chunkTicket = getActiveTicket(tickets, forSimulation);
      return chunkTicket == null ? ChunkLevels.INACCESSIBLE + 1 : chunkTicket.getLevel();
   }

   @Nullable
   private static ChunkTicket getActiveTicket(@Nullable List tickets, boolean forSimulation) {
      if (tickets == null) {
         return null;
      } else {
         ChunkTicket chunkTicket = null;
         Iterator var3 = tickets.iterator();

         while(true) {
            while(true) {
               ChunkTicket chunkTicket2;
               do {
                  if (!var3.hasNext()) {
                     return chunkTicket;
                  }

                  chunkTicket2 = (ChunkTicket)var3.next();
               } while(chunkTicket != null && chunkTicket2.getLevel() >= chunkTicket.getLevel());

               if (forSimulation && chunkTicket2.getType().isForSimulation()) {
                  chunkTicket = chunkTicket2;
               } else if (!forSimulation && chunkTicket2.getType().isForLoading()) {
                  chunkTicket = chunkTicket2;
               }
            }
         }
      }
   }

   public void removeTicket(ChunkTicketType type, ChunkPos pos, int radius) {
      ChunkTicket chunkTicket = new ChunkTicket(type, ChunkLevels.getLevelFromType(ChunkLevelType.FULL) - radius);
      this.removeTicket(pos.toLong(), chunkTicket);
   }

   public void removeTicket(ChunkTicket ticket, ChunkPos pos) {
      this.removeTicket(pos.toLong(), ticket);
   }

   public boolean removeTicket(long pos, ChunkTicket ticket) {
      List list = (List)this.tickets.get(pos);
      if (list == null) {
         return false;
      } else {
         boolean bl = false;
         Iterator iterator = list.iterator();

         while(iterator.hasNext()) {
            ChunkTicket chunkTicket = (ChunkTicket)iterator.next();
            if (ticketsEqual(ticket, chunkTicket)) {
               iterator.remove();
               bl = true;
               break;
            }
         }

         if (!bl) {
            return false;
         } else {
            if (list.isEmpty()) {
               this.tickets.remove(pos);
            }

            if (ticket.getType().isForSimulation() && this.simulationLevelUpdater != null) {
               this.simulationLevelUpdater.update(pos, getLevel(list, true), false);
            }

            if (ticket.getType().isForLoading() && this.loadingLevelUpdater != null) {
               this.loadingLevelUpdater.update(pos, getLevel(list, false), false);
            }

            if (ticket.getType().equals(ChunkTicketType.FORCED)) {
               this.recomputeForcedChunks();
            }

            this.markDirty();
            return true;
         }
      }
   }

   private void recomputeForcedChunks() {
      this.forcedChunks = this.getAllChunksMatching((ticket) -> {
         return ticket.getType().equals(ChunkTicketType.FORCED);
      });
   }

   public String getDebugString(long pos, boolean forSimulation) {
      List list = this.getTickets(pos);
      ChunkTicket chunkTicket = getActiveTicket(list, forSimulation);
      return chunkTicket == null ? "no_ticket" : chunkTicket.toString();
   }

   public void tick(ServerChunkLoadingManager serverChunkLoadingManager) {
      this.removeTicketsIf((long_, chunkTicket) -> {
         ChunkHolder chunkHolder = serverChunkLoadingManager.getCurrentChunkHolder(long_);
         boolean bl = chunkHolder != null && !chunkHolder.isSavable() && chunkTicket.getType().isForSimulation();
         if (bl) {
            return false;
         } else {
            chunkTicket.tick();
            return chunkTicket.isExpired();
         }
      }, (Long2ObjectOpenHashMap)null);
      this.markDirty();
   }

   public void shutdown() {
      this.removeTicketsIf((long_, chunkTicket) -> {
         return chunkTicket.getType() != ChunkTicketType.UNKNOWN;
      }, this.savedTickets);
   }

   public void removeTicketsIf(BiPredicate biPredicate, @Nullable Long2ObjectOpenHashMap transferTo) {
      ObjectIterator objectIterator = this.tickets.long2ObjectEntrySet().fastIterator();
      boolean bl = false;

      while(true) {
         Long2ObjectMap.Entry entry;
         long l;
         boolean bl2;
         boolean bl3;
         do {
            if (!objectIterator.hasNext()) {
               if (bl) {
                  this.recomputeForcedChunks();
               }

               return;
            }

            entry = (Long2ObjectMap.Entry)objectIterator.next();
            Iterator iterator = ((List)entry.getValue()).iterator();
            l = entry.getLongKey();
            bl2 = false;
            bl3 = false;

            while(iterator.hasNext()) {
               ChunkTicket chunkTicket = (ChunkTicket)iterator.next();
               if (biPredicate.test(l, chunkTicket)) {
                  if (transferTo != null) {
                     List list = (List)transferTo.computeIfAbsent(l, (pos) -> {
                        return new ObjectArrayList(((List)entry.getValue()).size());
                     });
                     list.add(chunkTicket);
                  }

                  iterator.remove();
                  if (chunkTicket.getType().isForLoading()) {
                     bl3 = true;
                  }

                  if (chunkTicket.getType().isForSimulation()) {
                     bl2 = true;
                  }

                  if (chunkTicket.getType().equals(ChunkTicketType.FORCED)) {
                     bl = true;
                  }
               }
            }
         } while(!bl3 && !bl2);

         if (bl3 && this.loadingLevelUpdater != null) {
            this.loadingLevelUpdater.update(l, getLevel((List)entry.getValue(), false), false);
         }

         if (bl2 && this.simulationLevelUpdater != null) {
            this.simulationLevelUpdater.update(l, getLevel((List)entry.getValue(), true), false);
         }

         this.markDirty();
         if (((List)entry.getValue()).isEmpty()) {
            objectIterator.remove();
         }
      }
   }

   public void updateLevel(int level, ChunkTicketType type) {
      List list = new ArrayList();
      ObjectIterator var4 = this.tickets.long2ObjectEntrySet().iterator();

      ChunkTicket chunkTicket;
      while(var4.hasNext()) {
         Long2ObjectMap.Entry entry = (Long2ObjectMap.Entry)var4.next();
         Iterator var6 = ((List)entry.getValue()).iterator();

         while(var6.hasNext()) {
            chunkTicket = (ChunkTicket)var6.next();
            if (chunkTicket.getType() == type) {
               list.add(Pair.of(chunkTicket, entry.getLongKey()));
            }
         }
      }

      Iterator var9 = list.iterator();

      while(var9.hasNext()) {
         Pair pair = (Pair)var9.next();
         Long long_ = (Long)pair.getSecond();
         chunkTicket = (ChunkTicket)pair.getFirst();
         this.removeTicket(long_, chunkTicket);
         ChunkTicketType chunkTicketType = chunkTicket.getType();
         this.addTicket(long_, new ChunkTicket(chunkTicketType, level));
      }

   }

   public boolean setChunkForced(ChunkPos pos, boolean forced) {
      ChunkTicket chunkTicket = new ChunkTicket(ChunkTicketType.FORCED, ServerChunkLoadingManager.FORCED_CHUNK_LEVEL);
      return forced ? this.addTicket(pos.toLong(), chunkTicket) : this.removeTicket(pos.toLong(), chunkTicket);
   }

   public LongSet getForcedChunks() {
      return this.forcedChunks;
   }

   private LongSet getAllChunksMatching(Predicate predicate) {
      LongOpenHashSet longOpenHashSet = new LongOpenHashSet();
      ObjectIterator var3 = Long2ObjectMaps.fastIterable(this.tickets).iterator();

      while(true) {
         while(var3.hasNext()) {
            Long2ObjectMap.Entry entry = (Long2ObjectMap.Entry)var3.next();
            Iterator var5 = ((List)entry.getValue()).iterator();

            while(var5.hasNext()) {
               ChunkTicket chunkTicket = (ChunkTicket)var5.next();
               if (predicate.test(chunkTicket)) {
                  longOpenHashSet.add(entry.getLongKey());
                  break;
               }
            }
         }

         return longOpenHashSet;
      }
   }

   static {
      TICKET_POS_CODEC = Codec.mapPair(ChunkPos.CODEC.fieldOf("chunk_pos"), ChunkTicket.CODEC).codec();
      CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(TICKET_POS_CODEC.listOf().optionalFieldOf("tickets", List.of()).forGetter(ChunkTicketManager::getTickets)).apply(instance, ChunkTicketManager::create);
      });
      STATE_TYPE = new PersistentStateType("chunks", ChunkTicketManager::new, CODEC, DataFixTypes.SAVED_DATA_FORCED_CHUNKS);
   }

   @FunctionalInterface
   public interface LevelUpdater {
      void update(long pos, int level, boolean added);
   }
}

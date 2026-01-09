package net.minecraft.village.raid;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Iterator;
import java.util.List;
import java.util.OptionalInt;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.PointOfInterestTypeTags;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;
import net.minecraft.world.poi.PointOfInterest;
import net.minecraft.world.poi.PointOfInterestStorage;
import org.jetbrains.annotations.Nullable;

public class RaidManager extends PersistentState {
   private static final String RAIDS = "raids";
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(RaidManager.RaidWithId.CODEC.listOf().optionalFieldOf("raids", List.of()).forGetter((raidManager) -> {
         return raidManager.raids.int2ObjectEntrySet().stream().map(RaidWithId::fromMapEntry).toList();
      }), Codec.INT.fieldOf("next_id").forGetter((raidManager) -> {
         return raidManager.nextAvailableId;
      }), Codec.INT.fieldOf("tick").forGetter((raidManager) -> {
         return raidManager.currentTime;
      })).apply(instance, RaidManager::new);
   });
   public static final PersistentStateType STATE_TYPE;
   public static final PersistentStateType END_STATE_TYPE;
   private final Int2ObjectMap raids = new Int2ObjectOpenHashMap();
   private int nextAvailableId = 1;
   private int currentTime;

   public static PersistentStateType getPersistentStateType(RegistryEntry dimensionType) {
      return dimensionType.matchesKey(DimensionTypes.THE_END) ? END_STATE_TYPE : STATE_TYPE;
   }

   public RaidManager() {
      this.markDirty();
   }

   private RaidManager(List raids, int nextAvailableId, int currentTime) {
      Iterator var4 = raids.iterator();

      while(var4.hasNext()) {
         RaidWithId raidWithId = (RaidWithId)var4.next();
         this.raids.put(raidWithId.id, raidWithId.raid);
      }

      this.nextAvailableId = nextAvailableId;
      this.currentTime = currentTime;
   }

   @Nullable
   public Raid getRaid(int id) {
      return (Raid)this.raids.get(id);
   }

   public OptionalInt getRaidId(Raid raid) {
      ObjectIterator var2 = this.raids.int2ObjectEntrySet().iterator();

      Int2ObjectMap.Entry entry;
      do {
         if (!var2.hasNext()) {
            return OptionalInt.empty();
         }

         entry = (Int2ObjectMap.Entry)var2.next();
      } while(entry.getValue() != raid);

      return OptionalInt.of(entry.getIntKey());
   }

   public void tick(ServerWorld world) {
      ++this.currentTime;
      Iterator iterator = this.raids.values().iterator();

      while(iterator.hasNext()) {
         Raid raid = (Raid)iterator.next();
         if (world.getGameRules().getBoolean(GameRules.DISABLE_RAIDS)) {
            raid.invalidate();
         }

         if (raid.hasStopped()) {
            iterator.remove();
            this.markDirty();
         } else {
            raid.tick(world);
         }
      }

      if (this.currentTime % 200 == 0) {
         this.markDirty();
      }

      DebugInfoSender.sendRaids(world, this.raids.values());
   }

   public static boolean isValidRaiderFor(RaiderEntity raider) {
      return raider.isAlive() && raider.canJoinRaid() && raider.getDespawnCounter() <= 2400;
   }

   @Nullable
   public Raid startRaid(ServerPlayerEntity player, BlockPos pos) {
      if (player.isSpectator()) {
         return null;
      } else {
         ServerWorld serverWorld = player.getWorld();
         if (serverWorld.getGameRules().getBoolean(GameRules.DISABLE_RAIDS)) {
            return null;
         } else {
            DimensionType dimensionType = serverWorld.getDimension();
            if (!dimensionType.hasRaids()) {
               return null;
            } else {
               List list = serverWorld.getPointOfInterestStorage().getInCircle((poiType) -> {
                  return poiType.isIn(PointOfInterestTypeTags.VILLAGE);
               }, pos, 64, PointOfInterestStorage.OccupationStatus.IS_OCCUPIED).toList();
               int i = 0;
               Vec3d vec3d = Vec3d.ZERO;

               for(Iterator var8 = list.iterator(); var8.hasNext(); ++i) {
                  PointOfInterest pointOfInterest = (PointOfInterest)var8.next();
                  BlockPos blockPos = pointOfInterest.getPos();
                  vec3d = vec3d.add((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());
               }

               BlockPos blockPos2;
               if (i > 0) {
                  vec3d = vec3d.multiply(1.0 / (double)i);
                  blockPos2 = BlockPos.ofFloored(vec3d);
               } else {
                  blockPos2 = pos;
               }

               Raid raid = this.getOrCreateRaid(serverWorld, blockPos2);
               if (!raid.hasStarted() && !this.raids.containsValue(raid)) {
                  this.raids.put(this.nextId(), raid);
               }

               if (!raid.hasStarted() || raid.getBadOmenLevel() < raid.getMaxAcceptableBadOmenLevel()) {
                  raid.start(player);
               }

               this.markDirty();
               return raid;
            }
         }
      }
   }

   private Raid getOrCreateRaid(ServerWorld world, BlockPos pos) {
      Raid raid = world.getRaidAt(pos);
      return raid != null ? raid : new Raid(pos, world.getDifficulty());
   }

   public static RaidManager fromNbt(NbtCompound nbt) {
      return (RaidManager)CODEC.parse(NbtOps.INSTANCE, nbt).resultOrPartial().orElseGet(RaidManager::new);
   }

   private int nextId() {
      return ++this.nextAvailableId;
   }

   @Nullable
   public Raid getRaidAt(BlockPos pos, int searchDistance) {
      Raid raid = null;
      double d = (double)searchDistance;
      ObjectIterator var6 = this.raids.values().iterator();

      while(var6.hasNext()) {
         Raid raid2 = (Raid)var6.next();
         double e = raid2.getCenter().getSquaredDistance(pos);
         if (raid2.isActive() && e < d) {
            raid = raid2;
            d = e;
         }
      }

      return raid;
   }

   static {
      STATE_TYPE = new PersistentStateType("raids", RaidManager::new, CODEC, DataFixTypes.SAVED_DATA_RAIDS);
      END_STATE_TYPE = new PersistentStateType("raids_end", RaidManager::new, CODEC, DataFixTypes.SAVED_DATA_RAIDS);
   }

   static record RaidWithId(int id, Raid raid) {
      final int id;
      final Raid raid;
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(Codec.INT.fieldOf("id").forGetter(RaidWithId::id), Raid.CODEC.forGetter(RaidWithId::raid)).apply(instance, RaidWithId::new);
      });

      private RaidWithId(int i, Raid raid) {
         this.id = i;
         this.raid = raid;
      }

      public static RaidWithId fromMapEntry(Int2ObjectMap.Entry entry) {
         return new RaidWithId(entry.getIntKey(), (Raid)entry.getValue());
      }

      public int id() {
         return this.id;
      }

      public Raid raid() {
         return this.raid;
      }
   }
}

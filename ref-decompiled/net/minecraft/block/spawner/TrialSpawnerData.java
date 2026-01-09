package net.minecraft.block.spawner;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.block.enums.TrialSpawnerState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import net.minecraft.util.Uuids;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class TrialSpawnerData {
   private static final String SPAWN_DATA_KEY = "spawn_data";
   private static final String NEXT_MOB_SPAWNS_AT_KEY = "next_mob_spawns_at";
   private static final int field_50190 = 20;
   private static final int field_50191 = 18000;
   final Set players = new HashSet();
   final Set spawnedMobsAlive = new HashSet();
   long cooldownEnd;
   long nextMobSpawnsAt;
   int totalSpawnedMobs;
   Optional spawnData = Optional.empty();
   Optional rewardLootTable = Optional.empty();
   @Nullable
   private Entity displayEntity;
   @Nullable
   private Pool itemsToDropWhenOminous;
   double displayEntityRotation;
   double lastDisplayEntityRotation;

   public Packed pack() {
      return new Packed(Set.copyOf(this.players), Set.copyOf(this.spawnedMobsAlive), this.cooldownEnd, this.nextMobSpawnsAt, this.totalSpawnedMobs, this.spawnData, this.rewardLootTable);
   }

   public void unpack(Packed packed) {
      this.players.clear();
      this.players.addAll(packed.detectedPlayers);
      this.spawnedMobsAlive.clear();
      this.spawnedMobsAlive.addAll(packed.currentMobs);
      this.cooldownEnd = packed.cooldownEndsAt;
      this.nextMobSpawnsAt = packed.nextMobSpawnsAt;
      this.totalSpawnedMobs = packed.totalMobsSpawned;
      this.spawnData = packed.nextSpawnData;
      this.rewardLootTable = packed.ejectingLootTable;
   }

   public void reset() {
      this.spawnedMobsAlive.clear();
      this.spawnData = Optional.empty();
      this.deactivate();
   }

   public void deactivate() {
      this.players.clear();
      this.totalSpawnedMobs = 0;
      this.nextMobSpawnsAt = 0L;
      this.cooldownEnd = 0L;
   }

   public boolean hasSpawnData(TrialSpawnerLogic logic, Random random) {
      boolean bl = this.getSpawnData(logic, random).getNbt().getString("id").isPresent();
      return bl || !logic.getConfig().spawnPotentials().isEmpty();
   }

   public boolean hasSpawnedAllMobs(TrialSpawnerConfig config, int additionalPlayers) {
      return this.totalSpawnedMobs >= config.getTotalMobs(additionalPlayers);
   }

   public boolean areMobsDead() {
      return this.spawnedMobsAlive.isEmpty();
   }

   public boolean canSpawnMore(ServerWorld world, TrialSpawnerConfig config, int additionalPlayers) {
      return world.getTime() >= this.nextMobSpawnsAt && this.spawnedMobsAlive.size() < config.getSimultaneousMobs(additionalPlayers);
   }

   public int getAdditionalPlayers(BlockPos pos) {
      if (this.players.isEmpty()) {
         Util.logErrorOrPause("Trial Spawner at " + String.valueOf(pos) + " has no detected players");
      }

      return Math.max(0, this.players.size() - 1);
   }

   public void updatePlayers(ServerWorld world, BlockPos pos, TrialSpawnerLogic logic) {
      boolean bl = (pos.asLong() + world.getTime()) % 20L != 0L;
      if (!bl) {
         if (!logic.getSpawnerState().equals(TrialSpawnerState.COOLDOWN) || !logic.isOminous()) {
            List list = logic.getEntityDetector().detect(world, logic.getEntitySelector(), pos, (double)logic.getDetectionRadius(), true);
            boolean bl2;
            if (!logic.isOminous() && !list.isEmpty()) {
               Optional optional = findPlayerWithOmen(world, list);
               optional.ifPresent((pair) -> {
                  PlayerEntity playerEntity = (PlayerEntity)pair.getFirst();
                  if (pair.getSecond() == StatusEffects.BAD_OMEN) {
                     applyTrialOmen(playerEntity);
                  }

                  world.syncWorldEvent(3020, BlockPos.ofFloored(playerEntity.getEyePos()), 0);
                  logic.setOminous(world, pos);
               });
               bl2 = optional.isPresent();
            } else {
               bl2 = false;
            }

            if (!logic.getSpawnerState().equals(TrialSpawnerState.COOLDOWN) || bl2) {
               boolean bl3 = logic.getData().players.isEmpty();
               List list2 = bl3 ? list : logic.getEntityDetector().detect(world, logic.getEntitySelector(), pos, (double)logic.getDetectionRadius(), false);
               if (this.players.addAll(list2)) {
                  this.nextMobSpawnsAt = Math.max(world.getTime() + 40L, this.nextMobSpawnsAt);
                  if (!bl2) {
                     int i = logic.isOminous() ? 3019 : 3013;
                     world.syncWorldEvent(i, pos, this.players.size());
                  }
               }

            }
         }
      }
   }

   private static Optional findPlayerWithOmen(ServerWorld world, List players) {
      PlayerEntity playerEntity = null;
      Iterator var3 = players.iterator();

      while(var3.hasNext()) {
         UUID uUID = (UUID)var3.next();
         PlayerEntity playerEntity2 = world.getPlayerByUuid(uUID);
         if (playerEntity2 != null) {
            RegistryEntry registryEntry = StatusEffects.TRIAL_OMEN;
            if (playerEntity2.hasStatusEffect(registryEntry)) {
               return Optional.of(Pair.of(playerEntity2, registryEntry));
            }

            if (playerEntity2.hasStatusEffect(StatusEffects.BAD_OMEN)) {
               playerEntity = playerEntity2;
            }
         }
      }

      return Optional.ofNullable(playerEntity).map((player) -> {
         return Pair.of(player, StatusEffects.BAD_OMEN);
      });
   }

   public void resetAndClearMobs(TrialSpawnerLogic logic, ServerWorld world) {
      Stream var10000 = this.spawnedMobsAlive.stream();
      Objects.requireNonNull(world);
      var10000.map(world::getEntity).forEach((entity) -> {
         if (entity != null) {
            world.syncWorldEvent(3012, entity.getBlockPos(), TrialSpawnerLogic.Type.NORMAL.getIndex());
            if (entity instanceof MobEntity) {
               MobEntity mobEntity = (MobEntity)entity;
               mobEntity.dropAllForeignEquipment(world);
            }

            entity.remove(Entity.RemovalReason.DISCARDED);
         }
      });
      if (!logic.getOminousConfig().spawnPotentials().isEmpty()) {
         this.spawnData = Optional.empty();
      }

      this.totalSpawnedMobs = 0;
      this.spawnedMobsAlive.clear();
      this.nextMobSpawnsAt = world.getTime() + (long)logic.getOminousConfig().ticksBetweenSpawn();
      logic.updateListeners();
      this.cooldownEnd = world.getTime() + logic.getOminousConfig().getCooldownLength();
   }

   private static void applyTrialOmen(PlayerEntity player) {
      StatusEffectInstance statusEffectInstance = player.getStatusEffect(StatusEffects.BAD_OMEN);
      if (statusEffectInstance != null) {
         int i = statusEffectInstance.getAmplifier() + 1;
         int j = 18000 * i;
         player.removeStatusEffect(StatusEffects.BAD_OMEN);
         player.addStatusEffect(new StatusEffectInstance(StatusEffects.TRIAL_OMEN, j, 0));
      }
   }

   public boolean isCooldownPast(ServerWorld world, float f, int i) {
      long l = this.cooldownEnd - (long)i;
      return (float)world.getTime() >= (float)l + f;
   }

   public boolean isCooldownAtRepeating(ServerWorld world, float f, int i) {
      long l = this.cooldownEnd - (long)i;
      return (float)(world.getTime() - l) % f == 0.0F;
   }

   public boolean isCooldownOver(ServerWorld world) {
      return world.getTime() >= this.cooldownEnd;
   }

   protected MobSpawnerEntry getSpawnData(TrialSpawnerLogic logic, Random random) {
      if (this.spawnData.isPresent()) {
         return (MobSpawnerEntry)this.spawnData.get();
      } else {
         Pool pool = logic.getConfig().spawnPotentials();
         Optional optional = pool.isEmpty() ? this.spawnData : pool.getOrEmpty(random);
         this.spawnData = Optional.of((MobSpawnerEntry)optional.orElseGet(MobSpawnerEntry::new));
         logic.updateListeners();
         return (MobSpawnerEntry)this.spawnData.get();
      }
   }

   @Nullable
   public Entity setDisplayEntity(TrialSpawnerLogic logic, World world, TrialSpawnerState state) {
      if (!state.doesDisplayRotate()) {
         return null;
      } else {
         if (this.displayEntity == null) {
            NbtCompound nbtCompound = this.getSpawnData(logic, world.getRandom()).getNbt();
            if (nbtCompound.getString("id").isPresent()) {
               this.displayEntity = EntityType.loadEntityWithPassengers(nbtCompound, world, SpawnReason.TRIAL_SPAWNER, Function.identity());
            }
         }

         return this.displayEntity;
      }
   }

   public NbtCompound getSpawnDataNbt(TrialSpawnerState state) {
      NbtCompound nbtCompound = new NbtCompound();
      if (state == TrialSpawnerState.ACTIVE) {
         nbtCompound.putLong("next_mob_spawns_at", this.nextMobSpawnsAt);
      }

      this.spawnData.ifPresent((spawnData) -> {
         nbtCompound.put("spawn_data", MobSpawnerEntry.CODEC, spawnData);
      });
      return nbtCompound;
   }

   public double getDisplayEntityRotation() {
      return this.displayEntityRotation;
   }

   public double getLastDisplayEntityRotation() {
      return this.lastDisplayEntityRotation;
   }

   Pool getItemsToDropWhenOminous(ServerWorld world, TrialSpawnerConfig config, BlockPos pos) {
      if (this.itemsToDropWhenOminous != null) {
         return this.itemsToDropWhenOminous;
      } else {
         LootTable lootTable = world.getServer().getReloadableRegistries().getLootTable(config.itemsToDropWhenOminous());
         LootWorldContext lootWorldContext = (new LootWorldContext.Builder(world)).build(LootContextTypes.EMPTY);
         long l = getLootSeed(world, pos);
         ObjectArrayList objectArrayList = lootTable.generateLoot(lootWorldContext, l);
         if (objectArrayList.isEmpty()) {
            return Pool.empty();
         } else {
            Pool.Builder builder = Pool.builder();
            ObjectListIterator var10 = objectArrayList.iterator();

            while(var10.hasNext()) {
               ItemStack itemStack = (ItemStack)var10.next();
               builder.add(itemStack.copyWithCount(1), itemStack.getCount());
            }

            this.itemsToDropWhenOminous = builder.build();
            return this.itemsToDropWhenOminous;
         }
      }
   }

   private static long getLootSeed(ServerWorld world, BlockPos pos) {
      BlockPos blockPos = new BlockPos(MathHelper.floor((float)pos.getX() / 30.0F), MathHelper.floor((float)pos.getY() / 20.0F), MathHelper.floor((float)pos.getZ() / 30.0F));
      return world.getSeed() + blockPos.asLong();
   }

   public static record Packed(Set detectedPlayers, Set currentMobs, long cooldownEndsAt, long nextMobSpawnsAt, int totalMobsSpawned, Optional nextSpawnData, Optional ejectingLootTable) {
      final Set detectedPlayers;
      final Set currentMobs;
      final long cooldownEndsAt;
      final long nextMobSpawnsAt;
      final int totalMobsSpawned;
      final Optional nextSpawnData;
      final Optional ejectingLootTable;
      public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(Uuids.SET_CODEC.lenientOptionalFieldOf("registered_players", Set.of()).forGetter(Packed::detectedPlayers), Uuids.SET_CODEC.lenientOptionalFieldOf("current_mobs", Set.of()).forGetter(Packed::currentMobs), Codec.LONG.lenientOptionalFieldOf("cooldown_ends_at", 0L).forGetter(Packed::cooldownEndsAt), Codec.LONG.lenientOptionalFieldOf("next_mob_spawns_at", 0L).forGetter(Packed::nextMobSpawnsAt), Codec.intRange(0, Integer.MAX_VALUE).lenientOptionalFieldOf("total_mobs_spawned", 0).forGetter(Packed::totalMobsSpawned), MobSpawnerEntry.CODEC.lenientOptionalFieldOf("spawn_data").forGetter(Packed::nextSpawnData), LootTable.TABLE_KEY.lenientOptionalFieldOf("ejecting_loot_table").forGetter(Packed::ejectingLootTable)).apply(instance, Packed::new);
      });

      public Packed(Set set, Set set2, long l, long m, int i, Optional optional, Optional optional2) {
         this.detectedPlayers = set;
         this.currentMobs = set2;
         this.cooldownEndsAt = l;
         this.nextMobSpawnsAt = m;
         this.totalMobsSpawned = i;
         this.nextSpawnData = optional;
         this.ejectingLootTable = optional2;
      }

      public Set detectedPlayers() {
         return this.detectedPlayers;
      }

      public Set currentMobs() {
         return this.currentMobs;
      }

      public long cooldownEndsAt() {
         return this.cooldownEndsAt;
      }

      public long nextMobSpawnsAt() {
         return this.nextMobSpawnsAt;
      }

      public int totalMobsSpawned() {
         return this.totalMobsSpawned;
      }

      public Optional nextSpawnData() {
         return this.nextSpawnData;
      }

      public Optional ejectingLootTable() {
         return this.ejectingLootTable;
      }
   }
}

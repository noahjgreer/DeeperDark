package net.minecraft.predicate.entity;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMaps;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.advancement.criterion.CriterionProgress;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.predicate.NumberRange;
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerRecipeBook;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatHandler;
import net.minecraft.stat.StatType;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameModeList;
import org.jetbrains.annotations.Nullable;

public record PlayerPredicate(NumberRange.IntRange experienceLevel, GameModeList gameMode, List stats, Object2BooleanMap recipes, Map advancements, Optional lookingAt, Optional input) implements EntitySubPredicate {
   public static final int LOOKING_AT_DISTANCE = 100;
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(NumberRange.IntRange.CODEC.optionalFieldOf("level", NumberRange.IntRange.ANY).forGetter(PlayerPredicate::experienceLevel), GameModeList.CODEC.optionalFieldOf("gamemode", GameModeList.ALL).forGetter(PlayerPredicate::gameMode), PlayerPredicate.StatMatcher.CODEC.listOf().optionalFieldOf("stats", List.of()).forGetter(PlayerPredicate::stats), Codecs.object2BooleanMap(Recipe.KEY_CODEC).optionalFieldOf("recipes", Object2BooleanMaps.emptyMap()).forGetter(PlayerPredicate::recipes), Codec.unboundedMap(Identifier.CODEC, PlayerPredicate.AdvancementPredicate.CODEC).optionalFieldOf("advancements", Map.of()).forGetter(PlayerPredicate::advancements), EntityPredicate.CODEC.optionalFieldOf("looking_at").forGetter(PlayerPredicate::lookingAt), InputPredicate.CODEC.optionalFieldOf("input").forGetter(PlayerPredicate::input)).apply(instance, PlayerPredicate::new);
   });

   public PlayerPredicate(NumberRange.IntRange experienceLevel, GameModeList gameModeList, List list, Object2BooleanMap recipes, Map advancements, Optional optional, Optional optional2) {
      this.experienceLevel = experienceLevel;
      this.gameMode = gameModeList;
      this.stats = list;
      this.recipes = recipes;
      this.advancements = advancements;
      this.lookingAt = optional;
      this.input = optional2;
   }

   public boolean test(Entity entity, ServerWorld world, @Nullable Vec3d pos) {
      if (!(entity instanceof ServerPlayerEntity serverPlayerEntity)) {
         return false;
      } else if (!this.experienceLevel.test(serverPlayerEntity.experienceLevel)) {
         return false;
      } else if (!this.gameMode.contains(serverPlayerEntity.getGameMode())) {
         return false;
      } else {
         StatHandler statHandler = serverPlayerEntity.getStatHandler();
         Iterator var6 = this.stats.iterator();

         while(var6.hasNext()) {
            StatMatcher statMatcher = (StatMatcher)var6.next();
            if (!statMatcher.test(statHandler)) {
               return false;
            }
         }

         ServerRecipeBook serverRecipeBook = serverPlayerEntity.getRecipeBook();
         ObjectIterator var13 = this.recipes.object2BooleanEntrySet().iterator();

         while(var13.hasNext()) {
            Object2BooleanMap.Entry entry = (Object2BooleanMap.Entry)var13.next();
            if (serverRecipeBook.isUnlocked((RegistryKey)entry.getKey()) != entry.getBooleanValue()) {
               return false;
            }
         }

         if (!this.advancements.isEmpty()) {
            PlayerAdvancementTracker playerAdvancementTracker = serverPlayerEntity.getAdvancementTracker();
            ServerAdvancementLoader serverAdvancementLoader = serverPlayerEntity.getServer().getAdvancementLoader();
            Iterator var9 = this.advancements.entrySet().iterator();

            while(var9.hasNext()) {
               Map.Entry entry2 = (Map.Entry)var9.next();
               AdvancementEntry advancementEntry = serverAdvancementLoader.get((Identifier)entry2.getKey());
               if (advancementEntry == null || !((AdvancementPredicate)entry2.getValue()).test(playerAdvancementTracker.getProgress(advancementEntry))) {
                  return false;
               }
            }
         }

         if (this.lookingAt.isPresent()) {
            label96: {
               Vec3d vec3d = serverPlayerEntity.getEyePos();
               Vec3d vec3d2 = serverPlayerEntity.getRotationVec(1.0F);
               Vec3d vec3d3 = vec3d.add(vec3d2.x * 100.0, vec3d2.y * 100.0, vec3d2.z * 100.0);
               EntityHitResult entityHitResult = ProjectileUtil.getEntityCollision(serverPlayerEntity.getWorld(), serverPlayerEntity, vec3d, vec3d3, (new Box(vec3d, vec3d3)).expand(1.0), (hitEntity) -> {
                  return !hitEntity.isSpectator();
               }, 0.0F);
               if (entityHitResult != null && entityHitResult.getType() == HitResult.Type.ENTITY) {
                  Entity entity2 = entityHitResult.getEntity();
                  if (((EntityPredicate)this.lookingAt.get()).test(serverPlayerEntity, entity2) && serverPlayerEntity.canSee(entity2)) {
                     break label96;
                  }

                  return false;
               }

               return false;
            }
         }

         if (this.input.isPresent() && !((InputPredicate)this.input.get()).matches(serverPlayerEntity.getPlayerInput())) {
            return false;
         } else {
            return true;
         }
      }
   }

   public MapCodec getCodec() {
      return EntitySubPredicateTypes.PLAYER;
   }

   public NumberRange.IntRange experienceLevel() {
      return this.experienceLevel;
   }

   public GameModeList gameMode() {
      return this.gameMode;
   }

   public List stats() {
      return this.stats;
   }

   public Object2BooleanMap recipes() {
      return this.recipes;
   }

   public Map advancements() {
      return this.advancements;
   }

   public Optional lookingAt() {
      return this.lookingAt;
   }

   public Optional input() {
      return this.input;
   }

   static record StatMatcher(StatType type, RegistryEntry value, NumberRange.IntRange range, Supplier stat) {
      public static final Codec CODEC;

      public StatMatcher(StatType type, RegistryEntry value, NumberRange.IntRange range) {
         this(type, value, range, Suppliers.memoize(() -> {
            return type.getOrCreateStat(value.value());
         }));
      }

      private StatMatcher(StatType statType, RegistryEntry registryEntry, NumberRange.IntRange intRange, Supplier supplier) {
         this.type = statType;
         this.value = registryEntry;
         this.range = intRange;
         this.stat = supplier;
      }

      private static MapCodec createCodec(StatType type) {
         return RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(type.getRegistry().getEntryCodec().fieldOf("stat").forGetter(StatMatcher::value), NumberRange.IntRange.CODEC.optionalFieldOf("value", NumberRange.IntRange.ANY).forGetter(StatMatcher::range)).apply(instance, (value, range) -> {
               return new StatMatcher(type, value, range);
            });
         });
      }

      public boolean test(StatHandler statHandler) {
         return this.range.test(statHandler.getStat((Stat)this.stat.get()));
      }

      public StatType type() {
         return this.type;
      }

      public RegistryEntry value() {
         return this.value;
      }

      public NumberRange.IntRange range() {
         return this.range;
      }

      public Supplier stat() {
         return this.stat;
      }

      static {
         CODEC = Registries.STAT_TYPE.getCodec().dispatch(StatMatcher::type, StatMatcher::createCodec);
      }
   }

   private interface AdvancementPredicate extends Predicate {
      Codec CODEC = Codec.either(PlayerPredicate.CompletedAdvancementPredicate.CODEC, PlayerPredicate.AdvancementCriteriaPredicate.CODEC).xmap(Either::unwrap, (predicate) -> {
         if (predicate instanceof CompletedAdvancementPredicate completedAdvancementPredicate) {
            return Either.left(completedAdvancementPredicate);
         } else if (predicate instanceof AdvancementCriteriaPredicate advancementCriteriaPredicate) {
            return Either.right(advancementCriteriaPredicate);
         } else {
            throw new UnsupportedOperationException();
         }
      });
   }

   public static class Builder {
      private NumberRange.IntRange experienceLevel;
      private GameModeList gameMode;
      private final ImmutableList.Builder stats;
      private final Object2BooleanMap recipes;
      private final Map advancements;
      private Optional lookingAt;
      private Optional input;

      public Builder() {
         this.experienceLevel = NumberRange.IntRange.ANY;
         this.gameMode = GameModeList.ALL;
         this.stats = ImmutableList.builder();
         this.recipes = new Object2BooleanOpenHashMap();
         this.advancements = Maps.newHashMap();
         this.lookingAt = Optional.empty();
         this.input = Optional.empty();
      }

      public static Builder create() {
         return new Builder();
      }

      public Builder experienceLevel(NumberRange.IntRange experienceLevel) {
         this.experienceLevel = experienceLevel;
         return this;
      }

      public Builder stat(StatType statType, RegistryEntry.Reference value, NumberRange.IntRange range) {
         this.stats.add(new StatMatcher(statType, value, range));
         return this;
      }

      public Builder recipe(RegistryKey recipeKey, boolean unlocked) {
         this.recipes.put(recipeKey, unlocked);
         return this;
      }

      public Builder gameMode(GameModeList gameMode) {
         this.gameMode = gameMode;
         return this;
      }

      public Builder lookingAt(EntityPredicate.Builder lookingAt) {
         this.lookingAt = Optional.of(lookingAt.build());
         return this;
      }

      public Builder advancement(Identifier id, boolean done) {
         this.advancements.put(id, new CompletedAdvancementPredicate(done));
         return this;
      }

      public Builder advancement(Identifier id, Map criteria) {
         this.advancements.put(id, new AdvancementCriteriaPredicate(new Object2BooleanOpenHashMap(criteria)));
         return this;
      }

      public Builder input(InputPredicate input) {
         this.input = Optional.of(input);
         return this;
      }

      public PlayerPredicate build() {
         return new PlayerPredicate(this.experienceLevel, this.gameMode, this.stats.build(), this.recipes, this.advancements, this.lookingAt, this.input);
      }
   }

   private static record AdvancementCriteriaPredicate(Object2BooleanMap criteria) implements AdvancementPredicate {
      public static final Codec CODEC;

      AdvancementCriteriaPredicate(Object2BooleanMap criteria) {
         this.criteria = criteria;
      }

      public boolean test(AdvancementProgress advancementProgress) {
         ObjectIterator var2 = this.criteria.object2BooleanEntrySet().iterator();

         Object2BooleanMap.Entry entry;
         CriterionProgress criterionProgress;
         do {
            if (!var2.hasNext()) {
               return true;
            }

            entry = (Object2BooleanMap.Entry)var2.next();
            criterionProgress = advancementProgress.getCriterionProgress((String)entry.getKey());
         } while(criterionProgress != null && criterionProgress.isObtained() == entry.getBooleanValue());

         return false;
      }

      public Object2BooleanMap criteria() {
         return this.criteria;
      }

      // $FF: synthetic method
      public boolean test(final Object progress) {
         return this.test((AdvancementProgress)progress);
      }

      static {
         CODEC = Codecs.object2BooleanMap(Codec.STRING).xmap(AdvancementCriteriaPredicate::new, AdvancementCriteriaPredicate::criteria);
      }
   }

   private static record CompletedAdvancementPredicate(boolean done) implements AdvancementPredicate {
      public static final Codec CODEC;

      CompletedAdvancementPredicate(boolean done) {
         this.done = done;
      }

      public boolean test(AdvancementProgress advancementProgress) {
         return advancementProgress.isDone() == this.done;
      }

      public boolean done() {
         return this.done;
      }

      // $FF: synthetic method
      public boolean test(final Object progress) {
         return this.test((AdvancementProgress)progress);
      }

      static {
         CODEC = Codec.BOOL.xmap(CompletedAdvancementPredicate::new, CompletedAdvancementPredicate::done);
      }
   }
}

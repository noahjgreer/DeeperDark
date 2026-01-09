package net.minecraft.loot.context;

import com.google.common.collect.Sets;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.loot.LootDataType;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

public class LootContext {
   private final LootWorldContext worldContext;
   private final Random random;
   private final RegistryEntryLookup.RegistryLookup lookup;
   private final Set activeEntries = Sets.newLinkedHashSet();

   LootContext(LootWorldContext worldContext, Random random, RegistryEntryLookup.RegistryLookup lookup) {
      this.worldContext = worldContext;
      this.random = random;
      this.lookup = lookup;
   }

   public boolean hasParameter(ContextParameter parameter) {
      return this.worldContext.getParameters().contains(parameter);
   }

   public Object getOrThrow(ContextParameter parameter) {
      return this.worldContext.getParameters().getOrThrow(parameter);
   }

   @Nullable
   public Object get(ContextParameter parameter) {
      return this.worldContext.getParameters().getNullable(parameter);
   }

   public void drop(Identifier id, Consumer lootConsumer) {
      this.worldContext.addDynamicDrops(id, lootConsumer);
   }

   public boolean isActive(Entry entry) {
      return this.activeEntries.contains(entry);
   }

   public boolean markActive(Entry entry) {
      return this.activeEntries.add(entry);
   }

   public void markInactive(Entry entry) {
      this.activeEntries.remove(entry);
   }

   public RegistryEntryLookup.RegistryLookup getLookup() {
      return this.lookup;
   }

   public Random getRandom() {
      return this.random;
   }

   public float getLuck() {
      return this.worldContext.getLuck();
   }

   public ServerWorld getWorld() {
      return this.worldContext.getWorld();
   }

   public static Entry table(LootTable table) {
      return new Entry(LootDataType.LOOT_TABLES, table);
   }

   public static Entry predicate(LootCondition predicate) {
      return new Entry(LootDataType.PREDICATES, predicate);
   }

   public static Entry itemModifier(LootFunction itemModifier) {
      return new Entry(LootDataType.ITEM_MODIFIERS, itemModifier);
   }

   public static record Entry(LootDataType type, Object value) {
      public Entry(LootDataType lootDataType, Object object) {
         this.type = lootDataType;
         this.value = object;
      }

      public LootDataType type() {
         return this.type;
      }

      public Object value() {
         return this.value;
      }
   }

   public static enum EntityTarget implements StringIdentifiable {
      THIS("this", LootContextParameters.THIS_ENTITY),
      ATTACKER("attacker", LootContextParameters.ATTACKING_ENTITY),
      DIRECT_ATTACKER("direct_attacker", LootContextParameters.DIRECT_ATTACKING_ENTITY),
      ATTACKING_PLAYER("attacking_player", LootContextParameters.LAST_DAMAGE_PLAYER);

      public static final StringIdentifiable.EnumCodec CODEC = StringIdentifiable.createCodec(EntityTarget::values);
      private final String type;
      private final ContextParameter parameter;

      private EntityTarget(final String type, final ContextParameter parameter) {
         this.type = type;
         this.parameter = parameter;
      }

      public ContextParameter getParameter() {
         return this.parameter;
      }

      public static EntityTarget fromString(String type) {
         EntityTarget entityTarget = (EntityTarget)CODEC.byId(type);
         if (entityTarget != null) {
            return entityTarget;
         } else {
            throw new IllegalArgumentException("Invalid entity target " + type);
         }
      }

      public String asString() {
         return this.type;
      }

      // $FF: synthetic method
      private static EntityTarget[] method_36793() {
         return new EntityTarget[]{THIS, ATTACKER, DIRECT_ATTACKER, ATTACKING_PLAYER};
      }
   }

   public static class Builder {
      private final LootWorldContext worldContext;
      @Nullable
      private Random random;

      public Builder(LootWorldContext worldContext) {
         this.worldContext = worldContext;
      }

      public Builder random(long seed) {
         if (seed != 0L) {
            this.random = Random.create(seed);
         }

         return this;
      }

      public Builder random(Random random) {
         this.random = random;
         return this;
      }

      public ServerWorld getWorld() {
         return this.worldContext.getWorld();
      }

      public LootContext build(Optional randomId) {
         ServerWorld serverWorld = this.getWorld();
         MinecraftServer minecraftServer = serverWorld.getServer();
         Optional var10000 = Optional.ofNullable(this.random).or(() -> {
            Objects.requireNonNull(serverWorld);
            return randomId.map(serverWorld::getOrCreateRandom);
         });
         Objects.requireNonNull(serverWorld);
         Random random = (Random)var10000.orElseGet(serverWorld::getRandom);
         return new LootContext(this.worldContext, random, minecraftServer.getReloadableRegistries().createRegistryLookup());
      }
   }
}

package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.loot.LootTable;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerGeneratesContainerLootCriterion extends AbstractCriterion {
   public Codec getConditionsCodec() {
      return PlayerGeneratesContainerLootCriterion.Conditions.CODEC;
   }

   public void trigger(ServerPlayerEntity player, RegistryKey lootTable) {
      this.trigger(player, (conditions) -> {
         return conditions.test(lootTable);
      });
   }

   public static record Conditions(Optional player, RegistryKey lootTable) implements AbstractCriterion.Conditions {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(Conditions::player), LootTable.TABLE_KEY.fieldOf("loot_table").forGetter(Conditions::lootTable)).apply(instance, Conditions::new);
      });

      public Conditions(Optional playerPredicate, RegistryKey registryKey) {
         this.player = playerPredicate;
         this.lootTable = registryKey;
      }

      public static AdvancementCriterion create(RegistryKey registryKey) {
         return Criteria.PLAYER_GENERATES_CONTAINER_LOOT.create(new Conditions(Optional.empty(), registryKey));
      }

      public boolean test(RegistryKey lootTable) {
         return this.lootTable == lootTable;
      }

      public Optional player() {
         return this.player;
      }

      public RegistryKey lootTable() {
         return this.lootTable;
      }
   }
}

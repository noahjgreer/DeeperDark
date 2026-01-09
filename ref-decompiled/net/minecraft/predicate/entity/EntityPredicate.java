package net.minecraft.predicate.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.loot.condition.EntityPropertiesLootCondition;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.predicate.component.ComponentsPredicate;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public record EntityPredicate(Optional type, Optional distance, Optional movement, PositionalPredicates location, Optional effects, Optional nbt, Optional flags, Optional equipment, Optional typeSpecific, Optional periodicTick, Optional vehicle, Optional passenger, Optional targetedEntity, Optional team, Optional slots, ComponentsPredicate components) {
   public static final Codec CODEC = Codec.recursive("EntityPredicate", (entityPredicateCodec) -> {
      return RecordCodecBuilder.create((instance) -> {
         return instance.group(EntityTypePredicate.CODEC.optionalFieldOf("type").forGetter(EntityPredicate::type), DistancePredicate.CODEC.optionalFieldOf("distance").forGetter(EntityPredicate::distance), MovementPredicate.CODEC.optionalFieldOf("movement").forGetter(EntityPredicate::movement), EntityPredicate.PositionalPredicates.CODEC.forGetter(EntityPredicate::location), EntityEffectPredicate.CODEC.optionalFieldOf("effects").forGetter(EntityPredicate::effects), NbtPredicate.CODEC.optionalFieldOf("nbt").forGetter(EntityPredicate::nbt), EntityFlagsPredicate.CODEC.optionalFieldOf("flags").forGetter(EntityPredicate::flags), EntityEquipmentPredicate.CODEC.optionalFieldOf("equipment").forGetter(EntityPredicate::equipment), EntitySubPredicate.CODEC.optionalFieldOf("type_specific").forGetter(EntityPredicate::typeSpecific), Codecs.POSITIVE_INT.optionalFieldOf("periodic_tick").forGetter(EntityPredicate::periodicTick), entityPredicateCodec.optionalFieldOf("vehicle").forGetter(EntityPredicate::vehicle), entityPredicateCodec.optionalFieldOf("passenger").forGetter(EntityPredicate::passenger), entityPredicateCodec.optionalFieldOf("targeted_entity").forGetter(EntityPredicate::targetedEntity), Codec.STRING.optionalFieldOf("team").forGetter(EntityPredicate::team), SlotsPredicate.CODEC.optionalFieldOf("slots").forGetter(EntityPredicate::slots), ComponentsPredicate.CODEC.forGetter(EntityPredicate::components)).apply(instance, EntityPredicate::new);
      });
   });
   public static final Codec LOOT_CONTEXT_PREDICATE_CODEC;

   public EntityPredicate(Optional optional, Optional optional2, Optional optional3, PositionalPredicates positionalPredicates, Optional optional4, Optional optional5, Optional optional6, Optional optional7, Optional optional8, Optional optional9, Optional optional10, Optional optional11, Optional optional12, Optional optional13, Optional optional14, ComponentsPredicate componentsPredicate) {
      this.type = optional;
      this.distance = optional2;
      this.movement = optional3;
      this.location = positionalPredicates;
      this.effects = optional4;
      this.nbt = optional5;
      this.flags = optional6;
      this.equipment = optional7;
      this.typeSpecific = optional8;
      this.periodicTick = optional9;
      this.vehicle = optional10;
      this.passenger = optional11;
      this.targetedEntity = optional12;
      this.team = optional13;
      this.slots = optional14;
      this.components = componentsPredicate;
   }

   public static LootContextPredicate contextPredicateFromEntityPredicate(Builder builder) {
      return asLootContextPredicate(builder.build());
   }

   public static Optional contextPredicateFromEntityPredicate(Optional entityPredicate) {
      return entityPredicate.map(EntityPredicate::asLootContextPredicate);
   }

   public static List contextPredicateFromEntityPredicates(Builder... builders) {
      return Stream.of(builders).map(EntityPredicate::contextPredicateFromEntityPredicate).toList();
   }

   public static LootContextPredicate asLootContextPredicate(EntityPredicate predicate) {
      LootCondition lootCondition = EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, predicate).build();
      return new LootContextPredicate(List.of(lootCondition));
   }

   public boolean test(ServerPlayerEntity player, @Nullable Entity entity) {
      return this.test(player.getWorld(), player.getPos(), entity);
   }

   public boolean test(ServerWorld world, @Nullable Vec3d pos, @Nullable Entity entity) {
      if (entity == null) {
         return false;
      } else if (this.type.isPresent() && !((EntityTypePredicate)this.type.get()).matches(entity.getType())) {
         return false;
      } else {
         if (pos == null) {
            if (this.distance.isPresent()) {
               return false;
            }
         } else if (this.distance.isPresent() && !((DistancePredicate)this.distance.get()).test(pos.x, pos.y, pos.z, entity.getX(), entity.getY(), entity.getZ())) {
            return false;
         }

         Vec3d vec3d;
         if (this.movement.isPresent()) {
            vec3d = entity.getMovement();
            Vec3d vec3d2 = vec3d.multiply(20.0);
            if (!((MovementPredicate)this.movement.get()).test(vec3d2.x, vec3d2.y, vec3d2.z, entity.fallDistance)) {
               return false;
            }
         }

         if (this.location.located.isPresent() && !((LocationPredicate)this.location.located.get()).test(world, entity.getX(), entity.getY(), entity.getZ())) {
            return false;
         } else {
            if (this.location.steppingOn.isPresent()) {
               vec3d = Vec3d.ofCenter(entity.getSteppingPos());
               if (!entity.isOnGround() || !((LocationPredicate)this.location.steppingOn.get()).test(world, vec3d.getX(), vec3d.getY(), vec3d.getZ())) {
                  return false;
               }
            }

            if (this.location.affectsMovement.isPresent()) {
               vec3d = Vec3d.ofCenter(entity.getVelocityAffectingPos());
               if (!((LocationPredicate)this.location.affectsMovement.get()).test(world, vec3d.getX(), vec3d.getY(), vec3d.getZ())) {
                  return false;
               }
            }

            if (this.effects.isPresent() && !((EntityEffectPredicate)this.effects.get()).test(entity)) {
               return false;
            } else if (this.flags.isPresent() && !((EntityFlagsPredicate)this.flags.get()).test(entity)) {
               return false;
            } else if (this.equipment.isPresent() && !((EntityEquipmentPredicate)this.equipment.get()).test(entity)) {
               return false;
            } else if (this.typeSpecific.isPresent() && !((EntitySubPredicate)this.typeSpecific.get()).test(entity, world, pos)) {
               return false;
            } else if (this.vehicle.isPresent() && !((EntityPredicate)this.vehicle.get()).test(world, pos, entity.getVehicle())) {
               return false;
            } else if (this.passenger.isPresent() && entity.getPassengerList().stream().noneMatch((entityx) -> {
               return ((EntityPredicate)this.passenger.get()).test(world, pos, entityx);
            })) {
               return false;
            } else if (this.targetedEntity.isPresent() && !((EntityPredicate)this.targetedEntity.get()).test(world, pos, entity instanceof MobEntity ? ((MobEntity)entity).getTarget() : null)) {
               return false;
            } else if (this.periodicTick.isPresent() && entity.age % (Integer)this.periodicTick.get() != 0) {
               return false;
            } else {
               if (this.team.isPresent()) {
                  AbstractTeam abstractTeam = entity.getScoreboardTeam();
                  if (abstractTeam == null || !((String)this.team.get()).equals(abstractTeam.getName())) {
                     return false;
                  }
               }

               if (this.slots.isPresent() && !((SlotsPredicate)this.slots.get()).matches(entity)) {
                  return false;
               } else if (!this.components.test((ComponentsAccess)entity)) {
                  return false;
               } else {
                  return this.nbt.isEmpty() || ((NbtPredicate)this.nbt.get()).test(entity);
               }
            }
         }
      }
   }

   public static LootContext createAdvancementEntityLootContext(ServerPlayerEntity player, Entity target) {
      LootWorldContext lootWorldContext = (new LootWorldContext.Builder(player.getWorld())).add(LootContextParameters.THIS_ENTITY, target).add(LootContextParameters.ORIGIN, player.getPos()).build(LootContextTypes.ADVANCEMENT_ENTITY);
      return (new LootContext.Builder(lootWorldContext)).build(Optional.empty());
   }

   public Optional type() {
      return this.type;
   }

   public Optional distance() {
      return this.distance;
   }

   public Optional movement() {
      return this.movement;
   }

   public PositionalPredicates location() {
      return this.location;
   }

   public Optional effects() {
      return this.effects;
   }

   public Optional nbt() {
      return this.nbt;
   }

   public Optional flags() {
      return this.flags;
   }

   public Optional equipment() {
      return this.equipment;
   }

   public Optional typeSpecific() {
      return this.typeSpecific;
   }

   public Optional periodicTick() {
      return this.periodicTick;
   }

   public Optional vehicle() {
      return this.vehicle;
   }

   public Optional passenger() {
      return this.passenger;
   }

   public Optional targetedEntity() {
      return this.targetedEntity;
   }

   public Optional team() {
      return this.team;
   }

   public Optional slots() {
      return this.slots;
   }

   public ComponentsPredicate components() {
      return this.components;
   }

   static {
      LOOT_CONTEXT_PREDICATE_CODEC = Codec.withAlternative(LootContextPredicate.CODEC, CODEC, EntityPredicate::asLootContextPredicate);
   }

   public static record PositionalPredicates(Optional located, Optional steppingOn, Optional affectsMovement) {
      final Optional located;
      final Optional steppingOn;
      final Optional affectsMovement;
      public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(LocationPredicate.CODEC.optionalFieldOf("location").forGetter(PositionalPredicates::located), LocationPredicate.CODEC.optionalFieldOf("stepping_on").forGetter(PositionalPredicates::steppingOn), LocationPredicate.CODEC.optionalFieldOf("movement_affected_by").forGetter(PositionalPredicates::affectsMovement)).apply(instance, PositionalPredicates::new);
      });

      public PositionalPredicates(Optional optional, Optional optional2, Optional optional3) {
         this.located = optional;
         this.steppingOn = optional2;
         this.affectsMovement = optional3;
      }

      public Optional located() {
         return this.located;
      }

      public Optional steppingOn() {
         return this.steppingOn;
      }

      public Optional affectsMovement() {
         return this.affectsMovement;
      }
   }

   public static class Builder {
      private Optional type = Optional.empty();
      private Optional distance = Optional.empty();
      private Optional movement = Optional.empty();
      private Optional location = Optional.empty();
      private Optional steppingOn = Optional.empty();
      private Optional movementAffectedBy = Optional.empty();
      private Optional effects = Optional.empty();
      private Optional nbt = Optional.empty();
      private Optional flags = Optional.empty();
      private Optional equipment = Optional.empty();
      private Optional typeSpecific = Optional.empty();
      private Optional periodicTick = Optional.empty();
      private Optional vehicle = Optional.empty();
      private Optional passenger = Optional.empty();
      private Optional targetedEntity = Optional.empty();
      private Optional team = Optional.empty();
      private Optional slots = Optional.empty();
      private ComponentsPredicate components;

      public Builder() {
         this.components = ComponentsPredicate.EMPTY;
      }

      public static Builder create() {
         return new Builder();
      }

      public Builder type(RegistryEntryLookup entityTypeRegistry, EntityType type) {
         this.type = Optional.of(EntityTypePredicate.create(entityTypeRegistry, type));
         return this;
      }

      public Builder type(RegistryEntryLookup entityTypeRegistry, TagKey tag) {
         this.type = Optional.of(EntityTypePredicate.create(entityTypeRegistry, tag));
         return this;
      }

      public Builder type(EntityTypePredicate type) {
         this.type = Optional.of(type);
         return this;
      }

      public Builder distance(DistancePredicate distance) {
         this.distance = Optional.of(distance);
         return this;
      }

      public Builder movement(MovementPredicate movement) {
         this.movement = Optional.of(movement);
         return this;
      }

      public Builder location(LocationPredicate.Builder location) {
         this.location = Optional.of(location.build());
         return this;
      }

      public Builder steppingOn(LocationPredicate.Builder steppingOn) {
         this.steppingOn = Optional.of(steppingOn.build());
         return this;
      }

      public Builder movementAffectedBy(LocationPredicate.Builder movementAffectedBy) {
         this.movementAffectedBy = Optional.of(movementAffectedBy.build());
         return this;
      }

      public Builder effects(EntityEffectPredicate.Builder effects) {
         this.effects = effects.build();
         return this;
      }

      public Builder nbt(NbtPredicate nbt) {
         this.nbt = Optional.of(nbt);
         return this;
      }

      public Builder flags(EntityFlagsPredicate.Builder flags) {
         this.flags = Optional.of(flags.build());
         return this;
      }

      public Builder equipment(EntityEquipmentPredicate.Builder equipment) {
         this.equipment = Optional.of(equipment.build());
         return this;
      }

      public Builder equipment(EntityEquipmentPredicate equipment) {
         this.equipment = Optional.of(equipment);
         return this;
      }

      public Builder typeSpecific(EntitySubPredicate typeSpecific) {
         this.typeSpecific = Optional.of(typeSpecific);
         return this;
      }

      public Builder periodicTick(int periodicTick) {
         this.periodicTick = Optional.of(periodicTick);
         return this;
      }

      public Builder vehicle(Builder vehicle) {
         this.vehicle = Optional.of(vehicle.build());
         return this;
      }

      public Builder passenger(Builder passenger) {
         this.passenger = Optional.of(passenger.build());
         return this;
      }

      public Builder targetedEntity(Builder targetedEntity) {
         this.targetedEntity = Optional.of(targetedEntity.build());
         return this;
      }

      public Builder team(String team) {
         this.team = Optional.of(team);
         return this;
      }

      public Builder slots(SlotsPredicate slots) {
         this.slots = Optional.of(slots);
         return this;
      }

      public Builder components(ComponentsPredicate components) {
         this.components = components;
         return this;
      }

      public EntityPredicate build() {
         return new EntityPredicate(this.type, this.distance, this.movement, new PositionalPredicates(this.location, this.steppingOn, this.movementAffectedBy), this.effects, this.nbt, this.flags, this.equipment, this.typeSpecific, this.periodicTick, this.vehicle, this.passenger, this.targetedEntity, this.team, this.slots, this.components);
      }
   }
}

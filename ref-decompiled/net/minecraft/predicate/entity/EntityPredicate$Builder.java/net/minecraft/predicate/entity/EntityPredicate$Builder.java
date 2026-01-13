/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.predicate.entity;

import java.util.Optional;
import net.minecraft.entity.EntityType;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.predicate.component.ComponentsPredicate;
import net.minecraft.predicate.entity.DistancePredicate;
import net.minecraft.predicate.entity.EntityEffectPredicate;
import net.minecraft.predicate.entity.EntityEquipmentPredicate;
import net.minecraft.predicate.entity.EntityFlagsPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.EntitySubPredicate;
import net.minecraft.predicate.entity.EntityTypePredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.predicate.entity.MovementPredicate;
import net.minecraft.predicate.entity.SlotsPredicate;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.tag.TagKey;

public static class EntityPredicate.Builder {
    private Optional<EntityTypePredicate> type = Optional.empty();
    private Optional<DistancePredicate> distance = Optional.empty();
    private Optional<MovementPredicate> movement = Optional.empty();
    private Optional<LocationPredicate> location = Optional.empty();
    private Optional<LocationPredicate> steppingOn = Optional.empty();
    private Optional<LocationPredicate> movementAffectedBy = Optional.empty();
    private Optional<EntityEffectPredicate> effects = Optional.empty();
    private Optional<NbtPredicate> nbt = Optional.empty();
    private Optional<EntityFlagsPredicate> flags = Optional.empty();
    private Optional<EntityEquipmentPredicate> equipment = Optional.empty();
    private Optional<EntitySubPredicate> typeSpecific = Optional.empty();
    private Optional<Integer> periodicTick = Optional.empty();
    private Optional<EntityPredicate> vehicle = Optional.empty();
    private Optional<EntityPredicate> passenger = Optional.empty();
    private Optional<EntityPredicate> targetedEntity = Optional.empty();
    private Optional<String> team = Optional.empty();
    private Optional<SlotsPredicate> slots = Optional.empty();
    private ComponentsPredicate components = ComponentsPredicate.EMPTY;

    public static EntityPredicate.Builder create() {
        return new EntityPredicate.Builder();
    }

    public EntityPredicate.Builder type(RegistryEntryLookup<EntityType<?>> entityTypeRegistry, EntityType<?> type) {
        this.type = Optional.of(EntityTypePredicate.create(entityTypeRegistry, type));
        return this;
    }

    public EntityPredicate.Builder type(RegistryEntryLookup<EntityType<?>> entityTypeRegistry, TagKey<EntityType<?>> tag) {
        this.type = Optional.of(EntityTypePredicate.create(entityTypeRegistry, tag));
        return this;
    }

    public EntityPredicate.Builder type(EntityTypePredicate type) {
        this.type = Optional.of(type);
        return this;
    }

    public EntityPredicate.Builder distance(DistancePredicate distance) {
        this.distance = Optional.of(distance);
        return this;
    }

    public EntityPredicate.Builder movement(MovementPredicate movement) {
        this.movement = Optional.of(movement);
        return this;
    }

    public EntityPredicate.Builder location(LocationPredicate.Builder location) {
        this.location = Optional.of(location.build());
        return this;
    }

    public EntityPredicate.Builder steppingOn(LocationPredicate.Builder steppingOn) {
        this.steppingOn = Optional.of(steppingOn.build());
        return this;
    }

    public EntityPredicate.Builder movementAffectedBy(LocationPredicate.Builder movementAffectedBy) {
        this.movementAffectedBy = Optional.of(movementAffectedBy.build());
        return this;
    }

    public EntityPredicate.Builder effects(EntityEffectPredicate.Builder effects) {
        this.effects = effects.build();
        return this;
    }

    public EntityPredicate.Builder nbt(NbtPredicate nbt) {
        this.nbt = Optional.of(nbt);
        return this;
    }

    public EntityPredicate.Builder flags(EntityFlagsPredicate.Builder flags) {
        this.flags = Optional.of(flags.build());
        return this;
    }

    public EntityPredicate.Builder equipment(EntityEquipmentPredicate.Builder equipment) {
        this.equipment = Optional.of(equipment.build());
        return this;
    }

    public EntityPredicate.Builder equipment(EntityEquipmentPredicate equipment) {
        this.equipment = Optional.of(equipment);
        return this;
    }

    public EntityPredicate.Builder typeSpecific(EntitySubPredicate typeSpecific) {
        this.typeSpecific = Optional.of(typeSpecific);
        return this;
    }

    public EntityPredicate.Builder periodicTick(int periodicTick) {
        this.periodicTick = Optional.of(periodicTick);
        return this;
    }

    public EntityPredicate.Builder vehicle(EntityPredicate.Builder vehicle) {
        this.vehicle = Optional.of(vehicle.build());
        return this;
    }

    public EntityPredicate.Builder passenger(EntityPredicate.Builder passenger) {
        this.passenger = Optional.of(passenger.build());
        return this;
    }

    public EntityPredicate.Builder targetedEntity(EntityPredicate.Builder targetedEntity) {
        this.targetedEntity = Optional.of(targetedEntity.build());
        return this;
    }

    public EntityPredicate.Builder team(String team) {
        this.team = Optional.of(team);
        return this;
    }

    public EntityPredicate.Builder slots(SlotsPredicate slots) {
        this.slots = Optional.of(slots);
        return this;
    }

    public EntityPredicate.Builder components(ComponentsPredicate components) {
        this.components = components;
        return this;
    }

    public EntityPredicate build() {
        return new EntityPredicate(this.type, this.distance, this.movement, new EntityPredicate.PositionalPredicates(this.location, this.steppingOn, this.movementAffectedBy), this.effects, this.nbt, this.flags, this.equipment, this.typeSpecific, this.periodicTick, this.vehicle, this.passenger, this.targetedEntity, this.team, this.slots, this.components);
    }
}

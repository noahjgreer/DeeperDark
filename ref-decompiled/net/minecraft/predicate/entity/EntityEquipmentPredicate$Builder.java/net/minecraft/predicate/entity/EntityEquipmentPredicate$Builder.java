/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.predicate.entity;

import java.util.Optional;
import net.minecraft.predicate.entity.EntityEquipmentPredicate;
import net.minecraft.predicate.item.ItemPredicate;

public static class EntityEquipmentPredicate.Builder {
    private Optional<ItemPredicate> head = Optional.empty();
    private Optional<ItemPredicate> chest = Optional.empty();
    private Optional<ItemPredicate> legs = Optional.empty();
    private Optional<ItemPredicate> feet = Optional.empty();
    private Optional<ItemPredicate> body = Optional.empty();
    private Optional<ItemPredicate> mainhand = Optional.empty();
    private Optional<ItemPredicate> offhand = Optional.empty();

    public static EntityEquipmentPredicate.Builder create() {
        return new EntityEquipmentPredicate.Builder();
    }

    public EntityEquipmentPredicate.Builder head(ItemPredicate.Builder item) {
        this.head = Optional.of(item.build());
        return this;
    }

    public EntityEquipmentPredicate.Builder chest(ItemPredicate.Builder item) {
        this.chest = Optional.of(item.build());
        return this;
    }

    public EntityEquipmentPredicate.Builder legs(ItemPredicate.Builder item) {
        this.legs = Optional.of(item.build());
        return this;
    }

    public EntityEquipmentPredicate.Builder feet(ItemPredicate.Builder item) {
        this.feet = Optional.of(item.build());
        return this;
    }

    public EntityEquipmentPredicate.Builder body(ItemPredicate.Builder item) {
        this.body = Optional.of(item.build());
        return this;
    }

    public EntityEquipmentPredicate.Builder mainhand(ItemPredicate.Builder item) {
        this.mainhand = Optional.of(item.build());
        return this;
    }

    public EntityEquipmentPredicate.Builder offhand(ItemPredicate.Builder item) {
        this.offhand = Optional.of(item.build());
        return this;
    }

    public EntityEquipmentPredicate build() {
        return new EntityEquipmentPredicate(this.head, this.chest, this.legs, this.feet, this.body, this.mainhand, this.offhand);
    }
}

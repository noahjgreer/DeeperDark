/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.predicate.entity;

import java.util.Optional;
import net.minecraft.predicate.entity.EntityFlagsPredicate;

public static class EntityFlagsPredicate.Builder {
    private Optional<Boolean> isOnGround = Optional.empty();
    private Optional<Boolean> isOnFire = Optional.empty();
    private Optional<Boolean> isSneaking = Optional.empty();
    private Optional<Boolean> isSprinting = Optional.empty();
    private Optional<Boolean> isSwimming = Optional.empty();
    private Optional<Boolean> isFlying = Optional.empty();
    private Optional<Boolean> isBaby = Optional.empty();
    private Optional<Boolean> isInWater = Optional.empty();
    private Optional<Boolean> isFallFlying = Optional.empty();

    public static EntityFlagsPredicate.Builder create() {
        return new EntityFlagsPredicate.Builder();
    }

    public EntityFlagsPredicate.Builder onGround(Boolean onGround) {
        this.isOnGround = Optional.of(onGround);
        return this;
    }

    public EntityFlagsPredicate.Builder onFire(Boolean onFire) {
        this.isOnFire = Optional.of(onFire);
        return this;
    }

    public EntityFlagsPredicate.Builder sneaking(Boolean sneaking) {
        this.isSneaking = Optional.of(sneaking);
        return this;
    }

    public EntityFlagsPredicate.Builder sprinting(Boolean sprinting) {
        this.isSprinting = Optional.of(sprinting);
        return this;
    }

    public EntityFlagsPredicate.Builder swimming(Boolean swimming) {
        this.isSwimming = Optional.of(swimming);
        return this;
    }

    public EntityFlagsPredicate.Builder flying(Boolean flying) {
        this.isFlying = Optional.of(flying);
        return this;
    }

    public EntityFlagsPredicate.Builder isBaby(Boolean isBaby) {
        this.isBaby = Optional.of(isBaby);
        return this;
    }

    public EntityFlagsPredicate.Builder isInWater(Boolean isInWater) {
        this.isInWater = Optional.of(isInWater);
        return this;
    }

    public EntityFlagsPredicate.Builder isFallFlying(Boolean isFallFlying) {
        this.isFallFlying = Optional.of(isFallFlying);
        return this;
    }

    public EntityFlagsPredicate build() {
        return new EntityFlagsPredicate(this.isOnGround, this.isOnFire, this.isSneaking, this.isSprinting, this.isSwimming, this.isFlying, this.isBaby, this.isInWater, this.isFallFlying);
    }
}

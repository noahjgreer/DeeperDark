/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.entity.mob;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.jspecify.annotations.Nullable;

static class GuardianEntity.GuardianTargetPredicate
implements TargetPredicate.EntityPredicate {
    private final GuardianEntity owner;

    public GuardianEntity.GuardianTargetPredicate(GuardianEntity owner) {
        this.owner = owner;
    }

    @Override
    public boolean test(@Nullable LivingEntity livingEntity, ServerWorld serverWorld) {
        return (livingEntity instanceof PlayerEntity || livingEntity instanceof SquidEntity || livingEntity instanceof AxolotlEntity) && livingEntity.squaredDistanceTo(this.owner) > 9.0;
    }
}

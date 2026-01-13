/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2LongOpenHashMap
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.entity.raid;

import it.unimi.dsi.fastutil.ints.Int2LongOpenHashMap;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PatrolEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.village.raid.Raid;
import org.jspecify.annotations.Nullable;

public static class RaiderEntity.PickUpBannerAsLeaderGoal<T extends RaiderEntity>
extends Goal {
    private final T actor;
    private Int2LongOpenHashMap bannerItemCache = new Int2LongOpenHashMap();
    private @Nullable Path path;
    private @Nullable ItemEntity bannerItemEntity;
    final /* synthetic */ RaiderEntity field_52512;

    public RaiderEntity.PickUpBannerAsLeaderGoal(T actor) {
        this.field_52512 = raiderEntity;
        this.actor = actor;
        this.setControls(EnumSet.of(Goal.Control.MOVE));
    }

    @Override
    public boolean canStart() {
        if (this.shouldStop()) {
            return false;
        }
        Int2LongOpenHashMap int2LongOpenHashMap = new Int2LongOpenHashMap();
        double d = this.field_52512.getAttributeValue(EntityAttributes.FOLLOW_RANGE);
        List<ItemEntity> list = ((Entity)this.actor).getEntityWorld().getEntitiesByClass(ItemEntity.class, ((Entity)this.actor).getBoundingBox().expand(d, 8.0, d), OBTAINABLE_OMINOUS_BANNER_PREDICATE);
        for (ItemEntity itemEntity : list) {
            long l = this.bannerItemCache.getOrDefault(itemEntity.getId(), Long.MIN_VALUE);
            if (this.field_52512.getEntityWorld().getTime() < l) {
                int2LongOpenHashMap.put(itemEntity.getId(), l);
                continue;
            }
            Path path = ((MobEntity)this.actor).getNavigation().findPathTo(itemEntity, 1);
            if (path != null && path.reachesTarget()) {
                this.path = path;
                this.bannerItemEntity = itemEntity;
                return true;
            }
            int2LongOpenHashMap.put(itemEntity.getId(), this.field_52512.getEntityWorld().getTime() + 600L);
        }
        this.bannerItemCache = int2LongOpenHashMap;
        return false;
    }

    @Override
    public boolean shouldContinue() {
        if (this.bannerItemEntity == null || this.path == null) {
            return false;
        }
        if (this.bannerItemEntity.isRemoved()) {
            return false;
        }
        if (this.path.isFinished()) {
            return false;
        }
        return !this.shouldStop();
    }

    private boolean shouldStop() {
        if (!((RaiderEntity)this.actor).hasActiveRaid()) {
            return true;
        }
        if (((RaiderEntity)this.actor).getRaid().isFinished()) {
            return true;
        }
        if (!((PatrolEntity)this.actor).canLead()) {
            return true;
        }
        if (ItemStack.areEqual(((LivingEntity)this.actor).getEquippedStack(EquipmentSlot.HEAD), Raid.createOminousBanner(((Entity)this.actor).getRegistryManager().getOrThrow(RegistryKeys.BANNER_PATTERN)))) {
            return true;
        }
        RaiderEntity raiderEntity = this.field_52512.raid.getCaptain(((RaiderEntity)this.actor).getWave());
        return raiderEntity != null && raiderEntity.isAlive();
    }

    @Override
    public void start() {
        ((MobEntity)this.actor).getNavigation().startMovingAlong(this.path, 1.15f);
    }

    @Override
    public void stop() {
        this.path = null;
        this.bannerItemEntity = null;
    }

    @Override
    public void tick() {
        if (this.bannerItemEntity != null && this.bannerItemEntity.isInRange((Entity)this.actor, 1.414)) {
            ((RaiderEntity)this.actor).loot(RaiderEntity.PickUpBannerAsLeaderGoal.castToServerWorld(this.field_52512.getEntityWorld()), this.bannerItemEntity);
        }
    }
}

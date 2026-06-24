package net.noahsarch.deeperdark.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.noahsarch.deeperdark.item.ModItems;

public class ThrownSplashMilk extends ThrowableItemProjectile {

    public ThrownSplashMilk(EntityType<? extends ThrownSplashMilk> type, Level level) {
        super(type, level);
    }

    public ThrownSplashMilk(ServerLevel level, LivingEntity owner, ItemStack stack) {
        super(ModEntities.THROWN_SPLASH_MILK, owner, level, stack);
    }

    @Override
    protected double getDefaultGravity() {
        return 0.05;
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.SPLASH_MILK_BOTTLE;
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (this.level() instanceof ServerLevel serverLevel) {
            AABB aabb = this.getBoundingBox().inflate(4.0, 2.0, 4.0);
            for (LivingEntity entity : serverLevel.getEntitiesOfClass(LivingEntity.class, aabb)) {
                if (this.distanceToSqr(entity) < 16.0) {
                    entity.removeAllEffects();
                }
            }
            serverLevel.levelEvent(2002, this.blockPosition(), 0xFFFFFF);
            this.discard();
        }
    }
}

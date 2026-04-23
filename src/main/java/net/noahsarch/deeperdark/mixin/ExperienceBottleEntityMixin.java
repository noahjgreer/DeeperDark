package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownExperienceBottle;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.Level;
import net.noahsarch.deeperdark.duck.EntityAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.world.phys.Vec3;

@Mixin(ThrownExperienceBottle.class)
public abstract class ExperienceBottleEntityMixin extends ThrowableItemProjectile {

    protected ExperienceBottleEntityMixin(EntityType<? extends ThrowableItemProjectile> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "onHit", at = @At("HEAD"), cancellable = true)
    private void onHit(HitResult hitResult, CallbackInfo ci) {
        super.onHit(hitResult);
        Level world = ((EntityAccessor)(Object)this).deeperdark$getWorld();
        if (!world.isClientSide()) {
            // Play splash effect (2002 is Potion Splash)
            // Color for water is 3694022
            world.levelEvent(2002, this.blockPosition(), 3694022);

            int amount = 10;
            ExperienceOrb.award((ServerLevel)world, new net.minecraft.world.phys.Vec3(this.getX(), this.getY(), this.getZ()), amount);

            this.discard();
            ci.cancel();
        }
    }
}

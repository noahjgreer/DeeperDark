package net.noahsarch.deeperdark.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.projectile.thrown.ExperienceBottleEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import net.noahsarch.deeperdark.duck.EntityAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExperienceBottleEntity.class)
public abstract class ExperienceBottleEntityMixin extends ThrownItemEntity {

    protected ExperienceBottleEntityMixin(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "onCollision", at = @At("HEAD"), cancellable = true)
    private void onCollision(HitResult hitResult, CallbackInfo ci) {
        super.onCollision(hitResult);
        World world = ((EntityAccessor)(Object)this).deeperdark$getWorld();
        if (!world.isClient()) {
            // Play splash effect (2002 is Potion Splash)
            // Color for water is 3694022
            world.syncWorldEvent(2002, this.getBlockPos(), 3694022);

            int amount = 10;
            ExperienceOrbEntity.spawn((ServerWorld)world, new net.minecraft.util.math.Vec3d(this.getX(), this.getY(), this.getZ()), amount);

            this.discard();
            ci.cancel();
        }
    }
}

package net.noahsarch.deeperdark.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.projectile.thrown.ExperienceBottleEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
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
        if (!this.getWorld().isClient) {
            // Play splash effect (2002 is Potion Splash)
            // Color for water is 3694022
            this.getWorld().syncWorldEvent(2002, this.getBlockPos(), 3694022);

            int amount = 10;
            ExperienceOrbEntity.spawn((ServerWorld)this.getWorld(), this.getPos(), amount);

            this.discard();
            ci.cancel();
        }
    }
}


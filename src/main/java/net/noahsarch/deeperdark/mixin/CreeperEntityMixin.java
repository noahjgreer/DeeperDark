package net.noahsarch.deeperdark.mixin;

import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.noahsarch.deeperdark.DeeperDarkConfig;
import net.noahsarch.deeperdark.duck.EntityAccessor;
import net.noahsarch.deeperdark.util.BabyCreeperAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(CreeperEntity.class)
public abstract class CreeperEntityMixin {

    @Shadow private int explosionRadius;

    /**
     * Helper to check if this creeper is a baby (accesses the state from MobEntity mixin)
     */
    @Unique
    private boolean deeperdark$isBaby() {
        CreeperEntity self = (CreeperEntity) (Object) this;
        if (self instanceof BabyCreeperAccessor accessor) {
            return accessor.deeperdark$isBabyCreeper();
        }
        return false;
    }

    @Inject(method = "spawnEffectsCloud", at = @At("HEAD"), cancellable = true)
    private void deeperdark$spawnEffectsCloud(CallbackInfo ci) {
        CreeperEntity self = (CreeperEntity) (Object) this;
        World world = ((EntityAccessor)self).deeperdark$getWorld();
        Collection<StatusEffectInstance> collection = self.getStatusEffects();
        boolean isBaby = deeperdark$isBaby();

        if (!collection.isEmpty()) {
            AreaEffectCloudEntity areaEffectCloudEntity = new AreaEffectCloudEntity(world, self.getX(), self.getY(), self.getZ());
            // Baby creepers have smaller effect cloud
            float radius = isBaby ? 0.5F : 2.5F;
            areaEffectCloudEntity.setRadius(radius);
            areaEffectCloudEntity.setRadiusOnUse(-0.5F);
            areaEffectCloudEntity.setWaitTime(10);
            areaEffectCloudEntity.setDuration(isBaby ? 60 : 300);
            areaEffectCloudEntity.setPotionDurationScale(0.25F);
            areaEffectCloudEntity.setRadiusGrowth(-areaEffectCloudEntity.getRadius() / (float)areaEffectCloudEntity.getDuration());

            DeeperDarkConfig.ConfigInstance cfg = DeeperDarkConfig.get();
            int min = Math.max(1, cfg.creeperEffectMinSeconds) * 20;
            int max = Math.max(min, cfg.creeperEffectMaxSeconds * 20);

            for (StatusEffectInstance statusEffectInstance : collection) {
                StatusEffectInstance toAdd;
                if (statusEffectInstance.isInfinite()) {
                    int roll = world.random.nextInt(max - min + 1) + min;
                    toAdd = new StatusEffectInstance(statusEffectInstance.getEffectType(), roll, statusEffectInstance.getAmplifier(), statusEffectInstance.isAmbient(), statusEffectInstance.shouldShowParticles(), statusEffectInstance.shouldShowIcon());
                } else {
                    toAdd = new StatusEffectInstance(statusEffectInstance);
                }
                areaEffectCloudEntity.addEffect(toAdd);
            }

            world.spawnEntity(areaEffectCloudEntity);
        }

        ci.cancel();
    }

    /**
     * Modifies explosion radius for baby creepers by temporarily changing the explosionRadius field.
     * We reduce it at the start of explode() - the explosion uses explosionRadius * chargedMultiplier
     */
    @Inject(method = "explode()V", at = @At("HEAD"))
    private void deeperdark$modifyExplosionRadiusAndSpawnParticles(CallbackInfo ci) {
        CreeperEntity self = (CreeperEntity) (Object) this;
        World world = ((EntityAccessor)self).deeperdark$getWorld();

        if (deeperdark$isBaby()) {
            // Reduce explosion radius to 1/5 for baby creepers
            // The field is used in: this.explosionRadius * f where f is 1.0 or 2.0 (charged)
            // Setting it to 0 or 1 will make a tiny explosion
            this.explosionRadius = 1; // Minimum explosion, will be ~0.6 blocks effective radius

            // Spawn poof particles instead of normal explosion particles
            if (world instanceof ServerWorld serverWorld) {
                for (int i = 0; i < 20; i++) {
                    double offsetX = (self.getRandom().nextDouble() - 0.5) * 2.0;
                    double offsetY = self.getRandom().nextDouble() * 1.5;
                    double offsetZ = (self.getRandom().nextDouble() - 0.5) * 2.0;
                    serverWorld.spawnParticles(ParticleTypes.POOF,
                        self.getX() + offsetX, self.getY() + offsetY, self.getZ() + offsetZ,
                        1, 0, 0, 0, 0.05);
                }
                for (int i = 0; i < 10; i++) {
                    double offsetX = (self.getRandom().nextDouble() - 0.5) * 1.5;
                    double offsetY = self.getRandom().nextDouble() * 1.0;
                    double offsetZ = (self.getRandom().nextDouble() - 0.5) * 1.5;
                    serverWorld.spawnParticles(ParticleTypes.SMOKE,
                        self.getX() + offsetX, self.getY() + offsetY, self.getZ() + offsetZ,
                        1, 0, 0.05, 0, 0.02);
                }
            }
        }
    }
}

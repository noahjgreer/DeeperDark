package net.noahsarch.deeperdark.mixin;

import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.CreeperEntity;
import net.noahsarch.deeperdark.DeeperDarkConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(CreeperEntity.class)
public class CreeperEntityMixin {

    @Inject(method = "spawnEffectsCloud", at = @At("HEAD"), cancellable = true)
    private void deeperdark$spawnEffectsCloud(CallbackInfo ci) {
        CreeperEntity self = (CreeperEntity) (Object) this;
        Collection<StatusEffectInstance> collection = self.getStatusEffects();
        if (!collection.isEmpty()) {
            AreaEffectCloudEntity areaEffectCloudEntity = new AreaEffectCloudEntity(self.getWorld(), self.getX(), self.getY(), self.getZ());
            areaEffectCloudEntity.setRadius(2.5F);
            areaEffectCloudEntity.setRadiusOnUse(-0.5F);
            areaEffectCloudEntity.setWaitTime(10);
            areaEffectCloudEntity.setDuration(300);
            areaEffectCloudEntity.setPotionDurationScale(0.25F);
            areaEffectCloudEntity.setRadiusGrowth(-areaEffectCloudEntity.getRadius() / (float)areaEffectCloudEntity.getDuration());

            DeeperDarkConfig.ConfigInstance cfg = DeeperDarkConfig.get();
            int min = Math.max(1, cfg.creeperEffectMinSeconds) * 20;
            int max = Math.max(min, cfg.creeperEffectMaxSeconds * 20);

            for (StatusEffectInstance statusEffectInstance : collection) {
                StatusEffectInstance toAdd;
                if (statusEffectInstance.isInfinite()) {
                    int roll = self.getWorld().random.nextInt(max - min + 1) + min;
                    toAdd = new StatusEffectInstance(statusEffectInstance.getEffectType(), roll, statusEffectInstance.getAmplifier(), statusEffectInstance.isAmbient(), statusEffectInstance.shouldShowParticles(), statusEffectInstance.shouldShowIcon());
                } else {
                    toAdd = new StatusEffectInstance(statusEffectInstance);
                }
                areaEffectCloudEntity.addEffect(toAdd);
            }

            self.getWorld().spawnEntity(areaEffectCloudEntity);
        }

        ci.cancel();
    }
}

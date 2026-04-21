package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.warden.Warden;
import net.noahsarch.deeperdark.Deeperdark;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Warden.class)
public class WardenEntityBrainMixin {

    /**
     * Makes the Warden ignore entities with the Scentless effect, but only during sniffing.
     * The Warden can still detect and chase these entities through other means (vibrations, etc.)
     */
    @Inject(
            method = "isValidTarget",
            at = @At("HEAD"),
            cancellable = true
    )
    private void ignoreScentlessEntitiesWhenSniffing(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        // Get this as a Warden instance
        Warden warden = (Warden)(Object)this;

        // Only ignore scentless entities when the warden is in the SNIFFING pose
        if (warden.isInPose(Pose.SNIFFING)) {
            if (entity instanceof LivingEntity livingEntity && livingEntity.hasStatusEffect(MobEffects.WIND_CHARGED)) {
                // The entity has Scentless effect and Warden is sniffing, so ignore it
                cir.setReturnValue(false);
            }
        }
    }
}


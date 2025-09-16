package net.noahsarch.deeperdark.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.WardenEntity;
import net.noahsarch.deeperdark.Deeperdark;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WardenEntity.class)
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
        // Get this as a WardenEntity instance
        WardenEntity warden = (WardenEntity)(Object)this;

        // Only ignore scentless entities when the warden is in the SNIFFING pose
        if (warden.isInPose(EntityPose.SNIFFING)) {
            if (entity instanceof LivingEntity livingEntity && livingEntity.hasStatusEffect(StatusEffects.WIND_CHARGED)) {
                // The entity has Scentless effect and Warden is sniffing, so ignore it
                cir.setReturnValue(false);
            }
        }
    }
}


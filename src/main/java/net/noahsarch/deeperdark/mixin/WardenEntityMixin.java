package net.noahsarch.deeperdark.mixin;

import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.WardenEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WardenEntity.class)
public abstract class WardenEntityMixin {
    @Inject(method = "addAttributes", at = @At("RETURN"), cancellable = true)
    private static void removeKnockbackResistance(CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir) {
        DefaultAttributeContainer.Builder builder = cir.getReturnValue();
        builder.add(EntityAttributes.KNOCKBACK_RESISTANCE, 0.0);
        cir.setReturnValue(builder);
    }
}

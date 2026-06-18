package net.noahsarch.deeperdark.mixin;

import net.minecraft.network.chat.Component;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.noahsarch.deeperdark.block.ModBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DamageSource.class)
public class DamageSourceQuicksandMixin {

    /**
     * When freeze damage kills an entity that is standing in quicksand, override
     * the death message so it says "suffocated in quicksand" rather than "froze to death".
     */
    @Inject(method = "getLocalizedDeathMessage", at = @At("HEAD"), cancellable = true)
    private void deeperdark$quicksandDeathMessage(LivingEntity victim, CallbackInfoReturnable<Component> cir) {
        DamageSource self = (DamageSource)(Object)this;
        if (!self.is(DamageTypeTags.IS_FREEZING)) return;
        if (!victim.getInBlockState().is(ModBlocks.QUICKSAND)) return;

        LivingEntity killer = victim.getKillCredit();
        if (killer != null) {
            cir.setReturnValue(Component.translatable(
                    "death.attack.quicksand_suffocation.player",
                    victim.getDisplayName(), killer.getDisplayName()));
        } else {
            cir.setReturnValue(Component.translatable(
                    "death.attack.quicksand_suffocation",
                    victim.getDisplayName()));
        }
    }
}

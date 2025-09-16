package net.noahsarch.deeperdark.mixin;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.noahsarch.deeperdark.Deeperdark;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.client.gui.hud.InGameHud.class)
public class InGameHudEffectIconMixin {
    @Inject(method = "getEffectTexture", at = @At("HEAD"), cancellable = true)
    private static void deeperdark$useWindChargedIcon(RegistryEntry<StatusEffect> effect, CallbackInfoReturnable<Identifier> cir) {
        if (effect != null &&
            Deeperdark.SCENTLESS_ENTRY != null &&
            effect.getKey().equals(Deeperdark.SCENTLESS_ENTRY.getKey())) {
            cir.setReturnValue(Identifier.of("minecraft", "mob_effect/wind_charged"));
        }
    }
}

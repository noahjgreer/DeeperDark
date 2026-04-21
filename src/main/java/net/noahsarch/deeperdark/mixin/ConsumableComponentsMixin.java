package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Consumables.class)
public class ConsumableComponentsMixin {
    @Shadow
    @Final
    @Mutable
    public static Consumable ROTTEN_FLESH;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void modifyRottenFlesh(CallbackInfo ci) {
        // Replace rotten flesh to give both hunger and nausea effects
        ROTTEN_FLESH = Consumables.food()
                .consumeEffect(new ApplyStatusEffectsConsumeEffect(
                        List.of(
                                new MobEffectInstance(MobEffects.HUNGER, 600, 0),
                                new MobEffectInstance(MobEffects.NAUSEA, 300, 0)
                        ),
                        0.8f
                ))
                .build();
    }
}

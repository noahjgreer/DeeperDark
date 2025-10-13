package net.noahsarch.deeperdark.mixin;

import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.component.type.ConsumableComponents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.consume.ApplyEffectsConsumeEffect;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ConsumableComponents.class)
public class ConsumableComponentsMixin {
    @Shadow
    @Final
    @Mutable
    public static ConsumableComponent ROTTEN_FLESH;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void modifyRottenFlesh(CallbackInfo ci) {
        // Replace rotten flesh to give both hunger and nausea effects
        ROTTEN_FLESH = ConsumableComponents.food()
                .consumeEffect(new ApplyEffectsConsumeEffect(
                        List.of(
                                new StatusEffectInstance(StatusEffects.HUNGER, 600, 0),
                                new StatusEffectInstance(StatusEffects.NAUSEA, 300, 0)
                        ),
                        0.8f
                ))
                .build();
    }
}

package net.noahsarch.deeperdark.mixin;

import net.minecraft.entity.mob.MobEntity;
import net.noahsarch.deeperdark.event.WorldBorderHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEntity.class)
public class MobEntityMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        if (!((MobEntity)(Object)this).getWorld().isClient) {
             WorldBorderHandler.applyBorderForce((MobEntity)(Object)this);
        }
    }
}


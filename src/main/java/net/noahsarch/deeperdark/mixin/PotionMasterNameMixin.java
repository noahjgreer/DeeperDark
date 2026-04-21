package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;
import net.noahsarch.deeperdark.duck.PotionMasterDuck;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class PotionMasterNameMixin {

    @Inject(method = "getDisplayName", at = @At("HEAD"), cancellable = true)
    public void getDisplayName(CallbackInfoReturnable<Component> cir) {
        if (this instanceof PotionMasterDuck potionMaster && potionMaster.deeperdark$isPotionMaster()) {
            cir.setReturnValue(Component.translatable("entity.deeperdark.villager.potion_master"));
        }
    }
}


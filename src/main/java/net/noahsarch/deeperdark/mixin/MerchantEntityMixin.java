package net.noahsarch.deeperdark.mixin;

import net.noahsarch.deeperdark.DeeperDarkConfig;
import net.noahsarch.deeperdark.entity.SimpleTemptGoal;
import net.minecraft.world.entity.npc.ClientSideMerchant;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.world.entity.npc.villager.AbstractVillager;

@Mixin(AbstractVillager.class)
public class MerchantEntityMixin {
    @Inject(method = "<init>", at = @org.spongepowered.asm.mixin.injection.At("TAIL"))
    private void addEmeraldTemptGoal(CallbackInfo callbackInfo) {
        AbstractVillager self = (AbstractVillager)(Object)this;
        ((MobEntityAccessor)self).deeperdark$getGoalSelector().addGoal(4, new SimpleTemptGoal(self, 0.5));
    }

    @Inject(method = "canBeLeashed", at = @At("HEAD"), cancellable = true)
    private void deeperdark$allowLeashing(CallbackInfoReturnable<Boolean> cir) {
        if (DeeperDarkConfig.get().villagerLeashing) cir.setReturnValue(true);
    }
}

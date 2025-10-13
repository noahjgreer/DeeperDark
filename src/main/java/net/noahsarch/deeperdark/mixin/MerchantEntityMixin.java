package net.noahsarch.deeperdark.mixin;

import net.noahsarch.deeperdark.entity.SimpleTemptGoal;
import net.minecraft.entity.passive.MerchantEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(MerchantEntity.class)
public class MerchantEntityMixin {
    @Inject(method = "<init>", at = @org.spongepowered.asm.mixin.injection.At("TAIL"))
    private void addEmeraldTemptGoal(CallbackInfo callbackInfo) {
        MerchantEntity self = (MerchantEntity)(Object)this;
        ((MobEntityAccessor)self).getGoalSelector().add(4, new SimpleTemptGoal(self, 0.5));
    }

    @Overwrite
    public boolean canBeLeashed() {
        return true;
    }
}

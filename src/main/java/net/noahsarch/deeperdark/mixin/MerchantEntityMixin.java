package net.noahsarch.deeperdark.mixin;

import net.noahsarch.deeperdark.entity.SimpleTemptGoal;
import net.minecraft.world.entity.npc.ClientSideMerchant;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.Overwrite;
import net.minecraft.world.entity.npc.villager.AbstractVillager;

@Mixin(AbstractVillager.class)
public class MerchantEntityMixin {
    @Inject(method = "<init>", at = @org.spongepowered.asm.mixin.injection.At("TAIL"))
    private void addEmeraldTemptGoal(CallbackInfo callbackInfo) {
        AbstractVillager self = (AbstractVillager)(Object)this;
        ((MobEntityAccessor)self).getGoalSelector().addGoal(4, new SimpleTemptGoal(self, 0.5));
    }

    @Overwrite
    public boolean canBeLeashed() {
        return true;
    }
}

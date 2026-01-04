package net.noahsarch.deeperdark.mixin;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEntity.class)
public class SquidMobMixin {

    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    private void deeperdark$interactMob(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        MobEntity self = (MobEntity)(Object)this;
        if (!(self instanceof SquidEntity)) return;

        ItemStack stack = player.getStackInHand(hand);
        if (stack.isOf(Items.BUCKET)) {
            player.playSound(SoundEvents.ENTITY_COW_MILK, 1.0F, 1.0F);
            ItemStack milk = ItemUsage.exchangeStack(stack, player, new ItemStack(Items.MILK_BUCKET));
            player.setStackInHand(hand, milk);
            cir.setReturnValue(ActionResult.SUCCESS);
        }
    }
}


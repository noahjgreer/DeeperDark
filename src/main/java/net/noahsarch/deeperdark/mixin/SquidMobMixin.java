package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.squid.Squid;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public class SquidMobMixin {

    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    private void deeperdark$interactMob(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        Mob self = (Mob)(Object)this;
        if (!(self instanceof Squid)) return;

        ItemStack stack = player.getStackInHand(hand);
        if (stack.isOf(Items.BUCKET)) {
            player.playSound(SoundEvents.ENTITY_COW_MILK, 1.0F, 1.0F);
            ItemStack milk = ItemUsage.exchangeStack(stack, player, new ItemStack(Items.MILK_BUCKET));
            player.setStackInHand(hand, milk);
            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }
}


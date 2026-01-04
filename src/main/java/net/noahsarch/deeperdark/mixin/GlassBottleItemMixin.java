package net.noahsarch.deeperdark.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.GlassBottleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GlassBottleItem.class)
public class GlassBottleItemMixin {

    @Inject(method = "use", at = @At("RETURN"), cancellable = true)
    private void deeperdark$use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (cir.getReturnValue() == ActionResult.PASS) {
            ItemStack stack = user.getStackInHand(hand);
            if (user.totalExperience >= 10 || user.isCreative()) {
                if (!world.isClient) {
                    if (!user.isCreative()) {
                        user.addExperience(-10);
                    }

                    world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_EXPERIENCE_BOTTLE_THROW, SoundCategory.NEUTRAL, 0.5F, 1.0F);

                    ItemStack bottle = ItemUsage.exchangeStack(stack, user, new ItemStack(Items.EXPERIENCE_BOTTLE));
                    user.setStackInHand(hand, bottle);
                    cir.setReturnValue(ActionResult.SUCCESS);
                } else {
                    cir.setReturnValue(ActionResult.CONSUME);
                }
            }
        }
    }
}

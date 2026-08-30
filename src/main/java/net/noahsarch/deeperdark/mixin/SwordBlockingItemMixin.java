package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.noahsarch.deeperdark.ported.SwordBlockingPort;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Stops a sword being raised when sword blocking does not apply.
 *
 * <p>{@link SwordBlockingPort} attaches {@code minecraft:blocks_attacks} to every sword, which is
 * what makes {@code Item#use} start the 1.8 block in the first place. This mixin is the gate on
 * top: with a real shield in hand, or with the feature switched off, we return
 * {@link InteractionResult#PASS} so the interaction falls through to the other hand and the
 * shield is used as normal.
 */
@Mixin(Item.class)
public class SwordBlockingItemMixin {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void deeperdark$gateSwordBlocking(
            Level level, Player player, InteractionHand hand,
            CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack stack = player.getItemInHand(hand);
        if (SwordBlockingPort.isBlockingSword(stack) && !SwordBlockingPort.canSwordBlock(player, stack)) {
            cir.setReturnValue(InteractionResult.PASS);
        }
    }
}

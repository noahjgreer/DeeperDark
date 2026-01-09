package net.noahsarch.deeperdark.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantingTableBlock.class)
public class EnchantingTableBlockMixin {

    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
    private void onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack stack = player.getStackInHand(Hand.MAIN_HAND);
        Hand hand = Hand.MAIN_HAND;

        if (!stack.isOf(Items.GLASS_BOTTLE)) {
            stack = player.getStackInHand(Hand.OFF_HAND);
            hand = Hand.OFF_HAND;
        }

        if (stack.isOf(Items.GLASS_BOTTLE)) {
            int totalExperience = getTotalExperience(player);

            if (totalExperience >= 10 || player.isCreative()) {
                if (!world.isClient) {
                    if (!player.isCreative()) {
                        player.addExperience(-10);
                    }

                    world.playSound(null, pos, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 0.5F, 1.0F);

                    ItemStack bottle = ItemUsage.exchangeStack(stack, player, new ItemStack(Items.EXPERIENCE_BOTTLE));
                    player.setStackInHand(hand, bottle);

                    cir.setReturnValue(ActionResult.SUCCESS);
                } else {
                    cir.setReturnValue(ActionResult.CONSUME);
                }
            }
        }
    }

    @Unique
    private int getTotalExperience(PlayerEntity player) {
        int level = player.experienceLevel;
        float progress = player.experienceProgress;

        int currentLevelXp = (int) (progress * player.getNextLevelExperience());

        int totalXp = currentLevelXp;
        for (int i = 0; i < level; i++) {
            totalXp += getExperienceForLevel(i);
        }
        return totalXp;
    }

    @Unique
    private int getExperienceForLevel(int level) {
        if (level >= 31) {
            return 9 * level - 158;
        } else if (level >= 16) {
            return 5 * level - 38;
        } else {
            return 2 * level + 7;
        }
    }
}

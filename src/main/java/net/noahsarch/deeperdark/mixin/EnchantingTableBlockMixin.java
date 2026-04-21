package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.EnchantingTableBlock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantingTableBlock.class)
public class EnchantingTableBlockMixin {

    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
    private void onUse(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        // Logic moved to SiphonEvents for Siphon block
    }

    @Unique
    private int getTotalExperience(Player player) {
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

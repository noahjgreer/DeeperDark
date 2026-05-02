package net.noahsarch.deeperdark.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

class GoldenCauldronHelper {

    /** Pour a bucket into a cauldron → give player empty bucket, set block to goldenState. */
    static InteractionResult fillGoldenCauldron(Level level, BlockPos pos, Player player, InteractionHand hand,
                                                 ItemStack stack, BlockState goldenState, SoundEvent sound) {
        if (!level.isClientSide()) {
            player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.BUCKET)));
            player.awardStat(Stats.FILL_CAULDRON);
            player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
            level.setBlockAndUpdate(pos, goldenState);
            level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent(null, GameEvent.FLUID_PLACE, pos);
        }
        return InteractionResult.SUCCESS;
    }

    /** Take fluid out of a cauldron with an empty bucket → give player filledBucket, set block to GOLDEN_CAULDRON. */
    static InteractionResult emptyGoldenCauldron(Level level, BlockPos pos, Player player, InteractionHand hand,
                                                  ItemStack stack, ItemStack filledBucket, SoundEvent sound) {
        if (!level.isClientSide()) {
            player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, filledBucket));
            player.awardStat(Stats.USE_CAULDRON);
            player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
            level.setBlockAndUpdate(pos, ModBlocks.GOLDEN_CAULDRON.defaultBlockState());
            level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent(null, GameEvent.FLUID_PICKUP, pos);
        }
        return InteractionResult.SUCCESS;
    }

    static boolean isUnderWater(Level level, BlockPos pos) {
        return level.getFluidState(pos.above()).is(FluidTags.WATER);
    }
}

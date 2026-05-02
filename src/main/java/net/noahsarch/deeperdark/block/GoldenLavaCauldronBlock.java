package net.noahsarch.deeperdark.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.LavaCauldronBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class GoldenLavaCauldronBlock extends LavaCauldronBlock {

    public GoldenLavaCauldronBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                           Player player, InteractionHand hand, BlockHitResult hit) {
        Item item = stack.getItem();

        if (item == Items.BUCKET) {
            return GoldenCauldronHelper.emptyGoldenCauldron(level, pos, player, hand, stack,
                new ItemStack(Items.LAVA_BUCKET), SoundEvents.BUCKET_FILL_LAVA);

        } else if (item == Items.LAVA_BUCKET) {
            if (GoldenCauldronHelper.isUnderWater(level, pos)) return InteractionResult.CONSUME;
            return GoldenCauldronHelper.fillGoldenCauldron(level, pos, player, hand, stack,
                ModBlocks.LAVA_GOLDEN_CAULDRON.defaultBlockState(),
                SoundEvents.BUCKET_EMPTY_LAVA);

        } else if (item == Items.WATER_BUCKET) {
            return GoldenCauldronHelper.fillGoldenCauldron(level, pos, player, hand, stack,
                ModBlocks.WATER_GOLDEN_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3),
                SoundEvents.BUCKET_EMPTY);

        } else if (item == Items.POWDER_SNOW_BUCKET) {
            if (GoldenCauldronHelper.isUnderWater(level, pos)) return InteractionResult.CONSUME;
            return GoldenCauldronHelper.fillGoldenCauldron(level, pos, player, hand, stack,
                ModBlocks.POWDER_SNOW_GOLDEN_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3),
                SoundEvents.BUCKET_EMPTY_POWDER_SNOW);
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hit);
    }
}

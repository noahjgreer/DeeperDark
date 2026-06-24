package net.noahsarch.deeperdark.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Golden variant of a layered cauldron (water or powder snow).
 * Intercepts all bucket interactions to redirect block transitions to golden variants.
 * All other interactions (glass bottle, dye, banners, shulkers) delegate to vanilla,
 * with a post-pass that swaps any accidental Blocks.CAULDRON transition to GOLDEN_CAULDRON.
 */
public class GoldenLayeredCauldronBlock extends LayeredCauldronBlock {

    /** The bucket item given when this cauldron is emptied (WATER_BUCKET or POWDER_SNOW_BUCKET). */
    private final Item emptyResultItem;
    /** Sound played when the cauldron is emptied into a bucket. */
    private final SoundEvent emptySound;

    public GoldenLayeredCauldronBlock(Biome.Precipitation precipitation,
                                       CauldronInteraction.Dispatcher vanillaDispatcher,
                                       Item emptyResultItem,
                                       SoundEvent emptySound,
                                       BlockBehaviour.Properties properties) {
        super(precipitation, vanillaDispatcher, properties);
        this.emptyResultItem = emptyResultItem;
        this.emptySound = emptySound;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                           Player player, InteractionHand hand, BlockHitResult hit) {
        Item item = stack.getItem();

        if (item == Items.BUCKET) {
            // Only allow emptying when fully filled (level 3)
            if (state.getValue(LayeredCauldronBlock.LEVEL) != 3) return InteractionResult.TRY_WITH_EMPTY_HAND;
            return GoldenCauldronHelper.emptyGoldenCauldron(level, pos, player, hand, stack,
                new ItemStack(emptyResultItem), emptySound);

        } else if (item == Items.MILK_BUCKET) {
            return GoldenCauldronHelper.fillGoldenCauldron(level, pos, player, hand, stack,
                ModBlocks.MILK_GOLDEN_CAULDRON.defaultBlockState(),
                SoundEvents.BUCKET_EMPTY);

        } else if (item == Items.WATER_BUCKET) {
            return GoldenCauldronHelper.fillGoldenCauldron(level, pos, player, hand, stack,
                ModBlocks.WATER_GOLDEN_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3),
                SoundEvents.BUCKET_EMPTY);

        } else if (item == Items.LAVA_BUCKET) {
            if (GoldenCauldronHelper.isUnderWater(level, pos)) return InteractionResult.CONSUME;
            return GoldenCauldronHelper.fillGoldenCauldron(level, pos, player, hand, stack,
                ModBlocks.LAVA_GOLDEN_CAULDRON.defaultBlockState(),
                SoundEvents.BUCKET_EMPTY_LAVA);

        } else if (item == Items.POWDER_SNOW_BUCKET) {
            if (GoldenCauldronHelper.isUnderWater(level, pos)) return InteractionResult.CONSUME;
            return GoldenCauldronHelper.fillGoldenCauldron(level, pos, player, hand, stack,
                ModBlocks.POWDER_SNOW_GOLDEN_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3),
                SoundEvents.BUCKET_EMPTY_POWDER_SNOW);
        }

        // All other interactions (glass bottle, dye, banners, shulker boxes) use the vanilla dispatcher
        // via super. If the level hits 0, vanilla's lowerFillLevel sets Blocks.CAULDRON — fix that up.
        InteractionResult result = super.useItemOn(stack, state, level, pos, player, hand, hit);
        if (!level.isClientSide() && level.getBlockState(pos).is(Blocks.CAULDRON)) {
            level.setBlockAndUpdate(pos, ModBlocks.GOLDEN_CAULDRON.defaultBlockState());
        }
        return result;
    }
}

package net.noahsarch.deeperdark.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.CauldronBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;

public class GoldenCauldronBlock extends CauldronBlock {

    public GoldenCauldronBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                           Player player, InteractionHand hand, BlockHitResult hit) {
        Item item = stack.getItem();
        if (item == Items.WATER_BUCKET) {
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
        } else if (item == Items.POTION) {
            PotionContents potion = stack.get(DataComponents.POTION_CONTENTS);
            if (potion != null && potion.is(Potions.WATER)) {
                if (!level.isClientSide()) {
                    player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
                    player.awardStat(Stats.USE_CAULDRON);
                    player.awardStat(Stats.ITEM_USED.get(item));
                    level.setBlockAndUpdate(pos, ModBlocks.WATER_GOLDEN_CAULDRON.defaultBlockState());
                    level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                    level.gameEvent(null, GameEvent.FLUID_PLACE, pos);
                }
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hit);
    }

    @Override
    public void handlePrecipitation(BlockState state, Level level, BlockPos pos, Biome.Precipitation precipitation) {
        if (shouldHandlePrecipitation(level, precipitation)) {
            if (precipitation == Biome.Precipitation.RAIN) {
                level.setBlockAndUpdate(pos, ModBlocks.WATER_GOLDEN_CAULDRON.defaultBlockState());
                level.gameEvent(null, GameEvent.BLOCK_CHANGE, pos);
            } else if (precipitation == Biome.Precipitation.SNOW) {
                level.setBlockAndUpdate(pos, ModBlocks.POWDER_SNOW_GOLDEN_CAULDRON.defaultBlockState());
                level.gameEvent(null, GameEvent.BLOCK_CHANGE, pos);
            }
        }
    }

    @Override
    protected boolean canReceiveStalactiteDrip(Fluid fluid) {
        return true;
    }

    @Override
    protected void receiveStalactiteDrip(BlockState state, Level level, BlockPos pos, Fluid fluid) {
        if (fluid == Fluids.WATER) {
            BlockState newState = ModBlocks.WATER_GOLDEN_CAULDRON.defaultBlockState();
            level.setBlockAndUpdate(pos, newState);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(newState));
            level.levelEvent(1047, pos, 0);
        } else if (fluid == Fluids.LAVA) {
            BlockState newState = ModBlocks.LAVA_GOLDEN_CAULDRON.defaultBlockState();
            level.setBlockAndUpdate(pos, newState);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(newState));
            level.levelEvent(1046, pos, 0);
        }
    }
}

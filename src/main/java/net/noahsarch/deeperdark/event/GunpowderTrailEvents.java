package net.noahsarch.deeperdark.event;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.noahsarch.deeperdark.block.ModBlocks;

public class GunpowderTrailEvents {

    public static void register() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            ItemStack stack = player.getItemInHand(hand);
            if (!stack.is(Items.GUNPOWDER)) return InteractionResult.PASS;

            // Only allow placement by clicking the top face of a block
            if (hitResult.getDirection() != Direction.UP) return InteractionResult.PASS;

            // BlockPlaceContext computes getClickedPos() as hitPos.relative(face) = the block above
            BlockPlaceContext ctx = new BlockPlaceContext(player, hand, stack, hitResult);
            BlockPos placePos = ctx.getClickedPos();
            BlockState current = world.getBlockState(placePos);

            // Target must be replaceable (air, tall grass, etc.)
            if (!current.canBeReplaced(ctx)) return InteractionResult.PASS;

            // Needs a solid support below (the block we just clicked)
            if (!ModBlocks.GUNPOWDER_TRAIL.defaultBlockState().canSurvive(world, placePos)) {
                return InteractionResult.PASS;
            }

            // Don't intercept right-clicks on interactive blocks unless sneaking
            if (!player.isShiftKeyDown()) {
                BlockState clickedState = world.getBlockState(hitResult.getBlockPos());
                if (clickedState.getMenuProvider(world, hitResult.getBlockPos()) != null) {
                    return InteractionResult.PASS;
                }
            }

            if (!world.isClientSide()) {
                BlockState newState = ModBlocks.GUNPOWDER_TRAIL.getStateForPlacement(ctx);
                if (newState == null) return InteractionResult.PASS;

                world.setBlock(placePos, newState, 3);
                world.playSound(null, placePos,
                        SoundType.GRAVEL.getPlaceSound(),
                        SoundSource.BLOCKS, 0.6F, 1.3F);

                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }

            return InteractionResult.SUCCESS;
        });
    }
}

package net.noahsarch.deeperdark.event;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.resources.Identifier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.noahsarch.deeperdark.util.CustomBlockManager;

public class LeatherBlockEvents {
    // Defines the leather block id (data component target)
    public static final Identifier LEATHER_BLOCK_MODEL_ID = Identifier.fromNamespaceAndPath("minecraft", "leather_block");

    public static void register() {
        // Placement Logic
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            ItemStack stack = player.getItemInHand(hand);
            if (stack.isEmpty()) return InteractionResult.PASS;

            // Check for item_model component
            Identifier modelId = stack.get(DataComponents.ITEM_MODEL);
            if (modelId == null || !modelId.equals(LEATHER_BLOCK_MODEL_ID)) {
                return InteractionResult.PASS;
            }

            BlockPos pos = hitResult.getBlockPos();
            BlockState state = world.getBlockState(pos);
            Direction side = hitResult.getDirection();
            BlockPos placePos = pos.relative(side);

            if (!world.getBlockState(placePos).canBeReplaced(new BlockPlaceContext(player, hand, stack, hitResult))) {
                 return InteractionResult.PASS;
            }

            if (!player.isShiftKeyDown() && isInteractable(state, world, pos)) {
                 return InteractionResult.PASS;
            }

            if (!world.isClientSide()) {
                if (CustomBlockManager.place(world, placePos, stack, Blocks.ORANGE_WOOL, null)) {
                    world.playSound(null, placePos, SoundType.WOOL.getPlaceSound(), SoundSource.BLOCKS, 1f, 1f);

                    if (!player.isCreative()) {
                        stack.shrink(1);
                    }
                }
            }

            return InteractionResult.SUCCESS;
        });

        // Break Logic
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            if (state.getBlock() == Blocks.ORANGE_WOOL) {
                if (CustomBlockManager.onBreak(world, pos, state, player, LEATHER_BLOCK_MODEL_ID, SoundType.WOOL, null, null)) {
                    return false;
                }
            }
            return true;
        });
    }

    private static boolean isInteractable(BlockState state, Level world, BlockPos pos) {
        return state.getMenuProvider(world, pos) != null ||
               state.getBlock() instanceof net.minecraft.world.level.block.DoorBlock ||
               state.getBlock() instanceof net.minecraft.world.level.block.TrapDoorBlock ||
               state.getBlock() instanceof net.minecraft.world.level.block.FenceGateBlock ||
               state.getBlock() instanceof net.minecraft.world.level.block.ButtonBlock ||
               state.getBlock() instanceof net.minecraft.world.level.block.LeverBlock;
    }
}

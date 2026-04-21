package net.noahsarch.deeperdark.event;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.resources.Identifier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.noahsarch.deeperdark.util.CustomBlockManager;

public class GunpowderBlockEvents {
    public static final Identifier GUNPOWDER_BLOCK_MODEL_ID = Identifier.fromNamespaceAndPath("minecraft", "gunpowder_block");

    public static void register() {
        // Placement Logic
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            ItemStack stack = player.getStackInHand(hand);
            if (stack.isEmpty()) return InteractionResult.PASS;

            // Check for item_model component
            Identifier modelId = stack.get(DataComponents.ITEM_MODEL);
            if (modelId == null || !modelId.equals(GUNPOWDER_BLOCK_MODEL_ID)) {
                return InteractionResult.PASS;
            }

            BlockPos pos = hitResult.getBlockPos();
            BlockState state = world.getBlockState(pos);
            Direction side = hitResult.getSide();
            BlockPos placePos = pos.offset(side);

            // Check if place position is replaceable logic is handled by standard placement context usually
            // But we do manual check
            if (!world.getBlockState(placePos).canReplace(new UseOnContext(player, hand, stack, hitResult))) {
                 return InteractionResult.PASS;
            }

            if (!player.isSneaking() && isInteractable(state, world, pos)) {
                 return InteractionResult.PASS;
            }

            if (!world.isClient()) {
                // Place Stone with Gunpowder Block Display
                if (CustomBlockManager.place(world, placePos, stack, Blocks.STONE, null)) {
                    world.playSound(null, placePos, SoundType.STONE.getPlaceSound(), SoundSource.BLOCKS, 1f, 1f);

                    if (!player.isCreative()) {
                        stack.decrement(1);
                    }
                }
            }

            return InteractionResult.SUCCESS;
        });

        // Break Logic
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            if (state.getBlock() == Blocks.STONE) {
                // Pass GUNPOWDER_BLOCK_MODEL_ID
                if (CustomBlockManager.onBreak(world, pos, state, player, GUNPOWDER_BLOCK_MODEL_ID, SoundType.STONE, null, null)) {
                    return false;
                }
            }
            return true;
        });
    }

    private static boolean isInteractable(BlockState state, Level world, BlockPos pos) {
        return state.createScreenHandlerFactory(world, pos) != null ||
               state.getBlock() instanceof net.minecraft.world.level.block.DoorBlock ||
               state.getBlock() instanceof net.minecraft.world.level.block.TrapdoorBlock ||
               state.getBlock() instanceof net.minecraft.world.level.block.FenceGateBlock ||
               state.getBlock() instanceof net.minecraft.world.level.block.ButtonBlock ||
               state.getBlock() instanceof net.minecraft.world.level.block.LeverBlock;
    }
}


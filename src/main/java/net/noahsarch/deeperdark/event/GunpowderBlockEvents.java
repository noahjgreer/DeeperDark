package net.noahsarch.deeperdark.event;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.noahsarch.deeperdark.util.CustomBlockManager;

public class GunpowderBlockEvents {
    public static final Identifier GUNPOWDER_BLOCK_MODEL_ID = Identifier.of("minecraft", "gunpowder_block");

    public static void register() {
        // Placement Logic
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            ItemStack stack = player.getStackInHand(hand);
            if (stack.isEmpty()) return ActionResult.PASS;

            // Check for item_model component
            Identifier modelId = stack.get(DataComponentTypes.ITEM_MODEL);
            if (modelId == null || !modelId.equals(GUNPOWDER_BLOCK_MODEL_ID)) {
                return ActionResult.PASS;
            }

            BlockPos pos = hitResult.getBlockPos();
            BlockState state = world.getBlockState(pos);
            Direction side = hitResult.getSide();
            BlockPos placePos = pos.offset(side);

            // Check if place position is replaceable logic is handled by standard placement context usually
            // But we do manual check
            if (!world.getBlockState(placePos).canReplace(new ItemPlacementContext(player, hand, stack, hitResult))) {
                 return ActionResult.PASS;
            }

            if (!player.isSneaking() && isInteractable(state, world, pos)) {
                 return ActionResult.PASS;
            }

            if (!world.isClient()) {
                // Place Stone with Gunpowder Block Display
                if (CustomBlockManager.place(world, placePos, stack, Blocks.STONE, null)) {
                    world.playSound(null, placePos, BlockSoundGroup.STONE.getPlaceSound(), SoundCategory.BLOCKS, 1f, 1f);

                    if (!player.isCreative()) {
                        stack.decrement(1);
                    }
                }
            }

            return ActionResult.SUCCESS;
        });

        // Break Logic
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            if (state.getBlock() == Blocks.STONE) {
                // Pass GUNPOWDER_BLOCK_MODEL_ID
                if (CustomBlockManager.onBreak(world, pos, state, player, GUNPOWDER_BLOCK_MODEL_ID, BlockSoundGroup.STONE, null, null)) {
                    return false;
                }
            }
            return true;
        });
    }

    private static boolean isInteractable(BlockState state, World world, BlockPos pos) {
        return state.createScreenHandlerFactory(world, pos) != null ||
               state.getBlock() instanceof net.minecraft.block.DoorBlock ||
               state.getBlock() instanceof net.minecraft.block.TrapdoorBlock ||
               state.getBlock() instanceof net.minecraft.block.FenceGateBlock ||
               state.getBlock() instanceof net.minecraft.block.ButtonBlock ||
               state.getBlock() instanceof net.minecraft.block.LeverBlock;
    }
}


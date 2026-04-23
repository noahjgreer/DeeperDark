package net.noahsarch.deeperdark.event;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Display;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.InteractionResult;
import net.minecraft.resources.Identifier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.noahsarch.deeperdark.util.CustomBlockManager;

import java.util.List;

public class SiphonEvents {
    public static final Identifier SIPHON_MODEL_ID = Identifier.fromNamespaceAndPath("minecraft", "siphon");

    public static void register() {
        // Placement Logic
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            ItemStack stack = player.getItemInHand(hand);
            BlockPos pos = hitResult.getBlockPos();
            BlockState state = world.getBlockState(pos);

            // Siphon Interaction (XP Bottling)
            if (stack.getItem() == Items.GLASS_BOTTLE && state.getBlock() == Blocks.COBBLED_DEEPSLATE_SLAB) {
                 AABB box = new AABB(pos);
                 List<Display.ItemDisplay> displays = world.getEntitiesOfClass(Display.ItemDisplay.class, box, entity -> {
                     ItemStack item = entity.getItemStack();
                     Identifier modelId = item.get(DataComponents.ITEM_MODEL);
                     return modelId != null && modelId.equals(SIPHON_MODEL_ID);
                 });

                 if (!displays.isEmpty()) {
                     int totalExperience = getTotalExperience(player);

                     if (totalExperience >= 10 || player.isCreative()) {
                         if (!world.isClientSide()) {
                             if (!player.isCreative()) {
                                 player.giveExperiencePoints(-10);
                             }

                             world.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 0.5F, 1.0F);

                             ItemStack bottle = ItemUtils.createFilledResult(stack, player, new ItemStack(Items.EXPERIENCE_BOTTLE));
                             player.setItemInHand(hand, bottle);
                         }
                         return InteractionResult.SUCCESS;
                     }
                 }
            }

            if (stack.isEmpty()) return InteractionResult.PASS;

            // Check for item_model component
            Identifier modelId = stack.get(DataComponents.ITEM_MODEL);
            if (modelId == null || !modelId.equals(SIPHON_MODEL_ID)) {
                return InteractionResult.PASS;
            }

            // We have a Siphon item. Determine placement.
            Direction side = hitResult.getDirection();
            BlockPos placePos = pos.relative(side);

            // Check if place position is replaceable.
            if (!world.getBlockState(placePos).canBeReplaced(new BlockPlaceContext(player, hand, stack, hitResult))) {
                 return InteractionResult.PASS;
            }

            // Prevent accidental placement when using interactable blocks
            if (!player.isShiftKeyDown() && isInteractable(state, world, pos)) {
                 return InteractionResult.PASS;
            }

            if (!world.isClientSide()) {
                // Place Siphon
                if (CustomBlockManager.place(world, placePos, stack, Blocks.COBBLED_DEEPSLATE_SLAB, null)) {
                    world.playSound(null, placePos, SoundType.STONE.getPlaceSound(), SoundSource.BLOCKS, 1f, 1f);

                    if (!player.isCreative()) {
                        stack.shrink(1);
                    }
                }
            }

            return InteractionResult.SUCCESS;
        });

        // Break Logic
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            if (state.getBlock() == Blocks.COBBLED_DEEPSLATE_SLAB) {
                if (CustomBlockManager.onBreak(world, pos, state, player, SIPHON_MODEL_ID, SoundType.STONE, null, null)) {
                    return false;
                }
            }
            return true;
        });
    }

    private static boolean isInteractable(BlockState state, net.minecraft.world.level.Level world, BlockPos pos) {
        return state.getMenuProvider(world, pos) != null ||
               state.getBlock() instanceof net.minecraft.world.level.block.DoorBlock ||
               state.getBlock() instanceof net.minecraft.world.level.block.TrapDoorBlock ||
               state.getBlock() instanceof net.minecraft.world.level.block.FenceGateBlock ||
               state.getBlock() instanceof net.minecraft.world.level.block.ButtonBlock ||
               state.getBlock() instanceof net.minecraft.world.level.block.LeverBlock;
    }

    private static int getTotalExperience(Player player) {
        int level = player.experienceLevel;
        float progress = player.experienceProgress;

        int totalXp = (int) (progress * player.getXpNeededForNextLevel());
        for (int i = 0; i < level; i++) {
            totalXp += getExperienceForLevel(i);
        }
        return totalXp;
    }

    private static int getExperienceForLevel(int level) {
        if (level >= 31) {
            return 9 * level - 158;
        } else if (level >= 16) {
            return 5 * level - 38;
        } else {
            return 2 * level + 7;
        }
    }
}

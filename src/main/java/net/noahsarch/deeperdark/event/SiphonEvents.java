package net.noahsarch.deeperdark.event;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.decoration.DisplayEntity.ItemDisplayEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.item.ItemUsage;
import net.noahsarch.deeperdark.util.CustomBlockManager;

import java.util.List;

public class SiphonEvents {
    // defined constants
    public static final Identifier SIPHON_MODEL_ID = Identifier.of("minecraft", "siphon");

    public static void register() {
        // Placement Logic
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            ItemStack stack = player.getStackInHand(hand);
            BlockPos pos = hitResult.getBlockPos();
            BlockState state = world.getBlockState(pos);

            // Siphon Interaction (XP Bottling)
            if (stack.isOf(Items.GLASS_BOTTLE) && state.isOf(Blocks.COBBLED_DEEPSLATE_SLAB)) {
                 Box box = new Box(pos);
                 List<ItemDisplayEntity> displays = world.getEntitiesByClass(ItemDisplayEntity.class, box, entity -> {
                     ItemStack item = entity.getItemStack();
                     Identifier modelId = item.get(DataComponentTypes.ITEM_MODEL);
                     return modelId != null && modelId.equals(SIPHON_MODEL_ID);
                 });

                 if (!displays.isEmpty()) {
                     int totalExperience = getTotalExperience(player);

                     if (totalExperience >= 10 || player.isCreative()) {
                         if (!world.isClient) {
                             if (!player.isCreative()) {
                                 player.addExperience(-10);
                             }

                             world.playSound(null, pos, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 0.5F, 1.0F);

                             ItemStack bottle = ItemUsage.exchangeStack(stack, player, new ItemStack(Items.EXPERIENCE_BOTTLE));
                             player.setStackInHand(hand, bottle);
                         }
                         return ActionResult.SUCCESS;
                     }
                 }
            }

            if (stack.isEmpty()) return ActionResult.PASS;

            // Check for item_model component
            Identifier modelId = stack.get(DataComponentTypes.ITEM_MODEL);
            if (modelId == null || !modelId.equals(SIPHON_MODEL_ID)) {
                return ActionResult.PASS;
            }

            // We have a Siphon item. Determine placement.
            Direction side = hitResult.getSide();
            BlockPos placePos = pos.offset(side);

            // Check if place position is replaceable.
            if (!world.getBlockState(placePos).canReplace(new ItemPlacementContext(player, hand, stack, hitResult))) {
                 return ActionResult.PASS;
            }

            // Prevent accidental placement when using interactable blocks
            if (!player.isSneaking() && isInteractable(state, world, pos)) {
                 return ActionResult.PASS;
            }

            if (!world.isClient) {
                // Place Siphon
                CustomBlockManager.place(world, placePos, stack, Blocks.COBBLED_DEEPSLATE_SLAB, null);

                world.playSound(null, placePos, BlockSoundGroup.STONE.getPlaceSound(), SoundCategory.BLOCKS, 1f, 1f);

                if (!player.isCreative()) {
                    stack.decrement(1);
                }
            }

            return ActionResult.SUCCESS;
        });

        // Break Logic
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            if (state.getBlock() == Blocks.COBBLED_DEEPSLATE_SLAB) {
                // Pass SIPHON_MODEL_ID
                if (CustomBlockManager.onBreak(world, pos, state, player, SIPHON_MODEL_ID, BlockSoundGroup.STONE, null, null)) {
                    return false;
                }
            }
            return true;
        });
    }

    private static boolean isInteractable(BlockState state, net.minecraft.world.World world, BlockPos pos) {
        return state.createScreenHandlerFactory(world, pos) != null ||
               state.getBlock() instanceof net.minecraft.block.DoorBlock ||
               state.getBlock() instanceof net.minecraft.block.TrapdoorBlock ||
               state.getBlock() instanceof net.minecraft.block.FenceGateBlock ||
               state.getBlock() instanceof net.minecraft.block.ButtonBlock ||
               state.getBlock() instanceof net.minecraft.block.LeverBlock;
    }

    private static int getTotalExperience(PlayerEntity player) {
        int level = player.experienceLevel;
        float progress = player.experienceProgress;

        int totalXp = (int) (progress * player.getNextLevelExperience());
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


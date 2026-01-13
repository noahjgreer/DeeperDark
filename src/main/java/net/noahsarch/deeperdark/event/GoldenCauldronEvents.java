package net.noahsarch.deeperdark.event;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.noahsarch.deeperdark.util.CustomBlockManager;

public class GoldenCauldronEvents {
    // Defines the golden cauldron model id (data component)
    public static final Identifier GOLDEN_CAULDRON_ITEM_MODEL_ID = Identifier.of("minecraft", "golden_cauldron_item");
    public static final Identifier GOLDEN_CAULDRON_BLOCK_MODEL_ID = Identifier.of("minecraft", "golden_cauldron");

    public static void register() {
        // Shine Particles
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_WORLD_TICK.register(world -> {
            if (world.getTime() % 10 != 0) return; // Run every 10 ticks

            for (net.minecraft.server.network.ServerPlayerEntity player : world.getPlayers()) {
                Box box = player.getBoundingBox().expand(32); // Scan around players
                world.getEntitiesByClass(net.minecraft.entity.decoration.DisplayEntity.ItemDisplayEntity.class, box, entity -> {
                    ItemStack stack = entity.getItemStack();
                    Identifier modelId = stack.get(DataComponentTypes.ITEM_MODEL);
                    return modelId != null && modelId.equals(GOLDEN_CAULDRON_BLOCK_MODEL_ID);
                }).forEach(entity -> {
                    if (world instanceof net.minecraft.server.world.ServerWorld serverWorld && serverWorld.random.nextFloat() < 0.3f) {
                        // Spread particles across the block
                        double offsetX = (serverWorld.random.nextDouble() - 0.5) * 0.8;
                        double offsetY = (serverWorld.random.nextDouble() - 0.5) * 0.8;
                        double offsetZ = (serverWorld.random.nextDouble() - 0.5) * 0.8;

                        serverWorld.spawnParticles(
                            ParticleTypes.END_ROD,
                            entity.getX() + offsetX, entity.getY() + 0.5 + offsetY, entity.getZ() + offsetZ,
                            1, 0, 0, 0, 0.01
                        );
                    }
                });
            }
        });

        // Placement Logic
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            ItemStack stack = player.getStackInHand(hand);
            if (stack.isEmpty()) return ActionResult.PASS;

            // Check for item_model component
            Identifier modelId = stack.get(DataComponentTypes.ITEM_MODEL);
            if (modelId == null || !modelId.equals(GOLDEN_CAULDRON_ITEM_MODEL_ID)) {
                return ActionResult.PASS;
            }

            BlockPos pos = hitResult.getBlockPos();
            BlockState state = world.getBlockState(pos);
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
                // Place Cauldron with Display
                if (CustomBlockManager.place(world, placePos, stack, Blocks.CAULDRON, display -> {
                    // Override the item stack on the display entity to use the block model
                    ItemStack blockStack = stack.copy();
                    blockStack.setCount(1);
                    blockStack.set(DataComponentTypes.ITEM_MODEL, GOLDEN_CAULDRON_BLOCK_MODEL_ID);
                    display.setItemStack(blockStack);
                })) {
                    // Sound
                    world.playSound(null, placePos, BlockSoundGroup.METAL.getPlaceSound(), SoundCategory.BLOCKS, 1f, 1f);

                    if (!player.isCreative()) {
                        stack.decrement(1);
                    }
                }
            }

            return ActionResult.SUCCESS;
        });

        // Break Logic
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            if (state.getBlock() == Blocks.CAULDRON) {
                // Custom break logic with golden particles
                Runnable particleFX = () -> {
                    if (world instanceof net.minecraft.server.world.ServerWorld serverWorld) {
                        serverWorld.spawnParticles(
                            new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.GOLD_BLOCK.getDefaultState()),
                            pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                            20, // count
                            0.25, 0.25, 0.25, // spread
                            0.0 // speed
                        );
                    }
                };

                // Check for block model ID since that's what's in the world
                if (CustomBlockManager.onBreak(world, pos, state, player, GOLDEN_CAULDRON_BLOCK_MODEL_ID, BlockSoundGroup.METAL, particleFX, stack -> {
                    ItemStack drop = stack.copy();
                    drop.set(DataComponentTypes.ITEM_MODEL, GOLDEN_CAULDRON_ITEM_MODEL_ID);
                    return drop;
                })) {
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

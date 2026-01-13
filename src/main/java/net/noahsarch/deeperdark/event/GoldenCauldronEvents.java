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

        // Intercept cauldron interactions to preserve golden cauldrons
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            BlockPos pos = hitResult.getBlockPos();
            BlockState state = world.getBlockState(pos);

            // Check if this is a golden cauldron (has our custom display entity)
            if (state.getBlock() == Blocks.CAULDRON || state.getBlock() == Blocks.WATER_CAULDRON ||
                state.getBlock() == Blocks.LAVA_CAULDRON || state.getBlock() == Blocks.POWDER_SNOW_CAULDRON) {

                if (!world.isClient() && world instanceof net.minecraft.server.world.ServerWorld) {
                    // Check if there's a golden cauldron display entity here
                    Box box = new Box(pos);
                    var displays = world.getEntitiesByClass(
                        net.minecraft.entity.decoration.DisplayEntity.ItemDisplayEntity.class, box, entity -> {
                            ItemStack stack = entity.getItemStack();
                            Identifier modelId = stack.get(DataComponentTypes.ITEM_MODEL);
                            return modelId != null && modelId.equals(GOLDEN_CAULDRON_BLOCK_MODEL_ID);
                        });

                    if (displays.size() > 0) {
                        // This is a golden cauldron - handle interaction ourselves
                        ItemStack heldStack = player.getStackInHand(hand);

                        // Allow bucket interactions but keep it as a regular cauldron
                        if (heldStack.isOf(net.minecraft.item.Items.WATER_BUCKET) ||
                            heldStack.isOf(net.minecraft.item.Items.LAVA_BUCKET) ||
                            heldStack.isOf(net.minecraft.item.Items.POWDER_SNOW_BUCKET)) {

                            // Just make sure the base block stays as CAULDRON
                            // The vanilla behavior will try to change it, so we prevent that
                            // by returning SUCCESS early, then manually placing water/lava

                            if (heldStack.isOf(net.minecraft.item.Items.WATER_BUCKET)) {
                                if (state.getBlock() != Blocks.WATER_CAULDRON) {
                                    world.setBlockState(pos, Blocks.WATER_CAULDRON.getDefaultState()
                                        .with(net.minecraft.block.LeveledCauldronBlock.LEVEL, 3));
                                    world.playSound(null, pos, net.minecraft.sound.SoundEvents.ITEM_BUCKET_EMPTY,
                                        net.minecraft.sound.SoundCategory.BLOCKS, 1.0f, 1.0f);

                                    if (!player.isCreative()) {
                                        player.setStackInHand(hand, new ItemStack(net.minecraft.item.Items.BUCKET));
                                    }
                                    return ActionResult.SUCCESS;
                                }
                            } else if (heldStack.isOf(net.minecraft.item.Items.LAVA_BUCKET)) {
                                if (state.getBlock() != Blocks.LAVA_CAULDRON) {
                                    world.setBlockState(pos, Blocks.LAVA_CAULDRON.getDefaultState());
                                    world.playSound(null, pos, net.minecraft.sound.SoundEvents.ITEM_BUCKET_EMPTY_LAVA,
                                        net.minecraft.sound.SoundCategory.BLOCKS, 1.0f, 1.0f);

                                    if (!player.isCreative()) {
                                        player.setStackInHand(hand, new ItemStack(net.minecraft.item.Items.BUCKET));
                                    }
                                    return ActionResult.SUCCESS;
                                }
                            }
                        }
                        // For bucket emptying (taking water out), we need to track the level
                        else if (heldStack.isOf(net.minecraft.item.Items.BUCKET)) {
                            if (state.getBlock() == Blocks.WATER_CAULDRON &&
                                state.get(net.minecraft.block.LeveledCauldronBlock.LEVEL) == 3) {
                                world.setBlockState(pos, Blocks.CAULDRON.getDefaultState());
                                world.playSound(null, pos, net.minecraft.sound.SoundEvents.ITEM_BUCKET_FILL,
                                    net.minecraft.sound.SoundCategory.BLOCKS, 1.0f, 1.0f);

                                if (!player.isCreative()) {
                                    player.setStackInHand(hand, new ItemStack(net.minecraft.item.Items.WATER_BUCKET));
                                }
                                return ActionResult.SUCCESS;
                            } else if (state.getBlock() == Blocks.LAVA_CAULDRON) {
                                world.setBlockState(pos, Blocks.CAULDRON.getDefaultState());
                                world.playSound(null, pos, net.minecraft.sound.SoundEvents.ITEM_BUCKET_FILL_LAVA,
                                    net.minecraft.sound.SoundCategory.BLOCKS, 1.0f, 1.0f);

                                if (!player.isCreative()) {
                                    player.setStackInHand(hand, new ItemStack(net.minecraft.item.Items.LAVA_BUCKET));
                                }
                                return ActionResult.SUCCESS;
                            }
                        }
                    }
                }
            }

            return ActionResult.PASS;
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

            if (!world.isClient()) {
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
            if (state.getBlock() == Blocks.CAULDRON || state.getBlock() == Blocks.WATER_CAULDRON ||
                state.getBlock() == Blocks.LAVA_CAULDRON || state.getBlock() == Blocks.POWDER_SNOW_CAULDRON) {
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
                return !CustomBlockManager.onBreak(world, pos, state, player, GOLDEN_CAULDRON_BLOCK_MODEL_ID, BlockSoundGroup.METAL, particleFX, stack -> {
                    ItemStack drop = stack.copy();
                    drop.set(DataComponentTypes.ITEM_MODEL, GOLDEN_CAULDRON_ITEM_MODEL_ID);
                    return drop;
                });
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

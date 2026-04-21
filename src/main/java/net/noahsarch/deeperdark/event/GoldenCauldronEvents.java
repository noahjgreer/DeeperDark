package net.noahsarch.deeperdark.event;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.resources.Identifier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.noahsarch.deeperdark.util.CustomBlockManager;


public class GoldenCauldronEvents {
    // Defines the golden cauldron model id (data component)
    public static final Identifier GOLDEN_CAULDRON_ITEM_MODEL_ID = Identifier.fromNamespaceAndPath("minecraft", "golden_cauldron_item");
    public static final Identifier GOLDEN_CAULDRON_BLOCK_MODEL_ID = Identifier.fromNamespaceAndPath("minecraft", "golden_cauldron");

    public static void register() {
        // Shine Particles
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_WORLD_TICK.register(world -> {
            if (world.getTime() % 10 != 0) return; // Run every 10 ticks

            for (net.minecraft.server.level.ServerPlayer player : world.getPlayers()) {
                AABB box = player.getBoundingBox().expand(32); // Scan around players
                world.getEntitiesByClass(net.minecraft.world.entity.Display.ItemDisplay.class, box, entity -> {
                    ItemStack stack = entity.getItemStack();
                    Identifier modelId = stack.get(DataComponents.ITEM_MODEL);
                    return modelId != null && modelId.equals(GOLDEN_CAULDRON_BLOCK_MODEL_ID);
                }).forEach(entity -> {
                    if (world instanceof net.minecraft.server.level.ServerLevel serverWorld && serverWorld.random.nextFloat() < 0.3f) {
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

                if (!world.isClient() && world instanceof net.minecraft.server.level.ServerLevel) {
                    // Check if there's a golden cauldron display entity here
                    AABB box = new AABB(pos);
                    var displays = world.getEntitiesByClass(
                        net.minecraft.world.entity.Display.ItemDisplay.class, box, entity -> {
                            ItemStack stack = entity.getItemStack();
                            Identifier modelId = stack.get(DataComponents.ITEM_MODEL);
                            return modelId != null && modelId.equals(GOLDEN_CAULDRON_BLOCK_MODEL_ID);
                        });

                    if (displays.size() > 0) {
                        // This is a golden cauldron - handle interaction ourselves
                        ItemStack heldStack = player.getStackInHand(hand);

                        // Allow bucket interactions but keep it as a regular cauldron
                        if (heldStack.isOf(net.minecraft.world.item.Items.WATER_BUCKET) ||
                            heldStack.isOf(net.minecraft.world.item.Items.LAVA_BUCKET) ||
                            heldStack.isOf(net.minecraft.world.item.Items.POWDER_SNOW_BUCKET)) {

                            // Just make sure the base block stays as CAULDRON
                            // The vanilla behavior will try to change it, so we prevent that
                            // by returning SUCCESS early, then manually placing water/lava

                            if (heldStack.isOf(net.minecraft.world.item.Items.WATER_BUCKET)) {
                                if (state.getBlock() != Blocks.WATER_CAULDRON) {
                                    world.setBlockState(pos, Blocks.WATER_CAULDRON.getDefaultState()
                                        .with(net.minecraft.world.level.block.LeveledCauldronBlock.LEVEL, 3));
                                    world.playSound(null, pos, net.minecraft.sounds.SoundEvents.ITEM_BUCKET_EMPTY,
                                        net.minecraft.sounds.SoundSource.BLOCKS, 1.0f, 1.0f);

                                    if (!player.isCreative()) {
                                        player.setStackInHand(hand, new ItemStack(net.minecraft.world.item.Items.BUCKET));
                                    }
                                    return InteractionResult.SUCCESS;
                                }
                            } else if (heldStack.isOf(net.minecraft.world.item.Items.LAVA_BUCKET)) {
                                if (state.getBlock() != Blocks.LAVA_CAULDRON) {
                                    world.setBlockState(pos, Blocks.LAVA_CAULDRON.getDefaultState());
                                    world.playSound(null, pos, net.minecraft.sounds.SoundEvents.ITEM_BUCKET_EMPTY_LAVA,
                                        net.minecraft.sounds.SoundSource.BLOCKS, 1.0f, 1.0f);

                                    if (!player.isCreative()) {
                                        player.setStackInHand(hand, new ItemStack(net.minecraft.world.item.Items.BUCKET));
                                    }
                                    return InteractionResult.SUCCESS;
                                }
                            }
                        }
                        // For bucket emptying (taking water out), we need to track the level
                        else if (heldStack.isOf(net.minecraft.world.item.Items.BUCKET)) {
                            if (state.getBlock() == Blocks.WATER_CAULDRON &&
                                state.get(net.minecraft.world.level.block.LeveledCauldronBlock.LEVEL) == 3) {
                                world.setBlockState(pos, Blocks.CAULDRON.getDefaultState());
                                world.playSound(null, pos, net.minecraft.sounds.SoundEvents.ITEM_BUCKET_FILL,
                                    net.minecraft.sounds.SoundSource.BLOCKS, 1.0f, 1.0f);

                                if (!player.isCreative()) {
                                    player.setStackInHand(hand, new ItemStack(net.minecraft.world.item.Items.WATER_BUCKET));
                                }
                                return InteractionResult.SUCCESS;
                            } else if (state.getBlock() == Blocks.LAVA_CAULDRON) {
                                world.setBlockState(pos, Blocks.CAULDRON.getDefaultState());
                                world.playSound(null, pos, net.minecraft.sounds.SoundEvents.ITEM_BUCKET_FILL_LAVA,
                                    net.minecraft.sounds.SoundSource.BLOCKS, 1.0f, 1.0f);

                                if (!player.isCreative()) {
                                    player.setStackInHand(hand, new ItemStack(net.minecraft.world.item.Items.LAVA_BUCKET));
                                }
                                return InteractionResult.SUCCESS;
                            }
                        }
                    }
                }
            }

            return InteractionResult.PASS;
        });

        // Placement Logic
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            ItemStack stack = player.getStackInHand(hand);
            if (stack.isEmpty()) return InteractionResult.PASS;

            // Check for item_model component
            Identifier modelId = stack.get(DataComponents.ITEM_MODEL);
            if (modelId == null || !modelId.equals(GOLDEN_CAULDRON_ITEM_MODEL_ID)) {
                return InteractionResult.PASS;
            }

            BlockPos pos = hitResult.getBlockPos();
            BlockState state = world.getBlockState(pos);
            Direction side = hitResult.getSide();
            BlockPos placePos = pos.offset(side);

            // Check if place position is replaceable.
            if (!world.getBlockState(placePos).canReplace(new UseOnContext(player, hand, stack, hitResult))) {
                 return InteractionResult.PASS;
            }

            // Prevent accidental placement when using interactable blocks
            if (!player.isSneaking() && isInteractable(state, world, pos)) {
                 return InteractionResult.PASS;
            }

            if (!world.isClient()) {
                // Place Cauldron with Display
                if (CustomBlockManager.place(world, placePos, stack, Blocks.CAULDRON, display -> {
                    // Override the item stack on the display entity to use the block model
                    ItemStack blockStack = stack.copy();
                    blockStack.setCount(1);
                    blockStack.set(DataComponents.ITEM_MODEL, GOLDEN_CAULDRON_BLOCK_MODEL_ID);
                    display.setItemStack(blockStack);
                })) {
                    // Sound
                    world.playSound(null, placePos, SoundType.METAL.getPlaceSound(), SoundSource.BLOCKS, 1f, 1f);

                    if (!player.isCreative()) {
                        stack.decrement(1);
                    }
                }
            }

            return InteractionResult.SUCCESS;
        });

        // Break Logic
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            if (state.getBlock() == Blocks.CAULDRON || state.getBlock() == Blocks.WATER_CAULDRON ||
                state.getBlock() == Blocks.LAVA_CAULDRON || state.getBlock() == Blocks.POWDER_SNOW_CAULDRON) {
                // Custom break logic with golden particles
                Runnable particleFX = () -> {
                    if (world instanceof net.minecraft.server.level.ServerLevel serverWorld) {
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
                return !CustomBlockManager.onBreak(world, pos, state, player, GOLDEN_CAULDRON_BLOCK_MODEL_ID, SoundType.METAL, particleFX, stack -> {
                    ItemStack drop = stack.copy();
                    drop.set(DataComponents.ITEM_MODEL, GOLDEN_CAULDRON_ITEM_MODEL_ID);
                    return drop;
                });
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

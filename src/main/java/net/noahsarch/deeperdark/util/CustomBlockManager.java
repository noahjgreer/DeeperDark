package net.noahsarch.deeperdark.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.decoration.DisplayEntity.ItemDisplayEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;
import java.util.function.Consumer;

public class CustomBlockManager {

    /**
     * Tries to place a custom block logic (placing base block + spawning display).
     * Does not handle permission checks or stack decrements, only the world modification.
     * @return true if successfully placed, false if blocked.
     */
    public static boolean place(World world, BlockPos pos, ItemStack stack, Block baseBlock, Consumer<ItemDisplayEntity> displayConfigurator) {
        if (world.isClient()) return false;

        // Collision Check: Prevent placing if entity is inside
        if (!world.canPlace(baseBlock.getDefaultState(), pos, ShapeContext.absent())) {
            return false; // Can't place
        }

        // Place Base Block
        world.setBlockState(pos, baseBlock.getDefaultState());

        // Prepare stack logic for tracker
        ItemStack displayStack = stack.copy();
        displayStack.setCount(1);

        // We need to know the final stack *after* displayConfigurator might have touched it.
        // But displayConfigurator works on an Entity. The tracker stores Data.
        // So we create a dummy entity first? No, we can just run the logic manually or assume strictness.

        // Actually, CustomBlockTracker spawns entities itself now.
        // So we register to tracker, and ask tracker to ensure entity.

        if (world instanceof ServerWorld serverWorld) {
            CustomBlockTracker tracker = CustomBlockTracker.get(serverWorld);

            ItemDisplayEntity display = EntityType.ITEM_DISPLAY.create(world, SpawnReason.MOB_SUMMONED);
            if (display != null) {
                // Default center pos
                display.refreshPositionAndAngles(pos.getX() + 0.5, pos.getY() + 0.5005, pos.getZ() + 0.5, 0, 0);

                ItemStack initialStack = stack.copy();
                initialStack.setCount(1);
                display.setItemStack(initialStack);
                display.setItemDisplayContext(ItemDisplayContext.HEAD);

                // Default transformation
                AffineTransformation transform = new AffineTransformation(
                    new Vector3f(0, 0, 0),
                    new Quaternionf(0, 0, 0, 1),
                    new Vector3f(1.01f, 1.01f, 1.01f),
                    new Quaternionf(0, 0, 0, 1)
                );
                display.setTransformation(transform);

                if (displayConfigurator != null) {
                    displayConfigurator.accept(display);
                }

                // Set initial brightness based on surroundings
                int maxBlock = 0;
                int maxSky = 0;
                for (net.minecraft.util.math.Direction dir : net.minecraft.util.math.Direction.values()) {
                    BlockPos neighbor = pos.offset(dir);
                    int b = world.getLightLevel(net.minecraft.world.LightType.BLOCK, neighbor);
                    int s = world.getLightLevel(net.minecraft.world.LightType.SKY, neighbor);
                    if (b > maxBlock) maxBlock = b;
                    if (s > maxSky) maxSky = s;
                }
                display.setBrightness(new net.minecraft.entity.decoration.Brightness(maxBlock, maxSky));

                world.spawnEntity(display);

                // Now register to tracker with the final state
                tracker.addBlock(pos, baseBlock, display.getItemStack(), transform);
            }
        }
        return true;
    }

    /**
     * Checked when a block is broken. If it matches the custom block criteria, it handles cleanup and drops.
     * @param dropTransformer Optional function to modify the ItemStack before dropping (e.g. changing model ID back to item version).
     * @return true if the event was handled (custom block broken), false otherwise.
     */
    public static boolean onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player, Identifier targetModelId, BlockSoundGroup breakSound, Runnable customAction, java.util.function.UnaryOperator<ItemStack> dropTransformer) {

        if (!world.isClient() && world instanceof ServerWorld serverWorld) {
            CustomBlockTracker tracker = CustomBlockTracker.get(serverWorld);
            if (tracker.hasBlock(pos)) {
                // Remove from tracker
                tracker.removeBlock(pos);
                // Tracker doesn't auto-remove entity on removeBlock call (it just stops tracking), so we continue to remove it manually below
            }
        }

        // Use strict box to prevent finding neighbors (e.g. block below)
        Box box = new Box(pos);
        List<ItemDisplayEntity> displays = world.getEntitiesByClass(ItemDisplayEntity.class, box, entity -> true);

        for (ItemDisplayEntity display : displays) {
             ItemStack stack = display.getItemStack();
             Identifier modelId = stack.get(DataComponentTypes.ITEM_MODEL);

             if (modelId != null && modelId.equals(targetModelId)) {
                 if (!world.isClient()) {
                     // Set air FIRST to prevent vanilla break sound/particles
                     world.setBlockState(pos, net.minecraft.block.Blocks.AIR.getDefaultState(), 2);

                     // Drops
                     if (!player.isCreative() && player.canHarvest(state)) {
                         ItemStack dropStack = stack;
                         if (dropTransformer != null) {
                             dropStack = dropTransformer.apply(stack);
                         }
                         net.minecraft.block.Block.dropStack(world, pos, dropStack);
                     }

                     // Custom logic (like particles)
                     if (customAction != null) {
                         customAction.run();
                     }

                     // Play break sound
                     if (breakSound != null) {
                        world.playSound(null, pos, breakSound.getBreakSound(), SoundCategory.BLOCKS,
                            breakSound.getVolume(), breakSound.getPitch());
                     }
                 }

                 // Clean up entity
                 display.discard();

                 return true;
             }
        }
        return false;
    }
}




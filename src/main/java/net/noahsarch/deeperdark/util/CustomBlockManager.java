package net.noahsarch.deeperdark.util;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.Display.ItemDisplay;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.sounds.SoundSource;
import net.minecraft.resources.Identifier;
import com.mojang.math.Transformation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.util.Brightness;

public class CustomBlockManager {

    /**
     * Tries to place a custom block logic (placing base block + spawning display).
     * Does not handle permission checks or stack decrements, only the world modification.
     * @return true if successfully placed, false if blocked.
     */
    public static boolean place(Level world, BlockPos pos, ItemStack stack, Block baseBlock, Consumer<ItemDisplay> displayConfigurator) {
        if (world.isClientSide()) return false;

        // Collision Check: Prevent placing if entity is inside
        if (!world.isUnobstructed(baseBlock.defaultBlockState(), pos, CollisionContext.empty())) {
            return false; // Can't place
        }

        // Place Base Block
        world.setBlock(pos, baseBlock.defaultBlockState(), 3);

        // Prepare stack logic for tracker
        ItemStack displayStack = stack.copy();
        displayStack.setCount(1);

        // We need to know the final stack *after* displayConfigurator might have touched it.
        // But displayConfigurator works on an Entity. The tracker stores Data.
        // So we create a dummy entity first? No, we can just run the logic manually or assume strictness.

        // Actually, CustomBlockTracker spawns entities itself now.
        // So we register to tracker, and ask tracker to ensure entity.

        if (world instanceof ServerLevel serverWorld) {
            CustomBlockTracker tracker = CustomBlockTracker.get(serverWorld);

            ItemDisplay display = EntityType.ITEM_DISPLAY.create(world, EntitySpawnReason.MOB_SUMMONED);
            if (display != null) {
                // Default center pos
                display.setPos(pos.getX() + 0.5, pos.getY() + 0.5005, pos.getZ() + 0.5);

                ItemStack initialStack = stack.copy();
                initialStack.setCount(1);
                display.setItemStack(initialStack);
                display.setItemTransform(ItemDisplayContext.HEAD);

                // Default transformation
                Transformation transform = new Transformation(
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
                for (net.minecraft.core.Direction dir : net.minecraft.core.Direction.values()) {
                    BlockPos neighbor = pos.relative(dir);
                    int b = world.getBrightness(net.minecraft.world.level.LightLayer.BLOCK, neighbor);
                    int s = world.getBrightness(net.minecraft.world.level.LightLayer.SKY, neighbor);
                    if (b > maxBlock) maxBlock = b;
                    if (s > maxSky) maxSky = s;
                }
                display.setBrightnessOverride(new net.minecraft.util.Brightness(maxBlock, maxSky));

                serverWorld.addFreshEntity(display);

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
    public static boolean onBreak(Level world, BlockPos pos, BlockState state, Player player, Identifier targetModelId, SoundType breakSound, Runnable customAction, java.util.function.UnaryOperator<ItemStack> dropTransformer) {

        if (!world.isClientSide() && world instanceof ServerLevel serverWorld) {
            CustomBlockTracker tracker = CustomBlockTracker.get(serverWorld);
            if (tracker.hasBlock(pos)) {
                // Remove from tracker
                tracker.removeBlock(pos);
                // Tracker doesn't auto-remove entity on removeBlock call (it just stops tracking), so we continue to remove it manually below
            }
        }

        // Use strict box to prevent finding neighbors (e.g. block below)
        AABB box = new AABB(pos);
        List<ItemDisplay> displays = world.getEntitiesOfClass(ItemDisplay.class, box, entity -> true);

        for (ItemDisplay display : displays) {
             ItemStack stack = display.getItemStack();
             Identifier modelId = stack.get(DataComponents.ITEM_MODEL);

             if (modelId != null && modelId.equals(targetModelId)) {
                 if (!world.isClientSide()) {
                     // Drops
                     if (!player.isCreative() && player.hasCorrectToolForDrops(state)) {
                         ItemStack dropStack = stack;
                         if (dropTransformer != null) {
                             dropStack = dropTransformer.apply(stack);
                         }
                         net.minecraft.world.level.block.Block.popResource(world, pos, dropStack);
                     }

                     // Custom logic (like particles)
                     if (customAction != null) {
                         customAction.run();
                     }

                     // Set air to prevent vanilla drop logic
                     world.setBlock(pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 3);
                 }

                 // Clean up entity
                 display.discard();

                 return true;
             }
        }
        return false;
    }
}


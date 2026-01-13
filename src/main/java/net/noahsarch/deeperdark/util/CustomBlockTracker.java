package net.noahsarch.deeperdark.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.decoration.DisplayEntity.ItemDisplayEntity;
import net.minecraft.entity.decoration.Brightness;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.LightType;
import net.minecraft.entity.decoration.Brightness;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;

public class CustomBlockTracker {
    private static final Map<ServerWorld, CustomBlockTracker> INSTANCES = new WeakHashMap<>();

    private final Map<BlockPos, CustomBlockData> blocks = new HashMap<>();

    public static CustomBlockTracker get(ServerWorld world) {
        return INSTANCES.computeIfAbsent(world, w -> new CustomBlockTracker());
    }

    public CustomBlockTracker() {}

    public void addBlock(BlockPos pos, Block baseBlock, ItemStack displayStack, AffineTransformation transform) {
        blocks.put(pos, new CustomBlockData(baseBlock, displayStack, transform));
    }

    public void removeBlock(BlockPos pos) {
        blocks.remove(pos);
    }

    public boolean hasBlock(BlockPos pos) {
        return blocks.containsKey(pos);
    }

    public void tick(ServerWorld world) {
        Iterator<Map.Entry<BlockPos, CustomBlockData>> it = blocks.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<BlockPos, CustomBlockData> entry = it.next();
            BlockPos pos = entry.getKey();
            CustomBlockData data = entry.getValue();

            if (!world.isChunkLoaded(pos)) continue;

            BlockState state = world.getBlockState(pos);
            if (!state.isOf(data.baseBlock)) {
                 removeEntity(world, pos);
                 it.remove();
                 continue;
            }

            ensureEntity(world, pos, data);
        }
    }

    private void removeEntity(ServerWorld world, BlockPos pos) {
        Box box = new Box(pos); // Strict box
        List<ItemDisplayEntity> entities = world.getEntitiesByClass(ItemDisplayEntity.class, box, entity -> true);
        for (ItemDisplayEntity entity : entities) {
            entity.discard();
        }
    }

    private void ensureEntity(ServerWorld world, BlockPos pos, CustomBlockData data) {
        Box box = new Box(pos); // Strict box
        List<ItemDisplayEntity> entities = world.getEntitiesByClass(ItemDisplayEntity.class, box, entity -> !entity.isRemoved());

        if (entities.isEmpty()) {
            spawnEntity(world, pos, data);
        } else {
            // Update brightness
            net.minecraft.entity.decoration.Brightness brightness = getBrightness(world, pos);
            entities.get(0).setBrightness(brightness);

            // Removing duplicates
            if (entities.size() > 1) {
                for (int i = 1; i < entities.size(); i++) {
                    entities.get(i).discard();
                }
            }
        }
    }

    private net.minecraft.entity.decoration.Brightness getBrightness(ServerWorld world, BlockPos pos) {
        int maxBlock = 0;
        int maxSky = 0;

        for (Direction dir : Direction.values()) {
            BlockPos neighbor = pos.offset(dir);
            int block = world.getLightLevel(net.minecraft.world.LightType.BLOCK, neighbor);
            int sky = world.getLightLevel(net.minecraft.world.LightType.SKY, neighbor);
            if (block > maxBlock) maxBlock = block;
            if (sky > maxSky) maxSky = sky;
        }
        return new net.minecraft.entity.decoration.Brightness(maxBlock, maxSky);
    }

    private void spawnEntity(ServerWorld world, BlockPos pos, CustomBlockData data) {
        ItemDisplayEntity display = EntityType.ITEM_DISPLAY.create(world, SpawnReason.MOB_SUMMONED);
        if (display != null) {
            display.refreshPositionAndAngles(pos.getX() + 0.5, pos.getY() + 0.5005, pos.getZ() + 0.5, 0, 0);

            ItemStack displayStack = data.displayStack.copy();
            displayStack.setCount(1);
            display.setItemStack(displayStack);
            display.setItemDisplayContext(ItemDisplayContext.HEAD);

            if (data.transform != null) {
                display.setTransformation(data.transform);
            } else {
                 display.setTransformation(new AffineTransformation(
                    new Vector3f(0, 0, 0),
                    new Quaternionf(0, 0, 0, 1),
                    new Vector3f(1.01f, 1.01f, 1.01f),
                    new Quaternionf(0, 0, 0, 1)
                ));
            }

            display.setBrightness(getBrightness(world, pos));

            world.spawnEntity(display);
        }
    }

    private static class CustomBlockData {
        final Block baseBlock;
        final ItemStack displayStack;
        final AffineTransformation transform;

        CustomBlockData(Block baseBlock, ItemStack displayStack, AffineTransformation transform) {
            this.baseBlock = baseBlock;
            this.displayStack = displayStack;
            this.transform = transform;
        }
    }
}


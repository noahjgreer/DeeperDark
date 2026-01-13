package net.noahsarch.deeperdark.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.decoration.DisplayEntity.ItemDisplayEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtIo;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class CustomBlockTracker {
    private static final Map<ServerWorld, CustomBlockTracker> INSTANCES = new WeakHashMap<>();
    private static final String FILE_NAME = "deeperdark_custom_blocks.dat";

    private final ServerWorld world;
    private final Map<BlockPos, CustomBlockData> blocks = new HashMap<>();
    private boolean needsSave = false;

    public static CustomBlockTracker get(ServerWorld world) {
        return INSTANCES.computeIfAbsent(world, CustomBlockTracker::new);
    }

    private CustomBlockTracker(ServerWorld world) {
        this.world = world;
        load();
    }

    private File getSaveFile() {
        try {
            File worldDir = world.getServer().getSavePath(net.minecraft.util.WorldSavePath.ROOT).toFile();
            File dataDir = new File(worldDir, "data");
            if (!dataDir.exists()) {
                if (!dataDir.mkdirs()) {
                    net.noahsarch.deeperdark.Deeperdark.LOGGER.warn("Failed to create data directory");
                }
            }
            return new File(dataDir, FILE_NAME);
        } catch (Exception e) {
            net.noahsarch.deeperdark.Deeperdark.LOGGER.error("Failed to get save file", e);
            return null;
        }
    }

    private void load() {
        File file = getSaveFile();
        if (file == null || !file.exists()) {
            return;
        }

        try {
            NbtCompound nbt = NbtIo.read(file.toPath());
            if (nbt == null) return;

            Optional<NbtList> blockListOpt = nbt.getList("Blocks");
            if (blockListOpt.isEmpty()) return;

            NbtList blockList = blockListOpt.get();
            for (int i = 0; i < blockList.size(); i++) {
                NbtCompound blockNbt = blockList.getCompound(i).orElse(null);
                if (blockNbt == null) continue;

                long posLong = blockNbt.getLong("Pos").orElse(0L);
                if (posLong == 0L) continue;

                BlockPos pos = BlockPos.fromLong(posLong);
                CustomBlockData data = CustomBlockData.fromNbt(blockNbt);
                if (data != null) {
                    blocks.put(pos, data);
                }
            }
        } catch (IOException e) {
            net.noahsarch.deeperdark.Deeperdark.LOGGER.error("Failed to load custom block data", e);
        }
    }

    public void save() {
        if (!needsSave) return;

        File file = getSaveFile();
        if (file == null) return;

        try {
            NbtCompound nbt = new NbtCompound();
            NbtList blockList = new NbtList();

            for (Map.Entry<BlockPos, CustomBlockData> entry : blocks.entrySet()) {
                NbtCompound blockNbt = new NbtCompound();
                blockNbt.putLong("Pos", entry.getKey().asLong());
                entry.getValue().writeNbt(blockNbt);
                blockList.add(blockNbt);
            }

            nbt.put("Blocks", blockList);

            NbtIo.write(nbt, file.toPath());
            needsSave = false;
        } catch (IOException e) {
            net.noahsarch.deeperdark.Deeperdark.LOGGER.error("Failed to save custom block data", e);
        }
    }

    public void addBlock(BlockPos pos, Block baseBlock, ItemStack displayStack, AffineTransformation transform) {
        blocks.put(pos.toImmutable(), new CustomBlockData(baseBlock, displayStack, transform));
        needsSave = true;
    }

    public void removeBlock(BlockPos pos) {
        blocks.remove(pos);
        needsSave = true;
    }

    public boolean hasBlock(BlockPos pos) {
        return blocks.containsKey(pos);
    }

    public CustomBlockData getBlockData(BlockPos pos) {
        return blocks.get(pos);
    }

    public void tick(ServerWorld world) {
        // Save every 5 minutes (6000 ticks)
        if (needsSave && world.getTime() % 6000 == 0) {
            save();
        }

        Iterator<Map.Entry<BlockPos, CustomBlockData>> it = blocks.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<BlockPos, CustomBlockData> entry = it.next();
            BlockPos pos = entry.getKey();
            CustomBlockData data = entry.getValue();

            if (!world.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)) continue;

            BlockState state = world.getBlockState(pos);

            // Special handling for cauldrons - they can change between CAULDRON, WATER_CAULDRON, LAVA_CAULDRON, etc.
            boolean isCauldronVariant = state.isOf(net.minecraft.block.Blocks.CAULDRON) ||
                                       state.isOf(net.minecraft.block.Blocks.WATER_CAULDRON) ||
                                       state.isOf(net.minecraft.block.Blocks.LAVA_CAULDRON) ||
                                       state.isOf(net.minecraft.block.Blocks.POWDER_SNOW_CAULDRON);

            boolean baseCauldron = data.baseBlock == net.minecraft.block.Blocks.CAULDRON;

            // If base is cauldron and current state is any cauldron variant, keep tracking
            // Otherwise use normal base block check
            if (baseCauldron ? !isCauldronVariant : !state.isOf(data.baseBlock)) {
                 removeEntity(world, pos);
                 it.remove();
                 needsSave = true;
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
            entities.getFirst().setBrightness(brightness);

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

            AffineTransformation transform = Objects.requireNonNullElseGet(data.transform, () ->
                new AffineTransformation(
                    new Vector3f(0, 0, 0),
                    new Quaternionf(0, 0, 0, 1),
                    new Vector3f(1.01f, 1.01f, 1.01f),
                    new Quaternionf(0, 0, 0, 1)
                )
            );
            display.setTransformation(transform);

            display.setBrightness(getBrightness(world, pos));

            world.spawnEntity(display);
        }
    }

    public static class CustomBlockData {
        final Block baseBlock;
        final ItemStack displayStack;
        final AffineTransformation transform;

        CustomBlockData(Block baseBlock, ItemStack displayStack, AffineTransformation transform) {
            this.baseBlock = baseBlock;
            this.displayStack = displayStack;
            this.transform = transform;
        }

        public void writeNbt(NbtCompound nbt) {
            // Store block identifier
            Identifier blockId = Registries.BLOCK.getId(baseBlock);
            nbt.putString("Block", blockId.toString());

            // Store display stack - serialize manually
            NbtCompound stackNbt = new NbtCompound();
            Identifier itemId = Registries.ITEM.getId(displayStack.getItem());
            stackNbt.putString("id", itemId.toString());
            stackNbt.putInt("count", displayStack.getCount());

            // Store components if they exist
            if (!displayStack.getComponents().isEmpty()) {
                // Store the model ID if present
                if (displayStack.contains(net.minecraft.component.DataComponentTypes.ITEM_MODEL)) {
                    Identifier modelId = displayStack.get(net.minecraft.component.DataComponentTypes.ITEM_MODEL);
                    if (modelId != null) {
                        stackNbt.putString("model", modelId.toString());
                    }
                }
            }
            nbt.put("DisplayStack", stackNbt);

            // Store transformation
            if (transform != null) {
                NbtCompound transformNbt = new NbtCompound();

                // Get components by creating new vectors to hold the data
                Vector3f translation = new Vector3f();
                transform.getTranslation().get(translation);
                transformNbt.putFloat("tx", translation.x);
                transformNbt.putFloat("ty", translation.y);
                transformNbt.putFloat("tz", translation.z);

                Quaternionf leftRotation = new Quaternionf();
                transform.getLeftRotation().get(leftRotation);
                transformNbt.putFloat("lrx", leftRotation.x);
                transformNbt.putFloat("lry", leftRotation.y);
                transformNbt.putFloat("lrz", leftRotation.z);
                transformNbt.putFloat("lrw", leftRotation.w);

                Vector3f scale = new Vector3f();
                transform.getScale().get(scale);
                transformNbt.putFloat("sx", scale.x);
                transformNbt.putFloat("sy", scale.y);
                transformNbt.putFloat("sz", scale.z);

                Quaternionf rightRotation = new Quaternionf();
                transform.getRightRotation().get(rightRotation);
                transformNbt.putFloat("rrx", rightRotation.x);
                transformNbt.putFloat("rry", rightRotation.y);
                transformNbt.putFloat("rrz", rightRotation.z);
                transformNbt.putFloat("rrw", rightRotation.w);

                nbt.put("Transform", transformNbt);
            }
        }

        public static CustomBlockData fromNbt(NbtCompound nbt) {
            try {
                // Read block
                String blockIdStr = nbt.getString("Block").orElse(null);
                if (blockIdStr == null) return null;

                Identifier blockId = Identifier.tryParse(blockIdStr);
                if (blockId == null) return null;

                Block block = Registries.BLOCK.get(blockId);

                // Read display stack
                NbtCompound stackNbt = nbt.getCompound("DisplayStack").orElse(null);
                if (stackNbt == null) return null;

                String itemIdStr = stackNbt.getString("id").orElse(null);
                if (itemIdStr == null) return null;

                Identifier itemId = Identifier.tryParse(itemIdStr);
                if (itemId == null) return null;

                net.minecraft.item.Item item = Registries.ITEM.get(itemId);
                int count = stackNbt.getInt("count").orElse(1);

                ItemStack displayStack = new ItemStack(item, count);

                // Restore model ID if present
                String modelIdStr = stackNbt.getString("model").orElse(null);
                if (modelIdStr != null) {
                    Identifier modelId = Identifier.tryParse(modelIdStr);
                    if (modelId != null) {
                        displayStack.set(net.minecraft.component.DataComponentTypes.ITEM_MODEL, modelId);
                    }
                }

                // Read transformation
                AffineTransformation transform = null;
                Optional<NbtCompound> transformNbtOpt = nbt.getCompound("Transform");
                if (transformNbtOpt.isPresent()) {
                    NbtCompound transformNbt = transformNbtOpt.get();
                    Vector3f translation = new Vector3f(
                        transformNbt.getFloat("tx").orElse(0f),
                        transformNbt.getFloat("ty").orElse(0f),
                        transformNbt.getFloat("tz").orElse(0f)
                    );
                    Quaternionf leftRotation = new Quaternionf(
                        transformNbt.getFloat("lrx").orElse(0f),
                        transformNbt.getFloat("lry").orElse(0f),
                        transformNbt.getFloat("lrz").orElse(0f),
                        transformNbt.getFloat("lrw").orElse(1f)
                    );
                    Vector3f scale = new Vector3f(
                        transformNbt.getFloat("sx").orElse(1f),
                        transformNbt.getFloat("sy").orElse(1f),
                        transformNbt.getFloat("sz").orElse(1f)
                    );
                    Quaternionf rightRotation = new Quaternionf(
                        transformNbt.getFloat("rrx").orElse(0f),
                        transformNbt.getFloat("rry").orElse(0f),
                        transformNbt.getFloat("rrz").orElse(0f),
                        transformNbt.getFloat("rrw").orElse(1f)
                    );
                    transform = new AffineTransformation(translation, leftRotation, scale, rightRotation);
                }

                return new CustomBlockData(block, displayStack, transform);
            } catch (Exception e) {
                return null;
            }
        }
    }
}


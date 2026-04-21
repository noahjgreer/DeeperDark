package net.noahsarch.deeperdark.util;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.Display.ItemDisplay;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.Identifier;
import com.mojang.math.Transformation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.Direction;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.io.File;
import java.io.IOException;
import java.util.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.util.Brightness;

public class CustomBlockTracker {
    private static final Map<ServerLevel, CustomBlockTracker> INSTANCES = new WeakHashMap<>();
    private static final String FILE_NAME = "deeperdark_custom_blocks.dat";

    private final ServerLevel world;
    private final Map<BlockPos, CustomBlockData> blocks = new HashMap<>();
    private boolean needsSave = false;

    public static CustomBlockTracker get(ServerLevel world) {
        return INSTANCES.computeIfAbsent(world, CustomBlockTracker::new);
    }

    private CustomBlockTracker(ServerLevel world) {
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
            CompoundTag nbt = NbtIo.read(file.toPath());
            if (nbt == null) return;

            Optional<ListTag> blockListOpt = nbt.getList("Blocks");
            if (blockListOpt.isEmpty()) return;

            ListTag blockList = blockListOpt.get();
            for (int i = 0; i < blockList.size(); i++) {
                CompoundTag blockNbt = blockList.getCompound(i).orElse(null);
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
            CompoundTag nbt = new CompoundTag();
            ListTag blockList = new ListTag();

            for (Map.Entry<BlockPos, CustomBlockData> entry : blocks.entrySet()) {
                CompoundTag blockNbt = new CompoundTag();
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

    public void addBlock(BlockPos pos, Block baseBlock, ItemStack displayStack, Transformation transform) {
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

    public void tick(ServerLevel world) {
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
            boolean isCauldronVariant = state.isOf(net.minecraft.world.level.block.Blocks.CAULDRON) ||
                                       state.isOf(net.minecraft.world.level.block.Blocks.WATER_CAULDRON) ||
                                       state.isOf(net.minecraft.world.level.block.Blocks.LAVA_CAULDRON) ||
                                       state.isOf(net.minecraft.world.level.block.Blocks.POWDER_SNOW_CAULDRON);

            boolean baseCauldron = data.baseBlock == net.minecraft.world.level.block.Blocks.CAULDRON;

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

    private void removeEntity(ServerLevel world, BlockPos pos) {
        AABB box = new AABB(pos); // Strict box
        List<ItemDisplayEntity> entities = world.getEntitiesByClass(ItemDisplayEntity.class, box, entity -> true);
        for (ItemDisplayEntity entity : entities) {
            entity.discard();
        }
    }

    private void ensureEntity(ServerLevel world, BlockPos pos, CustomBlockData data) {
        AABB box = new AABB(pos); // Strict box
        List<ItemDisplayEntity> entities = world.getEntitiesByClass(ItemDisplayEntity.class, box, entity -> !entity.isRemoved());

        if (entities.isEmpty()) {
            spawnEntity(world, pos, data);
        } else {
            // Update brightness
            net.minecraft.util.Brightness brightness = getBrightness(world, pos);
            entities.getFirst().setBrightness(brightness);

            // Removing duplicates
            if (entities.size() > 1) {
                for (int i = 1; i < entities.size(); i++) {
                    entities.get(i).discard();
                }
            }
        }
    }

    private net.minecraft.util.Brightness getBrightness(ServerLevel world, BlockPos pos) {
        int maxBlock = 0;
        int maxSky = 0;

        for (Direction dir : Direction.values()) {
            BlockPos neighbor = pos.offset(dir);
            int block = world.getLightLevel(net.minecraft.world.LightType.BLOCK, neighbor);
            int sky = world.getLightLevel(net.minecraft.world.LightType.SKY, neighbor);
            if (block > maxBlock) maxBlock = block;
            if (sky > maxSky) maxSky = sky;
        }
        return new net.minecraft.util.Brightness(maxBlock, maxSky);
    }

    private void spawnEntity(ServerLevel world, BlockPos pos, CustomBlockData data) {
        ItemDisplayEntity display = EntityType.ITEM_DISPLAY.create(world, EntitySpawnReason.MOB_SUMMONED);
        if (display != null) {
            display.refreshPositionAndAngles(pos.getX() + 0.5, pos.getY() + 0.5005, pos.getZ() + 0.5, 0, 0);

            ItemStack displayStack = data.displayStack.copy();
            displayStack.setCount(1);
            display.setItemStack(displayStack);
            display.setItemDisplayContext(ItemDisplayContext.HEAD);

            Transformation transform = Objects.requireNonNullElseGet(data.transform, () ->
                new Transformation(
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
        final Transformation transform;

        CustomBlockData(Block baseBlock, ItemStack displayStack, Transformation transform) {
            this.baseBlock = baseBlock;
            this.displayStack = displayStack;
            this.transform = transform;
        }

        public void writeNbt(CompoundTag nbt) {
            // Store block identifier
            Identifier blockId = Registries.BLOCK.getId(baseBlock);
            nbt.putString("Block", blockId.toString());

            // Store display stack - serialize manually
            CompoundTag stackNbt = new CompoundTag();
            Identifier itemId = Registries.ITEM.getId(displayStack.getItem());
            stackNbt.putString("id", itemId.toString());
            stackNbt.putInt("count", displayStack.getCount());

            // Store components if they exist
            if (!displayStack.getComponents().isEmpty()) {
                // Store the model ID if present
                if (displayStack.contains(net.minecraft.core.component.DataComponents.ITEM_MODEL)) {
                    Identifier modelId = displayStack.get(net.minecraft.core.component.DataComponents.ITEM_MODEL);
                    if (modelId != null) {
                        stackNbt.putString("model", modelId.toString());
                    }
                }

                // Store the item name if present (important for custom names!)
                if (displayStack.contains(net.minecraft.core.component.DataComponents.ITEM_NAME)) {
                    net.minecraft.network.chat.Component itemName = displayStack.get(net.minecraft.core.component.DataComponents.ITEM_NAME);
                    if (itemName != null) {
                        // Get the content to check if it's translatable
                        var content = itemName.getContent();
                        if (content instanceof net.minecraft.network.chat.contents.TranslatableContents translatableContent) {
                            // Store as translation key
                            stackNbt.putString("itemNameKey", translatableContent.getKey());
                            stackNbt.putBoolean("itemNameTranslatable", true);
                        } else {
                            // Store the rendered text content as fallback
                            stackNbt.putString("itemName", itemName.getString());
                            stackNbt.putBoolean("itemNameTranslatable", false);
                        }
                    }
                }
            }
            nbt.put("DisplayStack", stackNbt);

            // Store transformation
            if (transform != null) {
                CompoundTag transformNbt = new CompoundTag();

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

        public static CustomBlockData fromNbt(CompoundTag nbt) {
            try {
                // Read block
                String blockIdStr = nbt.getString("Block").orElse(null);
                if (blockIdStr == null) return null;

                Identifier blockId = Identifier.tryParse(blockIdStr);
                if (blockId == null) return null;

                Block block = Registries.BLOCK.get(blockId);

                // Read display stack
                CompoundTag stackNbt = nbt.getCompound("DisplayStack").orElse(null);
                if (stackNbt == null) return null;

                String itemIdStr = stackNbt.getString("id").orElse(null);
                if (itemIdStr == null) return null;

                Identifier itemId = Identifier.tryParse(itemIdStr);
                if (itemId == null) return null;

                net.minecraft.world.item.Item item = Registries.ITEM.get(itemId);
                int count = stackNbt.getInt("count").orElse(1);

                ItemStack displayStack = new ItemStack(item, count);

                // Restore model ID if present
                String modelIdStr = stackNbt.getString("model").orElse(null);
                if (modelIdStr != null) {
                    Identifier modelId = Identifier.tryParse(modelIdStr);
                    if (modelId != null) {
                        displayStack.set(net.minecraft.core.component.DataComponents.ITEM_MODEL, modelId);
                    }
                }

                // Restore item name if present
                boolean isTranslatable = stackNbt.getBoolean("itemNameTranslatable").orElse(false);
                if (isTranslatable) {
                    String translationKey = stackNbt.getString("itemNameKey").orElse(null);
                    if (translationKey != null && !translationKey.isEmpty()) {
                        displayStack.set(net.minecraft.core.component.DataComponents.ITEM_NAME,
                            net.minecraft.network.chat.Component.translatable(translationKey));
                    }
                } else {
                    String itemNameStr = stackNbt.getString("itemName").orElse(null);
                    if (itemNameStr != null && !itemNameStr.isEmpty()) {
                        displayStack.set(net.minecraft.core.component.DataComponents.ITEM_NAME,
                            net.minecraft.network.chat.Component.literal(itemNameStr));
                    }
                }

                // Read transformation
                Transformation transform = null;
                Optional<CompoundTag> transformNbtOpt = nbt.getCompound("Transform");
                if (transformNbtOpt.isPresent()) {
                    CompoundTag transformNbt = transformNbtOpt.get();
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
                    transform = new Transformation(translation, leftRotation, scale, rightRotation);
                }

                return new CustomBlockData(block, displayStack, transform);
            } catch (Exception e) {
                return null;
            }
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.serialization.Dynamic
 *  it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.ints.IntList
 *  it.unimi.dsi.fastutil.ints.IntListIterator
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.datafixer.fix;

import com.google.common.collect.Lists;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import java.util.ArrayList;
import java.util.Arrays;
import net.minecraft.datafixer.fix.ChunkPalettedStorageFix;
import org.jspecify.annotations.Nullable;

static final class ChunkPalettedStorageFix.Level {
    private int sidesToUpgrade;
    private final @Nullable ChunkPalettedStorageFix.Section[] sections = new ChunkPalettedStorageFix.Section[16];
    private final Dynamic<?> level;
    private final int x;
    private final int z;
    private final Int2ObjectMap<Dynamic<?>> blockEntities = new Int2ObjectLinkedOpenHashMap(16);

    public ChunkPalettedStorageFix.Level(Dynamic<?> chunkTag) {
        this.level = chunkTag;
        this.x = chunkTag.get("xPos").asInt(0) << 4;
        this.z = chunkTag.get("zPos").asInt(0) << 4;
        chunkTag.get("TileEntities").asStreamOpt().ifSuccess(stream -> stream.forEach(blockEntityTag -> {
            int k;
            int i = blockEntityTag.get("x").asInt(0) - this.x & 0xF;
            int j = blockEntityTag.get("y").asInt(0);
            int l = j << 8 | (k = blockEntityTag.get("z").asInt(0) - this.z & 0xF) << 4 | i;
            if (this.blockEntities.put(l, blockEntityTag) != null) {
                LOGGER.warn("In chunk: {}x{} found a duplicate block entity at position: [{}, {}, {}]", new Object[]{this.x, this.z, i, j, k});
            }
        }));
        boolean bl = chunkTag.get("convertedFromAlphaFormat").asBoolean(false);
        chunkTag.get("Sections").asStreamOpt().ifSuccess(stream -> stream.forEach(sectionTag -> {
            ChunkPalettedStorageFix.Section section = new ChunkPalettedStorageFix.Section((Dynamic<?>)sectionTag);
            this.sidesToUpgrade = section.visit(this.sidesToUpgrade);
            this.sections[section.y] = section;
        }));
        for (ChunkPalettedStorageFix.Section section : this.sections) {
            if (section == null) continue;
            block30: for (Int2ObjectMap.Entry entry : section.inPlaceUpdates.int2ObjectEntrySet()) {
                int i = section.y << 12;
                switch (entry.getIntKey()) {
                    case 2: {
                        Object string;
                        Dynamic<?> dynamic;
                        int j;
                        IntListIterator intListIterator = ((IntList)entry.getValue()).iterator();
                        while (intListIterator.hasNext()) {
                            j = (Integer)intListIterator.next();
                            dynamic = this.getBlock(j |= i);
                            if (!"minecraft:grass_block".equals(ChunkPalettedStorageFix.getName(dynamic)) || !"minecraft:snow".equals(string = ChunkPalettedStorageFix.getName(this.getBlock(ChunkPalettedStorageFix.Level.adjacentTo(j, ChunkPalettedStorageFix.Facing.UP)))) && !"minecraft:snow_layer".equals(string)) continue;
                            this.setBlock(j, ChunkPalettedStorageFix.Mapping.SNOWY_GRASS_BLOCK_STATE);
                        }
                        continue block30;
                    }
                    case 3: {
                        Object string;
                        Dynamic<?> dynamic;
                        int j;
                        IntListIterator intListIterator = ((IntList)entry.getValue()).iterator();
                        while (intListIterator.hasNext()) {
                            j = (Integer)intListIterator.next();
                            dynamic = this.getBlock(j |= i);
                            if (!"minecraft:podzol".equals(ChunkPalettedStorageFix.getName(dynamic)) || !"minecraft:snow".equals(string = ChunkPalettedStorageFix.getName(this.getBlock(ChunkPalettedStorageFix.Level.adjacentTo(j, ChunkPalettedStorageFix.Facing.UP)))) && !"minecraft:snow_layer".equals(string)) continue;
                            this.setBlock(j, ChunkPalettedStorageFix.Mapping.SNOWY_PODZOL_STATE);
                        }
                        continue block30;
                    }
                    case 110: {
                        Object string;
                        Dynamic<?> dynamic;
                        int j;
                        IntListIterator intListIterator = ((IntList)entry.getValue()).iterator();
                        while (intListIterator.hasNext()) {
                            j = (Integer)intListIterator.next();
                            dynamic = this.getBlock(j |= i);
                            if (!"minecraft:mycelium".equals(ChunkPalettedStorageFix.getName(dynamic)) || !"minecraft:snow".equals(string = ChunkPalettedStorageFix.getName(this.getBlock(ChunkPalettedStorageFix.Level.adjacentTo(j, ChunkPalettedStorageFix.Facing.UP)))) && !"minecraft:snow_layer".equals(string)) continue;
                            this.setBlock(j, ChunkPalettedStorageFix.Mapping.SNOWY_MYCELIUM_STATE);
                        }
                        continue block30;
                    }
                    case 25: {
                        Object string;
                        Dynamic<?> dynamic;
                        int j;
                        IntListIterator intListIterator = ((IntList)entry.getValue()).iterator();
                        while (intListIterator.hasNext()) {
                            j = (Integer)intListIterator.next();
                            dynamic = this.removeBlockEntity(j |= i);
                            if (dynamic == null) continue;
                            string = Boolean.toString(dynamic.get("powered").asBoolean(false)) + (byte)Math.min(Math.max(dynamic.get("note").asInt(0), 0), 24);
                            this.setBlock(j, ChunkPalettedStorageFix.Mapping.NOTE_BLOCK_IDS_TO_STATES.getOrDefault(string, ChunkPalettedStorageFix.Mapping.NOTE_BLOCK_IDS_TO_STATES.get("false0")));
                        }
                        continue block30;
                    }
                    case 26: {
                        String string2;
                        Dynamic<?> dynamic2;
                        Dynamic<?> dynamic;
                        int j;
                        IntListIterator intListIterator = ((IntList)entry.getValue()).iterator();
                        while (intListIterator.hasNext()) {
                            int k;
                            j = (Integer)intListIterator.next();
                            dynamic = this.getBlockEntity(j |= i);
                            dynamic2 = this.getBlock(j);
                            if (dynamic == null || (k = dynamic.get("color").asInt(0)) == 14 || k < 0 || k >= 16 || !ChunkPalettedStorageFix.Mapping.BED_IDS_TO_STATES.containsKey(string2 = ChunkPalettedStorageFix.getProperty(dynamic2, "facing") + ChunkPalettedStorageFix.getProperty(dynamic2, "occupied") + ChunkPalettedStorageFix.getProperty(dynamic2, "part") + k)) continue;
                            this.setBlock(j, ChunkPalettedStorageFix.Mapping.BED_IDS_TO_STATES.get(string2));
                        }
                        continue block30;
                    }
                    case 176: 
                    case 177: {
                        String string2;
                        Dynamic<?> dynamic2;
                        Dynamic<?> dynamic;
                        int j;
                        IntListIterator intListIterator = ((IntList)entry.getValue()).iterator();
                        while (intListIterator.hasNext()) {
                            int k;
                            j = (Integer)intListIterator.next();
                            dynamic = this.getBlockEntity(j |= i);
                            dynamic2 = this.getBlock(j);
                            if (dynamic == null || (k = dynamic.get("Base").asInt(0)) == 15 || k < 0 || k >= 16 || !ChunkPalettedStorageFix.Mapping.BANNER_IDS_TO_STATES.containsKey(string2 = ChunkPalettedStorageFix.getProperty(dynamic2, entry.getIntKey() == 176 ? "rotation" : "facing") + "_" + k)) continue;
                            this.setBlock(j, ChunkPalettedStorageFix.Mapping.BANNER_IDS_TO_STATES.get(string2));
                        }
                        continue block30;
                    }
                    case 86: {
                        Object string;
                        Dynamic<?> dynamic;
                        int j;
                        IntListIterator intListIterator = ((IntList)entry.getValue()).iterator();
                        while (intListIterator.hasNext()) {
                            j = (Integer)intListIterator.next();
                            dynamic = this.getBlock(j |= i);
                            if (!"minecraft:carved_pumpkin".equals(ChunkPalettedStorageFix.getName(dynamic)) || !"minecraft:grass_block".equals(string = ChunkPalettedStorageFix.getName(this.getBlock(ChunkPalettedStorageFix.Level.adjacentTo(j, ChunkPalettedStorageFix.Facing.DOWN)))) && !"minecraft:dirt".equals(string)) continue;
                            this.setBlock(j, ChunkPalettedStorageFix.Mapping.PUMPKIN_STATE);
                        }
                        continue block30;
                    }
                    case 140: {
                        Object string;
                        Dynamic<?> dynamic;
                        int j;
                        IntListIterator intListIterator = ((IntList)entry.getValue()).iterator();
                        while (intListIterator.hasNext()) {
                            j = (Integer)intListIterator.next();
                            dynamic = this.removeBlockEntity(j |= i);
                            if (dynamic == null) continue;
                            string = dynamic.get("Item").asString("") + dynamic.get("Data").asInt(0);
                            this.setBlock(j, ChunkPalettedStorageFix.Mapping.PLANT_TO_FLOWER_POT_STATES.getOrDefault(string, ChunkPalettedStorageFix.Mapping.PLANT_TO_FLOWER_POT_STATES.get("minecraft:air0")));
                        }
                        continue block30;
                    }
                    case 144: {
                        String string2;
                        Object string;
                        Dynamic<?> dynamic;
                        int j;
                        IntListIterator intListIterator = ((IntList)entry.getValue()).iterator();
                        while (intListIterator.hasNext()) {
                            j = (Integer)intListIterator.next();
                            dynamic = this.getBlockEntity(j |= i);
                            if (dynamic == null) continue;
                            string = String.valueOf(dynamic.get("SkullType").asInt(0));
                            String string3 = ChunkPalettedStorageFix.getProperty(this.getBlock(j), "facing");
                            string2 = "up".equals(string3) || "down".equals(string3) ? (String)string + dynamic.get("Rot").asInt(0) : (String)string + string3;
                            dynamic.remove("SkullType");
                            dynamic.remove("facing");
                            dynamic.remove("Rot");
                            this.setBlock(j, ChunkPalettedStorageFix.Mapping.SKULL_IDS_TO_STATES.getOrDefault(string2, ChunkPalettedStorageFix.Mapping.SKULL_IDS_TO_STATES.get("0north")));
                        }
                        continue block30;
                    }
                    case 64: 
                    case 71: 
                    case 193: 
                    case 194: 
                    case 195: 
                    case 196: 
                    case 197: {
                        Dynamic<?> dynamic2;
                        Dynamic<?> dynamic;
                        int j;
                        IntListIterator intListIterator = ((IntList)entry.getValue()).iterator();
                        while (intListIterator.hasNext()) {
                            j = (Integer)intListIterator.next();
                            dynamic = this.getBlock(j |= i);
                            if (!ChunkPalettedStorageFix.getName(dynamic).endsWith("_door") || !"lower".equals(ChunkPalettedStorageFix.getProperty(dynamic2 = this.getBlock(j), "half"))) continue;
                            int k = ChunkPalettedStorageFix.Level.adjacentTo(j, ChunkPalettedStorageFix.Facing.UP);
                            Dynamic<?> dynamic3 = this.getBlock(k);
                            String string4 = ChunkPalettedStorageFix.getName(dynamic2);
                            if (!string4.equals(ChunkPalettedStorageFix.getName(dynamic3))) continue;
                            String string5 = ChunkPalettedStorageFix.getProperty(dynamic2, "facing");
                            String string6 = ChunkPalettedStorageFix.getProperty(dynamic2, "open");
                            String string7 = bl ? "left" : ChunkPalettedStorageFix.getProperty(dynamic3, "hinge");
                            String string8 = bl ? "false" : ChunkPalettedStorageFix.getProperty(dynamic3, "powered");
                            this.setBlock(j, ChunkPalettedStorageFix.Mapping.DOOR_IDS_TO_STATES.get(string4 + string5 + "lower" + string7 + string6 + string8));
                            this.setBlock(k, ChunkPalettedStorageFix.Mapping.DOOR_IDS_TO_STATES.get(string4 + string5 + "upper" + string7 + string6 + string8));
                        }
                        continue block30;
                    }
                    case 175: {
                        Dynamic<?> dynamic2;
                        Dynamic<?> dynamic;
                        int j;
                        IntListIterator intListIterator = ((IntList)entry.getValue()).iterator();
                        while (intListIterator.hasNext()) {
                            String string3;
                            j = (Integer)intListIterator.next();
                            dynamic = this.getBlock(j |= i);
                            if (!"upper".equals(ChunkPalettedStorageFix.getProperty(dynamic, "half"))) continue;
                            dynamic2 = this.getBlock(ChunkPalettedStorageFix.Level.adjacentTo(j, ChunkPalettedStorageFix.Facing.DOWN));
                            switch (string3 = ChunkPalettedStorageFix.getName(dynamic2)) {
                                case "minecraft:sunflower": {
                                    this.setBlock(j, ChunkPalettedStorageFix.Mapping.UPPER_HALF_SUNFLOWER_STATE);
                                    break;
                                }
                                case "minecraft:lilac": {
                                    this.setBlock(j, ChunkPalettedStorageFix.Mapping.UPPER_HALF_LILAC_STATE);
                                    break;
                                }
                                case "minecraft:tall_grass": {
                                    this.setBlock(j, ChunkPalettedStorageFix.Mapping.UPPER_HALF_TALL_GRASS_STATE);
                                    break;
                                }
                                case "minecraft:large_fern": {
                                    this.setBlock(j, ChunkPalettedStorageFix.Mapping.UPPER_HALF_LARGE_FERN_STATE);
                                    break;
                                }
                                case "minecraft:rose_bush": {
                                    this.setBlock(j, ChunkPalettedStorageFix.Mapping.UPPER_HALF_ROSE_BUSH_STATE);
                                    break;
                                }
                                case "minecraft:peony": {
                                    this.setBlock(j, ChunkPalettedStorageFix.Mapping.UPPER_HALF_PEONY_STATE);
                                }
                            }
                        }
                        break;
                    }
                }
            }
        }
    }

    private @Nullable Dynamic<?> getBlockEntity(int packedLocalPos) {
        return (Dynamic)this.blockEntities.get(packedLocalPos);
    }

    private @Nullable Dynamic<?> removeBlockEntity(int packedLocalPos) {
        return (Dynamic)this.blockEntities.remove(packedLocalPos);
    }

    public static int adjacentTo(int packedLocalPos, ChunkPalettedStorageFix.Facing direction) {
        return switch (direction.getAxis().ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> {
                int i = (packedLocalPos & 0xF) + direction.getDirection().getOffset();
                if (i < 0 || i > 15) {
                    yield -1;
                }
                yield packedLocalPos & 0xFFFFFFF0 | i;
            }
            case 1 -> {
                int i = (packedLocalPos >> 8) + direction.getDirection().getOffset();
                if (i < 0 || i > 255) {
                    yield -1;
                }
                yield packedLocalPos & 0xFF | i << 8;
            }
            case 2 -> {
                int i = (packedLocalPos >> 4 & 0xF) + direction.getDirection().getOffset();
                if (i < 0 || i > 15) {
                    yield -1;
                }
                yield packedLocalPos & 0xFFFFFF0F | i << 4;
            }
        };
    }

    private void setBlock(int packedLocalPos, Dynamic<?> dynamic) {
        if (packedLocalPos < 0 || packedLocalPos > 65535) {
            return;
        }
        ChunkPalettedStorageFix.Section section = this.getSection(packedLocalPos);
        if (section == null) {
            return;
        }
        section.setBlock(packedLocalPos & 0xFFF, dynamic);
    }

    private @Nullable ChunkPalettedStorageFix.Section getSection(int packedLocalPos) {
        int i = packedLocalPos >> 12;
        return i < this.sections.length ? this.sections[i] : null;
    }

    public Dynamic<?> getBlock(int packedLocalPos) {
        if (packedLocalPos < 0 || packedLocalPos > 65535) {
            return ChunkPalettedStorageFix.Mapping.AIR_STATE;
        }
        ChunkPalettedStorageFix.Section section = this.getSection(packedLocalPos);
        if (section == null) {
            return ChunkPalettedStorageFix.Mapping.AIR_STATE;
        }
        return section.getBlock(packedLocalPos & 0xFFF);
    }

    public Dynamic<?> transform() {
        Dynamic dynamic = this.level;
        dynamic = this.blockEntities.isEmpty() ? dynamic.remove("TileEntities") : dynamic.set("TileEntities", dynamic.createList(this.blockEntities.values().stream()));
        Dynamic dynamic2 = dynamic.emptyMap();
        ArrayList list = Lists.newArrayList();
        for (ChunkPalettedStorageFix.Section section : this.sections) {
            if (section == null) continue;
            list.add(section.transform());
            dynamic2 = dynamic2.set(String.valueOf(section.y), dynamic2.createIntList(Arrays.stream(section.innerPositions.toIntArray())));
        }
        Dynamic dynamic3 = dynamic.emptyMap();
        dynamic3 = dynamic3.set("Sides", dynamic3.createByte((byte)this.sidesToUpgrade));
        dynamic3 = dynamic3.set("Indices", dynamic2);
        return dynamic.set("UpgradeData", dynamic3).set("Sections", dynamic3.createList(list.stream()));
    }
}

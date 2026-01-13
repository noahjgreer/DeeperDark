/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.serialization.Dynamic
 *  it.unimi.dsi.fastutil.ints.Int2IntMap
 *  it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
 *  it.unimi.dsi.fastutil.ints.IntOpenHashSet
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.Objects;
import net.minecraft.datafixer.fix.LeavesFix;
import net.minecraft.util.math.WordPackedArray;
import org.jspecify.annotations.Nullable;

public static final class LeavesFix.LeavesLogFixer
extends LeavesFix.ListFixer {
    private static final String PERSISTENT = "persistent";
    private static final String DECAYABLE = "decayable";
    private static final String DISTANCE = "distance";
    private @Nullable IntSet leafIndices;
    private @Nullable IntSet logIndices;
    private @Nullable Int2IntMap leafStates;

    public LeavesFix.LeavesLogFixer(Typed<?> typed, Schema schema) {
        super(typed, schema);
    }

    @Override
    protected boolean computeIsFixed() {
        this.leafIndices = new IntOpenHashSet();
        this.logIndices = new IntOpenHashSet();
        this.leafStates = new Int2IntOpenHashMap();
        for (int i = 0; i < this.properties.size(); ++i) {
            Dynamic dynamic = (Dynamic)this.properties.get(i);
            String string = dynamic.get("Name").asString("");
            if (LEAVES_MAP.containsKey((Object)string)) {
                boolean bl = Objects.equals(dynamic.get("Properties").get(DECAYABLE).asString(""), "false");
                this.leafIndices.add(i);
                this.leafStates.put(this.computeFlags(string, bl, 7), i);
                this.properties.set(i, this.createLeafProperties(dynamic, string, bl, 7));
            }
            if (!LOGS_MAP.contains(string)) continue;
            this.logIndices.add(i);
        }
        return this.leafIndices.isEmpty() && this.logIndices.isEmpty();
    }

    private Dynamic<?> createLeafProperties(Dynamic<?> tag, String name, boolean persistent, int distance) {
        Dynamic dynamic = tag.emptyMap();
        dynamic = dynamic.set(PERSISTENT, dynamic.createString(persistent ? "true" : "false"));
        dynamic = dynamic.set(DISTANCE, dynamic.createString(Integer.toString(distance)));
        Dynamic dynamic2 = tag.emptyMap();
        dynamic2 = dynamic2.set("Properties", dynamic);
        dynamic2 = dynamic2.set("Name", dynamic2.createString(name));
        return dynamic2;
    }

    public boolean isLog(int index) {
        return this.logIndices.contains(index);
    }

    public boolean isLeaf(int index) {
        return this.leafIndices.contains(index);
    }

    int getDistanceToLog(int index) {
        if (this.isLog(index)) {
            return 0;
        }
        return Integer.parseInt(((Dynamic)this.properties.get(index)).get("Properties").get(DISTANCE).asString(""));
    }

    void computeLeafStates(int packedLocalPos, int propertyIndex, int distance) {
        int j;
        boolean bl;
        Dynamic dynamic = (Dynamic)this.properties.get(propertyIndex);
        String string = dynamic.get("Name").asString("");
        int i = this.computeFlags(string, bl = Objects.equals(dynamic.get("Properties").get(PERSISTENT).asString(""), "true"), distance);
        if (!this.leafStates.containsKey(i)) {
            j = this.properties.size();
            this.leafIndices.add(j);
            this.leafStates.put(i, j);
            this.properties.add(this.createLeafProperties(dynamic, string, bl, distance));
        }
        j = this.leafStates.get(i);
        if (1 << this.blockStateMap.getUnitSize() <= j) {
            WordPackedArray wordPackedArray = new WordPackedArray(this.blockStateMap.getUnitSize() + 1, 4096);
            for (int k = 0; k < 4096; ++k) {
                wordPackedArray.set(k, this.blockStateMap.get(k));
            }
            this.blockStateMap = wordPackedArray;
        }
        this.blockStateMap.set(packedLocalPos, j);
    }
}

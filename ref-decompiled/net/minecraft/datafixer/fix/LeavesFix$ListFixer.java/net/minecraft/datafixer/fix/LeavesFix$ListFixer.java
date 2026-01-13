/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Dynamic
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.datafixer.fix;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.util.math.WordPackedArray;
import org.jspecify.annotations.Nullable;

public static abstract class LeavesFix.ListFixer {
    protected static final String BLOCK_STATES_KEY = "BlockStates";
    protected static final String NAME_KEY = "Name";
    protected static final String PROPERTIES_KEY = "Properties";
    private final Type<Pair<String, Dynamic<?>>> blockStateType = DSL.named((String)TypeReferences.BLOCK_STATE.typeName(), (Type)DSL.remainderType());
    protected final OpticFinder<List<Pair<String, Dynamic<?>>>> paletteFinder = DSL.fieldFinder((String)"Palette", (Type)DSL.list(this.blockStateType));
    protected final List<Dynamic<?>> properties;
    protected final int y;
    protected @Nullable WordPackedArray blockStateMap;

    public LeavesFix.ListFixer(Typed<?> sectionTyped, Schema inputSchema) {
        if (!Objects.equals(inputSchema.getType(TypeReferences.BLOCK_STATE), this.blockStateType)) {
            throw new IllegalStateException("Block state type is not what was expected.");
        }
        Optional optional = sectionTyped.getOptional(this.paletteFinder);
        this.properties = optional.map(palettes -> palettes.stream().map(Pair::getSecond).collect(Collectors.toList())).orElse((List)ImmutableList.of());
        Dynamic dynamic = (Dynamic)sectionTyped.get(DSL.remainderFinder());
        this.y = dynamic.get("Y").asInt(0);
        this.computeFixableBlockStates(dynamic);
    }

    protected void computeFixableBlockStates(Dynamic<?> dynamic) {
        if (this.computeIsFixed()) {
            this.blockStateMap = null;
        } else {
            long[] ls = dynamic.get(BLOCK_STATES_KEY).asLongStream().toArray();
            int i = Math.max(4, DataFixUtils.ceillog2((int)this.properties.size()));
            this.blockStateMap = new WordPackedArray(i, 4096, ls);
        }
    }

    public Typed<?> finalizeFix(Typed<?> typed) {
        if (this.isFixed()) {
            return typed;
        }
        return typed.update(DSL.remainderFinder(), remainder -> remainder.set(BLOCK_STATES_KEY, remainder.createLongList(Arrays.stream(this.blockStateMap.getAlignedArray())))).set(this.paletteFinder, this.properties.stream().map(propertiesDynamic -> Pair.of((Object)TypeReferences.BLOCK_STATE.typeName(), (Object)propertiesDynamic)).collect(Collectors.toList()));
    }

    public boolean isFixed() {
        return this.blockStateMap == null;
    }

    public int blockStateAt(int index) {
        return this.blockStateMap.get(index);
    }

    protected int computeFlags(String leafBlockName, boolean persistent, int distance) {
        return LEAVES_MAP.get((Object)leafBlockName) << 5 | (persistent ? 16 : 0) | distance;
    }

    int getY() {
        return this.y;
    }

    protected abstract boolean computeIsFixed();
}

/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  org.apache.commons.lang3.mutable.MutableBoolean
 */
package net.minecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.nbt.AbstractNbtList;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import org.apache.commons.lang3.mutable.MutableBoolean;

public static class NbtPathArgumentType.NbtPath {
    private final String string;
    private final Object2IntMap<NbtPathArgumentType.PathNode> nodeEndIndices;
    private final NbtPathArgumentType.PathNode[] nodes;
    public static final Codec<NbtPathArgumentType.NbtPath> CODEC = Codec.STRING.comapFlatMap(path -> {
        try {
            NbtPathArgumentType.NbtPath nbtPath = new NbtPathArgumentType().parse(new StringReader(path));
            return DataResult.success((Object)nbtPath);
        }
        catch (CommandSyntaxException commandSyntaxException) {
            return DataResult.error(() -> "Failed to parse path " + path + ": " + commandSyntaxException.getMessage());
        }
    }, NbtPathArgumentType.NbtPath::getString);

    public static NbtPathArgumentType.NbtPath parse(String path) throws CommandSyntaxException {
        return new NbtPathArgumentType().parse(new StringReader(path));
    }

    public NbtPathArgumentType.NbtPath(String string, NbtPathArgumentType.PathNode[] nodes, Object2IntMap<NbtPathArgumentType.PathNode> nodeEndIndices) {
        this.string = string;
        this.nodes = nodes;
        this.nodeEndIndices = nodeEndIndices;
    }

    public List<NbtElement> get(NbtElement element) throws CommandSyntaxException {
        List<NbtElement> list = Collections.singletonList(element);
        for (NbtPathArgumentType.PathNode pathNode : this.nodes) {
            if (!(list = pathNode.get(list)).isEmpty()) continue;
            throw this.createNothingFoundException(pathNode);
        }
        return list;
    }

    public int count(NbtElement element) {
        List<NbtElement> list = Collections.singletonList(element);
        for (NbtPathArgumentType.PathNode pathNode : this.nodes) {
            if (!(list = pathNode.get(list)).isEmpty()) continue;
            return 0;
        }
        return list.size();
    }

    private List<NbtElement> getTerminals(NbtElement start) throws CommandSyntaxException {
        List<NbtElement> list = Collections.singletonList(start);
        for (int i = 0; i < this.nodes.length - 1; ++i) {
            NbtPathArgumentType.PathNode pathNode = this.nodes[i];
            int j = i + 1;
            if (!(list = pathNode.getOrInit(list, this.nodes[j]::init)).isEmpty()) continue;
            throw this.createNothingFoundException(pathNode);
        }
        return list;
    }

    public List<NbtElement> getOrInit(NbtElement element, Supplier<NbtElement> source) throws CommandSyntaxException {
        List<NbtElement> list = this.getTerminals(element);
        NbtPathArgumentType.PathNode pathNode = this.nodes[this.nodes.length - 1];
        return pathNode.getOrInit(list, source);
    }

    private static int forEach(List<NbtElement> elements, Function<NbtElement, Integer> operation) {
        return elements.stream().map(operation).reduce(0, (a, b) -> a + b);
    }

    public static boolean isTooDeep(NbtElement element, int depth) {
        block4: {
            block3: {
                if (depth >= 512) {
                    return true;
                }
                if (!(element instanceof NbtCompound)) break block3;
                NbtCompound nbtCompound = (NbtCompound)element;
                for (NbtElement nbtElement : nbtCompound.values()) {
                    if (!NbtPathArgumentType.NbtPath.isTooDeep(nbtElement, depth + 1)) continue;
                    return true;
                }
                break block4;
            }
            if (!(element instanceof NbtList)) break block4;
            NbtList nbtList = (NbtList)element;
            for (NbtElement nbtElement : nbtList) {
                if (!NbtPathArgumentType.NbtPath.isTooDeep(nbtElement, depth + 1)) continue;
                return true;
            }
        }
        return false;
    }

    public int put(NbtElement element, NbtElement source) throws CommandSyntaxException {
        if (NbtPathArgumentType.NbtPath.isTooDeep(source, this.getDepth())) {
            throw TOO_DEEP_EXCEPTION.create();
        }
        NbtElement nbtElement = source.copy();
        List<NbtElement> list = this.getTerminals(element);
        if (list.isEmpty()) {
            return 0;
        }
        NbtPathArgumentType.PathNode pathNode = this.nodes[this.nodes.length - 1];
        MutableBoolean mutableBoolean = new MutableBoolean(false);
        return NbtPathArgumentType.NbtPath.forEach(list, nbt -> pathNode.set((NbtElement)nbt, () -> {
            if (mutableBoolean.isFalse()) {
                mutableBoolean.setTrue();
                return nbtElement;
            }
            return nbtElement.copy();
        }));
    }

    private int getDepth() {
        return this.nodes.length;
    }

    public int insert(int index, NbtCompound compound, List<NbtElement> elements) throws CommandSyntaxException {
        ArrayList<NbtElement> list = new ArrayList<NbtElement>(elements.size());
        for (NbtElement nbtElement : elements) {
            NbtElement nbtElement2 = nbtElement.copy();
            list.add(nbtElement2);
            if (!NbtPathArgumentType.NbtPath.isTooDeep(nbtElement2, this.getDepth())) continue;
            throw TOO_DEEP_EXCEPTION.create();
        }
        List<NbtElement> collection = this.getOrInit(compound, NbtList::new);
        int i = 0;
        boolean bl = false;
        for (NbtElement nbtElement3 : collection) {
            if (!(nbtElement3 instanceof AbstractNbtList)) {
                throw EXPECTED_LIST_EXCEPTION.create((Object)nbtElement3);
            }
            AbstractNbtList abstractNbtList = (AbstractNbtList)nbtElement3;
            boolean bl2 = false;
            int j = index < 0 ? abstractNbtList.size() + index + 1 : index;
            for (NbtElement nbtElement4 : list) {
                try {
                    if (!abstractNbtList.addElement(j, bl ? nbtElement4.copy() : nbtElement4)) continue;
                    ++j;
                    bl2 = true;
                }
                catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                    throw INVALID_INDEX_EXCEPTION.create((Object)j);
                }
            }
            bl = true;
            i += bl2 ? 1 : 0;
        }
        return i;
    }

    public int remove(NbtElement element) {
        List<NbtElement> list = Collections.singletonList(element);
        for (int i = 0; i < this.nodes.length - 1; ++i) {
            list = this.nodes[i].get(list);
        }
        NbtPathArgumentType.PathNode pathNode = this.nodes[this.nodes.length - 1];
        return NbtPathArgumentType.NbtPath.forEach(list, pathNode::clear);
    }

    private CommandSyntaxException createNothingFoundException(NbtPathArgumentType.PathNode node) {
        int i = this.nodeEndIndices.getInt((Object)node);
        return NOTHING_FOUND_EXCEPTION.create((Object)this.string.substring(0, i));
    }

    public String toString() {
        return this.string;
    }

    public String getString() {
        return this.string;
    }
}

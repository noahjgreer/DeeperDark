/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.command;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.command.DataCommandStorage;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;

static class DataCommandStorage.PersistentState
extends PersistentState {
    public static final Codec<DataCommandStorage.PersistentState> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.unboundedMap(Codecs.IDENTIFIER_PATH, NbtCompound.CODEC).fieldOf("contents").forGetter(state -> state.map)).apply((Applicative)instance, DataCommandStorage.PersistentState::new));
    private final Map<String, NbtCompound> map;

    private DataCommandStorage.PersistentState(Map<String, NbtCompound> map) {
        this.map = new HashMap<String, NbtCompound>(map);
    }

    private DataCommandStorage.PersistentState() {
        this(new HashMap<String, NbtCompound>());
    }

    public static PersistentStateType<DataCommandStorage.PersistentState> createStateType(String id) {
        return new PersistentStateType<DataCommandStorage.PersistentState>(DataCommandStorage.getSaveKey(id), DataCommandStorage.PersistentState::new, CODEC, DataFixTypes.SAVED_DATA_COMMAND_STORAGE);
    }

    public NbtCompound get(String name) {
        NbtCompound nbtCompound = this.map.get(name);
        return nbtCompound != null ? nbtCompound : new NbtCompound();
    }

    public void set(String name, NbtCompound nbt) {
        if (nbt.isEmpty()) {
            this.map.remove(name);
        } else {
            this.map.put(name, nbt);
        }
        this.markDirty();
    }

    public Stream<Identifier> getIds(String namespace) {
        return this.map.keySet().stream().map(key -> Identifier.of(namespace, key));
    }
}

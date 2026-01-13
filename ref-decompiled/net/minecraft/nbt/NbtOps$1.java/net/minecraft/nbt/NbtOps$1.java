/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.MapLike
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.nbt;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapLike;
import java.util.stream.Stream;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtString;
import org.jspecify.annotations.Nullable;

class NbtOps.1
implements MapLike<NbtElement> {
    final /* synthetic */ NbtCompound field_25129;

    NbtOps.1(NbtCompound nbtCompound) {
        this.field_25129 = nbtCompound;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public @Nullable NbtElement get(NbtElement nbtElement) {
        if (!(nbtElement instanceof NbtString)) throw new UnsupportedOperationException("Cannot get map entry with non-string key: " + String.valueOf(nbtElement));
        NbtString nbtString = (NbtString)nbtElement;
        try {
            String string;
            String string2 = string = nbtString.value();
            return this.field_25129.get(string2);
        }
        catch (Throwable throwable) {
            throw new MatchException(throwable.toString(), throwable);
        }
    }

    public @Nullable NbtElement get(String string) {
        return this.field_25129.get(string);
    }

    public Stream<Pair<NbtElement, NbtElement>> entries() {
        return this.field_25129.entrySet().stream().map(entry -> Pair.of((Object)NbtOps.this.createString((String)entry.getKey()), (Object)((NbtElement)entry.getValue())));
    }

    public String toString() {
        return "MapLike[" + String.valueOf(this.field_25129) + "]";
    }

    public /* synthetic */ @Nullable Object get(String key) {
        return this.get(key);
    }

    public /* synthetic */ @Nullable Object get(Object nbt) {
        return this.get((NbtElement)nbt);
    }
}

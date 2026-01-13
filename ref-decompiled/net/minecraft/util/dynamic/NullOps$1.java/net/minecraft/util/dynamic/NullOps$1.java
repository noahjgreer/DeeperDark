/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.MapLike
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.util.dynamic;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapLike;
import java.util.stream.Stream;
import net.minecraft.util.Unit;
import org.jspecify.annotations.Nullable;

class NullOps.1
implements MapLike<Unit> {
    NullOps.1() {
    }

    public @Nullable Unit get(Unit unit) {
        return null;
    }

    public @Nullable Unit get(String string) {
        return null;
    }

    public Stream<Pair<Unit, Unit>> entries() {
        return Stream.empty();
    }

    public /* synthetic */ @Nullable Object get(String string) {
        return this.get(string);
    }

    public /* synthetic */ @Nullable Object get(Object object) {
        return this.get((Unit)((Object)object));
    }
}

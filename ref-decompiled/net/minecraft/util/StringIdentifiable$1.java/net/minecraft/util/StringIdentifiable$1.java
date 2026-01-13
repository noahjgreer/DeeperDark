/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.Keyable
 */
package net.minecraft.util;

import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Keyable;
import java.util.Arrays;
import java.util.stream.Stream;
import net.minecraft.util.StringIdentifiable;

static class StringIdentifiable.1
implements Keyable {
    final /* synthetic */ StringIdentifiable[] field_35666;

    StringIdentifiable.1(StringIdentifiable[] stringIdentifiables) {
        this.field_35666 = stringIdentifiables;
    }

    public <T> Stream<T> keys(DynamicOps<T> ops) {
        return Arrays.stream(this.field_35666).map(StringIdentifiable::asString).map(arg_0 -> ops.createString(arg_0));
    }
}

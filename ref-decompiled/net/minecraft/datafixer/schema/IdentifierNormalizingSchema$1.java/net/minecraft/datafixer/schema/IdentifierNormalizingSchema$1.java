/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.codecs.PrimitiveCodec
 */
package net.minecraft.datafixer.schema;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.PrimitiveCodec;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

class IdentifierNormalizingSchema.1
implements PrimitiveCodec<String> {
    IdentifierNormalizingSchema.1() {
    }

    public <T> DataResult<String> read(DynamicOps<T> ops, T input) {
        return ops.getStringValue(input).map(IdentifierNormalizingSchema::normalize);
    }

    public <T> T write(DynamicOps<T> dynamicOps, String string) {
        return (T)dynamicOps.createString(string);
    }

    public String toString() {
        return "NamespacedString";
    }

    public /* synthetic */ Object write(DynamicOps ops, Object value) {
        return this.write(ops, (String)value);
    }
}

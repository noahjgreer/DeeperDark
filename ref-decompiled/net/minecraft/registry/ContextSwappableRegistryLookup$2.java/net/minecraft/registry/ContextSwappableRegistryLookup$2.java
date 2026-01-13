/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.JavaOps
 */
package net.minecraft.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JavaOps;
import net.minecraft.registry.ContextSwapper;
import net.minecraft.registry.RegistryWrapper;

class ContextSwappableRegistryLookup.2
implements ContextSwapper {
    ContextSwappableRegistryLookup.2() {
    }

    @Override
    public <T> DataResult<T> swapContext(Codec<T> codec, T value, RegistryWrapper.WrapperLookup registries) {
        return codec.encodeStart(ContextSwappableRegistryLookup.this.createRegistryOps(JavaOps.INSTANCE), value).flatMap(encodedValue -> codec.parse(registries.getOps(JavaOps.INSTANCE), encodedValue));
    }
}

/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.ClosableFactory;
import net.minecraft.client.util.ObjectAllocator;

@Environment(value=EnvType.CLIENT)
class ObjectAllocator.1
implements ObjectAllocator {
    ObjectAllocator.1() {
    }

    @Override
    public <T> T acquire(ClosableFactory<T> factory) {
        T object = factory.create();
        factory.prepare(object);
        return object;
    }

    @Override
    public <T> void release(ClosableFactory<T> factory, T value) {
        factory.close(value);
    }
}

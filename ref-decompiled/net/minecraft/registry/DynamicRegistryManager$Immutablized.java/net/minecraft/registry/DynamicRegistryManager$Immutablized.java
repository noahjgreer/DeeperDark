/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.registry;

import java.util.stream.Stream;
import net.minecraft.registry.DynamicRegistryManager;

class DynamicRegistryManager.Immutablized
extends DynamicRegistryManager.ImmutableImpl
implements DynamicRegistryManager.Immutable {
    protected DynamicRegistryManager.Immutablized(DynamicRegistryManager dynamicRegistryManager, Stream<DynamicRegistryManager.Entry<?>> entryStream) {
        super(entryStream);
    }
}

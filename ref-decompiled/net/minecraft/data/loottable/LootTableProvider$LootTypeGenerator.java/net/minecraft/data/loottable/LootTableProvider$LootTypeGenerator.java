/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.data.loottable;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.function.Function;
import net.minecraft.data.loottable.LootTableGenerator;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.context.ContextType;

public static final class LootTableProvider.LootTypeGenerator
extends Record {
    private final Function<RegistryWrapper.WrapperLookup, LootTableGenerator> provider;
    final ContextType paramSet;

    public LootTableProvider.LootTypeGenerator(Function<RegistryWrapper.WrapperLookup, LootTableGenerator> provider, ContextType paramSet) {
        this.provider = provider;
        this.paramSet = paramSet;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{LootTableProvider.LootTypeGenerator.class, "provider;paramSet", "provider", "paramSet"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{LootTableProvider.LootTypeGenerator.class, "provider;paramSet", "provider", "paramSet"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{LootTableProvider.LootTypeGenerator.class, "provider;paramSet", "provider", "paramSet"}, this, object);
    }

    public Function<RegistryWrapper.WrapperLookup, LootTableGenerator> provider() {
        return this.provider;
    }

    public ContextType paramSet() {
        return this.paramSet;
    }
}

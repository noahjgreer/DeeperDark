/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.test;

import java.util.stream.Stream;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.GameTestState;
import net.minecraft.test.TestInstance;

@FunctionalInterface
public static interface Batches.Decorator {
    public Stream<GameTestState> decorate(RegistryEntry.Reference<TestInstance> var1, ServerWorld var2);
}

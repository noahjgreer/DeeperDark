/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.test;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.List;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.TestEnvironmentDefinition;

public record TestEnvironmentDefinition.AllOf(List<RegistryEntry<TestEnvironmentDefinition>> definitions) implements TestEnvironmentDefinition
{
    public static final MapCodec<TestEnvironmentDefinition.AllOf> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)ENTRY_CODEC.listOf().fieldOf("definitions").forGetter(TestEnvironmentDefinition.AllOf::definitions)).apply((Applicative)instance, TestEnvironmentDefinition.AllOf::new));

    public TestEnvironmentDefinition.AllOf(TestEnvironmentDefinition ... definitionTypes) {
        this(Arrays.stream(definitionTypes).map(RegistryEntry::of).toList());
    }

    @Override
    public void setup(ServerWorld world) {
        this.definitions.forEach(definition -> ((TestEnvironmentDefinition)definition.value()).setup(world));
    }

    @Override
    public void teardown(ServerWorld world) {
        this.definitions.forEach(definition -> ((TestEnvironmentDefinition)definition.value()).teardown(world));
    }

    public MapCodec<TestEnvironmentDefinition.AllOf> getCodec() {
        return CODEC;
    }
}

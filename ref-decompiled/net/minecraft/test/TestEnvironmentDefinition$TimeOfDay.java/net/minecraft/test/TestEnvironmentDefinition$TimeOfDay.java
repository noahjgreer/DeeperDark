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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.TestEnvironmentDefinition;
import net.minecraft.util.dynamic.Codecs;

public record TestEnvironmentDefinition.TimeOfDay(int time) implements TestEnvironmentDefinition
{
    public static final MapCodec<TestEnvironmentDefinition.TimeOfDay> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codecs.NON_NEGATIVE_INT.fieldOf("time").forGetter(TestEnvironmentDefinition.TimeOfDay::time)).apply((Applicative)instance, TestEnvironmentDefinition.TimeOfDay::new));

    @Override
    public void setup(ServerWorld world) {
        world.setTimeOfDay(this.time);
    }

    public MapCodec<TestEnvironmentDefinition.TimeOfDay> getCodec() {
        return CODEC;
    }
}

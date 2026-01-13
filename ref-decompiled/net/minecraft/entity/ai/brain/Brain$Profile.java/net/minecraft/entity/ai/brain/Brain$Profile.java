/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.Dynamic
 *  org.slf4j.Logger
 */
package net.minecraft.entity.ai.brain;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import java.util.Collection;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import org.slf4j.Logger;

public static final class Brain.Profile<E extends LivingEntity> {
    private final Collection<? extends MemoryModuleType<?>> memoryModules;
    private final Collection<? extends SensorType<? extends Sensor<? super E>>> sensors;
    private final Codec<Brain<E>> codec;

    Brain.Profile(Collection<? extends MemoryModuleType<?>> memoryModules, Collection<? extends SensorType<? extends Sensor<? super E>>> sensors) {
        this.memoryModules = memoryModules;
        this.sensors = sensors;
        this.codec = Brain.createBrainCodec(memoryModules, sensors);
    }

    public Brain<E> deserialize(Dynamic<?> data) {
        return this.codec.parse(data).resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0)).orElseGet(() -> new Brain(this.memoryModules, this.sensors, ImmutableList.of(), () -> this.codec));
    }
}

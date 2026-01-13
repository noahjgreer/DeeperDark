/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.test;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.TestEnvironmentDefinition;
import net.minecraft.util.StringIdentifiable;

public record TestEnvironmentDefinition.Weather(State weather) implements TestEnvironmentDefinition
{
    public static final MapCodec<TestEnvironmentDefinition.Weather> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)State.CODEC.fieldOf("weather").forGetter(TestEnvironmentDefinition.Weather::weather)).apply((Applicative)instance, TestEnvironmentDefinition.Weather::new));

    @Override
    public void setup(ServerWorld world) {
        this.weather.apply(world);
    }

    @Override
    public void teardown(ServerWorld world) {
        world.resetWeather();
    }

    public MapCodec<TestEnvironmentDefinition.Weather> getCodec() {
        return CODEC;
    }

    public static final class State
    extends Enum<State>
    implements StringIdentifiable {
        public static final /* enum */ State CLEAR = new State("clear", 100000, 0, false, false);
        public static final /* enum */ State RAIN = new State("rain", 0, 100000, true, false);
        public static final /* enum */ State THUNDER = new State("thunder", 0, 100000, true, true);
        public static final Codec<State> CODEC;
        private final String name;
        private final int clearDuration;
        private final int rainDuration;
        private final boolean raining;
        private final boolean thundering;
        private static final /* synthetic */ State[] field_56217;

        public static State[] values() {
            return (State[])field_56217.clone();
        }

        public static State valueOf(String string) {
            return Enum.valueOf(State.class, string);
        }

        private State(String name, int clearDuration, int rainDuration, boolean raining, boolean thundering) {
            this.name = name;
            this.clearDuration = clearDuration;
            this.rainDuration = rainDuration;
            this.raining = raining;
            this.thundering = thundering;
        }

        void apply(ServerWorld world) {
            world.setWeather(this.clearDuration, this.rainDuration, this.raining, this.thundering);
        }

        @Override
        public String asString() {
            return this.name;
        }

        private static /* synthetic */ State[] method_67068() {
            return new State[]{CLEAR, RAIN, THUNDER};
        }

        static {
            field_56217 = State.method_67068();
            CODEC = StringIdentifiable.createCodec(State::values);
        }
    }
}

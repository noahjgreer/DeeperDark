/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.test;

import com.mojang.serialization.Codec;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.StringIdentifiable;

public static final class TestEnvironmentDefinition.Weather.State
extends Enum<TestEnvironmentDefinition.Weather.State>
implements StringIdentifiable {
    public static final /* enum */ TestEnvironmentDefinition.Weather.State CLEAR = new TestEnvironmentDefinition.Weather.State("clear", 100000, 0, false, false);
    public static final /* enum */ TestEnvironmentDefinition.Weather.State RAIN = new TestEnvironmentDefinition.Weather.State("rain", 0, 100000, true, false);
    public static final /* enum */ TestEnvironmentDefinition.Weather.State THUNDER = new TestEnvironmentDefinition.Weather.State("thunder", 0, 100000, true, true);
    public static final Codec<TestEnvironmentDefinition.Weather.State> CODEC;
    private final String name;
    private final int clearDuration;
    private final int rainDuration;
    private final boolean raining;
    private final boolean thundering;
    private static final /* synthetic */ TestEnvironmentDefinition.Weather.State[] field_56217;

    public static TestEnvironmentDefinition.Weather.State[] values() {
        return (TestEnvironmentDefinition.Weather.State[])field_56217.clone();
    }

    public static TestEnvironmentDefinition.Weather.State valueOf(String string) {
        return Enum.valueOf(TestEnvironmentDefinition.Weather.State.class, string);
    }

    private TestEnvironmentDefinition.Weather.State(String name, int clearDuration, int rainDuration, boolean raining, boolean thundering) {
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

    private static /* synthetic */ TestEnvironmentDefinition.Weather.State[] method_67068() {
        return new TestEnvironmentDefinition.Weather.State[]{CLEAR, RAIN, THUNDER};
    }

    static {
        field_56217 = TestEnvironmentDefinition.Weather.State.method_67068();
        CODEC = StringIdentifiable.createCodec(TestEnvironmentDefinition.Weather.State::values);
    }
}

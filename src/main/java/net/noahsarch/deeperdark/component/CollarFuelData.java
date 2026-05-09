package net.noahsarch.deeperdark.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record CollarFuelData(int fireTicks, int waterTicks) {

    public static final CollarFuelData EMPTY = new CollarFuelData(0, 0);

    public static final Codec<CollarFuelData> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.INT.optionalFieldOf("fire_ticks", 0).forGetter(CollarFuelData::fireTicks),
            Codec.INT.optionalFieldOf("water_ticks", 0).forGetter(CollarFuelData::waterTicks)
        ).apply(instance, CollarFuelData::new)
    );

    public static final StreamCodec<ByteBuf, CollarFuelData> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.INT, CollarFuelData::fireTicks,
        ByteBufCodecs.INT, CollarFuelData::waterTicks,
        CollarFuelData::new
    );

    public CollarFuelData withFire(int newFire) {
        return new CollarFuelData(newFire, waterTicks);
    }

    public CollarFuelData withWater(int newWater) {
        return new CollarFuelData(fireTicks, newWater);
    }

    public boolean isEmpty() {
        return fireTicks <= 0 && waterTicks <= 0;
    }
}

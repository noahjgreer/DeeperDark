package io.github.lucaargolo.seasons.utils;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class CropConfig {
    public static final Codec<CropConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.FLOAT.fieldOf("spring").forGetter(CropConfig::springModifier),
        Codec.FLOAT.fieldOf("summer").forGetter(CropConfig::summerModifier),
        Codec.FLOAT.fieldOf("fall").forGetter(CropConfig::fallModifier),
        Codec.FLOAT.fieldOf("winter").forGetter(CropConfig::winterModifier)
    ).apply(instance, CropConfig::new));
    public static final StreamCodec<ByteBuf, CropConfig> STREAM_CODEC = StreamCodec.of((buf, cfg) -> cfg.toBuf(buf), CropConfig::fromBuf);

    private final float springModifier;
    private final float summerModifier;
    private final float fallModifier;
    private final float winterModifier;

    public CropConfig(float springModifier, float summerModifier, float fallModifier, float winterModifier) {
        this.springModifier = springModifier;
        this.summerModifier = summerModifier;
        this.fallModifier = fallModifier;
        this.winterModifier = winterModifier;
    }

    public CropConfig(JsonElement json) {
        this.springModifier = Float.parseFloat(json.getAsJsonObject().get("spring").getAsString());
        this.summerModifier = Float.parseFloat(json.getAsJsonObject().get("summer").getAsString());
        this.fallModifier = Float.parseFloat(json.getAsJsonObject().get("fall").getAsString());
        this.winterModifier = Float.parseFloat(json.getAsJsonObject().get("winter").getAsString());
    }

    public float getModifier(Season season) {
        return switch (season) {
            case SPRING -> springModifier;
            case SUMMER -> summerModifier;
            case FALL -> fallModifier;
            case WINTER -> winterModifier;
        };
    }

    public void toBuf(ByteBuf buf) {
        buf.writeFloat(springModifier);
        buf.writeFloat(summerModifier);
        buf.writeFloat(fallModifier);
        buf.writeFloat(winterModifier);
    }

    public static CropConfig fromBuf(ByteBuf buf) {
        return new CropConfig(buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat());
    }

    public float springModifier() { return springModifier; }
    public float summerModifier() { return summerModifier; }
    public float fallModifier() { return fallModifier; }
    public float winterModifier() { return winterModifier; }
}

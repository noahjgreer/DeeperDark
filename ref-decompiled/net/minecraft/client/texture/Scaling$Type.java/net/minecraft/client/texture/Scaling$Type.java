/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.texture;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.Scaling;
import net.minecraft.util.StringIdentifiable;

@Environment(value=EnvType.CLIENT)
public static final class Scaling.Type
extends Enum<Scaling.Type>
implements StringIdentifiable {
    public static final /* enum */ Scaling.Type STRETCH = new Scaling.Type("stretch", Scaling.Stretch.CODEC);
    public static final /* enum */ Scaling.Type TILE = new Scaling.Type("tile", Scaling.Tile.CODEC);
    public static final /* enum */ Scaling.Type NINE_SLICE = new Scaling.Type("nine_slice", Scaling.NineSlice.CODEC);
    public static final Codec<Scaling.Type> CODEC;
    private final String name;
    private final MapCodec<? extends Scaling> codec;
    private static final /* synthetic */ Scaling.Type[] field_45662;

    public static Scaling.Type[] values() {
        return (Scaling.Type[])field_45662.clone();
    }

    public static Scaling.Type valueOf(String string) {
        return Enum.valueOf(Scaling.Type.class, string);
    }

    private Scaling.Type(String name, MapCodec<? extends Scaling> codec) {
        this.name = name;
        this.codec = codec;
    }

    @Override
    public String asString() {
        return this.name;
    }

    public MapCodec<? extends Scaling> getCodec() {
        return this.codec;
    }

    private static /* synthetic */ Scaling.Type[] method_52887() {
        return new Scaling.Type[]{STRETCH, TILE, NINE_SLICE};
    }

    static {
        field_45662 = Scaling.Type.method_52887();
        CODEC = StringIdentifiable.createCodec(Scaling.Type::values);
    }
}

/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.texture;

import com.mojang.serialization.Codec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.StringIdentifiable;

@Environment(value=EnvType.CLIENT)
public final class MipmapStrategy
extends Enum<MipmapStrategy>
implements StringIdentifiable {
    public static final /* enum */ MipmapStrategy AUTO = new MipmapStrategy("auto");
    public static final /* enum */ MipmapStrategy MEAN = new MipmapStrategy("mean");
    public static final /* enum */ MipmapStrategy CUTOUT = new MipmapStrategy("cutout");
    public static final /* enum */ MipmapStrategy STRICT_CUTOUT = new MipmapStrategy("strict_cutout");
    public static final /* enum */ MipmapStrategy DARK_CUTOUT = new MipmapStrategy("dark_cutout");
    public static final Codec<MipmapStrategy> CODEC;
    private final String name;
    private static final /* synthetic */ MipmapStrategy[] field_64083;

    public static MipmapStrategy[] values() {
        return (MipmapStrategy[])field_64083.clone();
    }

    public static MipmapStrategy valueOf(String string) {
        return Enum.valueOf(MipmapStrategy.class, string);
    }

    private MipmapStrategy(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }

    private static /* synthetic */ MipmapStrategy[] method_76036() {
        return new MipmapStrategy[]{AUTO, MEAN, CUTOUT, STRICT_CUTOUT, DARK_CUTOUT};
    }

    static {
        field_64083 = MipmapStrategy.method_76036();
        CODEC = StringIdentifiable.createBasicCodec(MipmapStrategy::values);
    }
}

/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.entity.passive;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

public static final class ChickenVariant.Model
extends Enum<ChickenVariant.Model>
implements StringIdentifiable {
    public static final /* enum */ ChickenVariant.Model NORMAL = new ChickenVariant.Model("normal");
    public static final /* enum */ ChickenVariant.Model COLD = new ChickenVariant.Model("cold");
    public static final Codec<ChickenVariant.Model> CODEC;
    private final String id;
    private static final /* synthetic */ ChickenVariant.Model[] field_56546;

    public static ChickenVariant.Model[] values() {
        return (ChickenVariant.Model[])field_56546.clone();
    }

    public static ChickenVariant.Model valueOf(String string) {
        return Enum.valueOf(ChickenVariant.Model.class, string);
    }

    private ChickenVariant.Model(String id) {
        this.id = id;
    }

    @Override
    public String asString() {
        return this.id;
    }

    private static /* synthetic */ ChickenVariant.Model[] method_67525() {
        return new ChickenVariant.Model[]{NORMAL, COLD};
    }

    static {
        field_56546 = ChickenVariant.Model.method_67525();
        CODEC = StringIdentifiable.createCodec(ChickenVariant.Model::values);
    }
}

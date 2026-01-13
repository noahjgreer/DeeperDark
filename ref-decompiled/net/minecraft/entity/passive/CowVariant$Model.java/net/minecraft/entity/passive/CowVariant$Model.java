/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.entity.passive;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

public static final class CowVariant.Model
extends Enum<CowVariant.Model>
implements StringIdentifiable {
    public static final /* enum */ CowVariant.Model NORMAL = new CowVariant.Model("normal");
    public static final /* enum */ CowVariant.Model COLD = new CowVariant.Model("cold");
    public static final /* enum */ CowVariant.Model WARM = new CowVariant.Model("warm");
    public static final Codec<CowVariant.Model> CODEC;
    private final String id;
    private static final /* synthetic */ CowVariant.Model[] field_56434;

    public static CowVariant.Model[] values() {
        return (CowVariant.Model[])field_56434.clone();
    }

    public static CowVariant.Model valueOf(String string) {
        return Enum.valueOf(CowVariant.Model.class, string);
    }

    private CowVariant.Model(String id) {
        this.id = id;
    }

    @Override
    public String asString() {
        return this.id;
    }

    private static /* synthetic */ CowVariant.Model[] method_67352() {
        return new CowVariant.Model[]{NORMAL, COLD, WARM};
    }

    static {
        field_56434 = CowVariant.Model.method_67352();
        CODEC = StringIdentifiable.createCodec(CowVariant.Model::values);
    }
}

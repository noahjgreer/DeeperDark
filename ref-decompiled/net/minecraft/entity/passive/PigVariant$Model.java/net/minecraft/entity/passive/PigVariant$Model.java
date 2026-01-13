/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.entity.passive;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

public static final class PigVariant.Model
extends Enum<PigVariant.Model>
implements StringIdentifiable {
    public static final /* enum */ PigVariant.Model NORMAL = new PigVariant.Model("normal");
    public static final /* enum */ PigVariant.Model COLD = new PigVariant.Model("cold");
    public static final Codec<PigVariant.Model> CODEC;
    private final String id;
    private static final /* synthetic */ PigVariant.Model[] field_55695;

    public static PigVariant.Model[] values() {
        return (PigVariant.Model[])field_55695.clone();
    }

    public static PigVariant.Model valueOf(String string) {
        return Enum.valueOf(PigVariant.Model.class, string);
    }

    private PigVariant.Model(String id) {
        this.id = id;
    }

    @Override
    public String asString() {
        return this.id;
    }

    private static /* synthetic */ PigVariant.Model[] method_66311() {
        return new PigVariant.Model[]{NORMAL, COLD};
    }

    static {
        field_55695 = PigVariant.Model.method_66311();
        CODEC = StringIdentifiable.createCodec(PigVariant.Model::values);
    }
}

/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.item;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

public static final class CrossbowItem.ChargeType
extends Enum<CrossbowItem.ChargeType>
implements StringIdentifiable {
    public static final /* enum */ CrossbowItem.ChargeType NONE = new CrossbowItem.ChargeType("none");
    public static final /* enum */ CrossbowItem.ChargeType ARROW = new CrossbowItem.ChargeType("arrow");
    public static final /* enum */ CrossbowItem.ChargeType ROCKET = new CrossbowItem.ChargeType("rocket");
    public static final Codec<CrossbowItem.ChargeType> CODEC;
    private final String name;
    private static final /* synthetic */ CrossbowItem.ChargeType[] field_55211;

    public static CrossbowItem.ChargeType[] values() {
        return (CrossbowItem.ChargeType[])field_55211.clone();
    }

    public static CrossbowItem.ChargeType valueOf(String string) {
        return Enum.valueOf(CrossbowItem.ChargeType.class, string);
    }

    private CrossbowItem.ChargeType(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }

    private static /* synthetic */ CrossbowItem.ChargeType[] method_65354() {
        return new CrossbowItem.ChargeType[]{NONE, ARROW, ROCKET};
    }

    static {
        field_55211 = CrossbowItem.ChargeType.method_65354();
        CODEC = StringIdentifiable.createCodec(CrossbowItem.ChargeType::values);
    }
}

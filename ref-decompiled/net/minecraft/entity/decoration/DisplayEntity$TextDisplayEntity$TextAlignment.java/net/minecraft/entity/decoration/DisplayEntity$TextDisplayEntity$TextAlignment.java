/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.entity.decoration;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

public static final class DisplayEntity.TextDisplayEntity.TextAlignment
extends Enum<DisplayEntity.TextDisplayEntity.TextAlignment>
implements StringIdentifiable {
    public static final /* enum */ DisplayEntity.TextDisplayEntity.TextAlignment CENTER = new DisplayEntity.TextDisplayEntity.TextAlignment("center");
    public static final /* enum */ DisplayEntity.TextDisplayEntity.TextAlignment LEFT = new DisplayEntity.TextDisplayEntity.TextAlignment("left");
    public static final /* enum */ DisplayEntity.TextDisplayEntity.TextAlignment RIGHT = new DisplayEntity.TextDisplayEntity.TextAlignment("right");
    public static final Codec<DisplayEntity.TextDisplayEntity.TextAlignment> CODEC;
    private final String name;
    private static final /* synthetic */ DisplayEntity.TextDisplayEntity.TextAlignment[] field_42455;

    public static DisplayEntity.TextDisplayEntity.TextAlignment[] values() {
        return (DisplayEntity.TextDisplayEntity.TextAlignment[])field_42455.clone();
    }

    public static DisplayEntity.TextDisplayEntity.TextAlignment valueOf(String string) {
        return Enum.valueOf(DisplayEntity.TextDisplayEntity.TextAlignment.class, string);
    }

    private DisplayEntity.TextDisplayEntity.TextAlignment(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }

    private static /* synthetic */ DisplayEntity.TextDisplayEntity.TextAlignment[] method_48920() {
        return new DisplayEntity.TextDisplayEntity.TextAlignment[]{CENTER, LEFT, RIGHT};
    }

    static {
        field_42455 = DisplayEntity.TextDisplayEntity.TextAlignment.method_48920();
        CODEC = StringIdentifiable.createCodec(DisplayEntity.TextDisplayEntity.TextAlignment::values);
    }
}

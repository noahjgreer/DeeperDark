/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.util.collection;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.collection.ListOperation;

public static final class ListOperation.Mode
extends Enum<ListOperation.Mode>
implements StringIdentifiable {
    public static final /* enum */ ListOperation.Mode REPLACE_ALL = new ListOperation.Mode("replace_all", ListOperation.ReplaceAll.CODEC);
    public static final /* enum */ ListOperation.Mode REPLACE_SECTION = new ListOperation.Mode("replace_section", ListOperation.ReplaceSection.CODEC);
    public static final /* enum */ ListOperation.Mode INSERT = new ListOperation.Mode("insert", ListOperation.Insert.CODEC);
    public static final /* enum */ ListOperation.Mode APPEND = new ListOperation.Mode("append", ListOperation.Append.CODEC);
    public static final Codec<ListOperation.Mode> CODEC;
    private final String id;
    final MapCodec<? extends ListOperation> codec;
    private static final /* synthetic */ ListOperation.Mode[] field_49864;

    public static ListOperation.Mode[] values() {
        return (ListOperation.Mode[])field_49864.clone();
    }

    public static ListOperation.Mode valueOf(String string) {
        return Enum.valueOf(ListOperation.Mode.class, string);
    }

    private ListOperation.Mode(String id, MapCodec<? extends ListOperation> codec) {
        this.id = id;
        this.codec = codec;
    }

    public MapCodec<? extends ListOperation> getCodec() {
        return this.codec;
    }

    @Override
    public String asString() {
        return this.id;
    }

    private static /* synthetic */ ListOperation.Mode[] method_58199() {
        return new ListOperation.Mode[]{REPLACE_ALL, REPLACE_SECTION, INSERT, APPEND};
    }

    static {
        field_49864 = ListOperation.Mode.method_58199();
        CODEC = StringIdentifiable.createCodec(ListOperation.Mode::values);
    }
}

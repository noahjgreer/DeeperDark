/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Lifecycle
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.text;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import net.minecraft.text.HoverEvent;
import net.minecraft.util.StringIdentifiable;

public static final class HoverEvent.Action
extends Enum<HoverEvent.Action>
implements StringIdentifiable {
    public static final /* enum */ HoverEvent.Action SHOW_TEXT = new HoverEvent.Action("show_text", true, HoverEvent.ShowText.CODEC);
    public static final /* enum */ HoverEvent.Action SHOW_ITEM = new HoverEvent.Action("show_item", true, HoverEvent.ShowItem.CODEC);
    public static final /* enum */ HoverEvent.Action SHOW_ENTITY = new HoverEvent.Action("show_entity", true, HoverEvent.ShowEntity.CODEC);
    public static final Codec<HoverEvent.Action> UNVALIDATED_CODEC;
    public static final Codec<HoverEvent.Action> CODEC;
    private final String name;
    private final boolean parsable;
    final MapCodec<? extends HoverEvent> codec;
    private static final /* synthetic */ HoverEvent.Action[] field_55910;

    public static HoverEvent.Action[] values() {
        return (HoverEvent.Action[])field_55910.clone();
    }

    public static HoverEvent.Action valueOf(String string) {
        return Enum.valueOf(HoverEvent.Action.class, string);
    }

    private HoverEvent.Action(String name, boolean parsable, MapCodec<? extends HoverEvent> codec) {
        this.name = name;
        this.parsable = parsable;
        this.codec = codec;
    }

    public boolean isParsable() {
        return this.parsable;
    }

    @Override
    public String asString() {
        return this.name;
    }

    public String toString() {
        return "<action " + this.name + ">";
    }

    private static DataResult<HoverEvent.Action> validate(HoverEvent.Action action) {
        if (!action.isParsable()) {
            return DataResult.error(() -> "Action not allowed: " + String.valueOf(action));
        }
        return DataResult.success((Object)action, (Lifecycle)Lifecycle.stable());
    }

    private static /* synthetic */ HoverEvent.Action[] method_66576() {
        return new HoverEvent.Action[]{SHOW_TEXT, SHOW_ITEM, SHOW_ENTITY};
    }

    static {
        field_55910 = HoverEvent.Action.method_66576();
        UNVALIDATED_CODEC = StringIdentifiable.createBasicCodec(HoverEvent.Action::values);
        CODEC = UNVALIDATED_CODEC.validate(HoverEvent.Action::validate);
    }
}

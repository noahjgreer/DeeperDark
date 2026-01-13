/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 */
package net.minecraft.dialog.action;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtString;

public static interface DialogAction.ValueGetter {
    public String get();

    public NbtElement getAsNbt();

    public static Map<String, String> resolveAll(Map<String, DialogAction.ValueGetter> valueGetters) {
        return Maps.transformValues(valueGetters, DialogAction.ValueGetter::get);
    }

    public static DialogAction.ValueGetter of(final String value) {
        return new DialogAction.ValueGetter(){

            @Override
            public String get() {
                return value;
            }

            @Override
            public NbtElement getAsNbt() {
                return NbtString.of(value);
            }
        };
    }

    public static DialogAction.ValueGetter of(final Supplier<String> valueSupplier) {
        return new DialogAction.ValueGetter(){

            @Override
            public String get() {
                return (String)valueSupplier.get();
            }

            @Override
            public NbtElement getAsNbt() {
                return NbtString.of((String)valueSupplier.get());
            }
        };
    }
}

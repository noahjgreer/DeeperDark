/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Suppliers
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.util;

import com.google.common.base.Suppliers;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public static final class InputUtil.Key {
    private final String translationKey;
    private final InputUtil.Type type;
    private final int code;
    private final Supplier<Text> localizedText;
    static final Map<String, InputUtil.Key> KEYS = Maps.newHashMap();

    InputUtil.Key(String translationKey, InputUtil.Type type, int code) {
        this.translationKey = translationKey;
        this.type = type;
        this.code = code;
        this.localizedText = Suppliers.memoize(() -> type.textTranslator.apply(code, translationKey));
        KEYS.put(translationKey, this);
    }

    public InputUtil.Type getCategory() {
        return this.type;
    }

    public int getCode() {
        return this.code;
    }

    public String getTranslationKey() {
        return this.translationKey;
    }

    public Text getLocalizedText() {
        return this.localizedText.get();
    }

    public OptionalInt toInt() {
        if (this.code >= 48 && this.code <= 57) {
            return OptionalInt.of(this.code - 48);
        }
        if (this.code >= 320 && this.code <= 329) {
            return OptionalInt.of(this.code - 320);
        }
        return OptionalInt.empty();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        InputUtil.Key key = (InputUtil.Key)o;
        return this.code == key.code && this.type == key.type;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.type, this.code});
    }

    public String toString() {
        return this.translationKey;
    }
}

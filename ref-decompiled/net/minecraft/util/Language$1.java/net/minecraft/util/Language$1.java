/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util;

import java.util.Map;
import java.util.Optional;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.TextVisitFactory;
import net.minecraft.util.Language;

static class Language.1
extends Language {
    final /* synthetic */ Map field_25308;

    Language.1(Map map) {
        this.field_25308 = map;
    }

    @Override
    public String get(String key, String fallback) {
        return this.field_25308.getOrDefault(key, fallback);
    }

    @Override
    public boolean hasTranslation(String key) {
        return this.field_25308.containsKey(key);
    }

    @Override
    public boolean isRightToLeft() {
        return false;
    }

    @Override
    public OrderedText reorder(StringVisitable text) {
        return visitor -> text.visit((style, string) -> TextVisitFactory.visitFormatted(string, style, visitor) ? Optional.empty() : StringVisitable.TERMINATE_VISIT, Style.EMPTY).isPresent();
    }
}

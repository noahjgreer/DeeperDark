/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.nbt;

import net.minecraft.util.packrat.CursorExceptionType;
import net.minecraft.util.packrat.TokenParsingRule;

class SnbtParsing.4
extends TokenParsingRule {
    SnbtParsing.4(int i, CursorExceptionType cursorExceptionType) {
        super(i, cursorExceptionType);
    }

    @Override
    protected boolean isValidChar(char c) {
        return switch (c) {
            case '\"', '\'', '\\' -> false;
            default -> true;
        };
    }
}

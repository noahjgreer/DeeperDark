/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.nbt;

import net.minecraft.util.packrat.CursorExceptionType;
import net.minecraft.util.packrat.NumeralParsingRule;

class SnbtParsing.1
extends NumeralParsingRule {
    SnbtParsing.1(CursorExceptionType cursorExceptionType, CursorExceptionType cursorExceptionType2) {
        super(cursorExceptionType, cursorExceptionType2);
    }

    @Override
    protected boolean accepts(char c) {
        return switch (c) {
            case '0', '1', '_' -> true;
            default -> false;
        };
    }
}

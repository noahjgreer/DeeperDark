/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.nbt;

import net.minecraft.util.packrat.CursorExceptionType;
import net.minecraft.util.packrat.NumeralParsingRule;

class SnbtParsing.2
extends NumeralParsingRule {
    SnbtParsing.2(CursorExceptionType cursorExceptionType, CursorExceptionType cursorExceptionType2) {
        super(cursorExceptionType, cursorExceptionType2);
    }

    @Override
    protected boolean accepts(char c) {
        return switch (c) {
            case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '_' -> true;
            default -> false;
        };
    }
}

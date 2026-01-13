/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.nbt;

import net.minecraft.util.packrat.CursorExceptionType;
import net.minecraft.util.packrat.NumeralParsingRule;

class SnbtParsing.3
extends NumeralParsingRule {
    SnbtParsing.3(CursorExceptionType cursorExceptionType, CursorExceptionType cursorExceptionType2) {
        super(cursorExceptionType, cursorExceptionType2);
    }

    @Override
    protected boolean accepts(char c) {
        return switch (c) {
            case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', '_', 'a', 'b', 'c', 'd', 'e', 'f' -> true;
            default -> false;
        };
    }
}

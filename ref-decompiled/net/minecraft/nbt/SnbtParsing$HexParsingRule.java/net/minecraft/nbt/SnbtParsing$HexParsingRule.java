/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.nbt;

import net.minecraft.util.packrat.CursorExceptionType;
import net.minecraft.util.packrat.TokenParsingRule;

static class SnbtParsing.HexParsingRule
extends TokenParsingRule {
    public SnbtParsing.HexParsingRule(int length) {
        super(length, length, CursorExceptionType.create(EXPECTED_HEX_ESCAPE_EXCEPTION, String.valueOf(length)));
    }

    @Override
    protected boolean isValidChar(char c) {
        return switch (c) {
            case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'a', 'b', 'c', 'd', 'e', 'f' -> true;
            default -> false;
        };
    }
}

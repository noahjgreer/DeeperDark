/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.chars.CharList
 */
package net.minecraft.nbt;

import it.unimi.dsi.fastutil.chars.CharList;
import net.minecraft.nbt.SnbtParsing;
import net.minecraft.util.packrat.Literals;

class SnbtParsing.5
extends Literals.CharacterLiteral {
    SnbtParsing.5(CharList charList) {
        super(charList);
    }

    @Override
    protected boolean accepts(char c) {
        return SnbtParsing.isPartOfDecimal(c);
    }
}

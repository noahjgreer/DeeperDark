/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.StringReader
 */
package net.minecraft.command.argument;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.StringReader;
import java.util.stream.Stream;
import net.minecraft.command.argument.ItemPredicateParsing;
import net.minecraft.util.Identifier;
import net.minecraft.util.packrat.IdentifiableParsingRule;
import net.minecraft.util.packrat.ParsingRuleEntry;

static class ItemPredicateParsing.ComponentParsingRule<T, C, P>
extends IdentifiableParsingRule<ItemPredicateParsing.Callbacks<T, C, P>, C> {
    ItemPredicateParsing.ComponentParsingRule(ParsingRuleEntry<StringReader, Identifier> idParsingRule, ItemPredicateParsing.Callbacks<T, C, P> callbacks) {
        super(idParsingRule, callbacks);
    }

    @Override
    protected C parse(ImmutableStringReader reader, Identifier id) throws Exception {
        return ((ItemPredicateParsing.Callbacks)this.callbacks).componentCheck(reader, id);
    }

    @Override
    public Stream<Identifier> possibleIds() {
        return ((ItemPredicateParsing.Callbacks)this.callbacks).streamComponentIds();
    }
}

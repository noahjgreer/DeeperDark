/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.schemas.Schema
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.schemas.Schema;
import java.util.function.Function;
import net.minecraft.datafixer.fix.ItemNameFix;

static class ItemNameFix.1
extends ItemNameFix {
    final /* synthetic */ Function field_5677;

    ItemNameFix.1(Schema schema, String string, Function function) {
        this.field_5677 = function;
        super(schema, string);
    }

    @Override
    protected String rename(String input) {
        return (String)this.field_5677.apply(input);
    }
}

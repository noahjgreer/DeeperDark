/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.schemas.Schema
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.schemas.Schema;
import java.util.function.Function;
import net.minecraft.datafixer.fix.BlockNameFix;

static class BlockNameFix.1
extends BlockNameFix {
    final /* synthetic */ Function field_15829;

    BlockNameFix.1(Schema schema, String string, Function function) {
        this.field_15829 = function;
        super(schema, string);
    }

    @Override
    protected String rename(String oldName) {
        return (String)this.field_15829.apply(oldName);
    }
}

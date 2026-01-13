/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL$TypeReference
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.serialization.Dynamic
 */
package net.minecraft.datafixer;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.fix.BlockEntityCustomNameToTextFix;
import net.minecraft.datafixer.fix.ChoiceWriteReadFix;

static class Schemas.1
extends ChoiceWriteReadFix {
    Schemas.1(Schema schema, boolean bl, String string, DSL.TypeReference typeReference, String string2) {
        super(schema, bl, string, typeReference, string2);
    }

    @Override
    protected <T> Dynamic<T> transform(Dynamic<T> data) {
        return BlockEntityCustomNameToTextFix.fixCustomName(data);
    }
}

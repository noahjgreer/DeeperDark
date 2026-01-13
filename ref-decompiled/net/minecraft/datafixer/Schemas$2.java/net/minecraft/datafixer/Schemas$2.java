/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 */
package net.minecraft.datafixer;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.TextFixes;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

static class Schemas.2
extends DataFix {
    Schemas.2(Schema schema, boolean bl) {
        super(schema, bl);
    }

    protected TypeRewriteRule makeRule() {
        Type type = this.getInputSchema().getType(TypeReferences.ENTITY);
        OpticFinder opticFinder = DSL.fieldFinder((String)"id", IdentifierNormalizingSchema.getIdentifierType());
        OpticFinder opticFinder2 = type.findField("CustomName");
        OpticFinder opticFinder3 = DSL.typeFinder((Type)this.getInputSchema().getType(TypeReferences.TEXT_COMPONENT));
        return this.fixTypeEverywhereTyped("Command block minecart custom name fix", type, typed2 -> {
            String string = typed2.getOptional(opticFinder).orElse("");
            if (!"minecraft:commandblock_minecart".equals(string)) {
                return typed2;
            }
            return typed2.updateTyped(opticFinder2, typed -> typed.update(opticFinder3, pair -> pair.mapSecond(TextFixes::text)));
        });
    }
}

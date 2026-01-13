/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.util.Pair
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.util.Pair;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

record ThrownPotionSplitFix.class_10681(OpticFinder<?> itemFinder, OpticFinder<Pair<String, String>> itemIdFinder) {
    public String method_67102(Typed<?> typed2) {
        return typed2.getOptionalTyped(this.itemFinder).flatMap(typed -> typed.getOptional(this.itemIdFinder)).map(Pair::getSecond).map(IdentifierNormalizingSchema::normalize).orElse("");
    }
}

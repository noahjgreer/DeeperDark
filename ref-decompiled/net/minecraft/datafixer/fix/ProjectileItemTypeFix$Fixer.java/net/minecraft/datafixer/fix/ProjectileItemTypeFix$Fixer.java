/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.types.Type
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.Typed;
import com.mojang.datafixers.types.Type;

static interface ProjectileItemTypeFix.Fixer<F> {
    public Typed<F> fix(Typed<?> var1, Type<F> var2);
}

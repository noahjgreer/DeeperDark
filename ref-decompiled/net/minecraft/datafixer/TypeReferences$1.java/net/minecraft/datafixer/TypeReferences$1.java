/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL$TypeReference
 */
package net.minecraft.datafixer;

import com.mojang.datafixers.DSL;

static class TypeReferences.1
implements DSL.TypeReference {
    final /* synthetic */ String field_51348;

    TypeReferences.1(String string) {
        this.field_51348 = string;
    }

    public String typeName() {
        return this.field_51348;
    }

    public String toString() {
        return "@" + this.field_51348;
    }
}

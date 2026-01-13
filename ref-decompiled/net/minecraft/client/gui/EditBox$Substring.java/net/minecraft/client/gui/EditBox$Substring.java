/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
protected static final class EditBox.Substring
extends Record {
    final int beginIndex;
    final int endIndex;
    static final EditBox.Substring EMPTY = new EditBox.Substring(0, 0);

    protected EditBox.Substring(int beginIndex, int endIndex) {
        this.beginIndex = beginIndex;
        this.endIndex = endIndex;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{EditBox.Substring.class, "beginIndex;endIndex", "beginIndex", "endIndex"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{EditBox.Substring.class, "beginIndex;endIndex", "beginIndex", "endIndex"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{EditBox.Substring.class, "beginIndex;endIndex", "beginIndex", "endIndex"}, this, object);
    }

    public int beginIndex() {
        return this.beginIndex;
    }

    public int endIndex() {
        return this.endIndex;
    }
}

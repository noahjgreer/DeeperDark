/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.storage;

import net.minecraft.storage.WriteView;

public static interface WriteView.ListView {
    public WriteView add();

    public void removeLast();

    public boolean isEmpty();
}

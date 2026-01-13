/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.decoration;

import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.text.Text;

@FunctionalInterface
public static interface DisplayEntity.TextDisplayEntity.LineSplitter {
    public DisplayEntity.TextDisplayEntity.TextLines split(Text var1, int var2);
}

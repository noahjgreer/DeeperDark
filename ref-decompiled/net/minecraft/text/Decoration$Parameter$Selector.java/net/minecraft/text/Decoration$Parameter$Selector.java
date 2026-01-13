/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.text;

import net.minecraft.network.message.MessageType;
import net.minecraft.text.Text;

public static interface Decoration.Parameter.Selector {
    public Text select(Text var1, MessageType.Parameters var2);
}

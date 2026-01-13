/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.font;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Style;

@FunctionalInterface
@Environment(value=EnvType.CLIENT)
public static interface TextHandler.WidthRetriever {
    public float getWidth(int var1, Style var2);
}

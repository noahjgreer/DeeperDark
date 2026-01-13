/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.widget;

import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.MutableText;

@Environment(value=EnvType.CLIENT)
public static interface ButtonWidget.NarrationSupplier {
    public MutableText createNarrationMessage(Supplier<MutableText> var1);
}

/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.GlException;

@Environment(value=EnvType.CLIENT)
public static class Window.GlErroredException
extends GlException {
    Window.GlErroredException(String string) {
        super(string);
    }
}

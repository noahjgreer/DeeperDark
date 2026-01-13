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
public static class NarratorManager.InactiveNarratorLibraryException
extends GlException {
    public NarratorManager.InactiveNarratorLibraryException(String string) {
        super(string);
    }
}

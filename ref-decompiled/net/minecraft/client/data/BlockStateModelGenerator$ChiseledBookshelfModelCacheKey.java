/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.data;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.data.Model;

@Environment(value=EnvType.CLIENT)
record BlockStateModelGenerator.ChiseledBookshelfModelCacheKey(Model template, String modelSuffix) {
}

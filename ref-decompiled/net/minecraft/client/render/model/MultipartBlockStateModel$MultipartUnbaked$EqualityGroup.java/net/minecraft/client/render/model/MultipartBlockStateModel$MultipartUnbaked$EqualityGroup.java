/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model;

import it.unimi.dsi.fastutil.ints.IntList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.MultipartBlockStateModel;

@Environment(value=EnvType.CLIENT)
record MultipartBlockStateModel.MultipartUnbaked.EqualityGroup(MultipartBlockStateModel.MultipartUnbaked model, IntList selectors) {
}

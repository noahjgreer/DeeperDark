/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.item;

import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ItemAsset;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public record ItemAssetsLoader.Result(Map<Identifier, ItemAsset> contents) {
}

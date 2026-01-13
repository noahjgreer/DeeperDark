/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.world;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.DataCache;

@FunctionalInterface
@Environment(value=EnvType.CLIENT)
public static interface DataCache.CacheContext<C extends DataCache.CacheContext<C>> {
    public void registerForCleaning(DataCache<C, ?> var1);
}

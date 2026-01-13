/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.exception.RealmsServiceException;

@FunctionalInterface
@Environment(value=EnvType.CLIENT)
public static interface RealmsUtil.RealmsSupplier<T> {
    public T apply(RealmsClient var1) throws RealmsServiceException;
}

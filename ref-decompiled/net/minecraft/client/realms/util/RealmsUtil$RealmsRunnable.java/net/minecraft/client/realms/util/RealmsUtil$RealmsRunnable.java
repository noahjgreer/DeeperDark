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
import net.minecraft.client.realms.util.RealmsUtil;

@FunctionalInterface
@Environment(value=EnvType.CLIENT)
public static interface RealmsUtil.RealmsRunnable
extends RealmsUtil.RealmsSupplier<Void> {
    public void accept(RealmsClient var1) throws RealmsServiceException;

    @Override
    default public Void apply(RealmsClient realmsClient) throws RealmsServiceException {
        this.accept(realmsClient);
        return null;
    }
}

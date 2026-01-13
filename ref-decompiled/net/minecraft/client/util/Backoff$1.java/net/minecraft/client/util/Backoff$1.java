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
import net.minecraft.client.util.Backoff;

@Environment(value=EnvType.CLIENT)
class Backoff.1
implements Backoff {
    Backoff.1() {
    }

    @Override
    public long success() {
        return 1L;
    }

    @Override
    public long fail() {
        return 1L;
    }
}

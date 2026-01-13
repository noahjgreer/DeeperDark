/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.slf4j.Logger
 */
package net.minecraft.client.util;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.Backoff;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
static class Backoff.2
implements Backoff {
    private static final Logger LOGGER = LogUtils.getLogger();
    private int failureCount;
    final /* synthetic */ int field_39715;

    Backoff.2(int i) {
        this.field_39715 = i;
    }

    @Override
    public long success() {
        this.failureCount = 0;
        return 1L;
    }

    @Override
    public long fail() {
        ++this.failureCount;
        long l = Math.min(1L << this.failureCount, (long)this.field_39715);
        LOGGER.debug("Skipping for {} extra cycles", (Object)l);
        return l;
    }
}

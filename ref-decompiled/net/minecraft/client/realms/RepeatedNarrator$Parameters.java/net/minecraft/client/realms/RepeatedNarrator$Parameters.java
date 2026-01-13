/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.util.concurrent.RateLimiter
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms;

import com.google.common.util.concurrent.RateLimiter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
static class RepeatedNarrator.Parameters {
    final Text message;
    final RateLimiter rateLimiter;

    RepeatedNarrator.Parameters(Text text, RateLimiter rateLimiter) {
        this.message = text;
        this.rateLimiter = rateLimiter;
    }
}

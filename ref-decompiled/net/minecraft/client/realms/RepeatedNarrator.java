/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.util.concurrent.RateLimiter
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.realms.RepeatedNarrator
 *  net.minecraft.client.realms.RepeatedNarrator$Parameters
 *  net.minecraft.client.util.NarratorManager
 *  net.minecraft.text.Text
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.realms;

import com.google.common.util.concurrent.RateLimiter;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RepeatedNarrator;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.text.Text;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class RepeatedNarrator {
    private final float permitsPerSecond;
    private final AtomicReference<// Could not load outer class - annotation placement on inner may be incorrect
    @Nullable RepeatedNarrator.Parameters> params = new AtomicReference();

    public RepeatedNarrator(Duration duration) {
        this.permitsPerSecond = 1000.0f / (float)duration.toMillis();
    }

    public void narrate(NarratorManager narratorManager, Text text) {
        Parameters parameters2 = (Parameters)this.params.updateAndGet(parameters -> {
            if (parameters == null || !text.equals((Object)parameters.message)) {
                return new Parameters(text, RateLimiter.create((double)this.permitsPerSecond));
            }
            return parameters;
        });
        if (parameters2.rateLimiter.tryAcquire(1)) {
            narratorManager.narrateSystemImmediately(text);
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.math.LongMath
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.objects.Object2BooleanFunction
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.resource.PeriodicNotificationManager
 *  net.minecraft.client.resource.PeriodicNotificationManager$Entry
 *  net.minecraft.client.resource.PeriodicNotificationManager$NotifyTask
 *  net.minecraft.resource.ResourceManager
 *  net.minecraft.resource.SinglePreparationResourceReloader
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.StrictJsonParser
 *  net.minecraft.util.Util
 *  net.minecraft.util.profiler.Profiler
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.resource;

import com.google.common.collect.ImmutableMap;
import com.google.common.math.LongMath;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2BooleanFunction;
import java.io.BufferedReader;
import java.io.Reader;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.PeriodicNotificationManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.StrictJsonParser;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.Profiler;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class PeriodicNotificationManager
extends SinglePreparationResourceReloader<Map<String, List<Entry>>>
implements AutoCloseable {
    private static final Codec<Map<String, List<Entry>>> CODEC = Codec.unboundedMap((Codec)Codec.STRING, (Codec)RecordCodecBuilder.create(instance -> instance.group((App)Codec.LONG.optionalFieldOf("delay", (Object)0L).forGetter(Entry::delay), (App)Codec.LONG.fieldOf("period").forGetter(Entry::period), (App)Codec.STRING.fieldOf("title").forGetter(Entry::title), (App)Codec.STRING.fieldOf("message").forGetter(Entry::message)).apply((Applicative)instance, Entry::new)).listOf());
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Identifier id;
    private final Object2BooleanFunction<String> countryPredicate;
    private @Nullable Timer timer;
    private // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable PeriodicNotificationManager.NotifyTask task;

    public PeriodicNotificationManager(Identifier id, Object2BooleanFunction<String> countryPredicate) {
        this.id = id;
        this.countryPredicate = countryPredicate;
    }

    protected Map<String, List<Entry>> prepare(ResourceManager resourceManager, Profiler profiler) {
        Map map;
        block8: {
            BufferedReader reader = resourceManager.openAsReader(this.id);
            try {
                map = (Map)CODEC.parse((DynamicOps)JsonOps.INSTANCE, (Object)StrictJsonParser.parse((Reader)reader)).result().orElseThrow();
                if (reader == null) break block8;
            }
            catch (Throwable throwable) {
                try {
                    if (reader != null) {
                        try {
                            ((Reader)reader).close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (Exception exception) {
                    LOGGER.warn("Failed to load {}", (Object)this.id, (Object)exception);
                    return ImmutableMap.of();
                }
            }
            ((Reader)reader).close();
        }
        return map;
    }

    protected void apply(Map<String, List<Entry>> map, ResourceManager resourceManager, Profiler profiler) {
        List list = map.entrySet().stream().filter(entry -> (Boolean)this.countryPredicate.apply((Object)((String)entry.getKey()))).map(Map.Entry::getValue).flatMap(Collection::stream).collect(Collectors.toList());
        if (list.isEmpty()) {
            this.cancelTimer();
            return;
        }
        if (list.stream().anyMatch(entry -> entry.period == 0L)) {
            Util.logErrorOrPause((String)("A periodic notification in " + String.valueOf(this.id) + " has a period of zero minutes"));
            this.cancelTimer();
            return;
        }
        long l = this.getMinDelay(list);
        long m = this.getPeriod(list, l);
        if (this.timer == null) {
            this.timer = new Timer();
        }
        this.task = this.task == null ? new NotifyTask(list, l, m) : this.task.reload(list, m);
        this.timer.scheduleAtFixedRate((TimerTask)this.task, TimeUnit.MINUTES.toMillis(l), TimeUnit.MINUTES.toMillis(m));
    }

    @Override
    public void close() {
        this.cancelTimer();
    }

    private void cancelTimer() {
        if (this.timer != null) {
            this.timer.cancel();
        }
    }

    private long getPeriod(List<Entry> entries, long minDelay) {
        return entries.stream().mapToLong(entry -> {
            long m = entry.delay - minDelay;
            return LongMath.gcd((long)m, (long)entry.period);
        }).reduce(LongMath::gcd).orElseThrow(() -> new IllegalStateException("Empty notifications from: " + String.valueOf(this.id)));
    }

    private long getMinDelay(List<Entry> entries) {
        return entries.stream().mapToLong(entry -> entry.delay).min().orElse(0L);
    }

    protected /* synthetic */ Object prepare(ResourceManager manager, Profiler profiler) {
        return this.prepare(manager, profiler);
    }
}


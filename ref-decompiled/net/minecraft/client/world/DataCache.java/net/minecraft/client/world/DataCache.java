/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.world;

import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class DataCache<C extends CacheContext<C>, D> {
    private final Function<C, D> dataFunction;
    private @Nullable C context;
    private @Nullable D data;

    public DataCache(Function<C, D> dataFunction) {
        this.dataFunction = dataFunction;
    }

    public D compute(C context) {
        if (context == this.context && this.data != null) {
            return this.data;
        }
        D object = this.dataFunction.apply(context);
        this.data = object;
        this.context = context;
        context.registerForCleaning(this);
        return object;
    }

    public void clean() {
        this.data = null;
        this.context = null;
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    public static interface CacheContext<C extends CacheContext<C>> {
        public void registerForCleaning(DataCache<C, ?> var1);
    }
}

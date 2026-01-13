/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.RunArgs;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record RunArgs.QuickPlay(@Nullable String logPath, RunArgs.QuickPlayVariant variant) {
    public boolean isEnabled() {
        return this.variant.isEnabled();
    }
}

/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.RunArgs;

@Environment(value=EnvType.CLIENT)
public record RunArgs.DisabledQuickPlay() implements RunArgs.QuickPlayVariant
{
    @Override
    public boolean isEnabled() {
        return false;
    }
}

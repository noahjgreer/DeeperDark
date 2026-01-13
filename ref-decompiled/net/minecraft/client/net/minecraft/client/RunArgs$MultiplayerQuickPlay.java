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
import net.minecraft.util.StringHelper;

@Environment(value=EnvType.CLIENT)
public record RunArgs.MultiplayerQuickPlay(String serverAddress) implements RunArgs.QuickPlayVariant
{
    @Override
    public boolean isEnabled() {
        return !StringHelper.isBlank(this.serverAddress);
    }
}

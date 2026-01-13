/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementManager;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.PlacedAdvancement;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public static interface ClientAdvancementManager.Listener
extends AdvancementManager.Listener {
    public void setProgress(PlacedAdvancement var1, AdvancementProgress var2);

    public void selectTab(@Nullable AdvancementEntry var1);
}

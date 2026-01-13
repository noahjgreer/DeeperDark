/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.tooltip;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.PlayerSkinCache;
import net.minecraft.item.tooltip.TooltipData;

@Environment(value=EnvType.CLIENT)
public record ProfilesTooltipComponent.ProfilesData(List<PlayerSkinCache.Entry> profiles) implements TooltipData
{
}

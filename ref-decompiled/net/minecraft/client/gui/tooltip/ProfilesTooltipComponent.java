/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.PlayerSkinDrawer
 *  net.minecraft.client.gui.tooltip.ProfilesTooltipComponent
 *  net.minecraft.client.gui.tooltip.ProfilesTooltipComponent$ProfilesData
 *  net.minecraft.client.gui.tooltip.TooltipComponent
 *  net.minecraft.client.texture.PlayerSkinCache$Entry
 *  net.minecraft.entity.player.SkinTextures
 */
package net.minecraft.client.gui.tooltip;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.gui.tooltip.ProfilesTooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.texture.PlayerSkinCache;
import net.minecraft.entity.player.SkinTextures;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class ProfilesTooltipComponent
implements TooltipComponent {
    private static final int field_52140 = 10;
    private static final int field_52141 = 2;
    private final List<PlayerSkinCache.Entry> profiles;

    public ProfilesTooltipComponent(ProfilesData data) {
        this.profiles = data.profiles();
    }

    public int getHeight(TextRenderer textRenderer) {
        return this.profiles.size() * 12 + 2;
    }

    private static String getName(PlayerSkinCache.Entry entry) {
        return entry.getProfile().name();
    }

    public int getWidth(TextRenderer textRenderer) {
        int i = 0;
        for (PlayerSkinCache.Entry entry : this.profiles) {
            int j = textRenderer.getWidth(ProfilesTooltipComponent.getName((PlayerSkinCache.Entry)entry));
            if (j <= i) continue;
            i = j;
        }
        return i + 10 + 6;
    }

    public void drawItems(TextRenderer textRenderer, int x, int y, int width, int height, DrawContext context) {
        for (int i = 0; i < this.profiles.size(); ++i) {
            PlayerSkinCache.Entry entry = (PlayerSkinCache.Entry)this.profiles.get(i);
            int j = y + 2 + i * 12;
            PlayerSkinDrawer.draw((DrawContext)context, (SkinTextures)entry.getTextures(), (int)(x + 2), (int)j, (int)10);
            context.drawTextWithShadow(textRenderer, ProfilesTooltipComponent.getName((PlayerSkinCache.Entry)entry), x + 10 + 4, j + 2, -1);
        }
    }
}


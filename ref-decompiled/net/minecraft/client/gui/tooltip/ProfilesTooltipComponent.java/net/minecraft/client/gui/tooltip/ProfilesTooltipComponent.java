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
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.texture.PlayerSkinCache;
import net.minecraft.item.tooltip.TooltipData;

@Environment(value=EnvType.CLIENT)
public class ProfilesTooltipComponent
implements TooltipComponent {
    private static final int field_52140 = 10;
    private static final int field_52141 = 2;
    private final List<PlayerSkinCache.Entry> profiles;

    public ProfilesTooltipComponent(ProfilesData data) {
        this.profiles = data.profiles();
    }

    @Override
    public int getHeight(TextRenderer textRenderer) {
        return this.profiles.size() * 12 + 2;
    }

    private static String getName(PlayerSkinCache.Entry entry) {
        return entry.getProfile().name();
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        int i = 0;
        for (PlayerSkinCache.Entry entry : this.profiles) {
            int j = textRenderer.getWidth(ProfilesTooltipComponent.getName(entry));
            if (j <= i) continue;
            i = j;
        }
        return i + 10 + 6;
    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, int width, int height, DrawContext context) {
        for (int i = 0; i < this.profiles.size(); ++i) {
            PlayerSkinCache.Entry entry = this.profiles.get(i);
            int j = y + 2 + i * 12;
            PlayerSkinDrawer.draw(context, entry.getTextures(), x + 2, j, 10);
            context.drawTextWithShadow(textRenderer, ProfilesTooltipComponent.getName(entry), x + 10 + 4, j + 2, -1);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record ProfilesData(List<PlayerSkinCache.Entry> profiles) implements TooltipData
    {
    }
}

/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.hud;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;

@Environment(value=EnvType.CLIENT)
class ChatHud.1
implements ChatHud.LineConsumer {
    boolean styledCurrentLine;
    final /* synthetic */ int field_63867;
    final /* synthetic */ int field_63868;
    final /* synthetic */ int field_63869;
    final /* synthetic */ ChatHud.Backend field_63870;
    final /* synthetic */ float field_63871;
    final /* synthetic */ int field_63872;

    ChatHud.1() {
        this.field_63867 = i;
        this.field_63868 = j;
        this.field_63869 = k;
        this.field_63870 = backend;
        this.field_63871 = f;
        this.field_63872 = l;
    }

    @Override
    public void accept(ChatHudLine.Visible visible, int i, float f) {
        boolean bl2;
        int j = this.field_63867 - i * this.field_63868;
        int k = j - this.field_63868;
        int l = j - this.field_63869;
        boolean bl = this.field_63870.text(l, f * this.field_63871, visible.content());
        this.styledCurrentLine |= bl;
        if (visible.endOfEntry()) {
            bl2 = this.styledCurrentLine;
            this.styledCurrentLine = false;
        } else {
            bl2 = false;
        }
        MessageIndicator messageIndicator = visible.indicator();
        if (messageIndicator != null) {
            this.field_63870.indicator(-4, k, -2, j, f * this.field_63871, messageIndicator);
            if (messageIndicator.icon() != null) {
                int m = visible.getWidth(ChatHud.this.client.textRenderer);
                int n = l + this.field_63872;
                this.field_63870.indicatorIcon(m, n, bl2, messageIndicator, messageIndicator.icon());
            }
        }
    }
}

/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.narration;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.narration.ScreenNarrator;

@Environment(value=EnvType.CLIENT)
class ScreenNarrator.1
implements Consumer<String> {
    private boolean first = true;
    final /* synthetic */ StringBuilder field_33798;

    ScreenNarrator.1(ScreenNarrator screenNarrator, StringBuilder stringBuilder) {
        this.field_33798 = stringBuilder;
    }

    @Override
    public void accept(String string) {
        if (!this.first) {
            this.field_33798.append(". ");
        }
        this.first = false;
        this.field_33798.append(string);
    }

    @Override
    public /* synthetic */ void accept(Object sentence) {
        this.accept((String)sentence);
    }
}

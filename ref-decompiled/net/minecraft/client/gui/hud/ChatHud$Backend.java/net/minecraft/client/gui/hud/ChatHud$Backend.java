/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix3x2f
 */
package net.minecraft.client.gui.hud;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.text.OrderedText;
import org.joml.Matrix3x2f;

@Environment(value=EnvType.CLIENT)
public static interface ChatHud.Backend {
    public void updatePose(Consumer<Matrix3x2f> var1);

    public void fill(int var1, int var2, int var3, int var4, int var5);

    public boolean text(int var1, float var2, OrderedText var3);

    public void indicator(int var1, int var2, int var3, int var4, float var5, MessageIndicator var6);

    public void indicatorIcon(int var1, int var2, boolean var3, MessageIndicator var4, MessageIndicator.Icon var5);
}

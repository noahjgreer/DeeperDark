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

@Environment(value=EnvType.CLIENT)
final class ChatHud.ChatMethod.1
extends ChatHud.ChatMethod {
    ChatHud.ChatMethod.1(String string2) {
    }

    @Override
    public boolean shouldKeepDraft(ChatHud.Draft draft) {
        return true;
    }
}

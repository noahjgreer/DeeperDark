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
public static abstract sealed class ChatHud.ChatMethod
extends Enum<ChatHud.ChatMethod> {
    public static final /* enum */ ChatHud.ChatMethod MESSAGE = new ChatHud.ChatMethod(""){

        @Override
        public boolean shouldKeepDraft(ChatHud.Draft draft) {
            return true;
        }
    };
    public static final /* enum */ ChatHud.ChatMethod COMMAND = new ChatHud.ChatMethod("/"){

        @Override
        public boolean shouldKeepDraft(ChatHud.Draft draft) {
            return this == draft.chatMethod;
        }
    };
    private final String replacement;
    private static final /* synthetic */ ChatHud.ChatMethod[] field_62007;

    public static ChatHud.ChatMethod[] values() {
        return (ChatHud.ChatMethod[])field_62007.clone();
    }

    public static ChatHud.ChatMethod valueOf(String string) {
        return Enum.valueOf(ChatHud.ChatMethod.class, string);
    }

    ChatHud.ChatMethod(String replacement) {
        this.replacement = replacement;
    }

    public String getReplacement() {
        return this.replacement;
    }

    public abstract boolean shouldKeepDraft(ChatHud.Draft var1);

    private static /* synthetic */ ChatHud.ChatMethod[] method_73209() {
        return new ChatHud.ChatMethod[]{MESSAGE, COMMAND};
    }

    static {
        field_62007 = ChatHud.ChatMethod.method_73209();
    }
}

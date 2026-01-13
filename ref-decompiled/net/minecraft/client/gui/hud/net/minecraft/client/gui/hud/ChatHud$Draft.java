/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.hud;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.ChatHud;

@Environment(value=EnvType.CLIENT)
public static final class ChatHud.Draft
extends Record {
    private final String text;
    final ChatHud.ChatMethod chatMethod;

    public ChatHud.Draft(String text, ChatHud.ChatMethod chatMethod) {
        this.text = text;
        this.chatMethod = chatMethod;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ChatHud.Draft.class, "text;chatMethod", "text", "chatMethod"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ChatHud.Draft.class, "text;chatMethod", "text", "chatMethod"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ChatHud.Draft.class, "text;chatMethod", "text", "chatMethod"}, this, object);
    }

    public String text() {
        return this.text;
    }

    public ChatHud.ChatMethod chatMethod() {
        return this.chatMethod;
    }
}

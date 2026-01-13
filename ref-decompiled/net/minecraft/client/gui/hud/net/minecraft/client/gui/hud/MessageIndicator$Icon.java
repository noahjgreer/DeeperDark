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
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public static final class MessageIndicator.Icon
extends Enum<MessageIndicator.Icon> {
    public static final /* enum */ MessageIndicator.Icon CHAT_MODIFIED = new MessageIndicator.Icon(Identifier.ofVanilla("icon/chat_modified"), 9, 9);
    public final Identifier texture;
    public final int width;
    public final int height;
    private static final /* synthetic */ MessageIndicator.Icon[] field_39768;

    public static MessageIndicator.Icon[] values() {
        return (MessageIndicator.Icon[])field_39768.clone();
    }

    public static MessageIndicator.Icon valueOf(String string) {
        return Enum.valueOf(MessageIndicator.Icon.class, string);
    }

    private MessageIndicator.Icon(Identifier texture, int width, int height) {
        this.texture = texture;
        this.width = width;
        this.height = height;
    }

    public void draw(DrawContext context, int x, int y) {
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, this.texture, x, y, this.width, this.height);
    }

    private static /* synthetic */ MessageIndicator.Icon[] method_44711() {
        return new MessageIndicator.Icon[]{CHAT_MODIFIED};
    }

    static {
        field_39768 = MessageIndicator.Icon.method_44711();
    }
}

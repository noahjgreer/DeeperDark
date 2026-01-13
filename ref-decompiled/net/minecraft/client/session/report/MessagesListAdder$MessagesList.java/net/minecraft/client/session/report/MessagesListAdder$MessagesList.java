/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.session.report;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.session.report.log.ReceivedMessage;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public static interface MessagesListAdder.MessagesList {
    public void addMessage(int var1, ReceivedMessage.ChatMessage var2);

    public void addText(Text var1);
}

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
import net.minecraft.network.message.MessageSignatureData;

@Environment(value=EnvType.CLIENT)
record ChatHud.RemovalQueuedMessage(MessageSignatureData signature, int deletableAfter) {
}

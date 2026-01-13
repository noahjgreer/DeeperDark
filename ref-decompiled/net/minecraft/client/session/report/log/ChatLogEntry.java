/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.session.report.log.ChatLogEntry
 *  net.minecraft.client.session.report.log.ChatLogEntry$Type
 *  net.minecraft.util.StringIdentifiable
 */
package net.minecraft.client.session.report.log;

import com.mojang.serialization.Codec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.session.report.log.ChatLogEntry;
import net.minecraft.util.StringIdentifiable;

@Environment(value=EnvType.CLIENT)
public interface ChatLogEntry {
    public static final Codec<ChatLogEntry> CODEC = StringIdentifiable.createCodec(Type::values).dispatch(ChatLogEntry::getType, Type::getCodec);

    public Type getType();
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.text;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.text.ClickEvent;
import net.minecraft.util.dynamic.Codecs;

public record ClickEvent.SuggestCommand(String command) implements ClickEvent
{
    public static final MapCodec<ClickEvent.SuggestCommand> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codecs.CHAT_TEXT.fieldOf("command").forGetter(ClickEvent.SuggestCommand::command)).apply((Applicative)instance, ClickEvent.SuggestCommand::new));

    @Override
    public ClickEvent.Action getAction() {
        return ClickEvent.Action.SUGGEST_COMMAND;
    }
}

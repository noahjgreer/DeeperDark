/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.text;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.text.ClickEvent;

public record ClickEvent.CopyToClipboard(String value) implements ClickEvent
{
    public static final MapCodec<ClickEvent.CopyToClipboard> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codec.STRING.fieldOf("value").forGetter(ClickEvent.CopyToClipboard::value)).apply((Applicative)instance, ClickEvent.CopyToClipboard::new));

    @Override
    public ClickEvent.Action getAction() {
        return ClickEvent.Action.COPY_TO_CLIPBOARD;
    }
}

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
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

public record HoverEvent.ShowText(Text value) implements HoverEvent
{
    public static final MapCodec<HoverEvent.ShowText> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)TextCodecs.CODEC.fieldOf("value").forGetter(HoverEvent.ShowText::value)).apply((Applicative)instance, HoverEvent.ShowText::new));

    @Override
    public HoverEvent.Action getAction() {
        return HoverEvent.Action.SHOW_TEXT;
    }
}

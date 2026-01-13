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

public record ClickEvent.ChangePage(int page) implements ClickEvent
{
    public static final MapCodec<ClickEvent.ChangePage> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codecs.POSITIVE_INT.fieldOf("page").forGetter(ClickEvent.ChangePage::page)).apply((Applicative)instance, ClickEvent.ChangePage::new));

    @Override
    public ClickEvent.Action getAction() {
        return ClickEvent.Action.CHANGE_PAGE;
    }
}

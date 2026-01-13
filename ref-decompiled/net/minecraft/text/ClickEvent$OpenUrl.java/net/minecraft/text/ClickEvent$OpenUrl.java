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
import java.net.URI;
import net.minecraft.text.ClickEvent;
import net.minecraft.util.dynamic.Codecs;

public record ClickEvent.OpenUrl(URI uri) implements ClickEvent
{
    public static final MapCodec<ClickEvent.OpenUrl> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codecs.URI.fieldOf("url").forGetter(ClickEvent.OpenUrl::uri)).apply((Applicative)instance, ClickEvent.OpenUrl::new));

    @Override
    public ClickEvent.Action getAction() {
        return ClickEvent.Action.OPEN_URL;
    }
}

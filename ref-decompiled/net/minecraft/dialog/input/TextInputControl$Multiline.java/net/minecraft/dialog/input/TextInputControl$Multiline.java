/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.dialog.input;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.util.dynamic.Codecs;

public record TextInputControl.Multiline(Optional<Integer> maxLines, Optional<Integer> height) {
    public static final int MAX_HEIGHT = 512;
    public static final Codec<TextInputControl.Multiline> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codecs.POSITIVE_INT.optionalFieldOf("max_lines").forGetter(TextInputControl.Multiline::maxLines), (App)Codecs.rangedInt(1, 512).optionalFieldOf("height").forGetter(TextInputControl.Multiline::height)).apply((Applicative)instance, TextInputControl.Multiline::new));
}

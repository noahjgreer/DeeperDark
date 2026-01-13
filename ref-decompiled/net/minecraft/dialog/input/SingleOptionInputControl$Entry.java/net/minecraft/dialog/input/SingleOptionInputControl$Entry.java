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
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

public record SingleOptionInputControl.Entry(String id, Optional<Text> display, boolean initial) {
    public static final Codec<SingleOptionInputControl.Entry> BASE_CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.STRING.fieldOf("id").forGetter(SingleOptionInputControl.Entry::id), (App)TextCodecs.CODEC.optionalFieldOf("display").forGetter(SingleOptionInputControl.Entry::display), (App)Codec.BOOL.optionalFieldOf("initial", (Object)false).forGetter(SingleOptionInputControl.Entry::initial)).apply((Applicative)instance, SingleOptionInputControl.Entry::new));
    public static final Codec<SingleOptionInputControl.Entry> CODEC = Codec.withAlternative(BASE_CODEC, (Codec)Codec.STRING, id -> new SingleOptionInputControl.Entry((String)id, Optional.empty(), false));

    public Text getDisplay() {
        return this.display.orElseGet(() -> Text.literal(this.id));
    }
}

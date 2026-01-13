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
import java.io.File;
import java.nio.file.Path;
import net.minecraft.text.ClickEvent;

public record ClickEvent.OpenFile(String path) implements ClickEvent
{
    public static final MapCodec<ClickEvent.OpenFile> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codec.STRING.fieldOf("path").forGetter(ClickEvent.OpenFile::path)).apply((Applicative)instance, ClickEvent.OpenFile::new));

    public ClickEvent.OpenFile(File file) {
        this(file.toString());
    }

    public ClickEvent.OpenFile(Path path) {
        this(path.toFile());
    }

    public File file() {
        return new File(this.path);
    }

    @Override
    public ClickEvent.Action getAction() {
        return ClickEvent.Action.OPEN_FILE;
    }
}

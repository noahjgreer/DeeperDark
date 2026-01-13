/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.option.TextureFilteringMode
 *  net.minecraft.text.Text
 *  net.minecraft.util.function.ValueLists
 *  net.minecraft.util.function.ValueLists$OutOfBoundsHandling
 */
package net.minecraft.client.option;

import com.mojang.serialization.Codec;
import java.util.function.IntFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.util.function.ValueLists;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public final class TextureFilteringMode
extends Enum<TextureFilteringMode> {
    public static final /* enum */ TextureFilteringMode NONE = new TextureFilteringMode("NONE", 0, 0, "options.textureFiltering.none");
    public static final /* enum */ TextureFilteringMode RGSS = new TextureFilteringMode("RGSS", 1, 1, "options.textureFiltering.rgss");
    public static final /* enum */ TextureFilteringMode ANISOTROPIC = new TextureFilteringMode("ANISOTROPIC", 2, 2, "options.textureFiltering.anisotropic");
    private static final IntFunction<TextureFilteringMode> BY_ID;
    public static final Codec<TextureFilteringMode> CODEC;
    private final int id;
    private final Text text;
    private static final /* synthetic */ TextureFilteringMode[] field_64670;

    public static TextureFilteringMode[] values() {
        return (TextureFilteringMode[])field_64670.clone();
    }

    public static TextureFilteringMode valueOf(String string) {
        return Enum.valueOf(TextureFilteringMode.class, string);
    }

    private TextureFilteringMode(int id, String translationKey) {
        this.id = id;
        this.text = Text.translatable((String)translationKey);
    }

    public Text getText() {
        return this.text;
    }

    private static /* synthetic */ TextureFilteringMode[] method_76753() {
        return new TextureFilteringMode[]{NONE, RGSS, ANISOTROPIC};
    }

    static {
        field_64670 = TextureFilteringMode.method_76753();
        BY_ID = ValueLists.createIndexToValueFunction(mode -> mode.id, (Object[])TextureFilteringMode.values(), (ValueLists.OutOfBoundsHandling)ValueLists.OutOfBoundsHandling.WRAP);
        CODEC = Codec.INT.xmap(BY_ID::apply, mode -> mode.id);
    }
}


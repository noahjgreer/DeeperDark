/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.option;

import com.mojang.serialization.Codec;
import java.util.function.IntFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.util.function.ValueLists;

@Environment(value=EnvType.CLIENT)
public final class TextureFilteringMode
extends Enum<TextureFilteringMode> {
    public static final /* enum */ TextureFilteringMode NONE = new TextureFilteringMode(0, "options.textureFiltering.none");
    public static final /* enum */ TextureFilteringMode RGSS = new TextureFilteringMode(1, "options.textureFiltering.rgss");
    public static final /* enum */ TextureFilteringMode ANISOTROPIC = new TextureFilteringMode(2, "options.textureFiltering.anisotropic");
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
        this.text = Text.translatable(translationKey);
    }

    public Text getText() {
        return this.text;
    }

    private static /* synthetic */ TextureFilteringMode[] method_76753() {
        return new TextureFilteringMode[]{NONE, RGSS, ANISOTROPIC};
    }

    static {
        field_64670 = TextureFilteringMode.method_76753();
        BY_ID = ValueLists.createIndexToValueFunction(mode -> mode.id, TextureFilteringMode.values(), ValueLists.OutOfBoundsHandling.WRAP);
        CODEC = Codec.INT.xmap(BY_ID::apply, mode -> mode.id);
    }
}

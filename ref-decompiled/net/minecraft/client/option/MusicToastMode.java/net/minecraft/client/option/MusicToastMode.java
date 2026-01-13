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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;

@Environment(value=EnvType.CLIENT)
public final class MusicToastMode
extends Enum<MusicToastMode>
implements StringIdentifiable {
    public static final /* enum */ MusicToastMode NEVER = new MusicToastMode("never", "options.musicToast.never");
    public static final /* enum */ MusicToastMode PAUSE = new MusicToastMode("pause", "options.musicToast.pauseMenu");
    public static final /* enum */ MusicToastMode PAUSE_AND_TOAST = new MusicToastMode("pause_and_toast", "options.musicToast.pauseMenuAndToast");
    public static final Codec<MusicToastMode> CODEC;
    private final String id;
    private final Text text;
    private final Text tooltipText;
    private static final /* synthetic */ MusicToastMode[] field_64532;

    public static MusicToastMode[] values() {
        return (MusicToastMode[])field_64532.clone();
    }

    public static MusicToastMode valueOf(String string) {
        return Enum.valueOf(MusicToastMode.class, string);
    }

    private MusicToastMode(String id, String translationKey) {
        this.id = id;
        this.text = Text.translatable(translationKey);
        this.tooltipText = Text.translatable(translationKey + ".tooltip");
    }

    public Text getText() {
        return this.text;
    }

    public Text getTooltipText() {
        return this.tooltipText;
    }

    @Override
    public String asString() {
        return this.id;
    }

    public boolean canShow() {
        return this != NEVER;
    }

    public boolean canShowAsToast() {
        return this == PAUSE_AND_TOAST;
    }

    private static /* synthetic */ MusicToastMode[] method_76608() {
        return new MusicToastMode[]{NEVER, PAUSE, PAUSE_AND_TOAST};
    }

    static {
        field_64532 = MusicToastMode.method_76608();
        CODEC = StringIdentifiable.createCodec(MusicToastMode::values);
    }
}

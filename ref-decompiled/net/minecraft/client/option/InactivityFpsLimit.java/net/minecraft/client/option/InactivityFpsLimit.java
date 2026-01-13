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
public final class InactivityFpsLimit
extends Enum<InactivityFpsLimit>
implements StringIdentifiable {
    public static final /* enum */ InactivityFpsLimit MINIMIZED = new InactivityFpsLimit("minimized", "options.inactivityFpsLimit.minimized");
    public static final /* enum */ InactivityFpsLimit AFK = new InactivityFpsLimit("afk", "options.inactivityFpsLimit.afk");
    public static final Codec<InactivityFpsLimit> CODEC;
    private final String name;
    private final Text text;
    private static final /* synthetic */ InactivityFpsLimit[] field_52749;

    public static InactivityFpsLimit[] values() {
        return (InactivityFpsLimit[])field_52749.clone();
    }

    public static InactivityFpsLimit valueOf(String string) {
        return Enum.valueOf(InactivityFpsLimit.class, string);
    }

    private InactivityFpsLimit(String name, String translationKey) {
        this.name = name;
        this.text = Text.translatable(translationKey);
    }

    public Text getText() {
        return this.text;
    }

    @Override
    public String asString() {
        return this.name;
    }

    private static /* synthetic */ InactivityFpsLimit[] method_61961() {
        return new InactivityFpsLimit[]{MINIMIZED, AFK};
    }

    static {
        field_52749 = InactivityFpsLimit.method_61961();
        CODEC = StringIdentifiable.createCodec(InactivityFpsLimit::values);
    }
}

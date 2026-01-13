/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.util;

import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.ColorLerper;
import net.minecraft.util.DyeColor;

@Environment(value=EnvType.CLIENT)
public static final class ColorLerper.Type
extends Enum<ColorLerper.Type> {
    public static final /* enum */ ColorLerper.Type SHEEP = new ColorLerper.Type(25, DyeColor.values(), 0.75f);
    public static final /* enum */ ColorLerper.Type MUSIC_NOTE = new ColorLerper.Type(30, RAINBOW_COLORS, 1.25f);
    final int colorDuration;
    private final Map<DyeColor, Integer> colorToArgb;
    final DyeColor[] colors;
    private static final /* synthetic */ ColorLerper.Type[] field_60692;

    public static ColorLerper.Type[] values() {
        return (ColorLerper.Type[])field_60692.clone();
    }

    public static ColorLerper.Type valueOf(String string) {
        return Enum.valueOf(ColorLerper.Type.class, string);
    }

    private ColorLerper.Type(int colorDuration, DyeColor[] colors, float multiplier) {
        this.colorDuration = colorDuration;
        this.colorToArgb = Maps.newHashMap(Arrays.stream(colors).collect(Collectors.toMap(color -> color, color -> ColorLerper.getArgb(color, multiplier))));
        this.colors = colors;
    }

    public final int getArgb(DyeColor color) {
        return this.colorToArgb.get(color);
    }

    private static /* synthetic */ ColorLerper.Type[] method_71791() {
        return new ColorLerper.Type[]{SHEEP, MUSIC_NOTE};
    }

    static {
        field_60692 = ColorLerper.Type.method_71791();
    }
}

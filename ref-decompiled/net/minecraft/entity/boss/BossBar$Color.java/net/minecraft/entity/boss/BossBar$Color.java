/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.entity.boss;

import com.mojang.serialization.Codec;
import net.minecraft.util.Formatting;
import net.minecraft.util.StringIdentifiable;

public static final class BossBar.Color
extends Enum<BossBar.Color>
implements StringIdentifiable {
    public static final /* enum */ BossBar.Color PINK = new BossBar.Color("pink", Formatting.RED);
    public static final /* enum */ BossBar.Color BLUE = new BossBar.Color("blue", Formatting.BLUE);
    public static final /* enum */ BossBar.Color RED = new BossBar.Color("red", Formatting.DARK_RED);
    public static final /* enum */ BossBar.Color GREEN = new BossBar.Color("green", Formatting.GREEN);
    public static final /* enum */ BossBar.Color YELLOW = new BossBar.Color("yellow", Formatting.YELLOW);
    public static final /* enum */ BossBar.Color PURPLE = new BossBar.Color("purple", Formatting.DARK_BLUE);
    public static final /* enum */ BossBar.Color WHITE = new BossBar.Color("white", Formatting.WHITE);
    public static final Codec<BossBar.Color> CODEC;
    private final String name;
    private final Formatting format;
    private static final /* synthetic */ BossBar.Color[] field_5789;

    public static BossBar.Color[] values() {
        return (BossBar.Color[])field_5789.clone();
    }

    public static BossBar.Color valueOf(String string) {
        return Enum.valueOf(BossBar.Color.class, string);
    }

    private BossBar.Color(String name, Formatting format) {
        this.name = name;
        this.format = format;
    }

    public Formatting getTextFormat() {
        return this.format;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String asString() {
        return this.name;
    }

    private static /* synthetic */ BossBar.Color[] method_36595() {
        return new BossBar.Color[]{PINK, BLUE, RED, GREEN, YELLOW, PURPLE, WHITE};
    }

    static {
        field_5789 = BossBar.Color.method_36595();
        CODEC = StringIdentifiable.createCodec(BossBar.Color::values);
    }
}

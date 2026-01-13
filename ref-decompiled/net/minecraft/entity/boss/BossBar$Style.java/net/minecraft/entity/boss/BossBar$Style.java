/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.entity.boss;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

public static final class BossBar.Style
extends Enum<BossBar.Style>
implements StringIdentifiable {
    public static final /* enum */ BossBar.Style PROGRESS = new BossBar.Style("progress");
    public static final /* enum */ BossBar.Style NOTCHED_6 = new BossBar.Style("notched_6");
    public static final /* enum */ BossBar.Style NOTCHED_10 = new BossBar.Style("notched_10");
    public static final /* enum */ BossBar.Style NOTCHED_12 = new BossBar.Style("notched_12");
    public static final /* enum */ BossBar.Style NOTCHED_20 = new BossBar.Style("notched_20");
    public static final Codec<BossBar.Style> CODEC;
    private final String name;
    private static final /* synthetic */ BossBar.Style[] field_5792;

    public static BossBar.Style[] values() {
        return (BossBar.Style[])field_5792.clone();
    }

    public static BossBar.Style valueOf(String string) {
        return Enum.valueOf(BossBar.Style.class, string);
    }

    private BossBar.Style(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String asString() {
        return this.name;
    }

    private static /* synthetic */ BossBar.Style[] method_36596() {
        return new BossBar.Style[]{PROGRESS, NOTCHED_6, NOTCHED_10, NOTCHED_12, NOTCHED_20};
    }

    static {
        field_5792 = BossBar.Style.method_36596();
        CODEC = StringIdentifiable.createCodec(BossBar.Style::values);
    }
}

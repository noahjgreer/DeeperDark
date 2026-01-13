/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.hud.debug.DebugProfileType
 *  net.minecraft.util.StringIdentifiable
 *  net.minecraft.util.StringIdentifiable$EnumCodec
 */
package net.minecraft.client.gui.hud.debug;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.StringIdentifiable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public final class DebugProfileType
extends Enum<DebugProfileType>
implements StringIdentifiable {
    public static final /* enum */ DebugProfileType DEFAULT = new DebugProfileType("DEFAULT", 0, "default", "debug.options.profile.default");
    public static final /* enum */ DebugProfileType PERFORMANCE = new DebugProfileType("PERFORMANCE", 1, "performance", "debug.options.profile.performance");
    public static final StringIdentifiable.EnumCodec<DebugProfileType> CODEC;
    private final String id;
    private final String translationKey;
    private static final /* synthetic */ DebugProfileType[] field_61604;

    public static DebugProfileType[] values() {
        return (DebugProfileType[])field_61604.clone();
    }

    public static DebugProfileType valueOf(String string) {
        return Enum.valueOf(DebugProfileType.class, string);
    }

    private DebugProfileType(String id, String translationKey) {
        this.id = id;
        this.translationKey = translationKey;
    }

    public String getTranslationKey() {
        return this.translationKey;
    }

    public String asString() {
        return this.id;
    }

    private static /* synthetic */ DebugProfileType[] method_72783() {
        return new DebugProfileType[]{DEFAULT, PERFORMANCE};
    }

    static {
        field_61604 = DebugProfileType.method_72783();
        CODEC = StringIdentifiable.createCodec(DebugProfileType::values);
    }
}


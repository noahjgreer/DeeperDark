/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client;

import com.mojang.serialization.Codec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.StringIdentifiable;

@Environment(value=EnvType.CLIENT)
public static final class QuickPlayLogger.WorldType
extends Enum<QuickPlayLogger.WorldType>
implements StringIdentifiable {
    public static final /* enum */ QuickPlayLogger.WorldType SINGLEPLAYER = new QuickPlayLogger.WorldType("singleplayer");
    public static final /* enum */ QuickPlayLogger.WorldType MULTIPLAYER = new QuickPlayLogger.WorldType("multiplayer");
    public static final /* enum */ QuickPlayLogger.WorldType REALMS = new QuickPlayLogger.WorldType("realms");
    static final Codec<QuickPlayLogger.WorldType> CODEC;
    private final String id;
    private static final /* synthetic */ QuickPlayLogger.WorldType[] field_44573;

    public static QuickPlayLogger.WorldType[] values() {
        return (QuickPlayLogger.WorldType[])field_44573.clone();
    }

    public static QuickPlayLogger.WorldType valueOf(String string) {
        return Enum.valueOf(QuickPlayLogger.WorldType.class, string);
    }

    private QuickPlayLogger.WorldType(String id) {
        this.id = id;
    }

    @Override
    public String asString() {
        return this.id;
    }

    private static /* synthetic */ QuickPlayLogger.WorldType[] method_51271() {
        return new QuickPlayLogger.WorldType[]{SINGLEPLAYER, MULTIPLAYER, REALMS};
    }

    static {
        field_44573 = QuickPlayLogger.WorldType.method_51271();
        CODEC = StringIdentifiable.createCodec(QuickPlayLogger.WorldType::values);
    }
}

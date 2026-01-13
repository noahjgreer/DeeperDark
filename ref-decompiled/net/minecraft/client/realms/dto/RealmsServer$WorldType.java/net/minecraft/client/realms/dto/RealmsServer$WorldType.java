/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.dto;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public static final class RealmsServer.WorldType
extends Enum<RealmsServer.WorldType> {
    public static final /* enum */ RealmsServer.WorldType NORMAL = new RealmsServer.WorldType("normal");
    public static final /* enum */ RealmsServer.WorldType MINIGAME = new RealmsServer.WorldType("minigame");
    public static final /* enum */ RealmsServer.WorldType ADVENTUREMAP = new RealmsServer.WorldType("adventureMap");
    public static final /* enum */ RealmsServer.WorldType EXPERIENCE = new RealmsServer.WorldType("experience");
    public static final /* enum */ RealmsServer.WorldType INSPIRATION = new RealmsServer.WorldType("inspiration");
    public static final /* enum */ RealmsServer.WorldType UNKNOWN = new RealmsServer.WorldType("unknown");
    private static final String TRANSLATION_KEY_PREFIX = "mco.backup.entry.worldType.";
    private final Text text;
    private static final /* synthetic */ RealmsServer.WorldType[] field_19442;

    public static RealmsServer.WorldType[] values() {
        return (RealmsServer.WorldType[])field_19442.clone();
    }

    public static RealmsServer.WorldType valueOf(String name) {
        return Enum.valueOf(RealmsServer.WorldType.class, name);
    }

    private RealmsServer.WorldType(String id) {
        this.text = Text.translatable(TRANSLATION_KEY_PREFIX + id);
    }

    public Text getText() {
        return this.text;
    }

    private static /* synthetic */ RealmsServer.WorldType[] method_36849() {
        return new RealmsServer.WorldType[]{NORMAL, MINIGAME, ADVENTUREMAP, EXPERIENCE, INSPIRATION, UNKNOWN};
    }

    static {
        field_19442 = RealmsServer.WorldType.method_36849();
    }
}

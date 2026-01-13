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

@Environment(value=EnvType.CLIENT)
public static final class WorldTemplate.WorldTemplateType
extends Enum<WorldTemplate.WorldTemplateType> {
    public static final /* enum */ WorldTemplate.WorldTemplateType WORLD_TEMPLATE = new WorldTemplate.WorldTemplateType();
    public static final /* enum */ WorldTemplate.WorldTemplateType MINIGAME = new WorldTemplate.WorldTemplateType();
    public static final /* enum */ WorldTemplate.WorldTemplateType ADVENTUREMAP = new WorldTemplate.WorldTemplateType();
    public static final /* enum */ WorldTemplate.WorldTemplateType EXPERIENCE = new WorldTemplate.WorldTemplateType();
    public static final /* enum */ WorldTemplate.WorldTemplateType INSPIRATION = new WorldTemplate.WorldTemplateType();
    private static final /* synthetic */ WorldTemplate.WorldTemplateType[] field_19452;

    public static WorldTemplate.WorldTemplateType[] values() {
        return (WorldTemplate.WorldTemplateType[])field_19452.clone();
    }

    public static WorldTemplate.WorldTemplateType valueOf(String name) {
        return Enum.valueOf(WorldTemplate.WorldTemplateType.class, name);
    }

    private static /* synthetic */ WorldTemplate.WorldTemplateType[] method_36851() {
        return new WorldTemplate.WorldTemplateType[]{WORLD_TEMPLATE, MINIGAME, ADVENTUREMAP, EXPERIENCE, INSPIRATION};
    }

    static {
        field_19452 = WorldTemplate.WorldTemplateType.method_36851();
    }
}

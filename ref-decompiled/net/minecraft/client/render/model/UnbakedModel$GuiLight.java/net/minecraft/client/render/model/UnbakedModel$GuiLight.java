/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public static final class UnbakedModel.GuiLight
extends Enum<UnbakedModel.GuiLight> {
    public static final /* enum */ UnbakedModel.GuiLight ITEM = new UnbakedModel.GuiLight("front");
    public static final /* enum */ UnbakedModel.GuiLight BLOCK = new UnbakedModel.GuiLight("side");
    private final String name;
    private static final /* synthetic */ UnbakedModel.GuiLight[] field_21861;

    public static UnbakedModel.GuiLight[] values() {
        return (UnbakedModel.GuiLight[])field_21861.clone();
    }

    public static UnbakedModel.GuiLight valueOf(String string) {
        return Enum.valueOf(UnbakedModel.GuiLight.class, string);
    }

    private UnbakedModel.GuiLight(String name) {
        this.name = name;
    }

    public static UnbakedModel.GuiLight byName(String value) {
        for (UnbakedModel.GuiLight guiLight : UnbakedModel.GuiLight.values()) {
            if (!guiLight.name.equals(value)) continue;
            return guiLight;
        }
        throw new IllegalArgumentException("Invalid gui light: " + value);
    }

    public boolean isSide() {
        return this == BLOCK;
    }

    private static /* synthetic */ UnbakedModel.GuiLight[] method_36920() {
        return new UnbakedModel.GuiLight[]{ITEM, BLOCK};
    }

    static {
        field_21861 = UnbakedModel.GuiLight.method_36920();
    }
}

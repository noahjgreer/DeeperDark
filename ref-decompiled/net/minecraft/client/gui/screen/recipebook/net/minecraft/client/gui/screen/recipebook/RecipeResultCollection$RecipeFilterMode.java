/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.recipebook;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public static final class RecipeResultCollection.RecipeFilterMode
extends Enum<RecipeResultCollection.RecipeFilterMode> {
    public static final /* enum */ RecipeResultCollection.RecipeFilterMode ANY = new RecipeResultCollection.RecipeFilterMode();
    public static final /* enum */ RecipeResultCollection.RecipeFilterMode CRAFTABLE = new RecipeResultCollection.RecipeFilterMode();
    public static final /* enum */ RecipeResultCollection.RecipeFilterMode NOT_CRAFTABLE = new RecipeResultCollection.RecipeFilterMode();
    private static final /* synthetic */ RecipeResultCollection.RecipeFilterMode[] field_52850;

    public static RecipeResultCollection.RecipeFilterMode[] values() {
        return (RecipeResultCollection.RecipeFilterMode[])field_52850.clone();
    }

    public static RecipeResultCollection.RecipeFilterMode valueOf(String string) {
        return Enum.valueOf(RecipeResultCollection.RecipeFilterMode.class, string);
    }

    private static /* synthetic */ RecipeResultCollection.RecipeFilterMode[] method_62052() {
        return new RecipeResultCollection.RecipeFilterMode[]{ANY, CRAFTABLE, NOT_CRAFTABLE};
    }

    static {
        field_52850 = RecipeResultCollection.RecipeFilterMode.method_62052();
    }
}

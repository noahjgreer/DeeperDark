/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.toast;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public static final class TutorialToast.Type
extends Enum<TutorialToast.Type> {
    public static final /* enum */ TutorialToast.Type MOVEMENT_KEYS = new TutorialToast.Type(Identifier.ofVanilla("toast/movement_keys"));
    public static final /* enum */ TutorialToast.Type MOUSE = new TutorialToast.Type(Identifier.ofVanilla("toast/mouse"));
    public static final /* enum */ TutorialToast.Type TREE = new TutorialToast.Type(Identifier.ofVanilla("toast/tree"));
    public static final /* enum */ TutorialToast.Type RECIPE_BOOK = new TutorialToast.Type(Identifier.ofVanilla("toast/recipe_book"));
    public static final /* enum */ TutorialToast.Type WOODEN_PLANKS = new TutorialToast.Type(Identifier.ofVanilla("toast/wooden_planks"));
    public static final /* enum */ TutorialToast.Type SOCIAL_INTERACTIONS = new TutorialToast.Type(Identifier.ofVanilla("toast/social_interactions"));
    public static final /* enum */ TutorialToast.Type RIGHT_CLICK = new TutorialToast.Type(Identifier.ofVanilla("toast/right_click"));
    private final Identifier texture;
    private static final /* synthetic */ TutorialToast.Type[] field_2234;

    public static TutorialToast.Type[] values() {
        return (TutorialToast.Type[])field_2234.clone();
    }

    public static TutorialToast.Type valueOf(String string) {
        return Enum.valueOf(TutorialToast.Type.class, string);
    }

    private TutorialToast.Type(Identifier texture) {
        this.texture = texture;
    }

    public void drawIcon(DrawContext context, int x, int y) {
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, this.texture, x, y, 20, 20);
    }

    private static /* synthetic */ TutorialToast.Type[] method_36873() {
        return new TutorialToast.Type[]{MOVEMENT_KEYS, MOUSE, TREE, RECIPE_BOOK, WOODEN_PLANKS, SOCIAL_INTERACTIONS, RIGHT_CLICK};
    }

    static {
        field_2234 = TutorialToast.Type.method_36873();
    }
}

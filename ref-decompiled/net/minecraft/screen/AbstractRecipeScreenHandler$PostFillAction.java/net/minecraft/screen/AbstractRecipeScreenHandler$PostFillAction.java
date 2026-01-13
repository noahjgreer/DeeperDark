/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.screen;

public static final class AbstractRecipeScreenHandler.PostFillAction
extends Enum<AbstractRecipeScreenHandler.PostFillAction> {
    public static final /* enum */ AbstractRecipeScreenHandler.PostFillAction NOTHING = new AbstractRecipeScreenHandler.PostFillAction();
    public static final /* enum */ AbstractRecipeScreenHandler.PostFillAction PLACE_GHOST_RECIPE = new AbstractRecipeScreenHandler.PostFillAction();
    private static final /* synthetic */ AbstractRecipeScreenHandler.PostFillAction[] field_52574;

    public static AbstractRecipeScreenHandler.PostFillAction[] values() {
        return (AbstractRecipeScreenHandler.PostFillAction[])field_52574.clone();
    }

    public static AbstractRecipeScreenHandler.PostFillAction valueOf(String string) {
        return Enum.valueOf(AbstractRecipeScreenHandler.PostFillAction.class, string);
    }

    private static /* synthetic */ AbstractRecipeScreenHandler.PostFillAction[] method_61636() {
        return new AbstractRecipeScreenHandler.PostFillAction[]{NOTHING, PLACE_GHOST_RECIPE};
    }

    static {
        field_52574 = AbstractRecipeScreenHandler.PostFillAction.method_61636();
    }
}

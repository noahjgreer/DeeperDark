/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.multiplayer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public static final class SocialInteractionsScreen.Tab
extends Enum<SocialInteractionsScreen.Tab> {
    public static final /* enum */ SocialInteractionsScreen.Tab ALL = new SocialInteractionsScreen.Tab();
    public static final /* enum */ SocialInteractionsScreen.Tab HIDDEN = new SocialInteractionsScreen.Tab();
    public static final /* enum */ SocialInteractionsScreen.Tab BLOCKED = new SocialInteractionsScreen.Tab();
    private static final /* synthetic */ SocialInteractionsScreen.Tab[] field_26892;

    public static SocialInteractionsScreen.Tab[] values() {
        return (SocialInteractionsScreen.Tab[])field_26892.clone();
    }

    public static SocialInteractionsScreen.Tab valueOf(String string) {
        return Enum.valueOf(SocialInteractionsScreen.Tab.class, string);
    }

    private static /* synthetic */ SocialInteractionsScreen.Tab[] method_36890() {
        return new SocialInteractionsScreen.Tab[]{ALL, HIDDEN, BLOCKED};
    }

    static {
        field_26892 = SocialInteractionsScreen.Tab.method_36890();
    }
}

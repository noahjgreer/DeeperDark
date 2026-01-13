/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.narration;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class NarrationPart
extends Enum<NarrationPart> {
    public static final /* enum */ NarrationPart TITLE = new NarrationPart();
    public static final /* enum */ NarrationPart POSITION = new NarrationPart();
    public static final /* enum */ NarrationPart HINT = new NarrationPart();
    public static final /* enum */ NarrationPart USAGE = new NarrationPart();
    private static final /* synthetic */ NarrationPart[] field_33792;

    public static NarrationPart[] values() {
        return (NarrationPart[])field_33792.clone();
    }

    public static NarrationPart valueOf(String string) {
        return Enum.valueOf(NarrationPart.class, string);
    }

    private static /* synthetic */ NarrationPart[] method_37030() {
        return new NarrationPart[]{TITLE, POSITION, HINT, USAGE};
    }

    static {
        field_33792 = NarrationPart.method_37030();
    }
}

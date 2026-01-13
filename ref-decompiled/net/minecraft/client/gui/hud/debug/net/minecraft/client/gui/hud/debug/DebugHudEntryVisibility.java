/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.hud.debug;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.StringIdentifiable;

@Environment(value=EnvType.CLIENT)
public final class DebugHudEntryVisibility
extends Enum<DebugHudEntryVisibility>
implements StringIdentifiable {
    public static final /* enum */ DebugHudEntryVisibility ALWAYS_ON = new DebugHudEntryVisibility("alwaysOn");
    public static final /* enum */ DebugHudEntryVisibility IN_OVERLAY = new DebugHudEntryVisibility("inOverlay");
    public static final /* enum */ DebugHudEntryVisibility NEVER = new DebugHudEntryVisibility("never");
    public static final StringIdentifiable.EnumCodec<DebugHudEntryVisibility> CODEC;
    private final String id;
    private static final /* synthetic */ DebugHudEntryVisibility[] field_61598;

    public static DebugHudEntryVisibility[] values() {
        return (DebugHudEntryVisibility[])field_61598.clone();
    }

    public static DebugHudEntryVisibility valueOf(String string) {
        return Enum.valueOf(DebugHudEntryVisibility.class, string);
    }

    private DebugHudEntryVisibility(String id) {
        this.id = id;
    }

    @Override
    public String asString() {
        return this.id;
    }

    private static /* synthetic */ DebugHudEntryVisibility[] method_72781() {
        return new DebugHudEntryVisibility[]{ALWAYS_ON, IN_OVERLAY, NEVER};
    }

    static {
        field_61598 = DebugHudEntryVisibility.method_72781();
        CODEC = StringIdentifiable.createCodec(DebugHudEntryVisibility::values);
    }
}

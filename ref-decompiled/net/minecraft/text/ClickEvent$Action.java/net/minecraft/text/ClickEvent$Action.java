/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Lifecycle
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.text;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import net.minecraft.text.ClickEvent;
import net.minecraft.util.StringIdentifiable;

public static final class ClickEvent.Action
extends Enum<ClickEvent.Action>
implements StringIdentifiable {
    public static final /* enum */ ClickEvent.Action OPEN_URL = new ClickEvent.Action("open_url", true, ClickEvent.OpenUrl.CODEC);
    public static final /* enum */ ClickEvent.Action OPEN_FILE = new ClickEvent.Action("open_file", false, ClickEvent.OpenFile.CODEC);
    public static final /* enum */ ClickEvent.Action RUN_COMMAND = new ClickEvent.Action("run_command", true, ClickEvent.RunCommand.CODEC);
    public static final /* enum */ ClickEvent.Action SUGGEST_COMMAND = new ClickEvent.Action("suggest_command", true, ClickEvent.SuggestCommand.CODEC);
    public static final /* enum */ ClickEvent.Action SHOW_DIALOG = new ClickEvent.Action("show_dialog", true, ClickEvent.ShowDialog.CODEC);
    public static final /* enum */ ClickEvent.Action CHANGE_PAGE = new ClickEvent.Action("change_page", true, ClickEvent.ChangePage.CODEC);
    public static final /* enum */ ClickEvent.Action COPY_TO_CLIPBOARD = new ClickEvent.Action("copy_to_clipboard", true, ClickEvent.CopyToClipboard.CODEC);
    public static final /* enum */ ClickEvent.Action CUSTOM = new ClickEvent.Action("custom", true, ClickEvent.Custom.CODEC);
    public static final Codec<ClickEvent.Action> UNVALIDATED_CODEC;
    public static final Codec<ClickEvent.Action> CODEC;
    private final boolean userDefinable;
    private final String name;
    final MapCodec<? extends ClickEvent> codec;
    private static final /* synthetic */ ClickEvent.Action[] field_11747;

    public static ClickEvent.Action[] values() {
        return (ClickEvent.Action[])field_11747.clone();
    }

    public static ClickEvent.Action valueOf(String string) {
        return Enum.valueOf(ClickEvent.Action.class, string);
    }

    private ClickEvent.Action(String name, boolean userDefinable, MapCodec<? extends ClickEvent> codec) {
        this.name = name;
        this.userDefinable = userDefinable;
        this.codec = codec;
    }

    public boolean isUserDefinable() {
        return this.userDefinable;
    }

    @Override
    public String asString() {
        return this.name;
    }

    public MapCodec<? extends ClickEvent> getCodec() {
        return this.codec;
    }

    public static DataResult<ClickEvent.Action> validate(ClickEvent.Action action) {
        if (!action.isUserDefinable()) {
            return DataResult.error(() -> "Click event type not allowed: " + String.valueOf(action));
        }
        return DataResult.success((Object)action, (Lifecycle)Lifecycle.stable());
    }

    private static /* synthetic */ ClickEvent.Action[] method_36945() {
        return new ClickEvent.Action[]{OPEN_URL, OPEN_FILE, RUN_COMMAND, SUGGEST_COMMAND, SHOW_DIALOG, CHANGE_PAGE, COPY_TO_CLIPBOARD, CUSTOM};
    }

    static {
        field_11747 = ClickEvent.Action.method_36945();
        UNVALIDATED_CODEC = StringIdentifiable.createCodec(ClickEvent.Action::values);
        CODEC = UNVALIDATED_CODEC.validate(ClickEvent.Action::validate);
    }
}

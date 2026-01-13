/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.minecraft.advancement.Advancement
 *  net.minecraft.advancement.AdvancementEntry
 *  net.minecraft.advancement.AdvancementFrame
 *  net.minecraft.server.network.ServerPlayerEntity
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.Text
 *  net.minecraft.util.Formatting
 *  net.minecraft.util.StringIdentifiable
 */
package net.minecraft.advancement;

import com.mojang.serialization.Codec;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.StringIdentifiable;

/*
 * Exception performing whole class analysis ignored.
 */
public final class AdvancementFrame
extends Enum<AdvancementFrame>
implements StringIdentifiable {
    public static final /* enum */ AdvancementFrame TASK = new AdvancementFrame("TASK", 0, "task", Formatting.GREEN);
    public static final /* enum */ AdvancementFrame CHALLENGE = new AdvancementFrame("CHALLENGE", 1, "challenge", Formatting.DARK_PURPLE);
    public static final /* enum */ AdvancementFrame GOAL = new AdvancementFrame("GOAL", 2, "goal", Formatting.GREEN);
    public static final Codec<AdvancementFrame> CODEC;
    private final String id;
    private final Formatting titleFormat;
    private final Text toastText;
    private static final /* synthetic */ AdvancementFrame[] field_1253;

    public static AdvancementFrame[] values() {
        return (AdvancementFrame[])field_1253.clone();
    }

    public static AdvancementFrame valueOf(String string) {
        return Enum.valueOf(AdvancementFrame.class, string);
    }

    private AdvancementFrame(String id, Formatting titleFormat) {
        this.id = id;
        this.titleFormat = titleFormat;
        this.toastText = Text.translatable((String)("advancements.toast." + id));
    }

    public Formatting getTitleFormat() {
        return this.titleFormat;
    }

    public Text getToastText() {
        return this.toastText;
    }

    public String asString() {
        return this.id;
    }

    public MutableText getChatAnnouncementText(AdvancementEntry advancementEntry, ServerPlayerEntity player) {
        return Text.translatable((String)("chat.type.advancement." + this.id), (Object[])new Object[]{player.getDisplayName(), Advancement.getNameFromIdentity((AdvancementEntry)advancementEntry)});
    }

    private static /* synthetic */ AdvancementFrame[] method_36593() {
        return new AdvancementFrame[]{TASK, CHALLENGE, GOAL};
    }

    static {
        field_1253 = AdvancementFrame.method_36593();
        CODEC = StringIdentifiable.createCodec(AdvancementFrame::values);
    }
}


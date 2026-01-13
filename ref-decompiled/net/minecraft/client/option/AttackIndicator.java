/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.option.AttackIndicator
 *  net.minecraft.text.Text
 *  net.minecraft.util.function.ValueLists
 *  net.minecraft.util.function.ValueLists$OutOfBoundsHandling
 */
package net.minecraft.client.option;

import com.mojang.serialization.Codec;
import java.util.function.IntFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.util.function.ValueLists;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public final class AttackIndicator
extends Enum<AttackIndicator> {
    public static final /* enum */ AttackIndicator OFF = new AttackIndicator("OFF", 0, 0, "options.off");
    public static final /* enum */ AttackIndicator CROSSHAIR = new AttackIndicator("CROSSHAIR", 1, 1, "options.attack.crosshair");
    public static final /* enum */ AttackIndicator HOTBAR = new AttackIndicator("HOTBAR", 2, 2, "options.attack.hotbar");
    private static final IntFunction<AttackIndicator> BY_ID;
    public static final Codec<AttackIndicator> CODEC;
    private final int id;
    private final Text text;
    private static final /* synthetic */ AttackIndicator[] field_18157;

    public static AttackIndicator[] values() {
        return (AttackIndicator[])field_18157.clone();
    }

    public static AttackIndicator valueOf(String string) {
        return Enum.valueOf(AttackIndicator.class, string);
    }

    private AttackIndicator(int id, String translationKey) {
        this.id = id;
        this.text = Text.translatable((String)translationKey);
    }

    public Text getText() {
        return this.text;
    }

    private static /* synthetic */ AttackIndicator[] method_36858() {
        return new AttackIndicator[]{OFF, CROSSHAIR, HOTBAR};
    }

    static {
        field_18157 = AttackIndicator.method_36858();
        BY_ID = ValueLists.createIndexToValueFunction(attackIndicator -> attackIndicator.id, (Object[])AttackIndicator.values(), (ValueLists.OutOfBoundsHandling)ValueLists.OutOfBoundsHandling.WRAP);
        CODEC = Codec.INT.xmap(BY_ID::apply, attackIndicator -> attackIndicator.id);
    }
}


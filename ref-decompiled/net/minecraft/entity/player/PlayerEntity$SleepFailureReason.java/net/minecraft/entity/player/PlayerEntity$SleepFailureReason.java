/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.entity.player;

import net.minecraft.text.Text;
import org.jspecify.annotations.Nullable;

public record PlayerEntity.SleepFailureReason(@Nullable Text message) {
    public static final PlayerEntity.SleepFailureReason TOO_FAR_AWAY = new PlayerEntity.SleepFailureReason(Text.translatable("block.minecraft.bed.too_far_away"));
    public static final PlayerEntity.SleepFailureReason OBSTRUCTED = new PlayerEntity.SleepFailureReason(Text.translatable("block.minecraft.bed.obstructed"));
    public static final PlayerEntity.SleepFailureReason OTHER = new PlayerEntity.SleepFailureReason(null);
    public static final PlayerEntity.SleepFailureReason NOT_SAFE = new PlayerEntity.SleepFailureReason(Text.translatable("block.minecraft.bed.not_safe"));
}

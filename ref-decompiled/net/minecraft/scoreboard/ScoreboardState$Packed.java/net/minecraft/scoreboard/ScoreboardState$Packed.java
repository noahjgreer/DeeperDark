/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.scoreboard;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;

public record ScoreboardState.Packed(List<ScoreboardObjective.Packed> objectives, List<Scoreboard.PackedEntry> scores, Map<ScoreboardDisplaySlot, String> displaySlots, List<Team.Packed> teams) {
    public static final ScoreboardState.Packed EMPTY = new ScoreboardState.Packed(List.of(), List.of(), Map.of(), List.of());
    public static final Codec<ScoreboardState.Packed> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)ScoreboardObjective.Packed.CODEC.listOf().optionalFieldOf("Objectives", List.of()).forGetter(ScoreboardState.Packed::objectives), (App)Scoreboard.PackedEntry.CODEC.listOf().optionalFieldOf("PlayerScores", List.of()).forGetter(ScoreboardState.Packed::scores), (App)Codec.unboundedMap(ScoreboardDisplaySlot.CODEC, (Codec)Codec.STRING).optionalFieldOf("DisplaySlots", Map.of()).forGetter(ScoreboardState.Packed::displaySlots), (App)Team.Packed.CODEC.listOf().optionalFieldOf("Teams", List.of()).forGetter(ScoreboardState.Packed::teams)).apply((Applicative)instance, ScoreboardState.Packed::new));
}

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
import java.util.Optional;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.scoreboard.number.NumberFormatTypes;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

public record ScoreboardObjective.Packed(String name, ScoreboardCriterion criteria, Text displayName, ScoreboardCriterion.RenderType renderType, boolean displayAutoUpdate, Optional<NumberFormat> numberFormat) {
    public static final Codec<ScoreboardObjective.Packed> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.STRING.fieldOf("Name").forGetter(ScoreboardObjective.Packed::name), (App)ScoreboardCriterion.CODEC.optionalFieldOf("CriteriaName", (Object)ScoreboardCriterion.DUMMY).forGetter(ScoreboardObjective.Packed::criteria), (App)TextCodecs.CODEC.fieldOf("DisplayName").forGetter(ScoreboardObjective.Packed::displayName), (App)ScoreboardCriterion.RenderType.CODEC.optionalFieldOf("RenderType", ScoreboardCriterion.RenderType.INTEGER).forGetter(ScoreboardObjective.Packed::renderType), (App)Codec.BOOL.optionalFieldOf("display_auto_update", (Object)false).forGetter(ScoreboardObjective.Packed::displayAutoUpdate), (App)NumberFormatTypes.CODEC.optionalFieldOf("format").forGetter(ScoreboardObjective.Packed::numberFormat)).apply((Applicative)instance, ScoreboardObjective.Packed::new));
}

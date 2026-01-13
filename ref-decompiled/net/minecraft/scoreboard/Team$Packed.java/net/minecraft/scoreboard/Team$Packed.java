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
import java.util.Optional;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Formatting;

public record Team.Packed(String name, Optional<Text> displayName, Optional<Formatting> color, boolean allowFriendlyFire, boolean seeFriendlyInvisibles, Text memberNamePrefix, Text memberNameSuffix, AbstractTeam.VisibilityRule nameTagVisibility, AbstractTeam.VisibilityRule deathMessageVisibility, AbstractTeam.CollisionRule collisionRule, List<String> players) {
    public static final Codec<Team.Packed> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.STRING.fieldOf("Name").forGetter(Team.Packed::name), (App)TextCodecs.CODEC.optionalFieldOf("DisplayName").forGetter(Team.Packed::displayName), (App)Formatting.COLOR_CODEC.optionalFieldOf("TeamColor").forGetter(Team.Packed::color), (App)Codec.BOOL.optionalFieldOf("AllowFriendlyFire", (Object)true).forGetter(Team.Packed::allowFriendlyFire), (App)Codec.BOOL.optionalFieldOf("SeeFriendlyInvisibles", (Object)true).forGetter(Team.Packed::seeFriendlyInvisibles), (App)TextCodecs.CODEC.optionalFieldOf("MemberNamePrefix", (Object)ScreenTexts.EMPTY).forGetter(Team.Packed::memberNamePrefix), (App)TextCodecs.CODEC.optionalFieldOf("MemberNameSuffix", (Object)ScreenTexts.EMPTY).forGetter(Team.Packed::memberNameSuffix), (App)AbstractTeam.VisibilityRule.CODEC.optionalFieldOf("NameTagVisibility", (Object)AbstractTeam.VisibilityRule.ALWAYS).forGetter(Team.Packed::nameTagVisibility), (App)AbstractTeam.VisibilityRule.CODEC.optionalFieldOf("DeathMessageVisibility", (Object)AbstractTeam.VisibilityRule.ALWAYS).forGetter(Team.Packed::deathMessageVisibility), (App)AbstractTeam.CollisionRule.CODEC.optionalFieldOf("CollisionRule", (Object)AbstractTeam.CollisionRule.ALWAYS).forGetter(Team.Packed::collisionRule), (App)Codec.STRING.listOf().optionalFieldOf("Players", List.of()).forGetter(Team.Packed::players)).apply((Applicative)instance, Team.Packed::new));
}

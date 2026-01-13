/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.hud.debug;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.debug.DebugHudEntryVisibility;
import net.minecraft.client.gui.hud.debug.DebugProfileType;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
record DebugHudProfile.Serialization(Optional<DebugProfileType> profile, Optional<Map<Identifier, DebugHudEntryVisibility>> custom) {
    private static final Codec<Map<Identifier, DebugHudEntryVisibility>> VISIBILITY_MAP_CODEC = Codec.unboundedMap(Identifier.CODEC, DebugHudEntryVisibility.CODEC);
    public static final Codec<DebugHudProfile.Serialization> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)DebugProfileType.CODEC.optionalFieldOf("profile").forGetter(DebugHudProfile.Serialization::profile), (App)VISIBILITY_MAP_CODEC.optionalFieldOf("custom").forGetter(DebugHudProfile.Serialization::custom)).apply((Applicative)instance, DebugHudProfile.Serialization::new));
}

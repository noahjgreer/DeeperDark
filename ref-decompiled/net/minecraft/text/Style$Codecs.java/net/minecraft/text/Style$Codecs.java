/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.text;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.StyleSpriteSource;
import net.minecraft.text.TextColor;
import net.minecraft.util.dynamic.Codecs;

public static class Style.Codecs {
    public static final MapCodec<Style> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)TextColor.CODEC.optionalFieldOf("color").forGetter(style -> Optional.ofNullable(style.color)), (App)Codecs.ARGB.optionalFieldOf("shadow_color").forGetter(style -> Optional.ofNullable(style.shadowColor)), (App)Codec.BOOL.optionalFieldOf("bold").forGetter(style -> Optional.ofNullable(style.bold)), (App)Codec.BOOL.optionalFieldOf("italic").forGetter(style -> Optional.ofNullable(style.italic)), (App)Codec.BOOL.optionalFieldOf("underlined").forGetter(style -> Optional.ofNullable(style.underlined)), (App)Codec.BOOL.optionalFieldOf("strikethrough").forGetter(style -> Optional.ofNullable(style.strikethrough)), (App)Codec.BOOL.optionalFieldOf("obfuscated").forGetter(style -> Optional.ofNullable(style.obfuscated)), (App)ClickEvent.CODEC.optionalFieldOf("click_event").forGetter(style -> Optional.ofNullable(style.clickEvent)), (App)HoverEvent.CODEC.optionalFieldOf("hover_event").forGetter(style -> Optional.ofNullable(style.hoverEvent)), (App)Codec.STRING.optionalFieldOf("insertion").forGetter(style -> Optional.ofNullable(style.insertion)), (App)StyleSpriteSource.FONT_CODEC.optionalFieldOf("font").forGetter(style -> Optional.ofNullable(style.font))).apply((Applicative)instance, Style::of));
    public static final Codec<Style> CODEC = MAP_CODEC.codec();
    public static final PacketCodec<RegistryByteBuf, Style> PACKET_CODEC = PacketCodecs.unlimitedRegistryCodec(CODEC);
}

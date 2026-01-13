/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.component.type;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;

public record BannerPatternsComponent.Layer(RegistryEntry<BannerPattern> pattern, DyeColor color) {
    public static final Codec<BannerPatternsComponent.Layer> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)BannerPattern.ENTRY_CODEC.fieldOf("pattern").forGetter(BannerPatternsComponent.Layer::pattern), (App)DyeColor.CODEC.fieldOf("color").forGetter(BannerPatternsComponent.Layer::color)).apply((Applicative)instance, BannerPatternsComponent.Layer::new));
    public static final PacketCodec<RegistryByteBuf, BannerPatternsComponent.Layer> PACKET_CODEC = PacketCodec.tuple(BannerPattern.ENTRY_PACKET_CODEC, BannerPatternsComponent.Layer::pattern, DyeColor.PACKET_CODEC, BannerPatternsComponent.Layer::color, BannerPatternsComponent.Layer::new);

    public MutableText getTooltipText() {
        String string = this.pattern.value().translationKey();
        return Text.translatable(string + "." + this.color.getId());
    }
}

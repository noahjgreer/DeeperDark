/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.item.tint.ConstantTintSource
 *  net.minecraft.client.render.item.tint.CustomModelDataTintSource
 *  net.minecraft.client.render.item.tint.DyeTintSource
 *  net.minecraft.client.render.item.tint.FireworkTintSource
 *  net.minecraft.client.render.item.tint.GrassTintSource
 *  net.minecraft.client.render.item.tint.MapColorTintSource
 *  net.minecraft.client.render.item.tint.PotionTintSource
 *  net.minecraft.client.render.item.tint.TeamTintSource
 *  net.minecraft.client.render.item.tint.TintSource
 *  net.minecraft.client.render.item.tint.TintSourceTypes
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.dynamic.Codecs$IdMapper
 */
package net.minecraft.client.render.item.tint;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.tint.ConstantTintSource;
import net.minecraft.client.render.item.tint.CustomModelDataTintSource;
import net.minecraft.client.render.item.tint.DyeTintSource;
import net.minecraft.client.render.item.tint.FireworkTintSource;
import net.minecraft.client.render.item.tint.GrassTintSource;
import net.minecraft.client.render.item.tint.MapColorTintSource;
import net.minecraft.client.render.item.tint.PotionTintSource;
import net.minecraft.client.render.item.tint.TeamTintSource;
import net.minecraft.client.render.item.tint.TintSource;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

@Environment(value=EnvType.CLIENT)
public class TintSourceTypes {
    public static final Codecs.IdMapper<Identifier, MapCodec<? extends TintSource>> ID_MAPPER = new Codecs.IdMapper();
    public static final Codec<TintSource> CODEC = ID_MAPPER.getCodec(Identifier.CODEC).dispatch(TintSource::getCodec, codec -> codec);

    public static void bootstrap() {
        ID_MAPPER.put((Object)Identifier.ofVanilla((String)"custom_model_data"), (Object)CustomModelDataTintSource.CODEC);
        ID_MAPPER.put((Object)Identifier.ofVanilla((String)"constant"), (Object)ConstantTintSource.CODEC);
        ID_MAPPER.put((Object)Identifier.ofVanilla((String)"dye"), (Object)DyeTintSource.CODEC);
        ID_MAPPER.put((Object)Identifier.ofVanilla((String)"grass"), (Object)GrassTintSource.CODEC);
        ID_MAPPER.put((Object)Identifier.ofVanilla((String)"firework"), (Object)FireworkTintSource.CODEC);
        ID_MAPPER.put((Object)Identifier.ofVanilla((String)"potion"), (Object)PotionTintSource.CODEC);
        ID_MAPPER.put((Object)Identifier.ofVanilla((String)"map_color"), (Object)MapColorTintSource.CODEC);
        ID_MAPPER.put((Object)Identifier.ofVanilla((String)"team"), (Object)TeamTintSource.CODEC);
    }
}


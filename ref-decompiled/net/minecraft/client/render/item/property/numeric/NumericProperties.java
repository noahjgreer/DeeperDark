/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.item.property.numeric.BundleFullnessProperty
 *  net.minecraft.client.render.item.property.numeric.CompassProperty
 *  net.minecraft.client.render.item.property.numeric.CooldownProperty
 *  net.minecraft.client.render.item.property.numeric.CountProperty
 *  net.minecraft.client.render.item.property.numeric.CrossbowPullProperty
 *  net.minecraft.client.render.item.property.numeric.CustomModelDataFloatProperty
 *  net.minecraft.client.render.item.property.numeric.DamageProperty
 *  net.minecraft.client.render.item.property.numeric.NumericProperties
 *  net.minecraft.client.render.item.property.numeric.NumericProperty
 *  net.minecraft.client.render.item.property.numeric.TimeProperty
 *  net.minecraft.client.render.item.property.numeric.UseCycleProperty
 *  net.minecraft.client.render.item.property.numeric.UseDurationProperty
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.dynamic.Codecs$IdMapper
 */
package net.minecraft.client.render.item.property.numeric;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.property.numeric.BundleFullnessProperty;
import net.minecraft.client.render.item.property.numeric.CompassProperty;
import net.minecraft.client.render.item.property.numeric.CooldownProperty;
import net.minecraft.client.render.item.property.numeric.CountProperty;
import net.minecraft.client.render.item.property.numeric.CrossbowPullProperty;
import net.minecraft.client.render.item.property.numeric.CustomModelDataFloatProperty;
import net.minecraft.client.render.item.property.numeric.DamageProperty;
import net.minecraft.client.render.item.property.numeric.NumericProperty;
import net.minecraft.client.render.item.property.numeric.TimeProperty;
import net.minecraft.client.render.item.property.numeric.UseCycleProperty;
import net.minecraft.client.render.item.property.numeric.UseDurationProperty;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

@Environment(value=EnvType.CLIENT)
public class NumericProperties {
    public static final Codecs.IdMapper<Identifier, MapCodec<? extends NumericProperty>> ID_MAPPER = new Codecs.IdMapper();
    public static final MapCodec<NumericProperty> CODEC = ID_MAPPER.getCodec(Identifier.CODEC).dispatchMap("property", NumericProperty::getCodec, codec -> codec);

    public static void bootstrap() {
        ID_MAPPER.put((Object)Identifier.ofVanilla((String)"custom_model_data"), (Object)CustomModelDataFloatProperty.CODEC);
        ID_MAPPER.put((Object)Identifier.ofVanilla((String)"bundle/fullness"), (Object)BundleFullnessProperty.CODEC);
        ID_MAPPER.put((Object)Identifier.ofVanilla((String)"damage"), (Object)DamageProperty.CODEC);
        ID_MAPPER.put((Object)Identifier.ofVanilla((String)"cooldown"), (Object)CooldownProperty.CODEC);
        ID_MAPPER.put((Object)Identifier.ofVanilla((String)"time"), (Object)TimeProperty.CODEC);
        ID_MAPPER.put((Object)Identifier.ofVanilla((String)"compass"), (Object)CompassProperty.CODEC);
        ID_MAPPER.put((Object)Identifier.ofVanilla((String)"crossbow/pull"), (Object)CrossbowPullProperty.CODEC);
        ID_MAPPER.put((Object)Identifier.ofVanilla((String)"use_cycle"), (Object)UseCycleProperty.CODEC);
        ID_MAPPER.put((Object)Identifier.ofVanilla((String)"use_duration"), (Object)UseDurationProperty.CODEC);
        ID_MAPPER.put((Object)Identifier.ofVanilla((String)"count"), (Object)CountProperty.CODEC);
    }
}


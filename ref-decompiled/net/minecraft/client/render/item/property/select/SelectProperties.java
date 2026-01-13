/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.item.property.select.ChargeTypeProperty
 *  net.minecraft.client.render.item.property.select.ComponentSelectProperty
 *  net.minecraft.client.render.item.property.select.ContextDimensionProperty
 *  net.minecraft.client.render.item.property.select.ContextEntityTypeProperty
 *  net.minecraft.client.render.item.property.select.CustomModelDataStringProperty
 *  net.minecraft.client.render.item.property.select.DisplayContextProperty
 *  net.minecraft.client.render.item.property.select.ItemBlockStateProperty
 *  net.minecraft.client.render.item.property.select.LocalTimeProperty
 *  net.minecraft.client.render.item.property.select.MainHandProperty
 *  net.minecraft.client.render.item.property.select.SelectProperties
 *  net.minecraft.client.render.item.property.select.SelectProperty$Type
 *  net.minecraft.client.render.item.property.select.TrimMaterialProperty
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.dynamic.Codecs$IdMapper
 */
package net.minecraft.client.render.item.property.select;

import com.mojang.serialization.Codec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.property.select.ChargeTypeProperty;
import net.minecraft.client.render.item.property.select.ComponentSelectProperty;
import net.minecraft.client.render.item.property.select.ContextDimensionProperty;
import net.minecraft.client.render.item.property.select.ContextEntityTypeProperty;
import net.minecraft.client.render.item.property.select.CustomModelDataStringProperty;
import net.minecraft.client.render.item.property.select.DisplayContextProperty;
import net.minecraft.client.render.item.property.select.ItemBlockStateProperty;
import net.minecraft.client.render.item.property.select.LocalTimeProperty;
import net.minecraft.client.render.item.property.select.MainHandProperty;
import net.minecraft.client.render.item.property.select.SelectProperty;
import net.minecraft.client.render.item.property.select.TrimMaterialProperty;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

@Environment(value=EnvType.CLIENT)
public class SelectProperties {
    public static final Codecs.IdMapper<Identifier, SelectProperty.Type<?, ?>> ID_MAPPER = new Codecs.IdMapper();
    public static final Codec<SelectProperty.Type<?, ?>> CODEC = ID_MAPPER.getCodec(Identifier.CODEC);

    public static void bootstrap() {
        ID_MAPPER.put((Object)Identifier.ofVanilla((String)"custom_model_data"), (Object)CustomModelDataStringProperty.TYPE);
        ID_MAPPER.put((Object)Identifier.ofVanilla((String)"main_hand"), (Object)MainHandProperty.TYPE);
        ID_MAPPER.put((Object)Identifier.ofVanilla((String)"charge_type"), (Object)ChargeTypeProperty.TYPE);
        ID_MAPPER.put((Object)Identifier.ofVanilla((String)"trim_material"), (Object)TrimMaterialProperty.TYPE);
        ID_MAPPER.put((Object)Identifier.ofVanilla((String)"block_state"), (Object)ItemBlockStateProperty.TYPE);
        ID_MAPPER.put((Object)Identifier.ofVanilla((String)"display_context"), (Object)DisplayContextProperty.TYPE);
        ID_MAPPER.put((Object)Identifier.ofVanilla((String)"local_time"), (Object)LocalTimeProperty.TYPE);
        ID_MAPPER.put((Object)Identifier.ofVanilla((String)"context_entity_type"), (Object)ContextEntityTypeProperty.TYPE);
        ID_MAPPER.put((Object)Identifier.ofVanilla((String)"context_dimension"), (Object)ContextDimensionProperty.TYPE);
        ID_MAPPER.put((Object)Identifier.ofVanilla((String)"component"), (Object)ComponentSelectProperty.getTypeInstance());
    }
}


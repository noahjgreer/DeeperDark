/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.item.property.bool.BooleanProperties
 *  net.minecraft.client.render.item.property.bool.BooleanProperty
 *  net.minecraft.client.render.item.property.bool.BrokenProperty
 *  net.minecraft.client.render.item.property.bool.BundleHasSelectedItemProperty
 *  net.minecraft.client.render.item.property.bool.CarriedProperty
 *  net.minecraft.client.render.item.property.bool.ComponentBooleanProperty
 *  net.minecraft.client.render.item.property.bool.CustomModelDataFlagProperty
 *  net.minecraft.client.render.item.property.bool.DamagedProperty
 *  net.minecraft.client.render.item.property.bool.ExtendedViewProperty
 *  net.minecraft.client.render.item.property.bool.FishingRodCastProperty
 *  net.minecraft.client.render.item.property.bool.HasComponentProperty
 *  net.minecraft.client.render.item.property.bool.KeybindDownProperty
 *  net.minecraft.client.render.item.property.bool.SelectedProperty
 *  net.minecraft.client.render.item.property.bool.UsingItemProperty
 *  net.minecraft.client.render.item.property.bool.ViewEntityProperty
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.dynamic.Codecs$IdMapper
 */
package net.minecraft.client.render.item.property.bool;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.property.bool.BooleanProperty;
import net.minecraft.client.render.item.property.bool.BrokenProperty;
import net.minecraft.client.render.item.property.bool.BundleHasSelectedItemProperty;
import net.minecraft.client.render.item.property.bool.CarriedProperty;
import net.minecraft.client.render.item.property.bool.ComponentBooleanProperty;
import net.minecraft.client.render.item.property.bool.CustomModelDataFlagProperty;
import net.minecraft.client.render.item.property.bool.DamagedProperty;
import net.minecraft.client.render.item.property.bool.ExtendedViewProperty;
import net.minecraft.client.render.item.property.bool.FishingRodCastProperty;
import net.minecraft.client.render.item.property.bool.HasComponentProperty;
import net.minecraft.client.render.item.property.bool.KeybindDownProperty;
import net.minecraft.client.render.item.property.bool.SelectedProperty;
import net.minecraft.client.render.item.property.bool.UsingItemProperty;
import net.minecraft.client.render.item.property.bool.ViewEntityProperty;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

@Environment(value=EnvType.CLIENT)
public class BooleanProperties {
    public static final Codecs.IdMapper<Identifier, MapCodec<? extends BooleanProperty>> ID_MAPPER = new Codecs.IdMapper();
    public static final MapCodec<BooleanProperty> CODEC = ID_MAPPER.getCodec(Identifier.CODEC).dispatchMap("property", BooleanProperty::getCodec, codec -> codec);

    public static void bootstrap() {
        ID_MAPPER.put((Object)Identifier.ofVanilla((String)"custom_model_data"), (Object)CustomModelDataFlagProperty.CODEC);
        ID_MAPPER.put((Object)Identifier.ofVanilla((String)"using_item"), (Object)UsingItemProperty.CODEC);
        ID_MAPPER.put((Object)Identifier.ofVanilla((String)"broken"), (Object)BrokenProperty.CODEC);
        ID_MAPPER.put((Object)Identifier.ofVanilla((String)"damaged"), (Object)DamagedProperty.CODEC);
        ID_MAPPER.put((Object)Identifier.ofVanilla((String)"fishing_rod/cast"), (Object)FishingRodCastProperty.CODEC);
        ID_MAPPER.put((Object)Identifier.ofVanilla((String)"has_component"), (Object)HasComponentProperty.CODEC);
        ID_MAPPER.put((Object)Identifier.ofVanilla((String)"bundle/has_selected_item"), (Object)BundleHasSelectedItemProperty.CODEC);
        ID_MAPPER.put((Object)Identifier.ofVanilla((String)"selected"), (Object)SelectedProperty.CODEC);
        ID_MAPPER.put((Object)Identifier.ofVanilla((String)"carried"), (Object)CarriedProperty.CODEC);
        ID_MAPPER.put((Object)Identifier.ofVanilla((String)"extended_view"), (Object)ExtendedViewProperty.CODEC);
        ID_MAPPER.put((Object)Identifier.ofVanilla((String)"keybind_down"), (Object)KeybindDownProperty.CODEC);
        ID_MAPPER.put((Object)Identifier.ofVanilla((String)"view_entity"), (Object)ViewEntityProperty.CODEC);
        ID_MAPPER.put((Object)Identifier.ofVanilla((String)"component"), (Object)ComponentBooleanProperty.CODEC);
    }
}


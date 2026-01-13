/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.item.model.BasicItemModel$Unbaked
 *  net.minecraft.client.render.item.model.BundleSelectedItemModel$Unbaked
 *  net.minecraft.client.render.item.model.CompositeItemModel$Unbaked
 *  net.minecraft.client.render.item.model.ConditionItemModel$Unbaked
 *  net.minecraft.client.render.item.model.EmptyItemModel$Unbaked
 *  net.minecraft.client.render.item.model.ItemModel$Unbaked
 *  net.minecraft.client.render.item.model.ItemModelTypes
 *  net.minecraft.client.render.item.model.RangeDispatchItemModel$Unbaked
 *  net.minecraft.client.render.item.model.SelectItemModel$Unbaked
 *  net.minecraft.client.render.item.model.SpecialItemModel$Unbaked
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.dynamic.Codecs$IdMapper
 */
package net.minecraft.client.render.item.model;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.model.BasicItemModel;
import net.minecraft.client.render.item.model.BundleSelectedItemModel;
import net.minecraft.client.render.item.model.CompositeItemModel;
import net.minecraft.client.render.item.model.ConditionItemModel;
import net.minecraft.client.render.item.model.EmptyItemModel;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.RangeDispatchItemModel;
import net.minecraft.client.render.item.model.SelectItemModel;
import net.minecraft.client.render.item.model.SpecialItemModel;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

@Environment(value=EnvType.CLIENT)
public class ItemModelTypes {
    public static final Codecs.IdMapper<Identifier, MapCodec<? extends ItemModel.Unbaked>> ID_MAPPER = new Codecs.IdMapper();
    public static final Codec<ItemModel.Unbaked> CODEC = ID_MAPPER.getCodec(Identifier.CODEC).dispatch(ItemModel.Unbaked::getCodec, codec -> codec);

    public static void bootstrap() {
        ID_MAPPER.put((Object)Identifier.ofVanilla((String)"empty"), (Object)EmptyItemModel.Unbaked.CODEC);
        ID_MAPPER.put((Object)Identifier.ofVanilla((String)"model"), (Object)BasicItemModel.Unbaked.CODEC);
        ID_MAPPER.put((Object)Identifier.ofVanilla((String)"range_dispatch"), (Object)RangeDispatchItemModel.Unbaked.CODEC);
        ID_MAPPER.put((Object)Identifier.ofVanilla((String)"special"), (Object)SpecialItemModel.Unbaked.CODEC);
        ID_MAPPER.put((Object)Identifier.ofVanilla((String)"composite"), (Object)CompositeItemModel.Unbaked.CODEC);
        ID_MAPPER.put((Object)Identifier.ofVanilla((String)"bundle/selected_item"), (Object)BundleSelectedItemModel.Unbaked.CODEC);
        ID_MAPPER.put((Object)Identifier.ofVanilla((String)"select"), (Object)SelectItemModel.Unbaked.CODEC);
        ID_MAPPER.put((Object)Identifier.ofVanilla((String)"condition"), (Object)ConditionItemModel.Unbaked.CODEC);
    }
}


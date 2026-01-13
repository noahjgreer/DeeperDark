/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.data.EquipmentAssetProvider
 *  net.minecraft.client.render.entity.equipment.EquipmentModel
 *  net.minecraft.client.render.entity.equipment.EquipmentModel$Layer
 *  net.minecraft.client.render.entity.equipment.EquipmentModel$LayerType
 *  net.minecraft.data.DataOutput
 *  net.minecraft.data.DataOutput$OutputType
 *  net.minecraft.data.DataOutput$PathResolver
 *  net.minecraft.data.DataProvider
 *  net.minecraft.data.DataWriter
 *  net.minecraft.item.equipment.EquipmentAsset
 *  net.minecraft.item.equipment.EquipmentAssetKeys
 *  net.minecraft.registry.RegistryKey
 *  net.minecraft.util.DyeColor
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.data;

import com.mojang.serialization.Codec;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.item.equipment.EquipmentAssetKeys;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class EquipmentAssetProvider
implements DataProvider {
    private final DataOutput.PathResolver pathResolver;

    public EquipmentAssetProvider(DataOutput output) {
        this.pathResolver = output.getResolver(DataOutput.OutputType.RESOURCE_PACK, "equipment");
    }

    private static void bootstrap(BiConsumer<RegistryKey<EquipmentAsset>, EquipmentModel> equipmentBiConsumer) {
        RegistryKey registryKey;
        DyeColor dyeColor;
        equipmentBiConsumer.accept((RegistryKey<EquipmentAsset>)EquipmentAssetKeys.LEATHER, EquipmentModel.builder().addHumanoidLayers(Identifier.ofVanilla((String)"leather"), true).addHumanoidLayers(Identifier.ofVanilla((String)"leather_overlay"), false).addLayers(EquipmentModel.LayerType.HORSE_BODY, new EquipmentModel.Layer[]{EquipmentModel.Layer.createWithLeatherColor((Identifier)Identifier.ofVanilla((String)"leather"), (boolean)true), EquipmentModel.Layer.createWithLeatherColor((Identifier)Identifier.ofVanilla((String)"leather_overlay"), (boolean)false)}).build());
        equipmentBiConsumer.accept((RegistryKey<EquipmentAsset>)EquipmentAssetKeys.CHAINMAIL, EquipmentAssetProvider.createHumanoidOnlyModel((String)"chainmail"));
        equipmentBiConsumer.accept((RegistryKey<EquipmentAsset>)EquipmentAssetKeys.COPPER, EquipmentAssetProvider.createHumanoidAndHorseModel((String)"copper"));
        equipmentBiConsumer.accept((RegistryKey<EquipmentAsset>)EquipmentAssetKeys.IRON, EquipmentAssetProvider.createHumanoidAndHorseModel((String)"iron"));
        equipmentBiConsumer.accept((RegistryKey<EquipmentAsset>)EquipmentAssetKeys.GOLD, EquipmentAssetProvider.createHumanoidAndHorseModel((String)"gold"));
        equipmentBiConsumer.accept((RegistryKey<EquipmentAsset>)EquipmentAssetKeys.DIAMOND, EquipmentAssetProvider.createHumanoidAndHorseModel((String)"diamond"));
        equipmentBiConsumer.accept((RegistryKey<EquipmentAsset>)EquipmentAssetKeys.TURTLE_SCUTE, EquipmentModel.builder().addMainHumanoidLayer(Identifier.ofVanilla((String)"turtle_scute"), false).build());
        equipmentBiConsumer.accept((RegistryKey<EquipmentAsset>)EquipmentAssetKeys.NETHERITE, EquipmentAssetProvider.createHumanoidAndHorseModel((String)"netherite"));
        equipmentBiConsumer.accept((RegistryKey<EquipmentAsset>)EquipmentAssetKeys.ARMADILLO_SCUTE, EquipmentModel.builder().addLayers(EquipmentModel.LayerType.WOLF_BODY, new EquipmentModel.Layer[]{EquipmentModel.Layer.create((Identifier)Identifier.ofVanilla((String)"armadillo_scute"), (boolean)false)}).addLayers(EquipmentModel.LayerType.WOLF_BODY, new EquipmentModel.Layer[]{EquipmentModel.Layer.create((Identifier)Identifier.ofVanilla((String)"armadillo_scute_overlay"), (boolean)true)}).build());
        equipmentBiConsumer.accept((RegistryKey<EquipmentAsset>)EquipmentAssetKeys.ELYTRA, EquipmentModel.builder().addLayers(EquipmentModel.LayerType.WINGS, new EquipmentModel.Layer[]{new EquipmentModel.Layer(Identifier.ofVanilla((String)"elytra"), Optional.empty(), true)}).build());
        EquipmentModel.Layer layer = new EquipmentModel.Layer(Identifier.ofVanilla((String)"saddle"));
        equipmentBiConsumer.accept((RegistryKey<EquipmentAsset>)EquipmentAssetKeys.SADDLE, EquipmentModel.builder().addLayers(EquipmentModel.LayerType.PIG_SADDLE, new EquipmentModel.Layer[]{layer}).addLayers(EquipmentModel.LayerType.STRIDER_SADDLE, new EquipmentModel.Layer[]{layer}).addLayers(EquipmentModel.LayerType.CAMEL_SADDLE, new EquipmentModel.Layer[]{layer}).addLayers(EquipmentModel.LayerType.CAMEL_HUSK_SADDLE, new EquipmentModel.Layer[]{layer}).addLayers(EquipmentModel.LayerType.HORSE_SADDLE, new EquipmentModel.Layer[]{layer}).addLayers(EquipmentModel.LayerType.DONKEY_SADDLE, new EquipmentModel.Layer[]{layer}).addLayers(EquipmentModel.LayerType.MULE_SADDLE, new EquipmentModel.Layer[]{layer}).addLayers(EquipmentModel.LayerType.SKELETON_HORSE_SADDLE, new EquipmentModel.Layer[]{layer}).addLayers(EquipmentModel.LayerType.ZOMBIE_HORSE_SADDLE, new EquipmentModel.Layer[]{layer}).addLayers(EquipmentModel.LayerType.NAUTILUS_SADDLE, new EquipmentModel.Layer[]{layer}).build());
        for (Map.Entry entry : EquipmentAssetKeys.HARNESS_FROM_COLOR.entrySet()) {
            dyeColor = (DyeColor)entry.getKey();
            registryKey = (RegistryKey)entry.getValue();
            equipmentBiConsumer.accept((RegistryKey<EquipmentAsset>)registryKey, EquipmentModel.builder().addLayers(EquipmentModel.LayerType.HAPPY_GHAST_BODY, new EquipmentModel.Layer[]{EquipmentModel.Layer.create((Identifier)Identifier.ofVanilla((String)(dyeColor.asString() + "_harness")), (boolean)false)}).build());
        }
        for (Map.Entry entry : EquipmentAssetKeys.CARPET_FROM_COLOR.entrySet()) {
            dyeColor = (DyeColor)entry.getKey();
            registryKey = (RegistryKey)entry.getValue();
            equipmentBiConsumer.accept((RegistryKey<EquipmentAsset>)registryKey, EquipmentModel.builder().addLayers(EquipmentModel.LayerType.LLAMA_BODY, new EquipmentModel.Layer[]{new EquipmentModel.Layer(Identifier.ofVanilla((String)dyeColor.asString()))}).build());
        }
        equipmentBiConsumer.accept((RegistryKey<EquipmentAsset>)EquipmentAssetKeys.TRADER_LLAMA, EquipmentModel.builder().addLayers(EquipmentModel.LayerType.LLAMA_BODY, new EquipmentModel.Layer[]{new EquipmentModel.Layer(Identifier.ofVanilla((String)"trader_llama"))}).build());
    }

    private static EquipmentModel createHumanoidOnlyModel(String id) {
        return EquipmentModel.builder().addHumanoidLayers(Identifier.ofVanilla((String)id)).build();
    }

    private static EquipmentModel createHumanoidAndHorseModel(String id) {
        return EquipmentModel.builder().addHumanoidLayers(Identifier.ofVanilla((String)id)).addLayers(EquipmentModel.LayerType.HORSE_BODY, new EquipmentModel.Layer[]{EquipmentModel.Layer.createWithLeatherColor((Identifier)Identifier.ofVanilla((String)id), (boolean)false)}).addLayers(EquipmentModel.LayerType.NAUTILUS_BODY, new EquipmentModel.Layer[]{EquipmentModel.Layer.createWithLeatherColor((Identifier)Identifier.ofVanilla((String)id), (boolean)false)}).build();
    }

    public CompletableFuture<?> run(DataWriter writer) {
        HashMap map = new HashMap();
        EquipmentAssetProvider.bootstrap((T key, U model) -> {
            if (map.putIfAbsent(key, model) != null) {
                throw new IllegalStateException("Tried to register equipment asset twice for id: " + String.valueOf(key));
            }
        });
        return DataProvider.writeAllToPath((DataWriter)writer, (Codec)EquipmentModel.CODEC, arg_0 -> ((DataOutput.PathResolver)this.pathResolver).resolveJson(arg_0), map);
    }

    public String getName() {
        return "Equipment Asset Definitions";
    }
}


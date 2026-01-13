/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.feature.VillagerClothingFeatureRenderer
 *  net.minecraft.client.render.entity.feature.VillagerResourceMetadata
 *  net.minecraft.client.render.entity.feature.VillagerResourceMetadata$HatType
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.entity.state.VillagerDataRenderState
 *  net.minecraft.client.texture.MissingSprite
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.registry.RegistryKey
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.resource.ResourceManager
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.Util
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.village.VillagerData
 *  net.minecraft.village.VillagerProfession
 *  net.minecraft.village.VillagerType
 */
package net.minecraft.client.render.entity.feature;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.io.IOException;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.VillagerResourceMetadata;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.VillagerDataRenderState;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class VillagerClothingFeatureRenderer<S extends LivingEntityRenderState, M extends EntityModel<S>>
extends FeatureRenderer<S, M> {
    private static final Int2ObjectMap<Identifier> LEVEL_TO_ID = (Int2ObjectMap)Util.make((Object)new Int2ObjectOpenHashMap(), levelToId -> {
        levelToId.put(1, (Object)Identifier.ofVanilla((String)"stone"));
        levelToId.put(2, (Object)Identifier.ofVanilla((String)"iron"));
        levelToId.put(3, (Object)Identifier.ofVanilla((String)"gold"));
        levelToId.put(4, (Object)Identifier.ofVanilla((String)"emerald"));
        levelToId.put(5, (Object)Identifier.ofVanilla((String)"diamond"));
    });
    private final Object2ObjectMap<RegistryKey<VillagerType>, VillagerResourceMetadata.HatType> villagerTypeToHat = new Object2ObjectOpenHashMap();
    private final Object2ObjectMap<RegistryKey<VillagerProfession>, VillagerResourceMetadata.HatType> professionToHat = new Object2ObjectOpenHashMap();
    private final ResourceManager resourceManager;
    private final String entityType;
    private final M adultModel;
    private final M babyModel;

    public VillagerClothingFeatureRenderer(FeatureRendererContext<S, M> context, ResourceManager resourceManager, String entityType, M adultModel, M babyModel) {
        super(context);
        this.resourceManager = resourceManager;
        this.entityType = entityType;
        this.adultModel = adultModel;
        this.babyModel = babyModel;
    }

    public void render(MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i, S livingEntityRenderState, float f, float g) {
        if (((LivingEntityRenderState)livingEntityRenderState).invisible) {
            return;
        }
        VillagerData villagerData = ((VillagerDataRenderState)livingEntityRenderState).getVillagerData();
        if (villagerData == null) {
            return;
        }
        RegistryEntry registryEntry = villagerData.type();
        RegistryEntry registryEntry2 = villagerData.profession();
        VillagerResourceMetadata.HatType hatType = this.getHatType(this.villagerTypeToHat, "type", registryEntry);
        VillagerResourceMetadata.HatType hatType2 = this.getHatType(this.professionToHat, "profession", registryEntry2);
        EntityModel entityModel = this.getContextModel();
        Identifier identifier = this.getTexture("type", registryEntry);
        boolean bl = hatType2 == VillagerResourceMetadata.HatType.NONE || hatType2 == VillagerResourceMetadata.HatType.PARTIAL && hatType != VillagerResourceMetadata.HatType.FULL;
        EntityModel entityModel2 = ((LivingEntityRenderState)livingEntityRenderState).baby ? this.babyModel : this.adultModel;
        VillagerClothingFeatureRenderer.renderModel((Model)(bl ? entityModel : entityModel2), (Identifier)identifier, (MatrixStack)matrixStack, (OrderedRenderCommandQueue)orderedRenderCommandQueue, (int)i, livingEntityRenderState, (int)-1, (int)1);
        if (!registryEntry2.matchesKey(VillagerProfession.NONE) && !((LivingEntityRenderState)livingEntityRenderState).baby) {
            Identifier identifier2 = this.getTexture("profession", registryEntry2);
            VillagerClothingFeatureRenderer.renderModel((Model)entityModel, (Identifier)identifier2, (MatrixStack)matrixStack, (OrderedRenderCommandQueue)orderedRenderCommandQueue, (int)i, livingEntityRenderState, (int)-1, (int)2);
            if (!registryEntry2.matchesKey(VillagerProfession.NITWIT)) {
                Identifier identifier3 = this.getTexture("profession_level", (Identifier)LEVEL_TO_ID.get(MathHelper.clamp((int)villagerData.level(), (int)1, (int)LEVEL_TO_ID.size())));
                VillagerClothingFeatureRenderer.renderModel((Model)entityModel, (Identifier)identifier3, (MatrixStack)matrixStack, (OrderedRenderCommandQueue)orderedRenderCommandQueue, (int)i, livingEntityRenderState, (int)-1, (int)3);
            }
        }
    }

    private Identifier getTexture(String keyType, Identifier keyId) {
        return keyId.withPath(path -> "textures/entity/" + this.entityType + "/" + keyType + "/" + path + ".png");
    }

    private Identifier getTexture(String keyType, RegistryEntry<?> entry) {
        return entry.getKey().map(key -> this.getTexture(keyType, key.getValue())).orElse(MissingSprite.getMissingSpriteId());
    }

    public <K> VillagerResourceMetadata.HatType getHatType(Object2ObjectMap<RegistryKey<K>, VillagerResourceMetadata.HatType> metadataMap, String keyType, RegistryEntry<K> entry) {
        RegistryKey registryKey = entry.getKey().orElse(null);
        if (registryKey == null) {
            return VillagerResourceMetadata.HatType.NONE;
        }
        return (VillagerResourceMetadata.HatType)metadataMap.computeIfAbsent((Object)registryKey, key -> this.resourceManager.getResource(this.getTexture(keyType, registryKey.getValue())).flatMap(resource -> {
            try {
                return resource.getMetadata().decode(VillagerResourceMetadata.SERIALIZER).map(VillagerResourceMetadata::hatType);
            }
            catch (IOException iOException) {
                return Optional.empty();
            }
        }).orElse(VillagerResourceMetadata.HatType.NONE));
    }
}


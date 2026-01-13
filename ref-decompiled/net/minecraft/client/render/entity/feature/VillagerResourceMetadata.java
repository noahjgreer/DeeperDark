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
 *  net.minecraft.client.render.entity.feature.VillagerResourceMetadata
 *  net.minecraft.client.render.entity.feature.VillagerResourceMetadata$HatType
 *  net.minecraft.resource.metadata.ResourceMetadataSerializer
 */
package net.minecraft.client.render.entity.feature;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.VillagerResourceMetadata;
import net.minecraft.resource.metadata.ResourceMetadataSerializer;

@Environment(value=EnvType.CLIENT)
public record VillagerResourceMetadata(HatType hatType) {
    private final HatType hatType;
    public static final Codec<VillagerResourceMetadata> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)HatType.CODEC.optionalFieldOf("hat", (Object)HatType.NONE).forGetter(VillagerResourceMetadata::hatType)).apply((Applicative)instance, VillagerResourceMetadata::new));
    public static final ResourceMetadataSerializer<VillagerResourceMetadata> SERIALIZER = new ResourceMetadataSerializer("villager", CODEC);

    public VillagerResourceMetadata(HatType hatType) {
        this.hatType = hatType;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{VillagerResourceMetadata.class, "hat", "hatType"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{VillagerResourceMetadata.class, "hat", "hatType"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{VillagerResourceMetadata.class, "hat", "hatType"}, this, object);
    }

    public HatType hatType() {
        return this.hatType;
    }
}


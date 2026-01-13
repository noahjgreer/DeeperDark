/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.resource.language.LanguageDefinition
 *  net.minecraft.client.resource.metadata.LanguageResourceMetadata
 *  net.minecraft.resource.metadata.ResourceMetadataSerializer
 */
package net.minecraft.client.resource.metadata;

import com.mojang.serialization.Codec;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.resource.metadata.ResourceMetadataSerializer;

@Environment(value=EnvType.CLIENT)
public record LanguageResourceMetadata(Map<String, LanguageDefinition> definitions) {
    private final Map<String, LanguageDefinition> definitions;
    public static final Codec<String> LANGUAGE_CODE_CODEC = Codec.string((int)1, (int)16);
    public static final Codec<LanguageResourceMetadata> CODEC = Codec.unboundedMap((Codec)LANGUAGE_CODE_CODEC, (Codec)LanguageDefinition.CODEC).xmap(LanguageResourceMetadata::new, LanguageResourceMetadata::definitions);
    public static final ResourceMetadataSerializer<LanguageResourceMetadata> SERIALIZER = new ResourceMetadataSerializer("language", CODEC);

    public LanguageResourceMetadata(Map<String, LanguageDefinition> definitions) {
        this.definitions = definitions;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{LanguageResourceMetadata.class, "languages", "definitions"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{LanguageResourceMetadata.class, "languages", "definitions"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{LanguageResourceMetadata.class, "languages", "definitions"}, this, object);
    }

    public Map<String, LanguageDefinition> definitions() {
        return this.definitions;
    }
}


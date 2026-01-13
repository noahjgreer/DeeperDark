/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.resource.metadata;

import java.util.Optional;
import net.minecraft.resource.metadata.ResourceMetadata;
import net.minecraft.resource.metadata.ResourceMetadataSerializer;

class ResourceMetadata.1
implements ResourceMetadata {
    ResourceMetadata.1() {
    }

    @Override
    public <T> Optional<T> decode(ResourceMetadataSerializer<T> serializer) {
        return Optional.empty();
    }
}

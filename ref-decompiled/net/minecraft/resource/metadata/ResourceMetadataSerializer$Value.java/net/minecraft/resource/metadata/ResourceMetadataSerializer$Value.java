/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.resource.metadata;

import java.util.Optional;
import net.minecraft.resource.metadata.ResourceMetadataSerializer;

public record ResourceMetadataSerializer.Value<T>(ResourceMetadataSerializer<T> type, T value) {
    public <U> Optional<U> getValueIfMatching(ResourceMetadataSerializer<U> o) {
        return o == this.type ? Optional.of(this.value) : Optional.empty();
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.resource;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import net.minecraft.resource.ResourcePackCompatibility;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.text.Text;

public static final class ResourcePackProfile.Metadata
extends Record {
    final Text description;
    private final ResourcePackCompatibility compatibility;
    private final FeatureSet requestedFeatures;
    private final List<String> overlays;

    public ResourcePackProfile.Metadata(Text description, ResourcePackCompatibility compatibility, FeatureSet requestedFeatures, List<String> overlays) {
        this.description = description;
        this.compatibility = compatibility;
        this.requestedFeatures = requestedFeatures;
        this.overlays = overlays;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ResourcePackProfile.Metadata.class, "description;compatibility;requestedFeatures;overlays", "description", "compatibility", "requestedFeatures", "overlays"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ResourcePackProfile.Metadata.class, "description;compatibility;requestedFeatures;overlays", "description", "compatibility", "requestedFeatures", "overlays"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ResourcePackProfile.Metadata.class, "description;compatibility;requestedFeatures;overlays", "description", "compatibility", "requestedFeatures", "overlays"}, this, object);
    }

    public Text description() {
        return this.description;
    }

    public ResourcePackCompatibility compatibility() {
        return this.compatibility;
    }

    public FeatureSet requestedFeatures() {
        return this.requestedFeatures;
    }

    public List<String> overlays() {
        return this.overlays;
    }
}

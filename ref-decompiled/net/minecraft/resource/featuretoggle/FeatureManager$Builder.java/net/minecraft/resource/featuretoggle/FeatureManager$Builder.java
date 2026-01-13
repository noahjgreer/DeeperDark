/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.resource.featuretoggle;

import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.resource.featuretoggle.FeatureFlag;
import net.minecraft.resource.featuretoggle.FeatureManager;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.resource.featuretoggle.FeatureUniverse;
import net.minecraft.util.Identifier;

public static class FeatureManager.Builder {
    private final FeatureUniverse universe;
    private int id;
    private final Map<Identifier, FeatureFlag> featureFlags = new LinkedHashMap<Identifier, FeatureFlag>();

    public FeatureManager.Builder(String universe) {
        this.universe = new FeatureUniverse(universe);
    }

    public FeatureFlag addVanillaFlag(String feature) {
        return this.addFlag(Identifier.ofVanilla(feature));
    }

    public FeatureFlag addFlag(Identifier feature) {
        FeatureFlag featureFlag;
        FeatureFlag featureFlag2;
        if (this.id >= 64) {
            throw new IllegalStateException("Too many feature flags");
        }
        if ((featureFlag2 = this.featureFlags.put(feature, featureFlag = new FeatureFlag(this.universe, this.id++))) != null) {
            throw new IllegalStateException("Duplicate feature flag " + String.valueOf(feature));
        }
        return featureFlag;
    }

    public FeatureManager build() {
        FeatureSet featureSet = FeatureSet.of(this.universe, this.featureFlags.values());
        return new FeatureManager(this.universe, featureSet, Map.copyOf(this.featureFlags));
    }
}

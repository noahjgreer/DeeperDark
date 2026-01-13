/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 */
package net.minecraft.datafixer.fix;

import com.google.common.collect.ImmutableMap;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import java.util.Map;

static final class StructuresToConfiguredStructuresFix.Mapping
extends Record {
    private final Map<String, String> biomeMapping;
    final String fallback;

    private StructuresToConfiguredStructuresFix.Mapping(Map<String, String> biomeMapping, String fallback) {
        this.biomeMapping = biomeMapping;
        this.fallback = fallback;
    }

    public static StructuresToConfiguredStructuresFix.Mapping create(String mapping) {
        return new StructuresToConfiguredStructuresFix.Mapping(Map.of(), mapping);
    }

    public static StructuresToConfiguredStructuresFix.Mapping create(Map<List<String>, String> biomeMapping, String fallback) {
        return new StructuresToConfiguredStructuresFix.Mapping(StructuresToConfiguredStructuresFix.Mapping.flattenBiomeMapping(biomeMapping), fallback);
    }

    private static Map<String, String> flattenBiomeMapping(Map<List<String>, String> biomeMapping) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        for (Map.Entry<List<String>, String> entry : biomeMapping.entrySet()) {
            entry.getKey().forEach(key -> builder.put(key, (Object)((String)entry.getValue())));
        }
        return builder.build();
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{StructuresToConfiguredStructuresFix.Mapping.class, "biomeMapping;fallback", "biomeMapping", "fallback"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{StructuresToConfiguredStructuresFix.Mapping.class, "biomeMapping;fallback", "biomeMapping", "fallback"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{StructuresToConfiguredStructuresFix.Mapping.class, "biomeMapping;fallback", "biomeMapping", "fallback"}, this, object);
    }

    public Map<String, String> biomeMapping() {
        return this.biomeMapping;
    }

    public String fallback() {
        return this.fallback;
    }
}

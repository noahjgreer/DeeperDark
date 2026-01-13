/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.resource;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.util.Identifier;

public static final class ResourceManager.Empty
extends Enum<ResourceManager.Empty>
implements ResourceManager {
    public static final /* enum */ ResourceManager.Empty INSTANCE = new ResourceManager.Empty();
    private static final /* synthetic */ ResourceManager.Empty[] field_25352;

    public static ResourceManager.Empty[] values() {
        return (ResourceManager.Empty[])field_25352.clone();
    }

    public static ResourceManager.Empty valueOf(String string) {
        return Enum.valueOf(ResourceManager.Empty.class, string);
    }

    @Override
    public Set<String> getAllNamespaces() {
        return Set.of();
    }

    @Override
    public Optional<Resource> getResource(Identifier identifier) {
        return Optional.empty();
    }

    @Override
    public List<Resource> getAllResources(Identifier id) {
        return List.of();
    }

    @Override
    public Map<Identifier, Resource> findResources(String startingPath, Predicate<Identifier> allowedPathPredicate) {
        return Map.of();
    }

    @Override
    public Map<Identifier, List<Resource>> findAllResources(String startingPath, Predicate<Identifier> allowedPathPredicate) {
        return Map.of();
    }

    @Override
    public Stream<ResourcePack> streamResourcePacks() {
        return Stream.of(new ResourcePack[0]);
    }

    private static /* synthetic */ ResourceManager.Empty[] method_36585() {
        return new ResourceManager.Empty[]{INSTANCE};
    }

    static {
        field_25352 = ResourceManager.Empty.method_36585();
    }
}

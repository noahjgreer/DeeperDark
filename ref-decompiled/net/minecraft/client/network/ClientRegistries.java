/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.network.ClientDynamicRegistryType
 *  net.minecraft.client.network.ClientRegistries
 *  net.minecraft.client.network.ClientRegistries$DynamicRegistries
 *  net.minecraft.client.network.ClientRegistries$Tags
 *  net.minecraft.registry.CombinedDynamicRegistries
 *  net.minecraft.registry.DynamicRegistryManager
 *  net.minecraft.registry.DynamicRegistryManager$Immutable
 *  net.minecraft.registry.Registry
 *  net.minecraft.registry.Registry$PendingTagLoad
 *  net.minecraft.registry.RegistryKey
 *  net.minecraft.registry.RegistryLoader
 *  net.minecraft.registry.RegistryLoader$ElementsAndTags
 *  net.minecraft.registry.SerializableRegistries
 *  net.minecraft.registry.SerializableRegistries$SerializedRegistryEntry
 *  net.minecraft.registry.tag.TagGroupLoader
 *  net.minecraft.registry.tag.TagPacketSerializer$Serialized
 *  net.minecraft.resource.ResourceFactory
 *  net.minecraft.util.crash.CrashException
 *  net.minecraft.util.crash.CrashReport
 *  net.minecraft.util.crash.CrashReportSection
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.network;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientDynamicRegistryType;
import net.minecraft.client.network.ClientRegistries;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryLoader;
import net.minecraft.registry.SerializableRegistries;
import net.minecraft.registry.tag.TagGroupLoader;
import net.minecraft.registry.tag.TagPacketSerializer;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class ClientRegistries {
    private // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable ClientRegistries.DynamicRegistries dynamicRegistries;
    private // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable ClientRegistries.Tags tags;

    public void putDynamicRegistry(RegistryKey<? extends Registry<?>> registryRef, List<SerializableRegistries.SerializedRegistryEntry> entries) {
        if (this.dynamicRegistries == null) {
            this.dynamicRegistries = new DynamicRegistries();
        }
        this.dynamicRegistries.put(registryRef, entries);
    }

    public void putTags(Map<RegistryKey<? extends Registry<?>>, TagPacketSerializer.Serialized> tags) {
        if (this.tags == null) {
            this.tags = new Tags();
        }
        tags.forEach((arg_0, arg_1) -> ((Tags)this.tags).put(arg_0, arg_1));
    }

    private static <T> Registry.PendingTagLoad<T> startTagReload(DynamicRegistryManager.Immutable registryManager, RegistryKey<? extends Registry<? extends T>> registryRef, TagPacketSerializer.Serialized tags) {
        Registry registry = registryManager.getOrThrow(registryRef);
        return registry.startTagReload(tags.toRegistryTags(registry));
    }

    private DynamicRegistryManager createRegistryManager(ResourceFactory resourceFactory, DynamicRegistries dynamicRegistries, boolean local) {
        DynamicRegistryManager.Immutable immutable2;
        CombinedDynamicRegistries combinedDynamicRegistries = ClientDynamicRegistryType.createCombinedDynamicRegistries();
        DynamicRegistryManager.Immutable immutable = combinedDynamicRegistries.getPrecedingRegistryManagers((Object)ClientDynamicRegistryType.REMOTE);
        HashMap map = new HashMap();
        dynamicRegistries.dynamicRegistries.forEach((registryRef, entries) -> map.put(registryRef, new RegistryLoader.ElementsAndTags(entries, TagPacketSerializer.Serialized.NONE)));
        ArrayList list = new ArrayList();
        if (this.tags != null) {
            this.tags.forEach((registryRef, tags) -> {
                if (tags.isEmpty()) {
                    return;
                }
                if (SerializableRegistries.isSynced((RegistryKey)registryRef)) {
                    map.compute(registryRef, (key, value) -> {
                        List list = value != null ? value.elements() : List.of();
                        return new RegistryLoader.ElementsAndTags(list, tags);
                    });
                } else if (!local) {
                    list.add(ClientRegistries.startTagReload((DynamicRegistryManager.Immutable)immutable, (RegistryKey)registryRef, (TagPacketSerializer.Serialized)tags));
                }
            });
        }
        List list2 = TagGroupLoader.collectRegistries((DynamicRegistryManager.Immutable)immutable, list);
        try {
            immutable2 = RegistryLoader.loadFromNetwork(map, (ResourceFactory)resourceFactory, (List)list2, (List)RegistryLoader.SYNCED_REGISTRIES).toImmutable();
        }
        catch (Exception exception) {
            CrashReport crashReport = CrashReport.create((Throwable)exception, (String)"Network Registry Load");
            ClientRegistries.addCrashReportSection((CrashReport)crashReport, map, list);
            throw new CrashException(crashReport);
        }
        DynamicRegistryManager.Immutable dynamicRegistryManager = combinedDynamicRegistries.with((Object)ClientDynamicRegistryType.REMOTE, new DynamicRegistryManager.Immutable[]{immutable2}).getCombinedRegistryManager();
        list.forEach(Registry.PendingTagLoad::apply);
        return dynamicRegistryManager;
    }

    private static void addCrashReportSection(CrashReport crashReport, Map<RegistryKey<? extends Registry<?>>, RegistryLoader.ElementsAndTags> data, List<Registry.PendingTagLoad<?>> tags) {
        CrashReportSection crashReportSection = crashReport.addElement("Received Elements and Tags");
        crashReportSection.add("Dynamic Registries", () -> data.entrySet().stream().sorted(Comparator.comparing(entry -> ((RegistryKey)entry.getKey()).getValue())).map(entry -> String.format(Locale.ROOT, "\n\t\t%s: elements=%d tags=%d", ((RegistryKey)entry.getKey()).getValue(), ((RegistryLoader.ElementsAndTags)entry.getValue()).elements().size(), ((RegistryLoader.ElementsAndTags)entry.getValue()).tags().size())).collect(Collectors.joining()));
        crashReportSection.add("Static Registries", () -> tags.stream().sorted(Comparator.comparing(tag -> tag.getKey().getValue())).map(tag -> String.format(Locale.ROOT, "\n\t\t%s: tags=%d", tag.getKey().getValue(), tag.size())).collect(Collectors.joining()));
    }

    private void loadTags(Tags tags, DynamicRegistryManager.Immutable registryManager, boolean local) {
        tags.forEach((registryRef, serialized) -> {
            if (local || SerializableRegistries.isSynced((RegistryKey)registryRef)) {
                ClientRegistries.startTagReload((DynamicRegistryManager.Immutable)registryManager, (RegistryKey)registryRef, (TagPacketSerializer.Serialized)serialized).apply();
            }
        });
    }

    public DynamicRegistryManager.Immutable createRegistryManager(ResourceFactory resourceFactory, DynamicRegistryManager.Immutable registryManager, boolean local) {
        DynamicRegistryManager.Immutable dynamicRegistryManager;
        if (this.dynamicRegistries != null) {
            dynamicRegistryManager = this.createRegistryManager(resourceFactory, this.dynamicRegistries, local);
        } else {
            if (this.tags != null) {
                this.loadTags(this.tags, registryManager, !local);
            }
            dynamicRegistryManager = registryManager;
        }
        return dynamicRegistryManager.toImmutable();
    }
}


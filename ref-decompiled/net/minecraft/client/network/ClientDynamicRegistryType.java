/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.network.ClientDynamicRegistryType
 *  net.minecraft.registry.CombinedDynamicRegistries
 *  net.minecraft.registry.DynamicRegistryManager
 *  net.minecraft.registry.DynamicRegistryManager$Immutable
 *  net.minecraft.registry.Registries
 *  net.minecraft.registry.Registry
 */
package net.minecraft.client.network;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public final class ClientDynamicRegistryType
extends Enum<ClientDynamicRegistryType> {
    public static final /* enum */ ClientDynamicRegistryType STATIC = new ClientDynamicRegistryType("STATIC", 0);
    public static final /* enum */ ClientDynamicRegistryType REMOTE = new ClientDynamicRegistryType("REMOTE", 1);
    private static final List<ClientDynamicRegistryType> VALUES;
    private static final DynamicRegistryManager.Immutable STATIC_REGISTRY_MANAGER;
    private static final /* synthetic */ ClientDynamicRegistryType[] field_40494;

    public static ClientDynamicRegistryType[] values() {
        return (ClientDynamicRegistryType[])field_40494.clone();
    }

    public static ClientDynamicRegistryType valueOf(String string) {
        return Enum.valueOf(ClientDynamicRegistryType.class, string);
    }

    public static CombinedDynamicRegistries<ClientDynamicRegistryType> createCombinedDynamicRegistries() {
        return new CombinedDynamicRegistries(VALUES).with((Object)STATIC, new DynamicRegistryManager.Immutable[]{STATIC_REGISTRY_MANAGER});
    }

    private static /* synthetic */ ClientDynamicRegistryType[] method_45739() {
        return new ClientDynamicRegistryType[]{STATIC, REMOTE};
    }

    static {
        field_40494 = ClientDynamicRegistryType.method_45739();
        VALUES = List.of(ClientDynamicRegistryType.values());
        STATIC_REGISTRY_MANAGER = DynamicRegistryManager.of((Registry)Registries.REGISTRIES);
    }
}


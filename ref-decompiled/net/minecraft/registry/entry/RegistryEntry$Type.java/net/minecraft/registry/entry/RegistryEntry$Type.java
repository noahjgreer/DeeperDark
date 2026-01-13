/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.registry.entry;

public static final class RegistryEntry.Type
extends Enum<RegistryEntry.Type> {
    public static final /* enum */ RegistryEntry.Type REFERENCE = new RegistryEntry.Type();
    public static final /* enum */ RegistryEntry.Type DIRECT = new RegistryEntry.Type();
    private static final /* synthetic */ RegistryEntry.Type[] field_36448;

    public static RegistryEntry.Type[] values() {
        return (RegistryEntry.Type[])field_36448.clone();
    }

    public static RegistryEntry.Type valueOf(String string) {
        return Enum.valueOf(RegistryEntry.Type.class, string);
    }

    private static /* synthetic */ RegistryEntry.Type[] method_40232() {
        return new RegistryEntry.Type[]{REFERENCE, DIRECT};
    }

    static {
        field_36448 = RegistryEntry.Type.method_40232();
    }
}

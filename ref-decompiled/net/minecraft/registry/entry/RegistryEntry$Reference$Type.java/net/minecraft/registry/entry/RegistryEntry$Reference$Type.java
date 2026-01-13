/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.registry.entry;

protected static final class RegistryEntry.Reference.Type
extends Enum<RegistryEntry.Reference.Type> {
    public static final /* enum */ RegistryEntry.Reference.Type STAND_ALONE = new RegistryEntry.Reference.Type();
    public static final /* enum */ RegistryEntry.Reference.Type INTRUSIVE = new RegistryEntry.Reference.Type();
    private static final /* synthetic */ RegistryEntry.Reference.Type[] field_36456;

    public static RegistryEntry.Reference.Type[] values() {
        return (RegistryEntry.Reference.Type[])field_36456.clone();
    }

    public static RegistryEntry.Reference.Type valueOf(String string) {
        return Enum.valueOf(RegistryEntry.Reference.Type.class, string);
    }

    private static /* synthetic */ RegistryEntry.Reference.Type[] method_40238() {
        return new RegistryEntry.Reference.Type[]{STAND_ALONE, INTRUSIVE};
    }

    static {
        field_36456 = RegistryEntry.Reference.Type.method_40238();
    }
}

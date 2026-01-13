/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.entity;

public static final class CommandBlockBlockEntity.Type
extends Enum<CommandBlockBlockEntity.Type> {
    public static final /* enum */ CommandBlockBlockEntity.Type SEQUENCE = new CommandBlockBlockEntity.Type();
    public static final /* enum */ CommandBlockBlockEntity.Type AUTO = new CommandBlockBlockEntity.Type();
    public static final /* enum */ CommandBlockBlockEntity.Type REDSTONE = new CommandBlockBlockEntity.Type();
    private static final /* synthetic */ CommandBlockBlockEntity.Type[] field_11925;

    public static CommandBlockBlockEntity.Type[] values() {
        return (CommandBlockBlockEntity.Type[])field_11925.clone();
    }

    public static CommandBlockBlockEntity.Type valueOf(String string) {
        return Enum.valueOf(CommandBlockBlockEntity.Type.class, string);
    }

    private static /* synthetic */ CommandBlockBlockEntity.Type[] method_36715() {
        return new CommandBlockBlockEntity.Type[]{SEQUENCE, AUTO, REDSTONE};
    }

    static {
        field_11925 = CommandBlockBlockEntity.Type.method_36715();
    }
}

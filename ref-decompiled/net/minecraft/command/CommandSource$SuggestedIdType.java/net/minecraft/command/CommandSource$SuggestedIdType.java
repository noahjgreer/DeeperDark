/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.command;

public static final class CommandSource.SuggestedIdType
extends Enum<CommandSource.SuggestedIdType> {
    public static final /* enum */ CommandSource.SuggestedIdType TAGS = new CommandSource.SuggestedIdType();
    public static final /* enum */ CommandSource.SuggestedIdType ELEMENTS = new CommandSource.SuggestedIdType();
    public static final /* enum */ CommandSource.SuggestedIdType ALL = new CommandSource.SuggestedIdType();
    private static final /* synthetic */ CommandSource.SuggestedIdType[] field_37265;

    public static CommandSource.SuggestedIdType[] values() {
        return (CommandSource.SuggestedIdType[])field_37265.clone();
    }

    public static CommandSource.SuggestedIdType valueOf(String string) {
        return Enum.valueOf(CommandSource.SuggestedIdType.class, string);
    }

    public boolean canSuggestTags() {
        return this == TAGS || this == ALL;
    }

    public boolean canSuggestElements() {
        return this == ELEMENTS || this == ALL;
    }

    private static /* synthetic */ CommandSource.SuggestedIdType[] method_41217() {
        return new CommandSource.SuggestedIdType[]{TAGS, ELEMENTS, ALL};
    }

    static {
        field_37265 = CommandSource.SuggestedIdType.method_41217();
    }
}

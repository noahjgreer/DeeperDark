/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.command;

public static final class CommandManager.RegistrationEnvironment
extends Enum<CommandManager.RegistrationEnvironment> {
    public static final /* enum */ CommandManager.RegistrationEnvironment ALL = new CommandManager.RegistrationEnvironment(true, true);
    public static final /* enum */ CommandManager.RegistrationEnvironment DEDICATED = new CommandManager.RegistrationEnvironment(false, true);
    public static final /* enum */ CommandManager.RegistrationEnvironment INTEGRATED = new CommandManager.RegistrationEnvironment(true, false);
    public final boolean integrated;
    public final boolean dedicated;
    private static final /* synthetic */ CommandManager.RegistrationEnvironment[] field_25424;

    public static CommandManager.RegistrationEnvironment[] values() {
        return (CommandManager.RegistrationEnvironment[])field_25424.clone();
    }

    public static CommandManager.RegistrationEnvironment valueOf(String string) {
        return Enum.valueOf(CommandManager.RegistrationEnvironment.class, string);
    }

    private CommandManager.RegistrationEnvironment(boolean integrated, boolean dedicated) {
        this.integrated = integrated;
        this.dedicated = dedicated;
    }

    private static /* synthetic */ CommandManager.RegistrationEnvironment[] method_36791() {
        return new CommandManager.RegistrationEnvironment[]{ALL, DEDICATED, INTEGRATED};
    }

    static {
        field_25424 = CommandManager.RegistrationEnvironment.method_36791();
    }
}

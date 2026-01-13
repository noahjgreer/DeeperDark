/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.command.permission.PermissionPredicate;
import net.minecraft.server.SaveLoading;
import net.minecraft.server.command.CommandManager;

public static final class SaveLoading.ServerConfig
extends Record {
    final SaveLoading.DataPacks dataPacks;
    private final CommandManager.RegistrationEnvironment commandEnvironment;
    private final PermissionPredicate functionCompilationPermissions;

    public SaveLoading.ServerConfig(SaveLoading.DataPacks dataPacks, CommandManager.RegistrationEnvironment commandEnvironment, PermissionPredicate functionCompilationPermissions) {
        this.dataPacks = dataPacks;
        this.commandEnvironment = commandEnvironment;
        this.functionCompilationPermissions = functionCompilationPermissions;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{SaveLoading.ServerConfig.class, "packConfig;commandSelection;functionCompilationPermissions", "dataPacks", "commandEnvironment", "functionCompilationPermissions"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{SaveLoading.ServerConfig.class, "packConfig;commandSelection;functionCompilationPermissions", "dataPacks", "commandEnvironment", "functionCompilationPermissions"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{SaveLoading.ServerConfig.class, "packConfig;commandSelection;functionCompilationPermissions", "dataPacks", "commandEnvironment", "functionCompilationPermissions"}, this, object);
    }

    public SaveLoading.DataPacks dataPacks() {
        return this.dataPacks;
    }

    public CommandManager.RegistrationEnvironment commandEnvironment() {
        return this.commandEnvironment;
    }

    public PermissionPredicate functionCompilationPermissions() {
        return this.functionCompilationPermissions;
    }
}

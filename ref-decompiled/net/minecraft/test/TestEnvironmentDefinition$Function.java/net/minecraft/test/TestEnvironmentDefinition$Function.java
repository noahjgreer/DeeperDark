/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  org.slf4j.Logger
 */
package net.minecraft.test;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.command.permission.LeveledPermissionPredicate;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.server.function.CommandFunctionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.TestEnvironmentDefinition;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

public record TestEnvironmentDefinition.Function(Optional<Identifier> setupFunction, Optional<Identifier> teardownFunction) implements TestEnvironmentDefinition
{
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final MapCodec<TestEnvironmentDefinition.Function> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Identifier.CODEC.optionalFieldOf("setup").forGetter(TestEnvironmentDefinition.Function::setupFunction), (App)Identifier.CODEC.optionalFieldOf("teardown").forGetter(TestEnvironmentDefinition.Function::teardownFunction)).apply((Applicative)instance, TestEnvironmentDefinition.Function::new));

    @Override
    public void setup(ServerWorld world) {
        this.setupFunction.ifPresent(functionId -> TestEnvironmentDefinition.Function.executeFunction(world, functionId));
    }

    @Override
    public void teardown(ServerWorld world) {
        this.teardownFunction.ifPresent(functionId -> TestEnvironmentDefinition.Function.executeFunction(world, functionId));
    }

    private static void executeFunction(ServerWorld world, Identifier functionId) {
        MinecraftServer minecraftServer = world.getServer();
        CommandFunctionManager commandFunctionManager = minecraftServer.getCommandFunctionManager();
        Optional<CommandFunction<ServerCommandSource>> optional = commandFunctionManager.getFunction(functionId);
        if (optional.isPresent()) {
            ServerCommandSource serverCommandSource = minecraftServer.getCommandSource().withPermissions(LeveledPermissionPredicate.GAMEMASTERS).withSilent().withWorld(world);
            commandFunctionManager.execute(optional.get(), serverCommandSource);
        } else {
            LOGGER.error("Test Batch failed for non-existent function {}", (Object)functionId);
        }
    }

    public MapCodec<TestEnvironmentDefinition.Function> getCodec() {
        return CODEC;
    }
}

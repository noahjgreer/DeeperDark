package net.noahsarch.deeperdark.creature;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.phys.Vec3;
import net.noahsarch.deeperdark.DeeperDarkConfig;

import java.util.concurrent.CompletableFuture;

/**
 * Command registration for the "dd creature" command tree.
 * All creature-related commands are registered as subcommands under "dd creature".
 * 
 * Commands:
 *   dd creature config <setting> [value]
 *   dd creature clear
 *   dd creature list
 *   dd creature summon <x> <y> <z>
 *   dd creature spawn <player> [maxDist]
 */
public class CreatureCommands {

    /**
     * Registers creature commands as a sub-tree to be added to the "dd" command.
     * Called from DeeperDarkCommands during command registration.
     */
    public static com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> buildCreatureCommand() {
        return Commands.literal("creature")
                // dd creature clear
                .then(Commands.literal("clear")
                        .executes(CreatureCommands::executeClear))

                // dd creature list
                .then(Commands.literal("list")
                        .executes(CreatureCommands::executeList))

                // dd creature summon <x> <y> <z>
                .then(Commands.literal("summon")
                        .then(Commands.argument("x", DoubleArgumentType.doubleArg())
                                .then(Commands.argument("y", DoubleArgumentType.doubleArg())
                                        .then(Commands.argument("z", DoubleArgumentType.doubleArg())
                                                .executes(CreatureCommands::executeSummon)))))

                // dd creature spawn <player> [maxDist]
                .then(Commands.literal("spawn")
                        .then(Commands.argument("player", StringArgumentType.word())
                                .suggests(CreatureCommands::suggestPlayers)
                                .executes(CreatureCommands::executeSpawn)
                                .then(Commands.argument("maxDist", IntegerArgumentType.integer(10, 500))
                                        .executes(CreatureCommands::executeSpawnWithDist))))

                // dd creature config ...
                .then(buildConfigSubcommand());
    }

    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> buildConfigSubcommand() {
        return Commands.literal("config")
                // Integer configs
                .then(configInt("pathfinding_min_dist", 1, 1000))
                .then(configInt("pathfinding_max_dist", 1, 1000))
                .then(configInt("pathfinding_max_y", -64, 320))
                .then(configInt("validity_frequency", 1, 100000))
                .then(configInt("entity_spacing", 1, 500))
                .then(configInt("trail_separation", 1, 50))
                .then(configInt("player_distance_tolerance", 1, 50))
                .then(configInt("chase_path_min_dist", 1, 500))
                .then(configInt("chase_path_max_dist", 1, 500))
                .then(configInt("evasion_timer", 1, 100000))
                .then(configInt("projectile_rejection_delay", 0, 200))
                .then(configInt("despawn_delay", 1, 1000000))
                .then(configInt("on_screen_tolerance", 1, 1000))
                .then(configInt("debug_path_duration", 1, 60))

                // Double configs
                .then(configDouble("validity_roll", 0.0, 1.0))
                .then(configDouble("trail_reach", 0.0, 1.0))
                .then(configDouble("jitter_max", 0.0, 10.0))
                .then(configDouble("chase_frequency", 0.0, 1.0))
                .then(configDouble("movement_speed", 0.1, 50.0))
                .then(configDouble("death_frequency", 0.0, 1.0))
                .then(configDouble("echo_chance", 0.0, 1.0))
                .then(configDouble("echo_trigger_radius", 0.1, 20.0))
                .then(configDouble("torch_removal_chance", 0.0, 1.0))
                .then(configDouble("projectile_rejection_chance", 0.0, 1.0))

                // Boolean configs
                .then(configBool("enable_debug_logging", c -> c.enableDebugLogging, (c, v) -> c.enableDebugLogging = v))
                .then(configBool("enable_debug_glow", c -> c.enableDebugGlow, (c, v) -> c.enableDebugGlow = v))
                .then(configBool("enable_debug_path", c -> c.enableDebugPath, (c, v) -> c.enableDebugPath = v));
    }

    // ===== Config Helper Builders =====

    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> configInt(String name, int min, int max) {
        return Commands.literal(name)
                .executes(ctx -> executeConfigQueryInt(ctx, name))
                .then(Commands.argument("value", IntegerArgumentType.integer(min, max))
                        .executes(ctx -> executeConfigSetInt(ctx, name)));
    }

    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> configDouble(String name, double min, double max) {
        return Commands.literal(name)
                .executes(ctx -> executeConfigQueryDouble(ctx, name))
                .then(Commands.argument("value", DoubleArgumentType.doubleArg(min, max))
                        .executes(ctx -> executeConfigSetDouble(ctx, name)));
    }

    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> configBool(
            String name,
            java.util.function.Function<CreatureConfig, Boolean> getter,
            java.util.function.BiConsumer<CreatureConfig, Boolean> setter) {
        return Commands.literal(name)
                .executes(ctx -> {
                    boolean current = getter.apply(DeeperDarkConfig.get().creature);
                    ctx.getSource().sendSuccess(() -> Component.literal(name + ": ")
                            .append(Component.literal(String.valueOf(current)).withStyle(current ? ChatFormatting.GREEN : ChatFormatting.RED)), false);
                    return 1;
                })
                .then(Commands.argument("value", BoolArgumentType.bool())
                        .executes(ctx -> {
                            boolean value = BoolArgumentType.getBool(ctx, "value");
                            setter.accept(DeeperDarkConfig.get().creature, value);
                            DeeperDarkConfig.save();
                            ctx.getSource().sendSuccess(() -> Component.literal("Set ")
                                    .append(Component.literal(name).withStyle(ChatFormatting.AQUA))
                                    .append(Component.literal(" to "))
                                    .append(Component.literal(String.valueOf(value)).withStyle(value ? ChatFormatting.GREEN : ChatFormatting.RED)), true);
                            return 1;
                        }));
    }

    // ===== Command Executors =====

    private static int executeClear(CommandContext<CommandSourceStack> ctx) {
        int count = CreatureManager.clearAllCreatures();
        ctx.getSource().sendSuccess(() -> Component.literal("Cleared ")
                .append(Component.literal(String.valueOf(count)).withStyle(ChatFormatting.GOLD))
                .append(Component.literal(" creature(s) from the world")), true);
        return count;
    }

    private static int executeList(CommandContext<CommandSourceStack> ctx) {
        var creatures = CreatureManager.getAllCreatures();
        if (creatures.isEmpty()) {
            ctx.getSource().sendSuccess(() -> Component.literal("No creatures currently active").withStyle(ChatFormatting.GRAY), false);
            return 0;
        }

        ctx.getSource().sendSuccess(() -> Component.literal("===== Active Creatures (" + creatures.size() + ") =====").withStyle(ChatFormatting.GOLD), false);
        for (CreatureInstance creature : creatures) {
            String line = creature.getStatusLine();
            ctx.getSource().sendSuccess(() -> Component.literal(line), false);
        }
        return creatures.size();
    }

    private static int executeSummon(CommandContext<CommandSourceStack> ctx) {
        double x = DoubleArgumentType.getDouble(ctx, "x");
        double y = DoubleArgumentType.getDouble(ctx, "y");
        double z = DoubleArgumentType.getDouble(ctx, "z");
        Vec3 pos = new Vec3(x, y, z);

        ServerLevel world;
        try {
            world = ctx.getSource().getWorld();
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal("Could not determine world").withStyle(ChatFormatting.RED));
            return 0;
        }

        CreatureInstance creature = CreatureManager.spawnCreatureAt(world, pos);
        ctx.getSource().sendSuccess(() -> Component.literal("Summoned creature ")
                .append(Component.literal(creature.getCreatureId().toString().substring(0, 8)).withStyle(ChatFormatting.YELLOW))
                .append(Component.literal(" at "))
                .append(Component.literal(String.format("(%.1f, %.1f, %.1f)", x, y, z)).withStyle(ChatFormatting.AQUA)), true);
        return 1;
    }

    private static int executeSpawn(CommandContext<CommandSourceStack> ctx) {
        return executeSpawnInternal(ctx, -1);
    }

    private static int executeSpawnWithDist(CommandContext<CommandSourceStack> ctx) {
        int maxDist = IntegerArgumentType.getInteger(ctx, "maxDist");
        return executeSpawnInternal(ctx, maxDist);
    }

    private static int executeSpawnInternal(CommandContext<CommandSourceStack> ctx, int maxDist) {
        String playerName = StringArgumentType.getString(ctx, "player");
        ServerPlayer target = ctx.getSource().getServer().getPlayerManager().getPlayer(playerName);

        if (target == null) {
            ctx.getSource().sendFailure(Component.literal("Player not found: " + playerName).withStyle(ChatFormatting.RED));
            return 0;
        }

        CreatureInstance creature = CreatureManager.spawnCreatureForPlayer(target, maxDist);
        if (creature == null) {
            ctx.getSource().sendFailure(Component.literal("Failed to find valid placement for creature near " + playerName).withStyle(ChatFormatting.RED));
            return 0;
        }

        ctx.getSource().sendSuccess(() -> Component.literal("Spawned creature ")
                .append(Component.literal(creature.getCreatureId().toString().substring(0, 8)).withStyle(ChatFormatting.YELLOW))
                .append(Component.literal(" targeting "))
                .append(Component.literal(playerName).withStyle(ChatFormatting.GREEN))
                .append(Component.literal(" at "))
                .append(Component.literal(String.format("(%.1f, %.1f, %.1f)", creature.getPosition().x, creature.getPosition().y, creature.getPosition().z)).withStyle(ChatFormatting.AQUA)), true);
        return 1;
    }

    // ===== Config Query/Set =====

    private static int executeConfigQueryInt(CommandContext<CommandSourceStack> ctx, String name) {
        CreatureConfig config = DeeperDarkConfig.get().creature;
        int value = getIntConfig(config, name);
        ctx.getSource().sendSuccess(() -> Component.literal(name + ": ")
                .append(Component.literal(String.valueOf(value)).withStyle(ChatFormatting.AQUA)), false);
        return 1;
    }

    private static int executeConfigSetInt(CommandContext<CommandSourceStack> ctx, String name) {
        int value = IntegerArgumentType.getInteger(ctx, "value");
        CreatureConfig config = DeeperDarkConfig.get().creature;
        setIntConfig(config, name, value);
        DeeperDarkConfig.save();
        ctx.getSource().sendSuccess(() -> Component.literal("Set ")
                .append(Component.literal(name).withStyle(ChatFormatting.AQUA))
                .append(Component.literal(" to "))
                .append(Component.literal(String.valueOf(value)).withStyle(ChatFormatting.GOLD)), true);
        return 1;
    }

    private static int executeConfigQueryDouble(CommandContext<CommandSourceStack> ctx, String name) {
        CreatureConfig config = DeeperDarkConfig.get().creature;
        double value = getDoubleConfig(config, name);
        ctx.getSource().sendSuccess(() -> Component.literal(name + ": ")
                .append(Component.literal(String.valueOf(value)).withStyle(ChatFormatting.AQUA)), false);
        return 1;
    }

    private static int executeConfigSetDouble(CommandContext<CommandSourceStack> ctx, String name) {
        double value = DoubleArgumentType.getDouble(ctx, "value");
        CreatureConfig config = DeeperDarkConfig.get().creature;
        setDoubleConfig(config, name, value);
        DeeperDarkConfig.save();
        ctx.getSource().sendSuccess(() -> Component.literal("Set ")
                .append(Component.literal(name).withStyle(ChatFormatting.AQUA))
                .append(Component.literal(" to "))
                .append(Component.literal(String.valueOf(value)).withStyle(ChatFormatting.GOLD)), true);
        return 1;
    }

    // ===== Config Reflective Getters/Setters =====

    private static int getIntConfig(CreatureConfig config, String name) {
        return switch (name) {
            case "pathfinding_min_dist" -> config.pathfindingMinDist;
            case "pathfinding_max_dist" -> config.pathfindingMaxDist;
            case "pathfinding_max_y" -> config.pathfindingMaxY;
            case "validity_frequency" -> config.validityFrequency;
            case "entity_spacing" -> config.entitySpacing;
            case "trail_separation" -> config.trailSeparation;
            case "player_distance_tolerance" -> config.playerDistanceTolerance;
            case "chase_path_min_dist" -> config.chasePathMinDist;
            case "chase_path_max_dist" -> config.chasePathMaxDist;
            case "evasion_timer" -> config.evasionTimer;
            case "projectile_rejection_delay" -> config.projectileRejectionDelay;
            case "despawn_delay" -> config.despawnDelay;
            case "on_screen_tolerance" -> config.onScreenTolerance;
            case "debug_path_duration" -> config.debugPathDuration;
            default -> 0;
        };
    }

    private static void setIntConfig(CreatureConfig config, String name, int value) {
        switch (name) {
            case "pathfinding_min_dist" -> config.pathfindingMinDist = value;
            case "pathfinding_max_dist" -> config.pathfindingMaxDist = value;
            case "pathfinding_max_y" -> config.pathfindingMaxY = value;
            case "validity_frequency" -> config.validityFrequency = value;
            case "entity_spacing" -> config.entitySpacing = value;
            case "trail_separation" -> config.trailSeparation = value;
            case "player_distance_tolerance" -> config.playerDistanceTolerance = value;
            case "chase_path_min_dist" -> config.chasePathMinDist = value;
            case "chase_path_max_dist" -> config.chasePathMaxDist = value;
            case "evasion_timer" -> config.evasionTimer = value;
            case "projectile_rejection_delay" -> config.projectileRejectionDelay = value;
            case "despawn_delay" -> config.despawnDelay = value;
            case "on_screen_tolerance" -> config.onScreenTolerance = value;
            case "debug_path_duration" -> config.debugPathDuration = value;
        }
    }

    private static double getDoubleConfig(CreatureConfig config, String name) {
        return switch (name) {
            case "validity_roll" -> config.validityRoll;
            case "trail_reach" -> config.trailReach;
            case "jitter_max" -> config.jitterMax;
            case "chase_frequency" -> config.chaseFrequency;
            case "movement_speed" -> config.movementSpeed;
            case "death_frequency" -> config.deathFrequency;
            case "echo_chance" -> config.echoChance;
            case "echo_trigger_radius" -> config.echoTriggerRadius;
            case "torch_removal_chance" -> config.torchRemovalChance;
            case "projectile_rejection_chance" -> config.projectileRejectionChance;
            default -> 0.0;
        };
    }

    private static void setDoubleConfig(CreatureConfig config, String name, double value) {
        switch (name) {
            case "validity_roll" -> config.validityRoll = value;
            case "trail_reach" -> config.trailReach = value;
            case "jitter_max" -> config.jitterMax = value;
            case "chase_frequency" -> config.chaseFrequency = value;
            case "movement_speed" -> config.movementSpeed = value;
            case "death_frequency" -> config.deathFrequency = value;
            case "echo_chance" -> config.echoChance = value;
            case "echo_trigger_radius" -> config.echoTriggerRadius = value;
            case "torch_removal_chance" -> config.torchRemovalChance = value;
            case "projectile_rejection_chance" -> config.projectileRejectionChance = value;
        }
    }

    // ===== Suggestions =====

    private static CompletableFuture<Suggestions> suggestPlayers(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(
                ctx.getSource().getServer().getPlayerManager().getPlayerList().stream()
                        .map(p -> p.getName().getString()),
                builder
        );
    }
}

package net.noahsarch.deeperdark.creepiness;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.noahsarch.deeperdark.DeeperDarkConfig;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * "/dd creepiness": shows the caller's haunt timer, edits the creepiness config and triggers
 * a haunt on demand for testing.
 */
public final class CreepinessCommands {

    private CreepinessCommands() {}

    public static LiteralArgumentBuilder<CommandSourceStack> buildCreepinessCommand() {
        LiteralArgumentBuilder<CommandSourceStack> config = Commands.literal("config")
            .then(configBool("enabled", c -> c.enabled, (c, v) -> c.enabled = v))
            .then(configInt("min_delay_ticks", c -> c.minDelayTicks, (c, v) -> c.minDelayTicks = v))
            .then(configInt("max_delay_ticks", c -> c.maxDelayTicks, (c, v) -> c.maxDelayTicks = v));
        LiteralArgumentBuilder<CommandSourceStack> trigger = Commands.literal("trigger");
        for (Haunt haunt : Haunt.values()) {
            config.then(configBool(haunt.key, haunt.enabled::test, haunt.setEnabled));
            trigger.then(Commands.literal(haunt.key)
                .executes(ctx -> executeTrigger(ctx.getSource(), ctx.getSource().getPlayerOrException(), haunt))
                .then(Commands.argument("player", EntityArgument.player())
                    .executes(ctx -> executeTrigger(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"), haunt))));
        }

        return Commands.literal("creepiness")
            .executes(ctx -> executeStatus(ctx.getSource()))
            .then(config)
            .then(trigger);
    }

    private static int executeStatus(CommandSourceStack source) throws CommandSyntaxException {
        CreepinessConfig config = DeeperDarkConfig.get().creepiness;
        int ticks = CreepinessManager.ticksUntilHaunt(source.getPlayerOrException());
        source.sendSuccess(() -> Component.literal("Creepiness: ")
            .append(Component.literal(config.enabled ? "enabled" : "disabled").withStyle(config.enabled ? ChatFormatting.GREEN : ChatFormatting.RED))
            .append(Component.literal(", your next haunt in "))
            .append(Component.literal(ticks + " ticks").withStyle(ChatFormatting.AQUA)), false);
        return 1;
    }

    private static int executeTrigger(CommandSourceStack source, ServerPlayer player, Haunt haunt) {
        if (!CreepinessManager.trigger(player, haunt)) {
            source.sendFailure(Component.literal("Nowhere suitable for " + haunt.key + " near " + player.getName().getString()).withStyle(ChatFormatting.RED));
            return 0;
        }
        source.sendSuccess(() -> Component.literal("Played ")
            .append(Component.literal(haunt.key).withStyle(ChatFormatting.AQUA))
            .append(Component.literal(" for "))
            .append(Component.literal(player.getName().getString()).withStyle(ChatFormatting.YELLOW)), false);
        return 1;
    }

    private static LiteralArgumentBuilder<CommandSourceStack> configBool(
            String name, Function<CreepinessConfig, Boolean> getter, BiConsumer<CreepinessConfig, Boolean> setter) {
        return Commands.literal(name)
            .executes(ctx -> {
                boolean current = getter.apply(DeeperDarkConfig.get().creepiness);
                ctx.getSource().sendSuccess(() -> Component.literal(name + ": ")
                    .append(Component.literal(String.valueOf(current)).withStyle(current ? ChatFormatting.GREEN : ChatFormatting.RED)), false);
                return 1;
            })
            .then(Commands.argument("value", BoolArgumentType.bool())
                .executes(ctx -> {
                    boolean value = BoolArgumentType.getBool(ctx, "value");
                    setter.accept(DeeperDarkConfig.get().creepiness, value);
                    DeeperDarkConfig.save();
                    ctx.getSource().sendSuccess(() -> Component.literal("Set ")
                        .append(Component.literal(name).withStyle(ChatFormatting.AQUA))
                        .append(Component.literal(" to "))
                        .append(Component.literal(String.valueOf(value)).withStyle(value ? ChatFormatting.GREEN : ChatFormatting.RED)), true);
                    return 1;
                }));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> configInt(
            String name, Function<CreepinessConfig, Integer> getter, BiConsumer<CreepinessConfig, Integer> setter) {
        return Commands.literal(name)
            .executes(ctx -> {
                int current = getter.apply(DeeperDarkConfig.get().creepiness);
                ctx.getSource().sendSuccess(() -> Component.literal(name + ": ")
                    .append(Component.literal(String.valueOf(current)).withStyle(ChatFormatting.AQUA)), false);
                return 1;
            })
            .then(Commands.argument("value", IntegerArgumentType.integer(20))
                .executes(ctx -> {
                    int value = IntegerArgumentType.getInteger(ctx, "value");
                    setter.accept(DeeperDarkConfig.get().creepiness, value);
                    DeeperDarkConfig.save();
                    ctx.getSource().sendSuccess(() -> Component.literal("Set ")
                        .append(Component.literal(name).withStyle(ChatFormatting.AQUA))
                        .append(Component.literal(" to "))
                        .append(Component.literal(String.valueOf(value)).withStyle(ChatFormatting.GOLD)), true);
                    return 1;
                }));
    }
}

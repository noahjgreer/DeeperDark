package io.github.lucaargolo.seasons.commands;

import com.mojang.brigadier.CommandDispatcher;
import io.github.lucaargolo.seasons.FabricSeasons;
import io.github.lucaargolo.seasons.payload.SeasonTimeSyncPacket;
import io.github.lucaargolo.seasons.utils.Season;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.clock.ServerClockManager;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.clock.WorldClocks;
import net.minecraft.world.level.Level;

public class SeasonCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("season")
            .then(Commands.literal("set").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.literal("spring")
                    .executes(context -> executeSet(context.getSource(),
                        switch (FabricSeasons.CONFIG.getStartingSeason()) {
                            case SPRING -> 0;
                            case WINTER -> (int) FabricSeasons.CONFIG.getWinterLength();
                            case FALL -> (int) (FabricSeasons.CONFIG.getFallLength() + FabricSeasons.CONFIG.getWinterLength());
                            case SUMMER -> (int) (FabricSeasons.CONFIG.getSummerLength() + FabricSeasons.CONFIG.getFallLength() + FabricSeasons.CONFIG.getWinterLength());
                        }
                    ))
                )
                .then(Commands.literal("summer")
                    .executes(context -> executeSet(context.getSource(),
                        switch (FabricSeasons.CONFIG.getStartingSeason()) {
                            case SUMMER -> 0;
                            case SPRING -> (int) FabricSeasons.CONFIG.getSpringLength();
                            case WINTER -> (int) (FabricSeasons.CONFIG.getWinterLength() + FabricSeasons.CONFIG.getSpringLength());
                            case FALL -> (int) (FabricSeasons.CONFIG.getFallLength() + FabricSeasons.CONFIG.getWinterLength() + FabricSeasons.CONFIG.getSpringLength());
                        }
                    ))
                )
                .then(Commands.literal("fall")
                    .executes(context -> executeSet(context.getSource(),
                        switch (FabricSeasons.CONFIG.getStartingSeason()) {
                            case FALL -> 0;
                            case SUMMER -> (int) FabricSeasons.CONFIG.getSummerLength();
                            case SPRING -> (int) (FabricSeasons.CONFIG.getSpringLength() + FabricSeasons.CONFIG.getSummerLength());
                            case WINTER -> (int) (FabricSeasons.CONFIG.getWinterLength() + FabricSeasons.CONFIG.getSpringLength() + FabricSeasons.CONFIG.getSummerLength());
                        }
                    ))
                )
                .then(Commands.literal("winter")
                    .executes(context -> executeSet(context.getSource(),
                        switch (FabricSeasons.CONFIG.getStartingSeason()) {
                            case WINTER -> 0;
                            case FALL -> (int) FabricSeasons.CONFIG.getFallLength();
                            case SUMMER -> (int) (FabricSeasons.CONFIG.getSummerLength() + FabricSeasons.CONFIG.getFallLength());
                            case SPRING -> (int) (FabricSeasons.CONFIG.getSpringLength() + FabricSeasons.CONFIG.getSummerLength() + FabricSeasons.CONFIG.getFallLength());
                        }
                    ))
                )
            )
            .then(Commands.literal("query")
                .executes(context -> {
                    Level world = context.getSource().getLevel();
                    Season currentSeason = FabricSeasons.getCurrentSeason(world);
                    Season nextSeason = FabricSeasons.getNextSeason(world, currentSeason);
                    long ticksLeft = FabricSeasons.getTimeToNextSeason(world);
                    context.getSource().sendSuccess(() -> Component.translatable("commands.seasons.query_1",
                        Component.translatable(currentSeason.getTranslationKey()).withStyle(currentSeason.getFormatting())
                    ), false);
                    context.getSource().sendSuccess(() -> Component.translatable("commands.seasons.query_2",
                        Long.toString(ticksLeft / 24000L),
                        Long.toString(ticksLeft),
                        Component.translatable(nextSeason.getTranslationKey()).withStyle(nextSeason.getFormatting())
                    ), false);
                    return currentSeason.ordinal();
                })
            )
            .then(Commands.literal("skip").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(context -> executeLongAdd(context.getSource(), FabricSeasons.getTimeToNextSeason(context.getSource().getLevel())))
                .then(Commands.literal("spring")
                    .executes(context -> {
                        Level world = context.getSource().getLevel();
                        Season season = FabricSeasons.getCurrentSeason(world);
                        return switch (season) {
                            case SPRING -> executeLongAdd(context.getSource(), FabricSeasons.getTimeToNextSeason(world) + Season.SUMMER.getSeasonLength() + Season.FALL.getSeasonLength() + Season.WINTER.getSeasonLength());
                            case SUMMER -> executeLongAdd(context.getSource(), FabricSeasons.getTimeToNextSeason(world) + Season.FALL.getSeasonLength() + Season.WINTER.getSeasonLength());
                            case FALL -> executeLongAdd(context.getSource(), FabricSeasons.getTimeToNextSeason(world) + Season.WINTER.getSeasonLength());
                            case WINTER -> executeLongAdd(context.getSource(), FabricSeasons.getTimeToNextSeason(world));
                        };
                    })
                )
                .then(Commands.literal("summer")
                    .executes(context -> {
                        Level world = context.getSource().getLevel();
                        Season season = FabricSeasons.getCurrentSeason(world);
                        return switch (season) {
                            case SPRING -> executeLongAdd(context.getSource(), FabricSeasons.getTimeToNextSeason(world));
                            case SUMMER -> executeLongAdd(context.getSource(), FabricSeasons.getTimeToNextSeason(world) + Season.FALL.getSeasonLength() + Season.WINTER.getSeasonLength() + Season.SPRING.getSeasonLength());
                            case FALL -> executeLongAdd(context.getSource(), FabricSeasons.getTimeToNextSeason(world) + Season.WINTER.getSeasonLength() + Season.SPRING.getSeasonLength());
                            case WINTER -> executeLongAdd(context.getSource(), FabricSeasons.getTimeToNextSeason(world) + Season.SPRING.getSeasonLength());
                        };
                    })
                )
                .then(Commands.literal("fall")
                    .executes(context -> {
                        Level world = context.getSource().getLevel();
                        Season season = FabricSeasons.getCurrentSeason(world);
                        return switch (season) {
                            case SPRING -> executeLongAdd(context.getSource(), FabricSeasons.getTimeToNextSeason(world) + Season.SUMMER.getSeasonLength());
                            case SUMMER -> executeLongAdd(context.getSource(), FabricSeasons.getTimeToNextSeason(world));
                            case FALL -> executeLongAdd(context.getSource(), FabricSeasons.getTimeToNextSeason(world) + Season.WINTER.getSeasonLength() + Season.SPRING.getSeasonLength() + Season.SUMMER.getSeasonLength());
                            case WINTER -> executeLongAdd(context.getSource(), FabricSeasons.getTimeToNextSeason(world) + Season.SPRING.getSeasonLength() + Season.SUMMER.getSeasonLength());
                        };
                    })
                )
                .then(Commands.literal("winter")
                    .executes(context -> {
                        Level world = context.getSource().getLevel();
                        Season season = FabricSeasons.getCurrentSeason(world);
                        return switch (season) {
                            case SPRING -> executeLongAdd(context.getSource(), FabricSeasons.getTimeToNextSeason(world) + Season.SUMMER.getSeasonLength() + Season.FALL.getSeasonLength());
                            case SUMMER -> executeLongAdd(context.getSource(), FabricSeasons.getTimeToNextSeason(world) + Season.FALL.getSeasonLength());
                            case FALL -> executeLongAdd(context.getSource(), FabricSeasons.getTimeToNextSeason(world));
                            case WINTER -> executeLongAdd(context.getSource(), FabricSeasons.getTimeToNextSeason(world) + Season.SPRING.getSeasonLength() + Season.SUMMER.getSeasonLength() + Season.FALL.getSeasonLength());
                        };
                    })
                )
            )
        );
    }

    public static int executeSet(CommandSourceStack source, long time) {
        for (ServerLevel serverLevel : source.getServer().getAllLevels()) {
            Holder<WorldClock> clock = serverLevel.registryAccess()
                .lookupOrThrow(Registries.WORLD_CLOCK)
                .getOrThrow(WorldClocks.OVERWORLD);
            serverLevel.clockManager().setTotalTicks(clock, time);
        }
        Holder<WorldClock> clock = source.getLevel().registryAccess()
            .lookupOrThrow(Registries.WORLD_CLOCK)
            .getOrThrow(WorldClocks.OVERWORLD);
        int i = (int) (source.getLevel().clockManager().getTotalTicks(clock) % 24000L);
        source.sendSuccess(() -> Component.translatable("commands.time.set", i), true);
        broadcastTimeSync(source);
        return i;
    }

    public static int executeLongAdd(CommandSourceStack source, long time) {
        for (ServerLevel serverLevel : source.getServer().getAllLevels()) {
            Holder<WorldClock> clock = serverLevel.registryAccess()
                .lookupOrThrow(Registries.WORLD_CLOCK)
                .getOrThrow(WorldClocks.OVERWORLD);
            ServerClockManager cm = serverLevel.clockManager();
            cm.setTotalTicks(clock, cm.getTotalTicks(clock) + time);
        }
        Holder<WorldClock> clock = source.getLevel().registryAccess()
            .lookupOrThrow(Registries.WORLD_CLOCK)
            .getOrThrow(WorldClocks.OVERWORLD);
        int i = (int) (source.getLevel().clockManager().getTotalTicks(clock) % 24000L);
        source.sendSuccess(() -> Component.translatable("commands.time.set", i), true);
        broadcastTimeSync(source);
        return i;
    }

    private static void broadcastTimeSync(CommandSourceStack source) {
        ServerLevel overworld = source.getServer().getLevel(Level.OVERWORLD);
        if (overworld == null) return;
        SeasonTimeSyncPacket packet = new SeasonTimeSyncPacket(
                FabricSeasons.getOverworldTime(overworld), overworld.getGameTime());
        source.getServer().getPlayerList().getPlayers()
                .forEach(p -> ServerPlayNetworking.send(p, packet));
    }
}

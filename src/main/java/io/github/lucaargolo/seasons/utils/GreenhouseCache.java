package io.github.lucaargolo.seasons.utils;

import io.github.lucaargolo.seasons.FabricSeasons;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.util.*;

public class GreenhouseCache {

    private static final HashMap<ResourceKey<Level>, HashMap<ChunkPos, ArrayList<GreenHouseTicket>>> CACHE = new HashMap<>();

    public static final int EXPIRATION_TIME = 5;
    private static int AGE;

    public static void add(Level world, ChunkPos chunkPos, GreenHouseTicket ticket) {
        ResourceKey<Level> worldKey = world.dimension();
        HashMap<ChunkPos, ArrayList<GreenHouseTicket>> chunkTickets = CACHE.computeIfAbsent(worldKey, k -> new HashMap<>());
        chunkTickets.computeIfAbsent(chunkPos, p -> new ArrayList<>()).add(ticket);
    }

    public static Season test(Level world, BlockPos pos) {
        Season currentSeason = FabricSeasons.getCurrentSeason(world);
        HashSet<Season> seasons = new HashSet<>();
        ResourceKey<Level> worldKey = world.dimension();
        HashMap<ChunkPos, ArrayList<GreenHouseTicket>> chunkTickets = CACHE.get(worldKey);
        if (chunkTickets != null) {
            ArrayList<GreenHouseTicket> tickets = chunkTickets.get(new ChunkPos(pos.getX() >> 4, pos.getZ() >> 4));
            if (tickets != null) {
                Iterator<GreenHouseTicket> iterator = tickets.iterator();
                while (iterator.hasNext()) {
                    GreenHouseTicket ticket = iterator.next();
                    if (AGE > ticket.age + EXPIRATION_TIME) {
                        ticket.expired = true;
                        iterator.remove();
                    } else {
                        seasons.addAll(ticket.test(pos));
                    }
                }
            }
        }
        return seasons.stream().max(Comparator.comparingInt(Season::getTemperature)).orElse(currentSeason);
    }

    public static void tick(MinecraftServer server) {
        AGE++;
    }

    public static class GreenHouseTicket {

        private final BoundingBox box;
        public final Set<Season> seasons;
        public int age;
        public boolean expired;

        public GreenHouseTicket(BoundingBox box, Season... season) {
            this.box = box;
            this.seasons = new HashSet<>(List.of(season));
            this.age = AGE;
            this.expired = false;
        }

        public Set<Season> test(BlockPos pos) {
            if (box.isInside(pos)) {
                return this.seasons;
            } else {
                return Set.of();
            }
        }
    }
}

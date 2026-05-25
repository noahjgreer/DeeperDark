package net.noahsarch.deeperdark.event;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LightLayer;
import net.noahsarch.deeperdark.DeeperDarkConfig;

/**
 * Ports void particles (depthsuspend) from Minecraft 1.3.1.
 *
 * In 1.3.1, the World class spawned "depthsuspend" particles per nearby chunk
 * per world tick whenever the player was near the void floor. Particle density
 * increased with depth, tied to the same formula as the fog distance effect.
 *
 * Port: uses ParticleTypes.MYCELIUM (small dark floating specks) as the
 * visual stand-in. Count scales with the void-fog d value so particles become
 * denser the deeper you descend — matching 1.3.1's per-chunk accumulation.
 */
@Environment(EnvType.CLIENT)
public class VoidParticleHandler {

    // Horizontal spread across ~2 chunk radii around the player
    private static final float SPAWN_RADIUS = 16.0f;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(VoidParticleHandler::tick);
    }

    private static void tick(Minecraft client) {
        if (!DeeperDarkConfig.get().voidFogEnabled) return;
        ClientLevel level = client.level;
        LocalPlayer player = client.player;
        if (level == null || player == null) return;
        if (client.isPaused()) return;
        if (player.isCreative()) return;

        // Use the same d formula as VoidFogMixin so particles and fog share one threshold
        BlockPos blockPos = player.blockPosition();
        int skyLight = level.getLightEngine().getLayerListener(LightLayer.SKY).getLightValue(blockPos);
        double relativeY = player.getY() - level.getMinY();
        double d = skyLight / 16.0 + (relativeY + 4.0) / 32.0;

        if (d >= 1.0) return;
        if (d < 0.0) d = 0.0;

        // Scale count with depth: ~4 at the threshold, up to ~40 at the void floor
        int count = Math.max(4, (int) (40.0 * (1.0 - d)));

        RandomSource random = player.getRandom();
        for (int i = 0; i < count; i++) {
            double spawnX = player.getX() + (random.nextFloat() - 0.5f) * SPAWN_RADIUS * 2;
            // Spawn around the player's Y in open air — anchoring to minY put everything inside bedrock
            double spawnY = player.getY() + (random.nextFloat() - 0.5f) * 8.0;
            spawnY = Math.max(level.getMinY(), spawnY);
            double spawnZ = player.getZ() + (random.nextFloat() - 0.5f) * SPAWN_RADIUS * 2;
            level.addParticle(ParticleTypes.MYCELIUM, spawnX, spawnY, spawnZ, 0.0, 0.0, 0.0);
        }
    }
}

package net.noahsarch.deeperdark.creature;

import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

/**
 * Represents a single creature instance in the world, tracking all of its internal state.
 * The creature is rendered server-side as an ItemDisplayEntity billboard.
 */
public class CreatureInstance {

    /**
     * The possible sequences/states a creature can be in.
     */
    public enum Sequence {
        /** Waiting for a player to come into range */
        IDLE,
        /** Playing the ambient sound to a nearby player */
        AMBIENT_SOUND,
        /** Trail of copper nuggets being placed */
        COPPER_TRAIL,
        /** Preparation phase before chase (freeze + hush sound) */
        CHASE_PREP,
        /** Actively chasing the player */
        CHASING,
        /** Creature has been triggered (default disappear behavior) */
        DISAPPEARING,
        /** Echo trigger zone active after disappearance */
        ECHO_ZONE,
        /** Creature is pending despawn */
        DESPAWNING
    }

    // ===== Identity =====
    private final UUID creatureId;
    private int displayEntityId = -1;
    private UUID displayEntityUuid = null;

    // ===== Position =====
    private Vec3d position;
    private final ServerWorld world;
    private float yaw = 0f;

    // ===== Visual State =====
    private int textureVariant; // 0-3
    private double currentJitterX = 0;
    private double currentJitterZ = 0;

    // ===== Behavioral State =====
    private Sequence currentSequence = Sequence.IDLE;
    private int ticksAlive = 0;
    private int sequenceTicks = 0; // Ticks spent in current sequence

    // ===== Sound State =====
    private boolean ambienceSoundPlayed = false;
    private int ambienceSoundVariant = -1; // Which ambience sound was chosen (0-6)

    // ===== Trail State =====
    private boolean copperTrailSpawned = false;
    private int copperTrailDelayTicks = -1; // -1 = not triggered yet, 0+ = ticks since trigger
    private UUID copperTrailTriggeredByPlayer = null;

    // ===== Interaction Flags (pre-rolled on spawn) =====
    private boolean willChase = false;
    private boolean willRemoveTorches = false;
    private boolean willEcho = true;
    private boolean willRejectProjectiles = true;

    // ===== On-Screen Tracking =====
    private int onScreenTicks = 0;

    // ===== Chase State =====
    private UUID targetPlayerUuid = null;
    private String targetPlayerName = null;
    private Vec3d chaseTargetPos = null;
    private int chaseTicks = 0;
    private float pitchInfluenceAccumulated = 0;

    // ===== Echo Zone State =====
    private Vec3d echoPosition = null;
    private int echoTicksRemaining = 0;
    private boolean echoTriggered = false;

    // ===== Despawn =====
    private int despawnTimer;

    public CreatureInstance(UUID creatureId, Vec3d position, ServerWorld world, int textureVariant, int despawnDelay) {
        this.creatureId = creatureId;
        this.position = position;
        this.world = world;
        this.textureVariant = textureVariant;
        this.despawnTimer = despawnDelay;
    }

    // ===== Getters =====

    public UUID getCreatureId() { return creatureId; }
    public int getDisplayEntityId() { return displayEntityId; }
    public UUID getDisplayEntityUuid() { return displayEntityUuid; }
    public Vec3d getPosition() { return position; }
    public ServerWorld getWorld() { return world; }
    public float getYaw() { return yaw; }
    public int getTextureVariant() { return textureVariant; }
    public Sequence getCurrentSequence() { return currentSequence; }
    public int getTicksAlive() { return ticksAlive; }
    public int getSequenceTicks() { return sequenceTicks; }
    public boolean isAmbienceSoundPlayed() { return ambienceSoundPlayed; }
    public int getAmbienceSoundVariant() { return ambienceSoundVariant; }
    public boolean isCopperTrailSpawned() { return copperTrailSpawned; }
    public int getCopperTrailDelayTicks() { return copperTrailDelayTicks; }
    public UUID getCopperTrailTriggeredByPlayer() { return copperTrailTriggeredByPlayer; }
    public boolean willChase() { return willChase; }
    public boolean willRemoveTorches() { return willRemoveTorches; }
    public boolean willEcho() { return willEcho; }
    public boolean willRejectProjectiles() { return willRejectProjectiles; }
    public UUID getTargetPlayerUuid() { return targetPlayerUuid; }
    public String getTargetPlayerName() { return targetPlayerName; }
    public Vec3d getChaseTargetPos() { return chaseTargetPos; }
    public int getChaseTicks() { return chaseTicks; }
    public float getPitchInfluenceAccumulated() { return pitchInfluenceAccumulated; }
    public Vec3d getEchoPosition() { return echoPosition; }
    public int getEchoTicksRemaining() { return echoTicksRemaining; }
    public boolean isEchoTriggered() { return echoTriggered; }
    public int getDespawnTimer() { return despawnTimer; }
    public double getCurrentJitterX() { return currentJitterX; }
    public double getCurrentJitterZ() { return currentJitterZ; }
    public int getOnScreenTicks() { return onScreenTicks; }

    // ===== Setters =====

    public void setDisplayEntityId(int id) { this.displayEntityId = id; }
    public void setDisplayEntityUuid(UUID uuid) { this.displayEntityUuid = uuid; }
    public void setPosition(Vec3d position) { this.position = position; }
    public void setYaw(float yaw) { this.yaw = yaw; }
    public void setTextureVariant(int variant) { this.textureVariant = variant; }
    public void setCurrentSequence(Sequence sequence) {
        this.currentSequence = sequence;
        this.sequenceTicks = 0;
    }
    public void setAmbienceSoundPlayed(boolean played) { this.ambienceSoundPlayed = played; }
    public void setAmbienceSoundVariant(int variant) { this.ambienceSoundVariant = variant; }
    public void setCopperTrailSpawned(boolean spawned) { this.copperTrailSpawned = spawned; }
    public void setCopperTrailDelayTicks(int ticks) { this.copperTrailDelayTicks = ticks; }
    public void setCopperTrailTriggeredByPlayer(UUID uuid) { this.copperTrailTriggeredByPlayer = uuid; }
    public void incrementCopperTrailDelayTicks() { this.copperTrailDelayTicks++; }
    public void setWillChase(boolean willChase) { this.willChase = willChase; }
    public void setWillRemoveTorches(boolean willRemove) { this.willRemoveTorches = willRemove; }
    public void setWillEcho(boolean willEcho) { this.willEcho = willEcho; }
    public void setWillRejectProjectiles(boolean willReject) { this.willRejectProjectiles = willReject; }
    public void setTargetPlayer(ServerPlayerEntity player) {
        if (player != null) {
            this.targetPlayerUuid = player.getUuid();
            this.targetPlayerName = player.getName().getString();
        } else {
            this.targetPlayerUuid = null;
            this.targetPlayerName = null;
        }
    }
    public void setTargetPlayerUuid(UUID uuid) { this.targetPlayerUuid = uuid; }
    public void setTargetPlayerName(String name) { this.targetPlayerName = name; }
    public void setChaseTargetPos(Vec3d pos) { this.chaseTargetPos = pos; }
    public void setChaseTicks(int ticks) { this.chaseTicks = ticks; }
    public void setPitchInfluenceAccumulated(float pitch) { this.pitchInfluenceAccumulated = pitch; }
    public void setEchoPosition(Vec3d pos) { this.echoPosition = pos; }
    public void setEchoTicksRemaining(int ticks) { this.echoTicksRemaining = ticks; }
    public void setEchoTriggered(boolean triggered) { this.echoTriggered = triggered; }
    public void setDespawnTimer(int timer) { this.despawnTimer = timer; }
    public void setCurrentJitterX(double jitter) { this.currentJitterX = jitter; }
    public void setCurrentJitterZ(double jitter) { this.currentJitterZ = jitter; }
    public void setOnScreenTicks(int ticks) { this.onScreenTicks = ticks; }
    public void incrementOnScreenTicks() { this.onScreenTicks++; }

    // ===== Tick Helpers =====

    public void incrementTicksAlive() { this.ticksAlive++; }
    public void incrementSequenceTicks() { this.sequenceTicks++; }
    public void incrementChaseTicks() { this.chaseTicks++; }
    public void decrementDespawnTimer() { this.despawnTimer--; }
    public void decrementEchoTicksRemaining() { this.echoTicksRemaining--; }
    public void addPitchInfluence(float delta) { this.pitchInfluenceAccumulated += delta; }

    /**
     * Gets a human-readable status line for the /dd creature list command.
     */
    public String getStatusLine() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("§e%s§r", creatureId.toString().substring(0, 8)));
        sb.append(String.format(" §7[%.1f, %.1f, %.1f]§r", position.x, position.y, position.z));
        sb.append(String.format(" | Sound: %s", ambienceSoundPlayed ? "§aPlayed§r" : "§7Pending§r"));
        sb.append(String.format(" | Trail: %s", copperTrailSpawned ? "§aSpawned§r" : "§7Pending§r"));
        sb.append(String.format(" | Chase: %s", willChase ? "§cYes§r" : "§aNo§r"));
        sb.append(String.format(" | Torch: %s", willRemoveTorches ? "§cYes§r" : "§aNo§r"));
        sb.append(String.format(" | Echo: %s", willEcho ? "§aYes§r" : "§7No§r"));
        sb.append(String.format(" | Projectile: %s", willRejectProjectiles ? "§aYes§r" : "§7No§r"));
        sb.append(String.format(" | Despawn: §6%d§r", despawnTimer));
        sb.append(String.format(" | Seq: §b%s§r", currentSequence.name()));
        sb.append(String.format(" | Target: %s", targetPlayerName != null ? "§e" + targetPlayerName + "§r" : "§7Pending§r"));
        return sb.toString();
    }
}

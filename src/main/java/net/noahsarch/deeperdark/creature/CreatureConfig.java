package net.noahsarch.deeperdark.creature;

/**
 * Configuration holder for all creature-related settings.
 * Values are stored in DeeperDarkConfig.ConfigInstance and accessed through this helper.
 */
public class CreatureConfig {
    // Pathfinding
    public int pathfindingMinDist = 60;
    public int pathfindingMaxDist = 120;
    public int pathfindingMaxY = 52;

    // Spawn validity
    public int validityFrequency = 600;
    public double validityRoll = 0.005;

    // Spacing
    public int entitySpacing = 20;

    // Trail
    public int trailSeparation = 3;
    public double trailReach = 0.7;

    // Jitter
    public double jitterMax = 2.0;

    // Interaction
    public int playerDistanceTolerance = 5;
    public double chaseFrequency = 0.4;

    // Chase
    public int chasePathMinDist = 50;
    public int chasePathMaxDist = 80;
    public double movementSpeed = 5.0;
    public int evasionTimer = 600;
    public double deathFrequency = 0.5;

    // Side effects
    public double echoChance = 1.0;
    public double echoTriggerRadius = 1.5;
    public double torchRemovalChance = 0.4;
    public double projectileRejectionChance = 1.0;
    public int projectileRejectionDelay = 4;

    // Despawn
    public int despawnDelay = 12000;

    // Debug
    public boolean enableDebugLogging = false;
    public boolean enableDebugGlow = false;
    public boolean enableDebugPath = false;
    public int debugPathDuration = 5; // seconds the debug path particles are visible
}

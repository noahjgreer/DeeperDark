package net.noahsarch.deeperdark.creepiness;

/** Settings for the old-Minecraft creepiness haunts, stored under "creepiness" in the main config. */
public class CreepinessConfig {
    public boolean enabled = true;

    // Each player rolls one haunt every minDelayTicks–maxDelayTicks (pre-20w12a cave sound timing)
    public int minDelayTicks = 6000;
    public int maxDelayTicks = 18000;

    public boolean caveSounds = true;
    public boolean mining = true;
    public boolean footsteps = true;
    public boolean doors = true;
    public boolean phantomMobs = true;
    public boolean follower = true;
    public boolean ghostBlocks = true;
}

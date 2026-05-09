package net.noahsarch.deeperdark.item;

public enum CollarTier {
    LEATHER(1, 40, 60, new int[]{4}),
    COPPER(2, 100, 120, new int[]{1, 3}),
    IRON(3, 180, 240, new int[]{1, 3, 4}),
    DIAMOND(4, 360, 480, new int[]{0, 1, 2, 3}),
    NETHERITE(5, 720, 960, new int[]{0, 1, 2, 3, 4});

    // All 5 trinket slot positions, shared across tiers (Netherite layout).
    // Active slots per tier are a subset of these 5.
    public static final int[] SLOT_X = {64, 52, 96, 108, 80};
    public static final int[] SLOT_Y = {16, 44, 16, 44, 56};

    public final int trinketSlots;
    public final int fireMax;
    public final int waterMax;
    /** Which of the 5 slot indices (0-4) are active for this tier. */
    public final int[] activeSlotIndices;

    CollarTier(int trinketSlots, int fireMax, int waterMax, int[] activeSlotIndices) {
        this.trinketSlots = trinketSlots;
        this.fireMax = fireMax;
        this.waterMax = waterMax;
        this.activeSlotIndices = activeSlotIndices;
    }

    public boolean isSlotActive(int slotIndex) {
        for (int i : activeSlotIndices) {
            if (i == slotIndex) return true;
        }
        return false;
    }

    public String overlaySpriteName() {
        return "slots_" + name().toLowerCase();
    }
}

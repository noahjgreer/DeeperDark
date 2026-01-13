/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.advancement.AdvancementFrame
 *  net.minecraft.client.gui.screen.advancement.AdvancementObtainedStatus
 *  net.minecraft.client.gui.screen.advancement.AdvancementObtainedStatus$1
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.gui.screen.advancement;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.client.gui.screen.advancement.AdvancementObtainedStatus;
import net.minecraft.util.Identifier;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public final class AdvancementObtainedStatus
extends Enum<AdvancementObtainedStatus> {
    public static final /* enum */ AdvancementObtainedStatus OBTAINED = new AdvancementObtainedStatus("OBTAINED", 0, Identifier.ofVanilla((String)"advancements/box_obtained"), Identifier.ofVanilla((String)"advancements/task_frame_obtained"), Identifier.ofVanilla((String)"advancements/challenge_frame_obtained"), Identifier.ofVanilla((String)"advancements/goal_frame_obtained"));
    public static final /* enum */ AdvancementObtainedStatus UNOBTAINED = new AdvancementObtainedStatus("UNOBTAINED", 1, Identifier.ofVanilla((String)"advancements/box_unobtained"), Identifier.ofVanilla((String)"advancements/task_frame_unobtained"), Identifier.ofVanilla((String)"advancements/challenge_frame_unobtained"), Identifier.ofVanilla((String)"advancements/goal_frame_unobtained"));
    private final Identifier boxTexture;
    private final Identifier taskFrameTexture;
    private final Identifier challengeFrameTexture;
    private final Identifier goalFrameTexture;
    private static final /* synthetic */ AdvancementObtainedStatus[] field_2698;

    public static AdvancementObtainedStatus[] values() {
        return (AdvancementObtainedStatus[])field_2698.clone();
    }

    public static AdvancementObtainedStatus valueOf(String string) {
        return Enum.valueOf(AdvancementObtainedStatus.class, string);
    }

    private AdvancementObtainedStatus(Identifier boxTexture, Identifier taskFrameTexture, Identifier challengeFrameTexture, Identifier goalFrameTexture) {
        this.boxTexture = boxTexture;
        this.taskFrameTexture = taskFrameTexture;
        this.challengeFrameTexture = challengeFrameTexture;
        this.goalFrameTexture = goalFrameTexture;
    }

    public Identifier getBoxTexture() {
        return this.boxTexture;
    }

    public Identifier getFrameTexture(AdvancementFrame frame) {
        return switch (1.field_45430[frame.ordinal()]) {
            default -> throw new MatchException(null, null);
            case 1 -> this.taskFrameTexture;
            case 2 -> this.challengeFrameTexture;
            case 3 -> this.goalFrameTexture;
        };
    }

    private static /* synthetic */ AdvancementObtainedStatus[] method_36884() {
        return new AdvancementObtainedStatus[]{OBTAINED, UNOBTAINED};
    }

    static {
        field_2698 = AdvancementObtainedStatus.method_36884();
    }
}


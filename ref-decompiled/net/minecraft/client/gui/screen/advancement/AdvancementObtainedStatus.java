package net.minecraft.client.gui.screen.advancement;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public enum AdvancementObtainedStatus {
   OBTAINED(Identifier.ofVanilla("advancements/box_obtained"), Identifier.ofVanilla("advancements/task_frame_obtained"), Identifier.ofVanilla("advancements/challenge_frame_obtained"), Identifier.ofVanilla("advancements/goal_frame_obtained")),
   UNOBTAINED(Identifier.ofVanilla("advancements/box_unobtained"), Identifier.ofVanilla("advancements/task_frame_unobtained"), Identifier.ofVanilla("advancements/challenge_frame_unobtained"), Identifier.ofVanilla("advancements/goal_frame_unobtained"));

   private final Identifier boxTexture;
   private final Identifier taskFrameTexture;
   private final Identifier challengeFrameTexture;
   private final Identifier goalFrameTexture;

   private AdvancementObtainedStatus(final Identifier boxTexture, final Identifier taskFrameTexture, final Identifier challengeFrameTexture, final Identifier goalFrameTexture) {
      this.boxTexture = boxTexture;
      this.taskFrameTexture = taskFrameTexture;
      this.challengeFrameTexture = challengeFrameTexture;
      this.goalFrameTexture = goalFrameTexture;
   }

   public Identifier getBoxTexture() {
      return this.boxTexture;
   }

   public Identifier getFrameTexture(AdvancementFrame frame) {
      Identifier var10000;
      switch (frame) {
         case TASK:
            var10000 = this.taskFrameTexture;
            break;
         case CHALLENGE:
            var10000 = this.challengeFrameTexture;
            break;
         case GOAL:
            var10000 = this.goalFrameTexture;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   // $FF: synthetic method
   private static AdvancementObtainedStatus[] method_36884() {
      return new AdvancementObtainedStatus[]{OBTAINED, UNOBTAINED};
   }
}

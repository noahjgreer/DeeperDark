package net.minecraft.server.command;

import net.minecraft.text.Text;

public interface CommandOutput {
   CommandOutput DUMMY = new CommandOutput() {
      public void sendMessage(Text message) {
      }

      public boolean shouldReceiveFeedback() {
         return false;
      }

      public boolean shouldTrackOutput() {
         return false;
      }

      public boolean shouldBroadcastConsoleToOps() {
         return false;
      }
   };

   void sendMessage(Text message);

   boolean shouldReceiveFeedback();

   boolean shouldTrackOutput();

   boolean shouldBroadcastConsoleToOps();

   default boolean cannotBeSilenced() {
      return false;
   }
}

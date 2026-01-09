package net.minecraft.client.gl;

import com.google.common.collect.EvictingQueue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.logging.LogUtils;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.Untracker;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.ARBDebugOutput;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengl.GLDebugMessageARBCallback;
import org.lwjgl.opengl.GLDebugMessageARBCallbackI;
import org.lwjgl.opengl.GLDebugMessageCallback;
import org.lwjgl.opengl.GLDebugMessageCallbackI;
import org.lwjgl.opengl.KHRDebug;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class GlDebug {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int DEBUG_MESSAGE_QUEUE_SIZE = 10;
   private final Queue debugMessages = EvictingQueue.create(10);
   @Nullable
   private volatile DebugMessage lastDebugMessage;
   private static final List KHR_VERBOSITY_LEVELS = ImmutableList.of(37190, 37191, 37192, 33387);
   private static final List ARB_VERBOSITY_LEVELS = ImmutableList.of(37190, 37191, 37192);

   private static String unknown(int opcode) {
      return "Unknown (0x" + Integer.toHexString(opcode).toUpperCase() + ")";
   }

   public static String getSource(int opcode) {
      switch (opcode) {
         case 33350:
            return "API";
         case 33351:
            return "WINDOW SYSTEM";
         case 33352:
            return "SHADER COMPILER";
         case 33353:
            return "THIRD PARTY";
         case 33354:
            return "APPLICATION";
         case 33355:
            return "OTHER";
         default:
            return unknown(opcode);
      }
   }

   public static String getType(int opcode) {
      switch (opcode) {
         case 33356:
            return "ERROR";
         case 33357:
            return "DEPRECATED BEHAVIOR";
         case 33358:
            return "UNDEFINED BEHAVIOR";
         case 33359:
            return "PORTABILITY";
         case 33360:
            return "PERFORMANCE";
         case 33361:
            return "OTHER";
         case 33384:
            return "MARKER";
         default:
            return unknown(opcode);
      }
   }

   public static String getSeverity(int opcode) {
      switch (opcode) {
         case 33387:
            return "NOTIFICATION";
         case 37190:
            return "HIGH";
         case 37191:
            return "MEDIUM";
         case 37192:
            return "LOW";
         default:
            return unknown(opcode);
      }
   }

   private void onDebugMessage(int source, int type, int id, int severity, int length, long message, long l) {
      String string = GLDebugMessageCallback.getMessage(length, message);
      DebugMessage debugMessage;
      synchronized(this.debugMessages) {
         debugMessage = this.lastDebugMessage;
         if (debugMessage != null && debugMessage.equals(source, type, id, severity, string)) {
            ++debugMessage.count;
         } else {
            debugMessage = new DebugMessage(source, type, id, severity, string);
            this.debugMessages.add(debugMessage);
            this.lastDebugMessage = debugMessage;
         }
      }

      LOGGER.info("OpenGL debug message: {}", debugMessage);
   }

   public List collectDebugMessages() {
      synchronized(this.debugMessages) {
         List list = Lists.newArrayListWithCapacity(this.debugMessages.size());
         Iterator var3 = this.debugMessages.iterator();

         while(var3.hasNext()) {
            DebugMessage debugMessage = (DebugMessage)var3.next();
            String var10001 = String.valueOf(debugMessage);
            list.add(var10001 + " x " + debugMessage.count);
         }

         return list;
      }
   }

   @Nullable
   public static GlDebug enableDebug(int verbosity, boolean sync, Set usedGlCaps) {
      if (verbosity <= 0) {
         return null;
      } else {
         GLCapabilities gLCapabilities = GL.getCapabilities();
         GlDebug glDebug;
         int i;
         boolean bl;
         if (gLCapabilities.GL_KHR_debug && GlBackend.allowGlKhrDebug) {
            glDebug = new GlDebug();
            usedGlCaps.add("GL_KHR_debug");
            GL11.glEnable(37600);
            if (sync) {
               GL11.glEnable(33346);
            }

            for(i = 0; i < KHR_VERBOSITY_LEVELS.size(); ++i) {
               bl = i < verbosity;
               KHRDebug.glDebugMessageControl(4352, 4352, (Integer)KHR_VERBOSITY_LEVELS.get(i), (int[])null, bl);
            }

            Objects.requireNonNull(glDebug);
            KHRDebug.glDebugMessageCallback((GLDebugMessageCallbackI)GLX.make(GLDebugMessageCallback.create(glDebug::onDebugMessage), Untracker::untrack), 0L);
            return glDebug;
         } else if (gLCapabilities.GL_ARB_debug_output && GlBackend.allowGlArbDebugOutput) {
            glDebug = new GlDebug();
            usedGlCaps.add("GL_ARB_debug_output");
            if (sync) {
               GL11.glEnable(33346);
            }

            for(i = 0; i < ARB_VERBOSITY_LEVELS.size(); ++i) {
               bl = i < verbosity;
               ARBDebugOutput.glDebugMessageControlARB(4352, 4352, (Integer)ARB_VERBOSITY_LEVELS.get(i), (int[])null, bl);
            }

            Objects.requireNonNull(glDebug);
            ARBDebugOutput.glDebugMessageCallbackARB((GLDebugMessageARBCallbackI)GLX.make(GLDebugMessageARBCallback.create(glDebug::onDebugMessage), Untracker::untrack), 0L);
            return glDebug;
         } else {
            return null;
         }
      }
   }

   @Environment(EnvType.CLIENT)
   private static class DebugMessage {
      private final int id;
      private final int source;
      private final int type;
      private final int severity;
      private final String message;
      int count = 1;

      DebugMessage(int source, int type, int id, int severity, String message) {
         this.id = id;
         this.source = source;
         this.type = type;
         this.severity = severity;
         this.message = message;
      }

      boolean equals(int source, int type, int id, int severity, String message) {
         return type == this.type && source == this.source && id == this.id && severity == this.severity && message.equals(this.message);
      }

      public String toString() {
         int var10000 = this.id;
         return "id=" + var10000 + ", source=" + GlDebug.getSource(this.source) + ", type=" + GlDebug.getType(this.type) + ", severity=" + GlDebug.getSeverity(this.severity) + ", message='" + this.message + "'";
      }
   }
}

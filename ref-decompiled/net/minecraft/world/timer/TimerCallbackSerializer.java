package net.minecraft.world.timer;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

public class TimerCallbackSerializer {
   public static final TimerCallbackSerializer INSTANCE;
   private final Codecs.IdMapper idMapper = new Codecs.IdMapper();
   private final Codec codec;

   @VisibleForTesting
   public TimerCallbackSerializer() {
      this.codec = this.idMapper.getCodec(Identifier.CODEC).dispatch("Type", TimerCallback::getCodec, Function.identity());
   }

   public TimerCallbackSerializer registerSerializer(Identifier id, MapCodec codec) {
      this.idMapper.put(id, codec);
      return this;
   }

   public Codec getCodec() {
      return this.codec;
   }

   static {
      INSTANCE = (new TimerCallbackSerializer()).registerSerializer(Identifier.ofVanilla("function"), FunctionTimerCallback.CODEC).registerSerializer(Identifier.ofVanilla("function_tag"), FunctionTagTimerCallback.CODEC);
   }
}

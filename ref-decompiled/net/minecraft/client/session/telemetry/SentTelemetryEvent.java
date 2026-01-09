package net.minecraft.client.session.telemetry;

import com.mojang.authlib.minecraft.TelemetryEvent;
import com.mojang.authlib.minecraft.TelemetrySession;
import com.mojang.serialization.Codec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public record SentTelemetryEvent(TelemetryEventType type, PropertyMap properties) {
   public static final Codec CODEC;

   public SentTelemetryEvent(TelemetryEventType telemetryEventType, PropertyMap propertyMap) {
      propertyMap.keySet().forEach((property) -> {
         if (!telemetryEventType.hasProperty(property)) {
            String var10002 = property.id();
            throw new IllegalArgumentException("Property '" + var10002 + "' not expected for event: '" + telemetryEventType.getId() + "'");
         }
      });
      this.type = telemetryEventType;
      this.properties = propertyMap;
   }

   public TelemetryEvent createEvent(TelemetrySession session) {
      return this.type.createEvent(session, this.properties);
   }

   public TelemetryEventType type() {
      return this.type;
   }

   public PropertyMap properties() {
      return this.properties;
   }

   static {
      CODEC = TelemetryEventType.CODEC.dispatchStable(SentTelemetryEvent::type, TelemetryEventType::getCodec);
   }
}

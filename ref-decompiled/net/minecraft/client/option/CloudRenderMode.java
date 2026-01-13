/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.option.CloudRenderMode
 *  net.minecraft.text.Text
 *  net.minecraft.util.StringIdentifiable
 */
package net.minecraft.client.option;

import com.mojang.serialization.Codec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public final class CloudRenderMode
extends Enum<CloudRenderMode>
implements StringIdentifiable {
    public static final /* enum */ CloudRenderMode OFF = new CloudRenderMode("OFF", 0, "false", "options.off");
    public static final /* enum */ CloudRenderMode FAST = new CloudRenderMode("FAST", 1, "fast", "options.clouds.fast");
    public static final /* enum */ CloudRenderMode FANCY = new CloudRenderMode("FANCY", 2, "true", "options.clouds.fancy");
    public static final Codec<CloudRenderMode> CODEC;
    private final String serializedId;
    private final Text text;
    private static final /* synthetic */ CloudRenderMode[] field_18168;

    public static CloudRenderMode[] values() {
        return (CloudRenderMode[])field_18168.clone();
    }

    public static CloudRenderMode valueOf(String string) {
        return Enum.valueOf(CloudRenderMode.class, string);
    }

    private CloudRenderMode(String serializedId, String translationKey) {
        this.serializedId = serializedId;
        this.text = Text.translatable((String)translationKey);
    }

    public Text getText() {
        return this.text;
    }

    public String asString() {
        return this.serializedId;
    }

    private static /* synthetic */ CloudRenderMode[] method_36860() {
        return new CloudRenderMode[]{OFF, FAST, FANCY};
    }

    static {
        field_18168 = CloudRenderMode.method_36860();
        CODEC = StringIdentifiable.createCodec(CloudRenderMode::values);
    }
}


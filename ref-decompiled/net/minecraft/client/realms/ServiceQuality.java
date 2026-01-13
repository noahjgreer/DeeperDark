/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.realms.ServiceQuality
 *  net.minecraft.util.Identifier
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.realms;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public final class ServiceQuality
extends Enum<ServiceQuality> {
    public static final /* enum */ ServiceQuality GREAT = new ServiceQuality("GREAT", 0, 1, "icon/ping_5");
    public static final /* enum */ ServiceQuality GOOD = new ServiceQuality("GOOD", 1, 2, "icon/ping_4");
    public static final /* enum */ ServiceQuality OKAY = new ServiceQuality("OKAY", 2, 3, "icon/ping_3");
    public static final /* enum */ ServiceQuality POOR = new ServiceQuality("POOR", 3, 4, "icon/ping_2");
    public static final /* enum */ ServiceQuality UNKNOWN = new ServiceQuality("UNKNOWN", 4, 5, "icon/ping_unknown");
    final int index;
    private final Identifier icon;
    private static final /* synthetic */ ServiceQuality[] field_60240;

    public static ServiceQuality[] values() {
        return (ServiceQuality[])field_60240.clone();
    }

    public static ServiceQuality valueOf(String string) {
        return Enum.valueOf(ServiceQuality.class, string);
    }

    private ServiceQuality(int index, String icon) {
        this.index = index;
        this.icon = Identifier.ofVanilla((String)icon);
    }

    public static @Nullable ServiceQuality byIndex(int index) {
        for (ServiceQuality serviceQuality : ServiceQuality.values()) {
            if (serviceQuality.getIndex() != index) continue;
            return serviceQuality;
        }
        return null;
    }

    public int getIndex() {
        return this.index;
    }

    public Identifier getIcon() {
        return this.icon;
    }

    private static /* synthetic */ ServiceQuality[] method_71196() {
        return new ServiceQuality[]{GREAT, GOOD, OKAY, POOR, UNKNOWN};
    }

    static {
        field_60240 = ServiceQuality.method_71196();
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

public static final class WolfSoundVariants.Type
extends Enum<WolfSoundVariants.Type> {
    public static final /* enum */ WolfSoundVariants.Type CLASSIC = new WolfSoundVariants.Type("classic", "");
    public static final /* enum */ WolfSoundVariants.Type PUGLIN = new WolfSoundVariants.Type("puglin", "_puglin");
    public static final /* enum */ WolfSoundVariants.Type SAD = new WolfSoundVariants.Type("sad", "_sad");
    public static final /* enum */ WolfSoundVariants.Type ANGRY = new WolfSoundVariants.Type("angry", "_angry");
    public static final /* enum */ WolfSoundVariants.Type GRUMPY = new WolfSoundVariants.Type("grumpy", "_grumpy");
    public static final /* enum */ WolfSoundVariants.Type BIG = new WolfSoundVariants.Type("big", "_big");
    public static final /* enum */ WolfSoundVariants.Type CUTE = new WolfSoundVariants.Type("cute", "_cute");
    private final String id;
    private final String suffix;
    private static final /* synthetic */ WolfSoundVariants.Type[] field_57096;

    public static WolfSoundVariants.Type[] values() {
        return (WolfSoundVariants.Type[])field_57096.clone();
    }

    public static WolfSoundVariants.Type valueOf(String string) {
        return Enum.valueOf(WolfSoundVariants.Type.class, string);
    }

    private WolfSoundVariants.Type(String id, String suffix) {
        this.id = id;
        this.suffix = suffix;
    }

    public String getId() {
        return this.id;
    }

    public String getSoundEventSuffix() {
        return this.suffix;
    }

    private static /* synthetic */ WolfSoundVariants.Type[] method_68141() {
        return new WolfSoundVariants.Type[]{CLASSIC, PUGLIN, SAD, ANGRY, GRUMPY, BIG, CUTE};
    }

    static {
        field_57096 = WolfSoundVariants.Type.method_68141();
    }
}

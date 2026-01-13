/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.dedicated;

import java.util.function.Supplier;
import javax.management.MBeanAttributeInfo;

static final class ServerMBean.Entry {
    final String name;
    final Supplier<Object> getter;
    private final String description;
    private final Class<?> type;

    ServerMBean.Entry(String name, Supplier<Object> getter, String description, Class<?> type) {
        this.name = name;
        this.getter = getter;
        this.description = description;
        this.type = type;
    }

    private MBeanAttributeInfo createInfo() {
        return new MBeanAttributeInfo(this.name, this.type.getSimpleName(), this.description, true, false, false);
    }
}

/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.EvictingQueue
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.client.particle.ParticleManager
 *  net.minecraft.client.particle.ParticleRenderer
 *  net.minecraft.client.particle.ParticleTextureSheet
 *  net.minecraft.client.render.Camera
 *  net.minecraft.client.render.Frustum
 *  net.minecraft.client.render.Submittable
 *  net.minecraft.util.crash.CrashException
 *  net.minecraft.util.crash.CrashReport
 *  net.minecraft.util.crash.CrashReportSection
 */
package net.minecraft.client.particle;

import com.google.common.collect.EvictingQueue;
import java.util.Iterator;
import java.util.Queue;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.Submittable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;

@Environment(value=EnvType.CLIENT)
public abstract class ParticleRenderer<P extends Particle> {
    private static final int QUEUE_SIZE = 16384;
    protected final ParticleManager particleManager;
    protected final Queue<P> particles = EvictingQueue.create((int)16384);

    public ParticleRenderer(ParticleManager particleManager) {
        this.particleManager = particleManager;
    }

    public boolean isEmpty() {
        return this.particles.isEmpty();
    }

    public void tick() {
        if (!this.particles.isEmpty()) {
            Iterator iterator = this.particles.iterator();
            while (iterator.hasNext()) {
                Particle particle = (Particle)iterator.next();
                this.tickParticle(particle);
                if (particle.isAlive()) continue;
                particle.getGroup().ifPresent(group -> this.particleManager.addTo(group, -1));
                iterator.remove();
            }
        }
    }

    private void tickParticle(Particle particle) {
        try {
            particle.tick();
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create((Throwable)throwable, (String)"Ticking Particle");
            CrashReportSection crashReportSection = crashReport.addElement("Particle being ticked");
            crashReportSection.add("Particle", () -> ((Particle)particle).toString());
            crashReportSection.add("Particle Type", () -> ((ParticleTextureSheet)particle.textureSheet()).toString());
            throw new CrashException(crashReport);
        }
    }

    public void add(Particle particle) {
        this.particles.add(particle);
    }

    public int size() {
        return this.particles.size();
    }

    public abstract Submittable render(Frustum var1, Camera var2, float var3);

    public Queue<P> getParticles() {
        return this.particles;
    }
}


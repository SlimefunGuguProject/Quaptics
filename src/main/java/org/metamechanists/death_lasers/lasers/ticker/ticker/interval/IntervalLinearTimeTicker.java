package org.metamechanists.death_lasers.lasers.ticker.ticker.interval;

import dev.sefiraat.sefilib.entity.display.builders.BlockDisplayBuilder;
import org.bukkit.Location;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.util.Vector;
import org.metamechanists.death_lasers.lasers.ticker.ticker.LaserBlockDisplayTicker;
import org.metamechanists.death_lasers.utils.ScaryMathsUtils;

public class IntervalLinearTimeTicker implements LaserBlockDisplayTicker {
    private static final float SCALE = 0.1F;
    private final int lifespanTicks;
    private final Vector velocity;
    private final BlockDisplay display;
    private int ageTicks = 0;

    public IntervalLinearTimeTicker(BlockDisplayBuilder displayBuilder, Location source, Location target, int lifespanTicks) {
        this.lifespanTicks = lifespanTicks;
        this.velocity = ScaryMathsUtils.getDisplacement(source, target).multiply(1.0/lifespanTicks);
        this.display = displayBuilder
                .setLocation(source)
                .setDisplayHeight(0.1F)
                .setDisplayWidth(0.1F)
                .setTransformation(ScaryMathsUtils.createDisplayTransformationType1(source, target, SCALE))
                .build();
    }

    @Override
    public void tick() {
        display.teleport(display.getLocation().add(velocity));
        ageTicks++;
    }

    @Override
    public void remove() {
        display.remove();
    }

    @Override
    public boolean expired() {
        return ageTicks >= lifespanTicks;
    }
}


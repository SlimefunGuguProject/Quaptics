package org.metamechanists.quaptics.items;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import lombok.Getter;
import org.metamechanists.quaptics.Quaptics;
import org.metamechanists.quaptics.implementation.base.ConnectedBlock;

import java.util.LinkedHashMap;
import java.util.Map;

public class Items {
    @Getter
    private static final Map<String, ConnectedBlock> blocks = new LinkedHashMap<>();

    public static void initialize() {
        final SlimefunAddon addon = Quaptics.getInstance();





































        Slimefun.getRegistry().getAllSlimefunItems().stream()
                .filter(slimefunItem -> slimefunItem instanceof ConnectedBlock)
                .map(slimefunItem -> (ConnectedBlock) slimefunItem)
                .forEach(connectedBlock -> blocks.put(connectedBlock.getId(), connectedBlock));
    }
}

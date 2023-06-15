package org.metamechanists.death_lasers.connections;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.metamechanists.death_lasers.ItemStacks;
import org.metamechanists.death_lasers.implementation.tools.TargetingWand;

public class ConnectionPointListener implements Listener {

    @EventHandler
    public void interactEvent(PlayerInteractEntityEvent event) {
        final Entity clickedEntity = event.getRightClicked();
        final ItemStack heldItem = event.getPlayer().getInventory().getItemInMainHand();

        if (!(clickedEntity instanceof Interaction)) {
            return;
        }

        if (!ConnectionPointStorage.hasConnectionPoint(clickedEntity.getLocation())) {
            return;
        }

        if (SlimefunUtils.isItemSimilar(heldItem, ItemStacks.TARGETING_WAND, true)) {
            final Location connectionPointLocation = clickedEntity.getLocation();
            if (SlimefunItem.getByItem(heldItem) instanceof TargetingWand wand) {
                wand.use(event.getPlayer(), clickedEntity.getLocation(), heldItem);
            }
        }
    }
}

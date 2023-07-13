package org.metamechanists.quaptics.implementation.blocks.concentrators;

import dev.sefiraat.sefilib.entity.display.DisplayGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.metamechanists.quaptics.connections.ConnectionPoint;
import org.metamechanists.quaptics.connections.ConnectionPointType;
import org.metamechanists.quaptics.connections.Link;
import org.metamechanists.quaptics.implementation.attachments.PowerAnimatedBlock;
import org.metamechanists.quaptics.implementation.base.ConnectedBlock;
import org.metamechanists.quaptics.implementation.blocks.Settings;
import org.metamechanists.quaptics.items.Lore;
import org.metamechanists.quaptics.items.Tier;
import org.metamechanists.quaptics.utils.Utils;
import org.metamechanists.quaptics.utils.id.complex.ConnectionGroupId;
import org.metamechanists.quaptics.utils.models.ModelBuilder;
import org.metamechanists.quaptics.utils.models.components.ModelCuboid;
import org.metamechanists.quaptics.utils.models.components.ModelItem;

import java.util.List;
import java.util.Optional;


public class SolarConcentrator extends ConnectedBlock implements PowerAnimatedBlock {
    public static final Settings SOLAR_CONCENTRATOR_1_SETTINGS = Settings.builder()
            .tier(Tier.PRIMITIVE)
            .emissionPower(1)
            .build();
    public static final Settings SOLAR_CONCENTRATOR_2_SETTINGS = Settings.builder()
            .tier(Tier.BASIC)
            .emissionPower(10)
            .build();
    public static final SlimefunItemStack SOLAR_CONCENTRATOR_1 = new SlimefunItemStack(
            "QP_SOLAR_CONCENTRATOR_1",
            Material.GLASS_PANE,
            "&eSolar Concentrator &bI",
            Lore.create(SOLAR_CONCENTRATOR_1_SETTINGS,
                    "&7● Only works during the day",
                    "&7● Concentrates sunlight into a quaptic ray"));
    public static final SlimefunItemStack SOLAR_CONCENTRATOR_2 = new SlimefunItemStack(
            "QP_SOLAR_CONCENTRATOR_2",
            Material.GLASS_PANE,
            "&eSolar Concentrator &bII",
            Lore.create(SOLAR_CONCENTRATOR_2_SETTINGS,
                    "&7● Only works during the day",
                    "&7● Concentrates sunlight into a quaptic ray"));

    private final Vector outputLocation = new Vector(0.0F, 0.0F, getConnectionRadius());

    public SolarConcentrator(final ItemGroup itemGroup, final SlimefunItemStack item, final RecipeType recipeType, final ItemStack[] recipe, final Settings settings) {
        super(itemGroup, item, recipeType, recipe, settings);
    }

    @Override
    protected float getConnectionRadius() {
        return 0.60F;
    }
    @Override
    protected DisplayGroup initModel(final @NotNull Location location, final @NotNull Player player) {
        return new ModelBuilder()
                .add("center", new ModelCuboid()
                        .material(settings.getTier().concreteMaterial)
                        .brightness(Utils.BRIGHTNESS_OFF)
                        .size(0.20F))
                .add("panel", new ModelItem()
                        .material(Material.GLASS_PANE)
                        .rotation(Math.PI / 2, 0, Math.PI / 4)
                        .size(0.90F))
                .buildAtBlockCenter(location);
    }
    @Override
    protected List<ConnectionPoint> initConnectionPoints(final ConnectionGroupId groupId, final Player player, final Location location) {
        return List.of(new ConnectionPoint(ConnectionPointType.OUTPUT, groupId, "output", formatPointLocation(player, location, outputLocation)));
    }

    @Override
    public void onSlimefunTick(@NotNull final Block block, final SlimefunItem item, final Config data) {
        super.onSlimefunTick(block, item, data);
        final Location location = block.getLocation();
        final double power = block.getWorld().isDayTime()
                ? settings.getEmissionPower()
                : 0;
        final Optional<Link> linkOptional = getLink(location, "output");
        onPoweredAnimation(location, linkOptional.isPresent() && block.getWorld().isDayTime());
        linkOptional.ifPresent(link -> link.setPower(power));
    }
    @Override
    public void onPoweredAnimation(final Location location, final boolean powered) {
        brightnessAnimation(location, "center", powered);
    }
}

package org.metamechanists.quaptics.implementation.blocks.consumers;

import dev.sefiraat.sefilib.entity.display.DisplayGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.metamechanists.quaptics.connections.ConnectionGroup;
import org.metamechanists.quaptics.connections.ConnectionPoint;
import org.metamechanists.quaptics.connections.ConnectionPointType;
import org.metamechanists.quaptics.connections.Link;
import org.metamechanists.quaptics.implementation.blocks.Settings;
import org.metamechanists.quaptics.implementation.blocks.attachments.ItemHolderBlock;
import org.metamechanists.quaptics.implementation.blocks.attachments.PanelBlock;
import org.metamechanists.quaptics.implementation.blocks.base.ConnectedBlock;
import org.metamechanists.quaptics.implementation.tools.QuapticChargeableItem;
import org.metamechanists.quaptics.panels.BlockPanel;
import org.metamechanists.quaptics.panels.PanelContainer;
import org.metamechanists.quaptics.panels.implementation.ChargerPanel;
import org.metamechanists.quaptics.utils.Transformations;
import org.metamechanists.quaptics.utils.builders.BlockDisplayBuilder;
import org.metamechanists.quaptics.utils.builders.ItemDisplayBuilder;
import org.metamechanists.quaptics.utils.id.complex.ConnectionGroupId;
import org.metamechanists.quaptics.utils.id.complex.PanelId;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.List;
import java.util.Optional;

public class Charger extends ConnectedBlock implements PanelBlock, ItemHolderBlock {
    private final Vector3f mainDisplaySize = new Vector3f(0.7F, 0.3F, 0.7F);
    private final Vector3f glassDisplaySize = new Vector3f(0.5F, 0.1F, 0.5F);
    private final Vector3f itemDisplaySize = new Vector3f(0.5F);
    private final Vector3f topOffset = new Vector3f(0, 0.35F, 0);
    private final Vector3f bottomOffset = new Vector3f(0, -0.35F, 0);
    private final Vector inputPointLocation = new Vector(0.0F, 0.0F, -settings.getConnectionRadius());

    public Charger(final ItemGroup itemGroup, final SlimefunItemStack item, final RecipeType recipeType, final ItemStack[] recipe, final Settings settings) {
        super(itemGroup, item, recipeType, recipe, settings);
    }

    @Override
    protected void addDisplays(@NotNull final DisplayGroup displayGroup, @NotNull final Location location, final @NotNull Player player) {
        displayGroup.addDisplay("mainTop", new BlockDisplayBuilder(location.toCenterLocation())
                .setBlockData(Material.SMOOTH_STONE_SLAB.createBlockData("[type=top]"))
                .setTransformation(Transformations.adjustedScaleAndOffset(mainDisplaySize, topOffset))
                .build());
        displayGroup.addDisplay("mainBottom", new BlockDisplayBuilder(location.toCenterLocation())
                .setBlockData(Material.SMOOTH_STONE_SLAB.createBlockData("[type=bottom]"))
                .setTransformation(Transformations.adjustedScaleAndOffset(mainDisplaySize, bottomOffset))
                .build());
        displayGroup.addDisplay("glassTop", new BlockDisplayBuilder(location.toCenterLocation())
                .setMaterial(Material.LIGHT_BLUE_STAINED_GLASS)
                .setTransformation(Transformations.adjustedScaleAndOffset(glassDisplaySize, topOffset))
                .build());
        displayGroup.addDisplay("glassBottom", new BlockDisplayBuilder(location.toCenterLocation())
                .setMaterial(Material.LIGHT_BLUE_STAINED_GLASS)
                .setTransformation(Transformations.adjustedScaleAndOffset(glassDisplaySize, bottomOffset))
                .build());
        displayGroup.addDisplay("item", new ItemDisplayBuilder(location.toCenterLocation())
                .setTransformation(Transformations.unadjustedScale(itemDisplaySize))
                .build());
    }

    @Override
    protected List<ConnectionPoint> generateConnectionPoints(final ConnectionGroupId groupId, final Player player, final Location location) {
        return List.of(new ConnectionPoint(ConnectionPointType.INPUT, groupId, "input", formatPointLocation(player, location, inputPointLocation)));
    }

    @Override
    public BlockPanel createPanel(final PanelId panelId, final ConnectionGroupId groupId) {
        return new ChargerPanel(panelId, groupId);
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    protected void onPlace(@NotNull final BlockPlaceEvent event) {
        super.onPlace(event);
        final Location location = event.getBlock().getLocation();
        final Optional<ConnectionGroup> optionalGroup = getGroup(location);
        optionalGroup.ifPresent(group -> PanelBlock.setPanelId(location, new ChargerPanel(location, group.getId()).getId()));
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    protected void onBreak(@NotNull final Location location) {
        super.onBreak(location);
        final Optional<PanelId> panelId = PanelBlock.getPanelId(location);
        final Optional<PanelContainer> panel = panelId.isPresent() ? panelId.get().get() : Optional.empty();
        panel.ifPresent(PanelContainer::remove);
        ItemHolderBlock.getStack(location).ifPresent(stack -> location.getWorld().dropItem(location, stack));
    }

    @Override
    protected void onRightClick(final @NotNull Location location, final @NotNull Player player) {
        interact(location, player);
    }

    @Override
    public void onQuapticTick(@NotNull final ConnectionGroup group) {
        final Optional<Location> location = group.getLocation();
        if (location.isEmpty()) {
            return;
        }

        final Optional<ItemStack> stack = ItemHolderBlock.getStack(group);
        setPanelHidden(group, stack.isEmpty());
        if (stack.isEmpty()) {
            return;
        }

        final Optional<Link> inputLink = getLink(group, "input");
        if (inputLink.isEmpty() || !settings.isOperational(inputLink)) {
            return;
        }

        final ItemStack newStack = QuapticChargeableItem.chargeItem(inputLink.get(), stack.get());
        ItemHolderBlock.insertItem(location.get(), newStack);
        updatePanel(group);
    }

    @Override
    public void onInputLinkUpdated(@NotNull final ConnectionGroup group) {
        doBurnoutCheck(group, "input");
    }

    @Override
    public boolean onInsert(@NotNull final Location location, @NotNull final ItemStack stack, @NotNull final Player player) {
        return SlimefunItem.getByItem(stack) instanceof QuapticChargeableItem;
    }

    @Override
    public Optional<ItemStack> onRemove(@NotNull final Location location, @NotNull final ItemStack stack, @NotNull final Player player) {
        QuapticChargeableItem.updateLore(stack);
        return Optional.of(stack);
    }
}

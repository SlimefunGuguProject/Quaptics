package org.metamechanists.quaptics.implementation.multiblocks.infuser;

import dev.sefiraat.sefilib.entity.display.DisplayGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.metamechanists.metalib.utils.ParticleUtils;
import org.metamechanists.quaptics.connections.ConnectionGroup;
import org.metamechanists.quaptics.connections.ConnectionPoint;
import org.metamechanists.quaptics.implementation.blocks.Settings;
import org.metamechanists.quaptics.implementation.blocks.attachments.InfoPanelBlock;
import org.metamechanists.quaptics.implementation.blocks.attachments.ItemHolderBlock;
import org.metamechanists.quaptics.implementation.blocks.base.ConnectedBlock;
import org.metamechanists.quaptics.implementation.multiblocks.ComplexMultiblock;
import org.metamechanists.quaptics.items.Lore;
import org.metamechanists.quaptics.items.Tier;
import org.metamechanists.quaptics.items.groups.Primitive;
import org.metamechanists.quaptics.panels.info.InfoPanelContainer;
import org.metamechanists.quaptics.storage.QuapticTicker;
import org.metamechanists.quaptics.utils.BlockStorageAPI;
import org.metamechanists.quaptics.utils.Keys;
import org.metamechanists.quaptics.utils.Language;
import org.metamechanists.quaptics.utils.Particles;
import org.metamechanists.quaptics.utils.builders.BlockDisplayBuilder;
import org.metamechanists.quaptics.utils.builders.ItemDisplayBuilder;
import org.metamechanists.quaptics.utils.id.complex.ConnectionGroupId;
import org.metamechanists.quaptics.utils.id.complex.InfoPanelId;
import org.metamechanists.quaptics.utils.transformations.TransformationMatrixBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.metamechanists.quaptics.implementation.multiblocks.infuser.InfusionPillar.INFUSION_PILLAR;


public class InfusionContainer extends ConnectedBlock implements ItemHolderBlock, ComplexMultiblock {
    public static final Settings INFUSION_CONTAINER_SETTINGS = Settings.builder()
            .tier(Tier.PRIMITIVE)
            .timePerItem(5)
            .build();
    public static final SlimefunItemStack INFUSION_CONTAINER = new SlimefunItemStack(
            "QP_INFUSION_CONTAINER",
            Material.GRAY_CONCRETE,
            "&6Infusion Container",
            Lore.create(INFUSION_CONTAINER_SETTINGS,
                    "&7● Multiblock structure: use the Multiblock Wand to build the structure",
                    "&7● Infuses items",
                    "&7● &eRight Click &7with an item to start infusing"));

    private static final Vector3f BASE_SCALE = new Vector3f(0.9F, 0.6F, 0.9F);
    private static final Vector3f BASE_OFFSET = new Vector3f(0.0F, -0.3F, 0.0F);
    private static final Vector3f PILLAR_SCALE = new Vector3f(0.2F, 0.8F, 0.2F);

    private static final Vector3f PILLAR_1_OFFSET = new Vector3f(-0.4F, -0.1F, -0.4F);
    private static final Vector3f PILLAR_2_OFFSET = new Vector3f(-0.4F, -0.1F, 0.4F);
    private static final Vector3f PILLAR_3_OFFSET = new Vector3f(0.4F, -0.1F, -0.4F);
    private static final Vector3f PILLAR_4_OFFSET = new Vector3f(0.4F, -0.1F, 0.4F);

    private static final Vector PILLAR_1_LOCATION = new Vector(2, 0, 0);
    private static final Vector PILLAR_2_LOCATION = new Vector(-2, 0, 0);
    private static final Vector PILLAR_3_LOCATION = new Vector(0, 0, 2);
    private static final Vector PILLAR_4_LOCATION = new Vector(0, 0, -2);
    private static final List<Vector> PILLAR_LOCATIONS = List.of(PILLAR_1_LOCATION, PILLAR_2_LOCATION, PILLAR_3_LOCATION, PILLAR_4_LOCATION);

    private static final Vector3f ITEM_DISPLAY_SIZE = new Vector3f(0.5F);
    private static final Vector3f ITEM_DISPLAY_OFFSET = new Vector3f(0, 0.3F, 0);

    private static final int PILLAR_PARTICLE_COUNT = 3;
    private static final double PILLAR_PARTICLE_ANIMATION_LENGTH_SECONDS = 0.5;
    private static final double CONTAINER_PARTICLE_RADIUS = 0.5;
    private static final int CONTAINER_PARTICLE_COUNT = 3;

    private static final Map<ItemStack, ItemStack> RECIPES = Map.of(
            new ItemStack(Material.QUARTZ), Primitive.PHASE_CRYSTAL_1,
            Primitive.PHASE_CRYSTAL_1, Primitive.PHASE_CRYSTAL_5,
            Primitive.PHASE_CRYSTAL_5, Primitive.PHASE_CRYSTAL_15,
            Primitive.PHASE_CRYSTAL_15, Primitive.PHASE_CRYSTAL_45,
            Primitive.PHASE_CRYSTAL_45, Primitive.PHASE_CRYSTAL_90,
            Primitive.PHASE_CRYSTAL_90, Primitive.PHASE_CRYSTAL_180
    );

    public InfusionContainer(final ItemGroup itemGroup, final SlimefunItemStack item, final RecipeType recipeType, final ItemStack[] recipe, final Settings settings) {
        super(itemGroup, item, recipeType, recipe, settings);
    }

    @Override
    protected void initDisplays(@NotNull final DisplayGroup displayGroup, @NotNull final Location location, final @NotNull Player player) {
        displayGroup.addDisplay("base", new BlockDisplayBuilder(location.toCenterLocation())
                .setBlockData(Material.GRAY_CONCRETE.createBlockData())
                .setTransformation(new TransformationMatrixBuilder()
                        .scale(BASE_SCALE)
                        .translate(BASE_OFFSET)
                        .buildForBlockDisplay())
                .build());
        displayGroup.addDisplay("pillar1", new BlockDisplayBuilder(location.toCenterLocation())
                .setBlockData(Material.WHITE_CONCRETE.createBlockData())
                .setTransformation(new TransformationMatrixBuilder()
                        .scale(PILLAR_SCALE)
                        .translate(PILLAR_1_OFFSET)
                        .buildForBlockDisplay())
                .build());
        displayGroup.addDisplay("pillar2", new BlockDisplayBuilder(location.toCenterLocation())
                .setBlockData(Material.WHITE_CONCRETE.createBlockData())
                .setTransformation(new TransformationMatrixBuilder()
                        .scale(PILLAR_SCALE)
                        .translate(PILLAR_2_OFFSET)
                        .buildForBlockDisplay())
                .build());
        displayGroup.addDisplay("pillar3", new BlockDisplayBuilder(location.toCenterLocation())
                .setBlockData(Material.WHITE_CONCRETE.createBlockData())
                .setTransformation(new TransformationMatrixBuilder()
                        .scale(PILLAR_SCALE)
                        .translate(PILLAR_3_OFFSET)
                        .buildForBlockDisplay())
                .build());
        displayGroup.addDisplay("pillar4", new BlockDisplayBuilder(location.toCenterLocation())
                .setBlockData(Material.WHITE_CONCRETE.createBlockData())
                .setTransformation(new TransformationMatrixBuilder()
                        .scale(PILLAR_SCALE)
                        .translate(PILLAR_4_OFFSET)
                        .buildForBlockDisplay())
                .build());
        displayGroup.addDisplay("item", new ItemDisplayBuilder(location.toCenterLocation())
                .setTransformation(new TransformationMatrixBuilder()
                        .scale(ITEM_DISPLAY_SIZE)
                        .translate(ITEM_DISPLAY_OFFSET)
                        .buildForItemDisplay())
                .build());
    }
    @Override
    protected List<ConnectionPoint> initConnectionPoints(final ConnectionGroupId groupId, final Player player, final Location location) {
        return new ArrayList<>();
    }
    @Override
    protected void initBlockStorage(final @NotNull Location location) {
        BlockStorageAPI.set(location, Keys.BS_CRAFT_IN_PROGRESS, false);
        BlockStorageAPI.set(location, Keys.BS_SECONDS_SINCE_CRAFT_STARTED, 0.0);
    }

    @Override
    protected void onBreak(@NotNull final Location location) {
        super.onBreak(location);
        final Optional<InfoPanelId> panelId = InfoPanelBlock.getPanelId(location);
        final Optional<InfoPanelContainer> panel = panelId.isPresent() ? panelId.get().get() : Optional.empty();
        panel.ifPresent(InfoPanelContainer::remove);
        ItemHolderBlock.getStack(location).ifPresent(stack -> location.getWorld().dropItem(location, stack));
    }
    @Override
    protected void onRightClick(final @NotNull Location location, final @NotNull Player player) {
        if (multiblockInteract(location.getBlock(), player)) {
            return;
        }
        itemHolderInteract(location, player);
    }
    @Override
    public void onQuapticTick(@NotNull final ConnectionGroup group, @NotNull final Location location) {
        if (!BlockStorageAPI.getBoolean(location, Keys.BS_CRAFT_IN_PROGRESS)) {
            return;
        }

        if (!allPillarsPowered(location)) {
            cancelCraft(location);
            return;
        }

        double secondsSinceCraftStarted = BlockStorageAPI.getDouble(location, Keys.BS_SECONDS_SINCE_CRAFT_STARTED);
        secondsSinceCraftStarted += 1.0 / QuapticTicker.QUAPTIC_TICKS_PER_SECOND;
        BlockStorageAPI.set(location, Keys.BS_SECONDS_SINCE_CRAFT_STARTED, secondsSinceCraftStarted);

        tickAnimation(location, secondsSinceCraftStarted);

        if (secondsSinceCraftStarted >= settings.getTimePerItem()) {
            completeCraft(location);
        }
    }
    @Override
    public boolean onInsert(@NotNull final Location location, @NotNull final ItemStack stack, @NotNull final Player player) {
        if (RECIPES.keySet().stream().noneMatch(input -> SlimefunUtils.isItemSimilar(input, stack, false))) {
            Language.sendLanguageMessage(player, "infuser.cannot-be-infused");
            return false;
        }

        if (!allPillarsPowered(location)) {
            Language.sendLanguageMessage(player, "infuser.pillars-not-powered");
            return false;
        }

        BlockStorageAPI.set(location, Keys.BS_SECONDS_SINCE_CRAFT_STARTED, 0);
        BlockStorageAPI.set(location, Keys.BS_CRAFT_IN_PROGRESS, true);
        return true;
    }
    @Override
    public Optional<ItemStack> onRemove(@NotNull final Location location, @NotNull final ItemStack stack) {
        BlockStorageAPI.set(location, Keys.BS_SECONDS_SINCE_CRAFT_STARTED, 0);
        BlockStorageAPI.set(location, Keys.BS_CRAFT_IN_PROGRESS, false);
        return Optional.of(stack);
    }

    @Override
    public Map<Vector, ItemStack> getStructure() {
        return Map.of(
                PILLAR_1_LOCATION, INFUSION_PILLAR,
                PILLAR_2_LOCATION, INFUSION_PILLAR,
                PILLAR_3_LOCATION, INFUSION_PILLAR,
                PILLAR_4_LOCATION, INFUSION_PILLAR
        );
    }
    @Override
    public void tickAnimation(@NotNull final Location centerLocation, final double timeSeconds) {
        // TODO make components light up
        PILLAR_LOCATIONS.forEach(pillarLocation -> animatePillar(centerLocation, centerLocation.clone().add(pillarLocation), timeSeconds));
        animateCenter(centerLocation, timeSeconds);
    }

    private static boolean isPillarPowered(@NotNull final Location pillarLocation) {
        return BlockStorageAPI.getBoolean(pillarLocation, Keys.BS_POWERED);
    }
    private static boolean allPillarsPowered(@NotNull final Location location) {
        return PILLAR_LOCATIONS.stream().allMatch(vector -> isPillarPowered(location.clone().add(vector)));
    }
    private static void animatePillar(@NotNull final Location center, @NotNull final Location pillarLocation, final double timeSinceCraftStarted) {
        Particles.animatedLine(Particle.ELECTRIC_SPARK,
                pillarLocation.clone().toCenterLocation(), center.clone().toCenterLocation(),
                PILLAR_PARTICLE_COUNT, (timeSinceCraftStarted % PILLAR_PARTICLE_ANIMATION_LENGTH_SECONDS) / PILLAR_PARTICLE_ANIMATION_LENGTH_SECONDS);
    }
    private static void animateCenter(@NotNull final Location center, final double timeSinceCraftStarted) {
        ParticleUtils.randomParticle(center.clone().toCenterLocation(), Particle.ENCHANTMENT_TABLE, CONTAINER_PARTICLE_RADIUS, CONTAINER_PARTICLE_COUNT);
    }

    private static void cancelCraft(@NotNull final Location location) {
        BlockStorageAPI.set(location, Keys.BS_SECONDS_SINCE_CRAFT_STARTED, 0);
        BlockStorageAPI.set(location, Keys.BS_CRAFT_IN_PROGRESS, false);
    }
    private static void completeCraft(@NotNull final Location location) {
        final Optional<ItemStack> stack = ItemHolderBlock.getStack(location);
        if (stack.isEmpty()) {
            return;
        }

        BlockStorageAPI.set(location, Keys.BS_SECONDS_SINCE_CRAFT_STARTED, 0);
        BlockStorageAPI.set(location, Keys.BS_CRAFT_IN_PROGRESS, false);
        ItemHolderBlock.insertItem(location, RECIPES.get(stack.get()));
    }
}
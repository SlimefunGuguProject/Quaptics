package org.metamechanists.quaptics.items.groups;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.utils.LoreBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.metamechanists.quaptics.Quaptics;
import org.metamechanists.quaptics.implementation.blocks.concentrators.EnergyConcentrator;
import org.metamechanists.quaptics.implementation.blocks.concentrators.SolarConcentrator;
import org.metamechanists.quaptics.implementation.blocks.manipulators.Combiner;
import org.metamechanists.quaptics.implementation.blocks.manipulators.Lens;
import org.metamechanists.quaptics.implementation.blocks.manipulators.Splitter;
import org.metamechanists.quaptics.items.Groups;
import org.metamechanists.quaptics.items.Lore;
import org.metamechanists.quaptics.items.Tiers;

public class Basic {

    public static final SlimefunItemStack SOLAR_CONCENTRATOR_2 = new SlimefunItemStack(
            "QP_SOLAR_CONCENTRATOR_2",
            Material.GLASS_PANE,
            "&eSolar Concentrator &bII",
            Tiers.BASIC.coloredName,
            "&7● Only works during the day",
            "&7● Concentrates sunlight into a quaptic ray",
            Lore.emissionPower(10));

    public static final SlimefunItemStack ENERGY_CONCENTRATOR_1 = new SlimefunItemStack(
            "QP_ENERGY_CONCENTRATOR_1",
            Material.PURPLE_CONCRETE,
            "&eEnergy Concentrator &bI",
            Tiers.BASIC.coloredName,
            "&7● Consumes energy",
            "&7● Concentrates energy into a quaptic ray",
            LoreBuilder.powerPerSecond(30),
            Lore.emissionPower(15));

    public static final SlimefunItemStack LENS_2 = new SlimefunItemStack(
            "QP_LENS_2",
            Material.GLASS,
            "&9Lens &bII",
            Tiers.BASIC.coloredName,
            "&7● &bRedirects &7a quaptic ray",
            Lore.maxPower(Tiers.BASIC.maxPower),
            Lore.powerLoss(7));

    public static final SlimefunItemStack COMBINER_2_2 = new SlimefunItemStack(
            "QP_COMBINER_2_2",
            Material.GRAY_STAINED_GLASS,
            "&9Combiner &eII &8(2 connections)",
            Tiers.BASIC.coloredName,
            "&7● &bCombines &7multiple quaptic rays into one",
            Lore.maxPower(Tiers.BASIC.maxPower),
            Lore.powerLoss(14),
            Lore.maxConnections(2));

    public static final SlimefunItemStack COMBINER_2_3 = new SlimefunItemStack(
            "QP_COMBINER_2_3",
            Material.GRAY_STAINED_GLASS,
            "&9Combiner &eII &8(3 connections)",
            Tiers.BASIC.coloredName,
            "&7● &bCombines &7multiple quaptic rays into one",
            Lore.maxPower(Tiers.BASIC.maxPower),
            Lore.powerLoss(14),
            Lore.maxConnections(3));

    public static final SlimefunItemStack SPLITTER_2_2 = new SlimefunItemStack(
            "QP_SPLITTER_2_2",
            Material.LIGHT_GRAY_STAINED_GLASS,
            "&9Splitter &eII &8(2 connections)",
            Tiers.BASIC.coloredName,
            "&7● &bSplits &7one quaptic ray into multiple",
            Lore.maxPower(Tiers.BASIC.maxPower),
            Lore.powerLoss(14),
            Lore.maxConnections(2));

    public static final SlimefunItemStack SPLITTER_2_3 = new SlimefunItemStack(
            "QP_SPLITTER_2_3",
            Material.LIGHT_GRAY_STAINED_GLASS,
            "&9Splitter &eII &8(3 connections)",
            Tiers.BASIC.coloredName,
            "&7● &bSplits &7one quaptic ray into multiple",
            Lore.maxPower(Tiers.BASIC.maxPower),
            Lore.powerLoss(14),
            Lore.maxConnections(3));

    public static void initialize() {
        final SlimefunAddon addon = Quaptics.getInstance();

        new SolarConcentrator(
                Groups.BASIC,
                SOLAR_CONCENTRATOR_2,
                RecipeType.NULL,
                new ItemStack[]{},
                0.35F,
                (float)(Math.PI/4),
                10,
                Tiers.BASIC.maxPower).register(addon);

        new EnergyConcentrator(
                Groups.BASIC,
                ENERGY_CONCENTRATOR_1,
                RecipeType.NULL,
                new ItemStack[]{},
                30,
                30,
                15,
                Tiers.BASIC.maxPower).register(addon);

        new Lens(
                Groups.BASIC,
                LENS_2,
                RecipeType.NULL,
                new ItemStack[]{},
                0.16F,
                Tiers.BASIC.maxPower,
                0.07).register(addon);

        new Combiner(
                Groups.BASIC,
                COMBINER_2_2,
                RecipeType.NULL,
                new ItemStack[]{},
                0.30F,
                2,
                Tiers.BASIC.maxPower,
                0.12).register(addon);

        new Combiner(
                Groups.BASIC,
                COMBINER_2_3,
                RecipeType.NULL,
                new ItemStack[]{},
                0.35F,
                3,
                Tiers.BASIC.maxPower,
                0.14).register(addon);

        new Splitter(
                Groups.BASIC,
                SPLITTER_2_2,
                RecipeType.NULL,
                new ItemStack[]{},
                0.30F,
                2,
                Tiers.BASIC.maxPower,
                0.12).register(addon);

        new Splitter(
                Groups.BASIC,
                SPLITTER_2_3,
                RecipeType.NULL,
                new ItemStack[]{},
                0.35F,
                2,
                Tiers.BASIC.maxPower,
                0.14).register(addon);
    }
}

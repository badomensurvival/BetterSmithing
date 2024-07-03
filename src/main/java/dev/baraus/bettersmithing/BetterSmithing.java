package dev.baraus.bettersmithing;

import org.bstats.bukkit.Metrics;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmithingRecipe;
import org.bukkit.inventory.SmithingTransformRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class BetterSmithing extends JavaPlugin implements Listener {

    private final List<String> toolTypes = Arrays.asList("SWORD", "SHOVEL", "PICKAXE", "AXE", "HOE");

    private final Material[] leatherTools = Arrays.stream(Material.values()).filter(
            material -> material.name().startsWith("LEATHER_") && toolTypes.contains(material.name().replace("LEATHER_", ""))
    ).toArray(Material[]::new);

    private final Material[] woodenTools = Arrays.stream(Material.values()).filter(
            material -> material.name().startsWith("WOODEN_") && toolTypes.contains(material.name().replace("WOODEN_", ""))
    ).toArray(Material[]::new);

    private final Material[] stoneTools = Arrays.stream(Material.values()).filter(
            material -> material.name().startsWith("STONE_") && toolTypes.contains(material.name().replace("STONE_", ""))
    ).toArray(Material[]::new);

    private final Material[] ironTools = Arrays.stream(Material.values()).filter(
            material -> material.name().startsWith("IRON_") && toolTypes.contains(material.name().replace("IRON_", ""))
    ).toArray(Material[]::new);

    private final Material[] goldenTools = Arrays.stream(Material.values()).filter(
            material -> material.name().startsWith("GOLDEN_") && toolTypes.contains(material.name().replace("GOLDEN_", ""))
    ).toArray(Material[]::new);

    private final Material[] diamondTools = Arrays.stream(Material.values()).filter(
            material -> material.name().startsWith("DIAMOND_") && toolTypes.contains(material.name().replace("DIAMOND_", ""))
    ).toArray(Material[]::new);

    public void addToolsRecipe(Material template, Material[] baseTools, Material[] resultTools,
                               Material additionMaterial, String basePrefix, String resultPrefix) {
        for (Material tool : baseTools) {
            Material equivalent = Arrays.stream(resultTools)
                    .filter(material -> tool.name().replace(basePrefix, "").equals(material.name().replace(resultPrefix, "")))
                    .findFirst()
                    .orElse(null);

            if (equivalent != null) {
                SmithingTransformRecipe recipe = new SmithingTransformRecipe(
                        new NamespacedKey(this, tool.name() + "_" + equivalent.name()),
                        new ItemStack(equivalent),
                        new RecipeChoice.MaterialChoice(template),
                        new RecipeChoice.MaterialChoice(tool),
                        new RecipeChoice.MaterialChoice(additionMaterial)
                );
                getServer().addRecipe(recipe);
            }
        }
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);

        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        String templateName = getConfig().getString("template", "AIR");
        Material templateMaterial = Material.getMaterial(templateName);

        Map<String, Material[]> tiersMap = new HashMap<>();
        tiersMap.put("leather", leatherTools);
        tiersMap.put("wooden", woodenTools);
        tiersMap.put("stone", stoneTools);
        tiersMap.put("iron", ironTools);
        tiersMap.put("golden", goldenTools);
        tiersMap.put("diamond", diamondTools);

        for (Map.Entry<String, Material[]> entry : tiersMap.entrySet()) {
            String tier = entry.getKey();
            Material[] tools = entry.getValue();

            if (getConfig().contains("tiers." + tier)) {
                String upgradeTo = getConfig().getString("tiers." + tier + ".upgrade_to");
                String upgradeItem = getConfig().getString("tiers." + tier + ".upgrade_item");

                if (upgradeTo != null && upgradeItem != null) {
                    Material[] upgradeToTools = tiersMap.get(upgradeTo);

                    if (upgradeToTools != null) {
                        addToolsRecipe(templateMaterial, tools, upgradeToTools, Material.valueOf(upgradeItem.toUpperCase()), tier.toUpperCase() + "_", upgradeTo.toUpperCase() + "_");
                    }
                }
            }
        }

        int pluginId = 17593; // Replace with your plugin ID
        Metrics metrics = new Metrics(this, pluginId);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}

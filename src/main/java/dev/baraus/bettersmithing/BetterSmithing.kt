package dev.baraus.bettersmithing

import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit.addRecipe
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.SmithingRecipe
import org.bukkit.inventory.SmithingTransformRecipe
import org.bukkit.plugin.java.JavaPlugin


class BetterSmithing : JavaPlugin(), Listener {
    val toolTypes = listOf(
        "SWORD", "SHOVEL", "PICKAXE", "AXE", "HOE", "HELMET", "CHESTPLATE", "LEGGINGS", "BOOTS"
    )

    var leatherTools = Material.values().filter {
        it.name.startsWith("LEATHER_") && it.name.removePrefix("LEATHER_") in toolTypes
    }

    var woodenTools = Material.values().filter {
        it.name.startsWith("WOODEN_") && it.name.removePrefix("WOODEN_") in toolTypes
    }

    val stoneTools = Material.values().filter {
        it.name.startsWith("STONE_") && it.name.removePrefix("STONE_") in toolTypes
    }

    val ironTools = Material.values().filter {
        it.name.startsWith("IRON_") && it.name.removePrefix("IRON_") in toolTypes
    }

    val goldenTools = Material.values().filter {
        it.name.startsWith("GOLDEN_") && it.name.removePrefix("GOLDEN_") in toolTypes
    }

    val diamondTools = Material.values().filter {
        it.name.startsWith("DIAMOND_") && it.name.removePrefix("DIAMOND_") in toolTypes
    }

    fun addToolsRecipe(
        baseTools: List<Material>,
        resultTools: List<Material>,
        additionMaterial: Material,
        basePrefix: String,
        resultPrefix: String
    ) {
        for (tool in baseTools) {
            val equivalent =
                resultTools.first { tool.name.removePrefix(basePrefix) == it.name.removePrefix(resultPrefix) }

            val recipe = SmithingTransformRecipe(
                NamespacedKey(this, tool.name + '_' + equivalent.name),
                ItemStack(equivalent),
                RecipeChoice.MaterialChoice(Material.AIR),
                RecipeChoice.MaterialChoice(tool),
                RecipeChoice.MaterialChoice(additionMaterial)
            )
            addRecipe(recipe)
        }
    }

    override fun onEnable() {
        server.pluginManager.registerEvents(this, this)

        val config = config
        saveDefaultConfig()

        val tiers = config.getConfigurationSection("tiers")
        val tiersMap = hashMapOf(
            "leather" to leatherTools,
            "wooden" to woodenTools,
            "stone" to stoneTools,
            "iron" to ironTools,
            "golden" to goldenTools,
            "diamond" to diamondTools
        )

        for ((tier, tools) in tiersMap) {
            if (tiers?.getConfigurationSection(tier) != null) {
                val upgradeTo = tiers.getString("$tier.upgrade_to")
                val upgradeItem = tiers.getString("$tier.upgrade_item")
                if (upgradeTo != null) {
                    val upgradeToTools = tiersMap[upgradeTo]
                    if (upgradeToTools != null && upgradeItem != null) {
                        addToolsRecipe(
                            tools,
                            upgradeToTools,
                            Material.valueOf(upgradeItem.uppercase()),
                            "${tier.uppercase()}_",
                            "${upgradeTo.uppercase()}_"
                        )
                    }
                }
            }
        }


        val pluginId = 17593 // <-- Replace with the id of your plugin!

        Metrics(this, pluginId)
//
//        addToolsRecipe(woodenTools, stoneTools, Material.STONE, "WOODEN_", "STONE_")
//        addToolsRecipe(stoneTools, ironTools, Material.IRON_INGOT, "STONE_", "IRON_")
//        addToolsRecipe(ironTools, goldenTools, Material.GOLD_INGOT, "IRON_", "GOLDEN_")
//        addToolsRecipe(goldenTools, diamondTools, Material.DIAMOND, "GOLDEN_", "DIAMOND_")
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}
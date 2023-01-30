package dev.baraus.bettersmithing

import org.bukkit.Bukkit.addRecipe
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.SmithingRecipe
import org.bukkit.plugin.java.JavaPlugin


class BetterSmithing : JavaPlugin(), Listener {
    val toolTypes = listOf(
        "SWORD", "SHOVEL", "PICKAXE", "AXE", "HOE", "HELMET", "CHESTPLATE", "LEGGINGS", "BOOTS"
    )

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

            val recipe = SmithingRecipe(
                NamespacedKey(this, equivalent.name),
                ItemStack(equivalent),
                RecipeChoice.MaterialChoice(tool),
                RecipeChoice.MaterialChoice(additionMaterial)
            )
            addRecipe(recipe)
        }
    }


    override fun onEnable() {
        server.pluginManager.registerEvents(this, this)

        addToolsRecipe(woodenTools, stoneTools, Material.STONE, "WOODEN_", "STONE_")
        addToolsRecipe(stoneTools, ironTools, Material.IRON_INGOT, "STONE_", "IRON_")
        addToolsRecipe(ironTools, goldenTools, Material.GOLD_INGOT, "IRON_", "GOLDEN_")
        addToolsRecipe(goldenTools, diamondTools, Material.DIAMOND, "GOLDEN_", "DIAMOND_")
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}
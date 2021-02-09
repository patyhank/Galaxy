/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2021
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package one.oktw.galaxy.block

import net.minecraft.block.BlockState
import net.minecraft.block.Blocks.BARRIER
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import one.oktw.galaxy.block.entity.CustomBlockEntity
import one.oktw.galaxy.item.CustomBlockItem
import one.oktw.galaxy.item.CustomItemHelper
import one.oktw.galaxy.util.CustomRegistry
import one.oktw.galaxy.util.Registrable

open class CustomBlock(final override val identifier: Identifier, val modelItem: ItemStack? = null, val baseBlock: BlockState = BARRIER.defaultState) :
    Registrable {
    constructor(id: String, modelItem: ItemStack? = null, baseBlock: BlockState = BARRIER.defaultState) :
        this(Identifier("galaxy", "block/$id"), modelItem, baseBlock)

    private val blockEntityType =
        Registry.register(Registry.BLOCK_ENTITY_TYPE, identifier, BlockEntityType.Builder.create({ createBlockEntity() }, BARRIER).build(null))

    companion object {
        val registry = CustomRegistry<CustomBlock>()

        val CONTROL_PANEL = Companion.registry.register(CustomBlock("control_panel"))
        val PLANET_TERMINAL = Companion.registry.register(CustomBlock("planet_terminal"))
        val HT_CRAFTING_TABLE = registry.register(CustomBlock("ht_crafting_table", CustomBlockItem.HT_CRAFTING_TABLE.createItemStack()))
        val ELEVATOR = registry.register(CustomBlock("elevator", CustomBlockItem.ELEVATOR.createItemStack()))
        val ANGEL_BLOCK = registry.register(CustomBlock("angel_block", CustomBlockItem.ANGEL_BLOCK.createItemStack()))
        val TELEPORTER_CORE_BASIC = registry.register(CustomBlock("teleporter_core_basic", CustomBlockItem.TELEPORTER_CORE_BASIC.createItemStack()))
        val TELEPORTER_CORE_ADVANCE = registry.register(CustomBlock("teleporter_core_advance", CustomBlockItem.TELEPORTER_CORE_ADVANCE.createItemStack()))
        val TELEPORTER_FRAME = registry.register(CustomBlock("teleporter_frame", CustomBlockItem.TELEPORTER_FRAME.createItemStack()))
    }

    open fun toItem(): CustomBlockItem? {
        return modelItem?.let { CustomItemHelper.getItem(it) as? CustomBlockItem }
    }

    open fun createBlockEntity(): CustomBlockEntity = CustomBlockEntity(blockEntityType)
}

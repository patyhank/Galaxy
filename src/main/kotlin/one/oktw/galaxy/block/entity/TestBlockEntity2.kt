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

package one.oktw.galaxy.block.entity

import net.minecraft.block.entity.BlockEntity
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.text.LiteralText
import one.oktw.galaxy.gui.GUI

class TestBlockEntity2 : BlockEntity(BlockEntityTypes.Test2), BlockWithGUI {
    private val gui = GUI(ScreenHandlerType.GENERIC_9X6, LiteralText.EMPTY)
    override fun getGUI() = gui
}

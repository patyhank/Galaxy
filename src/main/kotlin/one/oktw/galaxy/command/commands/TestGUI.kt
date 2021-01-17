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

package one.oktw.galaxy.command.commands

import com.mojang.brigadier.Command.SINGLE_SUCCESS
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import kotlinx.coroutines.*
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.LiteralText
import one.oktw.galaxy.command.Command
import one.oktw.galaxy.gui.GUI
import one.oktw.galaxy.gui.GUISBackStackManager
import one.oktw.galaxy.item.Gui
import one.oktw.galaxy.item.type.GuiType
import org.apache.logging.log4j.LogManager

class TestGUI : Command, CoroutineScope by CoroutineScope(Dispatchers.Default + SupervisorJob()) {
    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            CommandManager.literal("gui")
                .executes { context ->
                    execute(context.source)
                }
                .then(
                    CommandManager.argument("row", IntegerArgumentType.integer(1, 6))
                        .suggests { _, builder ->
                            for (i in 1..6) builder.suggest(i)
                            builder.buildFuture()
                        }
                        .executes {
                            val row = IntegerArgumentType.getInteger(it, "row")
                            val gui = when (row) {
                                1 -> GUI(ScreenHandlerType.GENERIC_9X1, LiteralText.EMPTY)
                                2 -> GUI(ScreenHandlerType.GENERIC_9X2, LiteralText.EMPTY)
                                3 -> GUI(ScreenHandlerType.GENERIC_9X3, LiteralText.EMPTY)
                                4 -> GUI(ScreenHandlerType.GENERIC_9X4, LiteralText.EMPTY)
                                5 -> GUI(ScreenHandlerType.GENERIC_9X5, LiteralText.EMPTY)
                                6 -> GUI(ScreenHandlerType.GENERIC_9X6, LiteralText.EMPTY)
                                else -> return@executes SINGLE_SUCCESS
                            }

//                            for (x in 0..8) for (y in 0 until row) gui.setAllowUse(x, y, true)

                            GUISBackStackManager.openGUI(it.source.player, gui)

                            return@executes SINGLE_SUCCESS
                        }
                )
        )
    }

    private fun execute(source: ServerCommandSource): Int {
        val gui = GUI(ScreenHandlerType.GENERIC_9X6, LiteralText("Test"))
        val gui2 = GUI(ScreenHandlerType.GENERIC_9X1, LiteralText.EMPTY)

        gui2.editInventory {
            fillAll(Gui(GuiType.BLANK).createItemStack())
        }

        gui.addBinding(0..8, 0..5) {
            LogManager.getLogger().info(item)
            gui.editInventory {
                source.player.sendMessage(LiteralText("Action: $action"), false)
//                if (item.isEmpty) set(x, y, ItemStack(Items.STICK)) else set(x, y, ItemStack.EMPTY)
            }
        }

//        for (x in 1..7) for (y in 1..4) gui.setAllowUse(x, y, true)

//        gui.setAllowUse(1, 1, false)
        gui.addBinding(1, 1) {
            GUISBackStackManager.openGUI(source.player, gui2)
        }

        launch {
            while (!source.player.isDisconnected) {
                for (x in 0..8) {
                    gui.editInventory { set(x, 0, Gui(GuiType.INFO).createItemStack()) }
                    delay(100)
                }

                for (x in 0..8) {
                    gui.editInventory { set(x, 0, ItemStack.EMPTY) }
                    delay(100)
                }
            }
        }

        launch {
            while (!source.player.isDisconnected) {
                for (x in 8 downTo 0) {
                    gui.editInventory { set(x, 5, Gui(GuiType.INFO).createItemStack()) }
                    delay(100)
                }

                for (x in 8 downTo 0) {
                    gui.editInventory { set(x, 5, ItemStack.EMPTY) }
                    delay(100)
                }
            }
        }

        launch {
            while (!source.player.isDisconnected) {
                for (y in 1..4) {
                    gui.editInventory { set(0, y, Gui(GuiType.INFO).createItemStack()) }
                    delay(100)
                }

                for (y in 1..4) {
                    gui.editInventory { set(0, y, ItemStack.EMPTY) }
                    delay(100)
                }
            }
        }

        launch {
            while (!source.player.isDisconnected) {
                for (y in 4 downTo 1) {
                    gui.editInventory { set(8, y, Gui(GuiType.INFO).createItemStack()) }
                    delay(100)
                }

                for (y in 4 downTo 1) {
                    gui.editInventory { set(8, y, ItemStack.EMPTY) }
                    delay(100)
                }
            }
        }

//        gui.editInventory {
//            set(0, ItemStack(Items.STICK))
//        }

        GUISBackStackManager.openGUI(source.player, gui)

        return com.mojang.brigadier.Command.SINGLE_SUCCESS
    }
}

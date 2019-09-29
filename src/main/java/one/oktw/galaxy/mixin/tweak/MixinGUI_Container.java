/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2019
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

package one.oktw.galaxy.mixin.tweak;

import net.minecraft.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import one.oktw.galaxy.container.ContainerForceUpdate;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Container.class)
public abstract class MixinGUI_Container implements ContainerForceUpdate {
    @Shadow
    @Final
    private DefaultedList<ItemStack> stackList;

    @Shadow
    public abstract void sendContentUpdates();

    public void forceUpdateSlot(int slot) {
        stackList.set(slot, ItemStack.EMPTY);
        sendContentUpdates();
    }
}

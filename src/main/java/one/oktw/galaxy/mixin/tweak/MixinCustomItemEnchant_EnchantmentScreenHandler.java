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

package one.oktw.galaxy.mixin.tweak;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.EnchantmentScreenHandler;
import one.oktw.galaxy.item.type.ItemType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EnchantmentScreenHandler.class)
public class MixinCustomItemEnchant_EnchantmentScreenHandler {
    @Redirect(method = "onContentChanged", at = @At(
        value = "INVOKE",
        target = "Lnet/minecraft/item/ItemStack;isEnchantable()Z"
    ))
    private boolean onContentChanged(ItemStack itemStack) {
        CompoundTag tag = itemStack.getTag();
        if (tag != null) {
            String type = tag.getString("customItemType");
            for (ItemType i : ItemType.values()) {
                if (i.name().equals(type)) return false;
            }
        }
        return itemStack.isEnchantable();
    }

}

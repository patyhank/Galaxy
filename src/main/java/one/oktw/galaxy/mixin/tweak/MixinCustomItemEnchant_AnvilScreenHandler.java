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

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.*;
import net.minecraft.server.network.ServerPlayerEntity;
import one.oktw.galaxy.item.type.ItemType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilScreenHandler.class)
public abstract class MixinCustomItemEnchant_AnvilScreenHandler extends ForgingScreenHandler {
    @Shadow
    @Final
    private Property levelCost;

    public MixinCustomItemEnchant_AnvilScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }

    @Inject(method = "updateResult", at = @At(value = "HEAD"), cancellable = true)
    private void onUpdateResult(CallbackInfo ci) {
        ItemStack itemStack = this.input.getStack(0);

        CompoundTag tag = itemStack.getTag();
        if (tag != null) {
            String type = tag.getString("customItemType");
            for (ItemType i : ItemType.values()) {
                if (i.name().equals(type)) {
                    ci.cancel();
                    this.output.setStack(0, ItemStack.EMPTY);
                    this.levelCost.set(0);
//                    this.sendContentUpdates();
                    ((ServerPlayerEntity) player).refreshScreenHandler(player.currentScreenHandler); // TODO Client De-sync
                    break;
                }
            }
        }
    }
}

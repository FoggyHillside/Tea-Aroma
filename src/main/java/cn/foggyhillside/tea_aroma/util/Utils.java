package cn.foggyhillside.tea_aroma.util;

import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class Utils {
    public static void addItem(ItemStack pStack, Player pPlayer, ItemStack pRemainingStack) {
        boolean flag = pPlayer.getAbilities().instabuild;
        pPlayer.awardStat(Stats.ITEM_USED.get(pStack.getItem()));
        if (flag) {
            if (pRemainingStack != null) {
                if (!pPlayer.getInventory().contains(pRemainingStack)) {
                    pPlayer.getInventory().add(pRemainingStack);
                }
            }
        } else {
            pStack.shrink(1);
            if (pRemainingStack != null) {
                if (!pPlayer.getInventory().add(pRemainingStack)) {
                    pPlayer.drop(pRemainingStack, false);
                }
            }
        }
    }
}
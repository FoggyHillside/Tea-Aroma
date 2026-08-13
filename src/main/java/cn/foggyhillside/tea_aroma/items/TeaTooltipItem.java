package cn.foggyhillside.tea_aroma.items;

import cn.foggyhillside.tea_aroma.util.Utils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class TeaTooltipItem extends Item {
    private final List<String> completedKeys;
    private final List<String> nextKeys;

    public TeaTooltipItem(Properties pProperties, List<String> completedKeys, List<String> nextKeys) {
        super(pProperties);
        this.completedKeys = completedKeys;
        this.nextKeys = nextKeys;
    }

    @Override
    public void appendHoverText(ItemStack pStack, TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
        pTooltipComponents.add(Utils.buildProcessTooltip(completedKeys, nextKeys));
    }
}
package dev.gigaherz.jsonthings.util;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

public class DrinkableBottleItem extends Item
{
    private static final int DRINK_DURATION = 40;

    private final Supplier<Item> returnOnDrink;

    public DrinkableBottleItem(Supplier<Item> returnOnDrink, Properties p_41346_)
    {
        super(p_41346_);
        this.returnOnDrink = returnOnDrink;
    }

    public ItemStack finishUsingItem(ItemStack p_41348_, Level p_41349_, LivingEntity p_41350_)
    {
        super.finishUsingItem(p_41348_, p_41349_, p_41350_);
        if (p_41350_ instanceof ServerPlayer serverplayer)
        {
            CriteriaTriggers.CONSUME_ITEM.trigger(serverplayer, p_41348_);
            serverplayer.awardStat(Stats.ITEM_USED.get(this));
        }

        if (p_41348_.isEmpty())
        {
            return new ItemStack(returnOnDrink.get());
        }
        else
        {
            if (p_41350_ instanceof Player && !((Player) p_41350_).getAbilities().instabuild)
            {
                ItemStack itemstack = new ItemStack(returnOnDrink.get());
                Player player = (Player) p_41350_;
                if (!player.getInventory().add(itemstack))
                {
                    player.drop(itemstack, false);
                }
            }

            return p_41348_;
        }
    }

    public InteractionResultHolder<ItemStack> use(Level p_41352_, Player p_41353_, InteractionHand p_41354_)
    {
        return ItemUtils.startUsingInstantly(p_41352_, p_41353_, p_41354_);
    }
}
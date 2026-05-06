package dev.gigaherz.jsonthings.things.scripting.client;

import static dev.gigaherz.jsonthings.things.scripting.McFunctionScript.*;

import com.google.gson.JsonObject;

import dev.gigaherz.jsonthings.things.events.FlexEventContext;
import dev.gigaherz.jsonthings.things.events.FlexEventType;
import dev.gigaherz.jsonthings.util.parse.JParse;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.api.distmarker.Dist;

@OnlyIn(Dist.CLIENT)
public class BasicCompareLogic implements IClientLogic {

    public BasicCompareLogic(String item, String block, String hand) {
        this.item = item;
        this.block = block;
        this.hand = hand;
    }

    public String item = null;
    public String block = null;
    public String hand = null;

    public void putItemRequirement(String item) {
        this.item = item;
    }

    public void putBlockRequirement(String block) {
        this.block = block;
    }

    public void putHandRequirement(String hand) {
        this.hand = hand;
    }

    @Override
    public Object getResult(FlexEventType<?> event, FlexEventContext context) {
        if (this.item == null && this.block == null && this.hand == null)
            return getDefaultByEventType(event, context);
        Entity user = context.get(FlexEventContext.USER);
        Level level = user.level();
        if (!level.isClientSide)
            return getDefaultByEventType(event, context); // Dont call me plz
        InteractionHand hand = context.get(FlexEventContext.HAND);
        if (this.item != null) {
            ItemStack stack1 = context.get(FlexEventContext.STACK);
            Player player = user instanceof Player p ? p : null;
            ItemStack stack2 = player != null
                    ? hand == InteractionHand.OFF_HAND ? player.getOffhandItem() : player.getMainHandItem()
                    : null;
            Item ano = BuiltInRegistries.ITEM.get(ResourceLocation.parse(item));
            LOGGER.debug("clientLogic item: {}", ano);
            boolean a = stack1 == null;
            boolean b = stack2 == null;
            LOGGER.debug("clientLogic item: {}, stack1: {}, stack2: {}", ano, stack1, stack2);
            if ((a || !stack1.is(ano)) && (b || !stack2.is(ano)))
                return getDefaultByEventType(event, context);
        }
        if (this.hand != null) {
            if (hand == null)
                return getDefaultByEventType(event, context);
            if (!hand.toString().equals(this.hand))
                return getDefaultByEventType(event, context);
        }
        if (this.block != null) {
            BlockPos pos = context.get(FlexEventContext.BLOCK_POS);
            if (pos == null)
                return getDefaultByEventType(event, context);
            Block one = level.getBlockState(pos).getBlock();
            Block ano = BuiltInRegistries.BLOCK.get(ResourceLocation.tryParse(this.block));
            if (!one.equals(ano))
                return getDefaultByEventType(event, context);
        }
        return getResultByEventType(event, context, 1);
    }

    public BasicCompareLogic(JsonObject data){
        JParse.begin(data).ifKey("item", val -> item = val.string().getAsString())
                .ifKey("block", val -> block = val.string().getAsString())
                .ifKey("hand", val -> hand = val.string().getAsString());
    }
}

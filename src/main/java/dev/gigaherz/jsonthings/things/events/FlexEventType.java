package dev.gigaherz.jsonthings.things.events;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public record FlexEventType<T>(String name)
{
    // Implementation
    private static final Map<String, FlexEventType<?>> eventTypes = new HashMap<>();

    // Blocks
    public static final FlexEventType<InteractionResult> USE_BLOCK_WITHOUT_ITEM = new FlexEventType<>("use_without_item");
    public static final FlexEventType<ItemInteractionResult> USE_BLOCK_WITH_ITEM = new FlexEventType<>("use_item_on");
    public static final FlexEventType<BlockState> GET_STATE_FOR_PLACEMENT = new FlexEventType<>("get_state_for_placement");

    // Items
    public static final FlexEventType<InteractionResultHolder<ItemStack>> USE_ITEM_ON_AIR = new FlexEventType<>("use_item");
    public static final FlexEventType<InteractionResultHolder<ItemStack>> USE_ITEM_ON_BLOCK = new FlexEventType<>("use_item_on");
    public static final FlexEventType<InteractionResultHolder<ItemStack>> BEGIN_USING_ITEM = new FlexEventType<>("begin_using");
    public static final FlexEventType<Void> STOPPED_USING = new FlexEventType<>("stopped_using");
    public static final FlexEventType<InteractionResultHolder<ItemStack>> END_USING = new FlexEventType<>("end_using");
    public static final FlexEventType<ItemStack> UPDATE = new FlexEventType<>("update");

    // Enchantments
    public static final FlexEventType<Void> POST_ATTACK = new FlexEventType<>("post_attack");
    public static final FlexEventType<Void> POST_HURT = new FlexEventType<>("post_hurt");

    // Fluid
    public static final FlexEventType<InteractionResult> BEFORE_DESTROY = new FlexEventType<>("before_destroy");

    public FlexEventType(String name)
    {
        this.name = name;
        eventTypes.put(name, this);
    }

    public static FlexEventType<?> byName(String name)
    {
        return eventTypes.get(name);
    }
}

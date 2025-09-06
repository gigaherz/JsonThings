package dev.gigaherz.jsonthings.things.events;

import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.block.Block;

import java.util.Map;
import java.util.Set;

public class FlexEventContext
{
    public static final ContextValue<ItemStack> STACK = ContextValue.create("stack", ItemStack.class);
    public static final ContextValue<LivingEntity> USER = ContextValue.create("user", LivingEntity.class);
    public static final ContextValue<Level> WORLD = ContextValue.create("world", Level.class);
    public static final ContextValue<InteractionHand> HAND = ContextValue.create("hand", InteractionHand.class);
    public static final ContextValue<HitResult> RAYTRACE_RESULT = ContextValue.create("raytraceResult", HitResult.class);
    public static final ContextValue<BlockPos> HIT_POS = ContextValue.create("hitPos", BlockPos.class);
    public static final ContextValue<Direction> HIT_FACE = ContextValue.create("hitFace", Direction.class);
    public static final ContextValue<Vec3> HIT_VEC = ContextValue.create("hitVec", Vec3.class);
    public static final ContextValue<Boolean> HIT_INSIDE = ContextValue.create("hitInside", Boolean.class);
    public static final ContextValue<Entity> HIT_ENTITY = ContextValue.create("hitEntity", Entity.class);
    public static final ContextValue<Integer> SLOT = ContextValue.create("slot", Integer.class);
    public static final ContextValue<Boolean> SELECTED = ContextValue.create("selected", Boolean.class);
    public static final ContextValue<Entity> OTHER_USER = ContextValue.create("other_user", Entity.class);
    public static final ContextValue<Integer> TIME_LEFT = ContextValue.create("timeLeft", Integer.class);
    public static final ContextValue<BlockPos> BLOCK_POS = ContextValue.create("blockPos", BlockPos.class);
    public static final ContextValue<BlockState> BLOCK_STATE = ContextValue.create("blockState", BlockState.class);
    public static final ContextValue<Entity> ATTACKER = ContextValue.create("attacker", Entity.class);
    public static final ContextValue<Entity> TARGET = ContextValue.create("target", Entity.class);
    public static final ContextValue<Enchantment> ENCHANTMENT = ContextValue.create("enchantment", Enchantment.class);
    public static final ContextValue<Integer> ENCHANTMENT_LEVEL = ContextValue.create("enchantment_level", Integer.class);
    public static final ContextValue<UseOnContext> USE_CONTEXT = ContextValue.create("use_context", UseOnContext.class);
    public static final ContextValue<StateDefinition> STATE_DEFINITION = ContextValue.create("state_definition", StateDefinition.class);

    private final Map<ContextValue<?>, Object> parameters = Maps.newHashMap();

    public FlexEventContext()
    {
    }

    public ItemStack getStack()
    {
        return get(STACK);
    }

    public <T> FlexEventContext with(ContextValue<T> key, T value)
    {
        parameters.put(key, value);
        return this;
    }

    public FlexEventContext withRayTrace(HitResult rayTraceResult)
    {
        if (rayTraceResult.getType() == HitResult.Type.ENTITY)
            return withRayTrace((EntityHitResult) rayTraceResult);
        else if (rayTraceResult.getType() == HitResult.Type.BLOCK)
            return withRayTrace((BlockHitResult) rayTraceResult);

        return this.with(RAYTRACE_RESULT, rayTraceResult).with(HIT_VEC, rayTraceResult.getLocation());
    }

    public FlexEventContext withRayTrace(BlockHitResult rayTraceResult)
    {
        return this
                .with(RAYTRACE_RESULT, rayTraceResult)
                .with(HIT_POS, rayTraceResult.getBlockPos())
                .with(HIT_FACE, rayTraceResult.getDirection())
                .with(HIT_VEC, rayTraceResult.getLocation())
                .with(HIT_INSIDE, rayTraceResult.isInside());
    }

    public FlexEventContext withRayTrace(EntityHitResult rayTraceResult)
    {
        return this
                .with(RAYTRACE_RESULT, rayTraceResult)
                .with(HIT_ENTITY, rayTraceResult.getEntity())
                .with(HIT_VEC, rayTraceResult.getLocation());
    }

    public FlexEventContext withHand(Player player, InteractionHand hand)
    {
        ItemStack held = player.getItemInHand(hand);
        return this.with(USER, player).with(HAND, hand).with(STACK, held);
    }

    public <T> boolean has(ContextValue<T> key)
    {
        return parameters.containsKey(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(ContextValue<T> key)
    {
        return (T) parameters.get(key);
    }

    public Set<ContextValue<?>> keySet()
    {
        return parameters.keySet();
    }

    public static FlexEventContext of(UseOnContext ctx)
    {
        FlexEventContext eventContext = new FlexEventContext()
                .with(STACK, ctx.getItemInHand())
                .with(WORLD, ctx.getLevel())
                .with(HAND, ctx.getHand())
                .with(HIT_POS, ctx.getClickedPos())
                .with(HIT_FACE, ctx.getClickedFace())
                .with(HIT_VEC, ctx.getClickLocation())
                .with(HIT_INSIDE, ctx.isInside());
        Player player = ctx.getPlayer();
        if (player != null) eventContext.with(USER, player);
        return eventContext;
    }

    public static FlexEventContext of(ItemStack stack)
    {
        return new FlexEventContext().with(STACK, stack);
    }

    public static FlexEventContext of(Level world, LivingEntity user, ItemStack stack)
    {
        return new FlexEventContext()
                .with(STACK, stack)
                .with(WORLD, world)
                .with(USER, user);
    }

    public static FlexEventContext of(Level world, LivingEntity user, InteractionHand hand, ItemStack stack)
    {
        return new FlexEventContext()
                .with(STACK, stack)
                .with(WORLD, world)
                .with(USER, user)
                .with(HAND, hand);
    }

    public static FlexEventContext of(Level world, Entity entity, ItemStack stack)
    {
        if (entity instanceof LivingEntity)
            return new FlexEventContext().with(STACK, stack).with(WORLD, world).with(USER, (LivingEntity) entity);
        else
            return new FlexEventContext().with(STACK, stack).with(WORLD, world).with(OTHER_USER, entity);
    }

    public static FlexEventContext of(Level world, BlockPos pos, BlockState state)
    {
        return new FlexEventContext().with(WORLD, world).with(BLOCK_POS, pos).with(BLOCK_STATE, state);
    }

    public static FlexEventContext of(Enchantment enchantment, int level)
    {
        return new FlexEventContext().with(ENCHANTMENT, enchantment).with(ENCHANTMENT_LEVEL, level);
    }
}

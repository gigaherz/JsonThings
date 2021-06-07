package gigaherz.jsonthings.things.events;

import com.google.common.collect.Maps;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.Map;

public class FlexEventContext
{
    public static final ContextValue<ItemStack> STACK = ContextValue.create("stack", ItemStack.class);
    public static final ContextValue<LivingEntity> USER = ContextValue.create("user", LivingEntity.class);
    public static final ContextValue<World> WORLD = ContextValue.create("world", World.class);
    public static final ContextValue<Hand> HAND = ContextValue.create("hand", Hand.class);
    public static final ContextValue<RayTraceResult> RAYTRACE_RESULT = ContextValue.create("raytraceResult", RayTraceResult.class);
    public static final ContextValue<BlockPos> HIT_POS = ContextValue.create("hitPos", BlockPos.class);
    public static final ContextValue<Direction> HIT_FACE = ContextValue.create("hitFace", Direction.class);
    public static final ContextValue<Vector3d> HIT_VEC = ContextValue.create("hitVec", Vector3d.class);
    public static final ContextValue<Boolean> HIT_INSIDE = ContextValue.create("hitInside", Boolean.class);
    public static final ContextValue<Entity> HIT_ENTITY = ContextValue.create("hitEntity", Entity.class);
    public static final ContextValue<Integer> SLOT = ContextValue.create("slot", Integer.class);
    public static final ContextValue<Boolean> SELECTED = ContextValue.create("selected", Boolean.class);
    public static final ContextValue<Entity> OTHER_USER = ContextValue.create("user", Entity.class);
    public static final ContextValue<Integer> TIME_LEFT = ContextValue.create("timeLeft", Integer.class);
    public static final ContextValue<BlockPos> BLOCK_POS = ContextValue.create("blockPos", BlockPos.class);
    public static final ContextValue<BlockState> BLOCK_STATE = ContextValue.create("blockState", BlockState.class);

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

    public FlexEventContext withRayTrace(RayTraceResult rayTraceResult)
    {
        if (rayTraceResult.getType() == RayTraceResult.Type.ENTITY)
            return withRayTrace((EntityRayTraceResult) rayTraceResult);
        else if (rayTraceResult.getType() == RayTraceResult.Type.BLOCK)
            return withRayTrace((BlockRayTraceResult) rayTraceResult);

        return this.with(RAYTRACE_RESULT, rayTraceResult).with(HIT_VEC, rayTraceResult.getLocation());
    }

    public FlexEventContext withRayTrace(BlockRayTraceResult rayTraceResult)
    {
        return this
            .with(RAYTRACE_RESULT, rayTraceResult)
            .with(HIT_POS, rayTraceResult.getBlockPos())
            .with(HIT_FACE, rayTraceResult.getDirection())
            .with(HIT_VEC, rayTraceResult.getLocation())
            .with(HIT_INSIDE, rayTraceResult.isInside());
    }

    public FlexEventContext withRayTrace(EntityRayTraceResult rayTraceResult)
    {
        return this
                .with(RAYTRACE_RESULT, rayTraceResult)
                .with(HIT_ENTITY, rayTraceResult.getEntity())
                .with(HIT_VEC, rayTraceResult.getLocation());
    }

    public FlexEventContext withHand(PlayerEntity player, Hand hand)
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

    public static FlexEventContext of(ItemUseContext ctx)
    {
        FlexEventContext eventContext = new FlexEventContext()
                .with(STACK, ctx.getItemInHand())
                .with(WORLD, ctx.getLevel())
                .with(HAND, ctx.getHand())
                .with(HIT_POS, ctx.getClickedPos())
                .with(HIT_FACE, ctx.getClickedFace())
                .with(HIT_VEC, ctx.getClickLocation())
                .with(HIT_INSIDE, ctx.isInside());
        PlayerEntity player = ctx.getPlayer();
        if (player != null) eventContext.with(USER, player);
        return eventContext;
    }

    public static FlexEventContext of(ItemStack stack)
    {
        return new FlexEventContext().with(STACK, stack);
    }

    public static FlexEventContext of(World world, LivingEntity user, ItemStack stack)
    {
        return new FlexEventContext()
                .with(STACK, stack)
                .with(WORLD, world)
                .with(USER, user);
    }

    public static FlexEventContext of(World world, LivingEntity user, Hand hand, ItemStack stack)
    {
        return new FlexEventContext()
                .with(STACK, stack)
                .with(WORLD, world)
                .with(USER, user)
                .with(HAND, hand);
    }

    public static FlexEventContext of(World world, Entity entity, ItemStack stack)
    {
        if (entity instanceof LivingEntity)
            return new FlexEventContext().with(STACK, stack).with(WORLD, world).with(USER, (LivingEntity) entity);
        else
            return new FlexEventContext().with(STACK, stack).with(WORLD, world).with(OTHER_USER, entity);
    }

    public static FlexEventContext of(World world, BlockPos pos, BlockState state)
    {
        return new FlexEventContext().with(WORLD, world).with(BLOCK_POS, pos).with(BLOCK_STATE, state);
    }
}

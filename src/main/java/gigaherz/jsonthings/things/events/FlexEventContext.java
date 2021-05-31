package gigaherz.jsonthings.things.events;

import com.google.common.collect.Maps;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.Map;

public class FlexEventContext
{

    public static final ContextValue<LivingEntity> USER = ContextValue.create("user", LivingEntity.class);
    public static final ContextValue<World> WORLD = ContextValue.create("world", World.class);
    public static final ContextValue<Hand> HAND = ContextValue.create("hand", Hand.class);
    public static final ContextValue<RayTraceResult> RAYTRACE_RESULT = ContextValue.create("raytraceResult", RayTraceResult.class);
    public static final ContextValue<BlockPos> HIT_POS = ContextValue.create("hitPos", BlockPos.class);
    public static final ContextValue<Direction> HIT_FACE = ContextValue.create("hitFace", Direction.class);
    public static final ContextValue<Vector3d> HIT_VEC = ContextValue.create("hitVec", Vector3d.class);
    public static final ContextValue<Boolean> HIT_INSIDE = ContextValue.create("hitInside", Boolean.class);
    public static final ContextValue<Integer> SLOT = ContextValue.create("slot", Integer.class);
    public static final ContextValue<Boolean> SELECTED = ContextValue.create("selected", Boolean.class);
    public static final ContextValue<Entity> OTHER_USER = ContextValue.create("user", Entity.class);
    public static final ContextValue<Integer> TIME_LEFT = ContextValue.create("timeLeft", Integer.class);

    private final ItemStack stack;
    private final Map<ContextValue<?>, Object> extra = Maps.newHashMap();

    public FlexEventContext(ItemStack stack)
    {
        this.stack = stack;
    }

    public ItemStack getStack()
    {
        return stack;
    }

    public <T> FlexEventContext with(ContextValue<T> key, T value)
    {
        extra.put(key, value);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(ContextValue<T> key)
    {
        return (T) extra.get(key);
    }

    public static FlexEventContext of(ItemUseContext ctx)
    {
        return new FlexEventContext(ctx.getItem())
                .with(WORLD, ctx.getWorld())
                .with(USER, ctx.getPlayer())
                .with(HAND, ctx.getHand())
                .with(HIT_POS, ctx.getPos())
                .with(HIT_FACE, ctx.getFace())
                .with(HIT_VEC, ctx.getHitVec())
                .with(HIT_INSIDE, ctx.isInside());
    }

    public static FlexEventContext of(ItemStack stack)
    {
        return new FlexEventContext(stack);
    }

    public static FlexEventContext of(World world, LivingEntity user, ItemStack stack)
    {
        return new FlexEventContext(stack)
                .with(WORLD, world)
                .with(USER, user);
    }

    public static FlexEventContext of(World world, LivingEntity user, Hand hand, ItemStack stack)
    {
        return new FlexEventContext(stack)
                .with(WORLD, world)
                .with(USER, user)
                .with(HAND, hand);
    }

    public static FlexEventContext of(World world, Entity entity, ItemStack stack)
    {
        if (entity instanceof LivingEntity)
            return new FlexEventContext(stack).with(WORLD, world).with(USER, (LivingEntity) entity);
        else
            return new FlexEventContext(stack).with(WORLD, world).with(OTHER_USER, entity);
    }
}

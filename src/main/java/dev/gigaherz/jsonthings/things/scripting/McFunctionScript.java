package dev.gigaherz.jsonthings.things.scripting;

import java.util.ArrayList;
import java.util.Map;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import dev.gigaherz.jsonthings.things.events.FlexEventContext;
import static dev.gigaherz.jsonthings.things.events.FlexEventContext.*;
import dev.gigaherz.jsonthings.things.events.FlexEventType;
import dev.gigaherz.jsonthings.things.scripting.client.IClientLogic;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class McFunctionScript extends ThingScript {
    public final String function;
    public final Boolean debug;
    public static final String NO_TARGET = "@s[distance=1]"; // an empty target-selector
    public static final String TRUE = "predicate []";
    public static final String FALSE = "predicate {condition:value_check,value:1,range:0}";

    public final IClientLogic clientLogic;

    public static final Logger LOGGER = LogUtils.getLogger();

    public McFunctionScript(String string, Boolean debug, IClientLogic logic) {
        this.function = string;
        this.debug = debug;
        this.clientLogic = logic;
    }

    public static InteractionResult getResult(int i) {
        switch (i) {
            case -1:
                return null;
            case 0:
                return InteractionResult.PASS;
            case 1:
                return InteractionResult.SUCCESS;
            case 2:
                return InteractionResult.FAIL;
            case 3:
                return InteractionResult.CONSUME;
            default:
                return InteractionResult.PASS;
        }
    }

    public static ItemInteractionResult getItemResult(int i) {
        switch (i) {
            case -1:
                return null;
            case 0:
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            case 1:
                return ItemInteractionResult.SUCCESS;
            case 2:
                return ItemInteractionResult.FAIL;
            case 3:
                return ItemInteractionResult.CONSUME;
            default:
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
    }

    public static Object getDefaultByEventType(FlexEventType event, FlexEventContext context) {
        if (event == FlexEventType.USE_BLOCK_WITH_ITEM)
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (event == FlexEventType.USE_BLOCK_WITHOUT_ITEM || event == FlexEventType.BEFORE_DESTROY)
            return InteractionResult.PASS;
        if (event == FlexEventType.USE_ITEM_ON_AIR || event == FlexEventType.USE_ITEM_ON_BLOCK
                || event == FlexEventType.BEGIN_USING_ITEM || event == FlexEventType.END_USING)
            return new InteractionResultHolder<ItemStack>(InteractionResult.PASS,
                    context.get(FlexEventContext.STACK));
        if (event == FlexEventType.UPDATE)
            return context.get(FlexEventContext.STACK);
        return null;
    }

    public static Object getResultByEventType(FlexEventType event, FlexEventContext context, Object o) {
        if (event == FlexEventType.USE_BLOCK_WITH_ITEM) {
            if (o instanceof Integer r)
                return getItemResult(r);
        }
        if (event == FlexEventType.USE_BLOCK_WITHOUT_ITEM || event == FlexEventType.BEFORE_DESTROY) {
            if (o instanceof Integer r)
                return getResult(r);
        }
        if (event == FlexEventType.USE_ITEM_ON_AIR || event == FlexEventType.USE_ITEM_ON_BLOCK
                || event == FlexEventType.BEGIN_USING_ITEM || event == FlexEventType.END_USING)
            return new InteractionResultHolder<ItemStack>(
                    (InteractionResult) getResultByEventType(FlexEventType.USE_BLOCK_WITHOUT_ITEM, context, o),
                    context.get(FlexEventContext.STACK));
        if (event == FlexEventType.UPDATE) {
            Player user = ((Player) context.get(FlexEventContext.USER));
            InteractionHand hand = context.get(FlexEventContext.HAND);
            if (hand == InteractionHand.OFF_HAND) {
                return user.getOffhandItem();
            }
            return user.getMainHandItem();
        }

        return getDefaultByEventType(event, context);
    }

    public static <T> T orElse(T a, T b) {
        return a != null ? a : b;
    }

    @Override
    public Object apply(FlexEventType event, FlexEventContext context) {
        try {
            Level world = context.get(WORLD); // Nonnull
            LOGGER.debug("Executing mcfunction script: {}, client: {}", function,
                    world.isClientSide ? "true" : "false");
            if (world.isClientSide) { // User can ONLY be clientplayer
                Object result = clientLogic.getResult(event, context);
                LOGGER.debug("ClientLogic result: {}", result.toString());
                return result;
            }
            if (world instanceof ServerLevel level) {
                // Try to pass in all the *useless* args
                ItemStack stack = context.get(STACK);
                String Item = stack == null ? "minecraft:air" : stack.getItem().toString();
                int Count = stack == null ? 0 : stack.getCount();
                LivingEntity user = context.get(USER);
                String UserUUID = user == null ? NO_TARGET : user.getUUID().toString();
                String Sneaking = user != null ? user.isShiftKeyDown() ? TRUE : FALSE : FALSE;
                InteractionHand hand = context.get(HAND);
                String HandSlot = hand == InteractionHand.OFF_HAND ? "weapon.offhand" : "weapon.mainhand";
                HitResult rayTraceResult = context.get(RAYTRACE_RESULT);
                Vec3 RayPos = rayTraceResult == null ? new Vec3(0, 0, 0) : rayTraceResult.getLocation();
                BlockPos hitPos = context.get(HIT_POS);
                Vec3 HitPos = hitPos == null ? new Vec3(0, 0, 0)
                        : new Vec3(hitPos.getX(), hitPos.getY(), hitPos.getZ());
                Direction hitFace = context.get(HIT_FACE);
                String HitFace = hitFace == null ? "east" : hitFace.name().toLowerCase();
                Vec3 HitVec = orElse(context.get(HIT_VEC), new Vec3(0, 0, 0));
                Boolean hitInside = context.get(HIT_INSIDE);
                String HitInside = hitInside == null ? FALSE : hitInside == true ? TRUE : FALSE;
                Entity hitEntity = context.get(HIT_ENTITY);
                String HitEntityUUID = hitEntity == null ? NO_TARGET : hitEntity.getUUID().toString();
                int Slot = orElse(context.get(SLOT), 0);
                Boolean selected = context.get(SELECTED);
                String Selected = selected == null ? FALSE : selected == true ? TRUE : FALSE;
                Entity otherUser = context.get(OTHER_USER);
                String OtherUserUUID = otherUser == null ? NO_TARGET : otherUser.getUUID().toString();
                int TimeLeft = orElse(context.get(TIME_LEFT), 0);
                BlockPos blockPos = context.get(BLOCK_POS);
                BlockPos BlockPos = blockPos == null ? new BlockPos(0, 0, 0)
                        : new BlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                BlockState blockState = context.get(BLOCK_STATE);
                String Block = blockState == null ? "minecraft:air"
                        : BuiltInRegistries.BLOCK.wrapAsHolder(blockState.getBlock()).getRegisteredName();
                String States = "";
                if (blockState != null) { // OK now I know
                    StringBuilder sb = new StringBuilder();
                    Map<Property<?>, Comparable<?>> properties = blockState.getValues();
                    properties.forEach(
                            (key, val) -> sb.append(String.format(",State_%s:%s", key.getName(), val.toString())));
                    States = sb.toString();
                }

                Entity attacker = context.get(ATTACKER);
                String AttackerUUID = attacker == null ? NO_TARGET : attacker.getUUID().toString();
                Entity target = context.get(TARGET);
                String TargetUUID = target == null ? NO_TARGET : target.getUUID().toString();
                // Abandon Enchantments
                Vec3 pos;
                if (blockPos != null) {
                    pos = new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                } else {
                    pos = new Vec3(user.getX(), user.getY(), user.getZ());
                }
                String args = String.format(
                        "{Item:\"%s\",Count:%d,User:\"%s\",Hand:\"%s\",RayX:%f,RayY:%f,RayZ:%f,HitX:%f,HitY:%f,HitZ:%f,HitFace:\"%s\",HitVX:%f,HitVY:%f,HitVZ:%f,HitInside:\"%s\",HitEntity:\"%s\",Slot:%d,Selected:\"%s\",OtherUser:\"%s\",TimeLeft:%d,BlockX:%d,BlockY:%d,BlockZ:%d,Attacker:\"%s\",Target:\"%s\",Block:\"%s\",Sneaking:\"%s\"%s}",
                        Item, Count, UserUUID, HandSlot, RayPos.x, RayPos.y, RayPos.z, HitPos.x, HitPos.y, HitPos.z,
                        HitFace, HitVec.x, HitVec.y, HitVec.z, HitInside, HitEntityUUID, Slot, Selected, OtherUserUUID,
                        TimeLeft, BlockPos.getX(), BlockPos.getY(), BlockPos.getZ(), AttackerUUID, TargetUUID, Block,
                        Sneaking,States);
                LOGGER.debug(event.toString());
                MinecraftServer server = level.getServer();
                if (server != null) {
                    ArrayList<Component> resultComponents = new ArrayList<>();
                    server.getCommands().performPrefixedCommand(new CommandSourceStack(new CommandSource() {
                        @Override
                        public void sendSystemMessage(Component message) {
                            if (debug)
                                server.getPlayerList().broadcastSystemMessage(message, true);
                            // first is start info and last is the result
                            resultComponents.add(message);
                        }

                        @Override
                        public boolean acceptsSuccess() {
                            return true;
                        }

                        @Override
                        public boolean acceptsFailure() {
                            return true;
                        }

                        @Override
                        public boolean shouldInformAdmins() {
                            return false;
                        }
                    }, pos, Vec2.ZERO, level, 4, "", Component.literal(""),
                            server, user),
                            "function " + function + " " + args);
                    try {
                        Object result = null;
                        for (Component component : resultComponents) {
                            LOGGER.debug("Function {} ends with messages {}", function, component);
                        }
                        ComponentContents message = resultComponents.getLast().getContents();
                        if (message instanceof TranslatableContents tcontents) {
                            Object[] results = tcontents.getArgs();
                            result = (int) results[results.length - 1];
                        }

                        result = getResultByEventType(event, context, result);
                        LOGGER.debug(result.toString());
                        return result;
                    } catch (Exception e) {
                        LOGGER.error("Error processing function result: {} Check your function returning please.");
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error executing mcfunction script: {}", function, e);
        }
        LOGGER.debug("Function {} fall to default result", function);
        return getDefaultByEventType(event, context);
    }
}

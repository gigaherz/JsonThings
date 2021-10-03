package dev.gigaherz.jsonthings.things.items;

import com.google.common.collect.*;
import com.google.common.io.Files;
import cpw.mods.modlauncher.api.INameMappingService;
import dev.gigaherz.jsonthings.things.CompletionMode;
import dev.gigaherz.jsonthings.things.IFlexItem;
import dev.gigaherz.jsonthings.things.StackContext;
import dev.gigaherz.jsonthings.things.events.FlexEventContext;
import dev.gigaherz.jsonthings.things.events.FlexEventHandler;
import dev.gigaherz.jsonthings.util.Utils;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.NamingStrategy;
import net.bytebuddy.description.NamedElement;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.modifier.FieldManifestation;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.Implementation.Composable;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.implementation.bind.annotation.FieldValue;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.implementation.bind.annotation.This;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.implementation.bytecode.assign.TypeCasting;
import net.bytebuddy.implementation.bytecode.assign.primitive.PrimitiveBoxingDelegate;
import net.bytebuddy.implementation.bytecode.assign.primitive.PrimitiveUnboxingDelegate;
import net.bytebuddy.implementation.bytecode.assign.primitive.PrimitiveWideningDelegate;
import net.bytebuddy.implementation.bytecode.assign.primitive.VoidAwareAssigner;
import net.bytebuddy.matcher.ElementMatcher;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.extensions.IForgeItem;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static net.bytebuddy.implementation.MethodCall.invoke;
import static net.bytebuddy.implementation.MethodDelegation.to;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

/*
WIP unused code
 */
public class MakeFlexItem
{
    public static boolean debugDumpSubclasses = true;

    public static final Map<Class<? extends Item>, Class<? extends Item>> flexSubclasses = new IdentityHashMap<>();

    private static ByteBuddy byteBuddy = new ByteBuddy()
            .with(new NamingStrategy.AbstractBase()
            {
                private final String prefix = MakeFlexItem.class.getPackage().getName();

                @Override
                protected String name(TypeDescription superClass)
                {
                    return prefix + ".flex." + superClass.getName();
                }
            });

    /*
    setUseAction
    getUseAction
    setUseTime
    getUseTime
    setUseFinishMode
    getUseFinishMode
    addEventHandler
    getEventHandler
    addCreativeStack
    addAttributeModifier
     */
    public static <T extends Item> Class<? extends T> makeFlex(Class<T> clsIn)
    {
        List<Constructor<?>> constructors = Arrays.stream(clsIn.getDeclaredConstructors())
                .filter(cns -> Modifier.isPublic(cns.getModifiers()))
                .collect(Collectors.toList());
        //noinspection unchecked
        return (Class<? extends T>) flexSubclasses.computeIfAbsent(clsIn, cls -> {
            DynamicType.Builder<T> built = byteBuddy
                    .subclass(clsIn, ConstructorStrategy.Default.IMITATE_SUPER_CLASS_PUBLIC)
                    .implement(IFlexItem.class)
                    .implement(IForgeItem.class)
                    .defineField("perTabStacks", Multimap.class, Visibility.PRIVATE, FieldManifestation.FINAL)
                    .defineField("searchTabStacks", List.class, Visibility.PRIVATE, FieldManifestation.FINAL)
                    .defineField("tooltipStrings", List.class, Visibility.PRIVATE, FieldManifestation.FINAL)
                    .defineField("attributeModifiers", Map.class, Visibility.PRIVATE, FieldManifestation.FINAL)
                    .defineField("eventHandlers", Map.class, Visibility.PRIVATE, FieldManifestation.FINAL)
                    .defineField("useAction", UseAnim.class, Visibility.PRIVATE)
                    .defineField("useTime", int.class, Visibility.PRIVATE)
                    .defineField("useFinishMode", CompletionMode.class, Visibility.PRIVATE)
                    .defineField("containerResult", InteractionResultHolder.class, Visibility.PRIVATE)
                    .defineMethod("initializeFlex", void.class, Visibility.PRIVATE).intercept(toExactly(FlexInitialize.class))
                    .method(named("setUseAction")).intercept(FieldAccessor.ofBeanProperty())
                    .method(named("getUseAction")).intercept(FieldAccessor.ofBeanProperty())
                    .method(named("setUseTime")).intercept(FieldAccessor.ofBeanProperty())
                    .method(named("getUseTime")).intercept(FieldAccessor.ofBeanProperty())
                    .method(named("setUseFinishMode")).intercept(FieldAccessor.ofBeanProperty())
                    .method(named("getUseFinishMode")).intercept(FieldAccessor.ofBeanProperty())
                    .method(named("addEventHandler")).intercept(toExactly(FlexItemImplementation.class))
                    .method(named("getEventHandler")).intercept(toExactly(FlexItemImplementation.class))
                    .method(named("addCreativeStack")).intercept(toExactly(FlexItemImplementation.class))
                    .method(mapped("useOn", "useOn")).intercept(toExactly(ItemOverrides.class))
                    .method(mapped("releaseUsing", "releaseUsing")).intercept(toExactly(ItemOverrides.class))
                    .method(mapped("finishUsingItem", "finishUsingItem")).intercept(toExactly(ItemOverrides.class))
                    .method(mapped("use", "use")).intercept(toExactly(ItemOverrides.class))
                    .method(mapped("appendHoverText", "appendHoverText")).intercept(toExactly(ItemOverrides.class))
                    .method(mapped("fillItemCategory", "fillItemCategory")).intercept(toExactly(ItemOverrides.class))
                    .method(mapped("inventoryTick", "inventoryTick")).intercept(toExactly(ItemOverrides.class))
                    .method(named("getAttributeModifiers")).intercept(toExactly(ItemOverrides.class));
            for (Constructor<?> c : constructors)
            {
                try
                {
                    built = built
                            .constructor(takesArguments(c.getParameterTypes()))
                            .intercept(
                                    SuperMethodCall.INSTANCE
                                            .andThen(assignField(named("perTabStacks"), invoke(ArrayListMultimap.class.getMethod("create"))))
                                            .andThen(assignField(named("searchTabStacks"), invoke(Lists.class.getMethod("newArrayList"))))
                                            .andThen(assignField(named("tooltipStrings"), invoke(Lists.class.getMethod("newArrayList"))))
                                            .andThen(assignField(named("attributeModifiers"), invoke(Maps.class.getMethod("newHashMap"))))
                                            .andThen(assignField(named("eventHandlers"), invoke(Maps.class.getMethod("newHashMap"))))
                                            .andThen(invoke(named("initializeFlex")))
                            );
                }
                catch (NoSuchMethodException e)
                {
                    e.printStackTrace();
                }
            }
            DynamicType.Unloaded<T> make = built.make();

            if (debugDumpSubclasses)
            {
                byte[] bytes = make.getBytes();
                Path debugDump = FMLPaths.GAMEDIR.get().resolve("debug");
                debugDump.toFile().mkdirs();
                try
                {
                    Files.write(bytes, debugDump.resolve(make.getTypeDescription().getName() + ".class").toFile());
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }

            return make.load(cls.getClassLoader()).getLoaded();
        });
    }

    private static Composable assignField(ElementMatcher<? super FieldDescription> field, MethodCall from)
    {
        return from.setsField(field);
    }

    private static final Assigner CUSTOM_ASSIGNER = new VoidAwareAssigner(new Assigner()
    {
        @Override
        public StackManipulation assign(TypeDescription.Generic source, TypeDescription.Generic target, Typing typing)
        {
            if (source.isEnum() != target.isEnum())
                return StackManipulation.Illegal.INSTANCE;

            if (source.equals(target))
            {
                return StackManipulation.Trivial.INSTANCE;
            }

            if (source.isPrimitive() && target.isPrimitive())
            {
                return PrimitiveWideningDelegate.forPrimitive(source).widenTo(target);
            }

            if (source.isPrimitive())
            {
                return PrimitiveBoxingDelegate.forPrimitive(source).assignBoxedTo(target, this, typing);
            }

            if (target.isPrimitive())
            {
                return PrimitiveUnboxingDelegate.forReferenceType(source).assignUnboxedTo(target, this, typing);
            }

            if (source.asErasure().isAssignableTo(target.asErasure()))
            {
                return StackManipulation.Trivial.INSTANCE;
            }

            if (typing.isDynamic())
            {
                return TypeCasting.to(target);
            }

            return StackManipulation.Illegal.INSTANCE;
        }
    });

    private static Composable toExactly(Class<?> _class)
    {
        return to(_class).withAssigner(CUSTOM_ASSIGNER);
    }

    private static <T extends NamedElement> ElementMatcher.Junction<T> mapped(String methodName, String prettyName)
    {
        return named(ObfuscationReflectionHelper.remapName(INameMappingService.Domain.METHOD, methodName));
    }

    public static class FlexInitialize
    {
        public static void initializeFlex(@This IForgeItem self,
                                          @FieldValue("attributeModifiers") Map<EquipmentSlot, Multimap<Attribute, AttributeModifier>> attributeModifiers)
        {
            for (EquipmentSlot slot1 : EquipmentSlot.values())
            {
                Multimap<Attribute, AttributeModifier> multimap = ArrayListMultimap.create();
                multimap.putAll(self.getAttributeModifiers(EquipmentSlot.CHEST, ItemStack.EMPTY));
                attributeModifiers.put(slot1, multimap);
            }
        }
    }

    public static class FlexItemImplementation
    {
        public static void addEventHandler(String eventName, FlexEventHandler<InteractionResultHolder<ItemStack>> eventHandler,
                                           @FieldValue("eventHandlers") Map<String, FlexEventHandler<InteractionResultHolder<ItemStack>>> eventHandlers)
        {
            eventHandlers.put(eventName, eventHandler);
        }

        public static FlexEventHandler<InteractionResultHolder<ItemStack>> getEventHandler(String eventName,
                                                                                           @FieldValue("eventHandlers") Map<String, FlexEventHandler<InteractionResultHolder<ItemStack>>> eventHandlers)
        {
            return eventHandlers.get(eventName);
        }

        public static void addCreativeStack(StackContext stack, Iterable<CreativeModeTab> tabs,
                                            @FieldValue("perTabStacks") Multimap<CreativeModeTab, StackContext> perTabStacks,
                                            @FieldValue("searchTabStacks") List<StackContext> searchTabStacks)
        {
            for (CreativeModeTab tab : tabs)
            {
                perTabStacks.put(tab, stack);
            }
            searchTabStacks.add(stack);
        }

        public static void addAttributeModifier(@Nullable EquipmentSlot slot, Attribute attribute, AttributeModifier modifier,
                                                @FieldValue("attributeModifiers") Map<EquipmentSlot, Multimap<Attribute, AttributeModifier>> attributeModifiers)
        {
            if (slot != null)
            {
                attributeModifiers.get(slot).put(attribute, modifier);
            }
            else
            {
                for (EquipmentSlot slot1 : EquipmentSlot.values())
                {attributeModifiers.get(slot1).put(attribute, modifier);}
            }
        }
    }

    public static class ItemOverrides
    {
        public static InteractionResult useOn(UseOnContext context, @This IFlexItem self, @SuperCall Callable<InteractionResult> doSuper) throws Exception
        {
            ItemStack heldItem = context.getItemInHand();

            InteractionResultHolder<ItemStack> result = self.runEventThrowing("use_on_block", FlexEventContext.of(context), () -> new InteractionResultHolder<>(doSuper.call(), heldItem));

            if (result.getObject() != heldItem)
            {
                context.getPlayer().setItemInHand(context.getHand(), result.getObject());
            }

            return result.getResult();
        }

        public static void releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft, @This IFlexItem self, @SuperCall Runnable doSuper)
        {
            self.runEvent("stopped_using",
                    FlexEventContext.of(worldIn, entityLiving, stack).with(FlexEventContext.TIME_LEFT, timeLeft),
                    () -> {
                        doSuper.run();
                        return new InteractionResultHolder<>(InteractionResult.PASS, stack);
                    });
        }

        public static ItemStack finishUsingItem(ItemStack heldItem, Level worldIn, LivingEntity entityLiving, @This IFlexItem self, @SuperCall Callable<ItemStack> doSuper) throws Exception
        {
            Callable<InteractionResultHolder<ItemStack>> resultSupplier = () -> new InteractionResultHolder<>(InteractionResult.SUCCESS, doSuper.call());

            InteractionResultHolder<ItemStack> result = self.runEventThrowing("end_using", FlexEventContext.of(worldIn, entityLiving, heldItem), resultSupplier);
            if (result.getResult() != InteractionResult.SUCCESS)
                return result.getObject();

            return self.runEventThrowing("use", FlexEventContext.of(worldIn, entityLiving, heldItem), resultSupplier).getObject();
        }

        public static InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn,
                                                             @FieldValue("useTime") int useTime, @This IFlexItem self, @SuperCall Callable<InteractionResultHolder<ItemStack>> doSuper) throws Exception
        {
            ItemStack heldItem = playerIn.getItemInHand(handIn);
            if (useTime > 0)
                return self.runEventThrowing("begin_using", FlexEventContext.of(worldIn, playerIn, handIn, heldItem), doSuper);
            else
                return self.runEventThrowing("use_on_air", FlexEventContext.of(worldIn, playerIn, handIn, heldItem), doSuper);
        }

        public static void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn,
                                           @FieldValue("tooltipStrings") List<Component> tooltipStrings,
                                           @SuperCall Runnable doSuper)
        {
            doSuper.run();
            tooltip.addAll(tooltipStrings);
        }

        public static void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> items,
                                            @This Item self,
                                            @FieldValue("perTabStacks") Multimap<CreativeModeTab, StackContext> perTabStacks,
                                            @FieldValue("searchTabStacks") List<StackContext> searchTabStacks)
        {
            if (tab == CreativeModeTab.TAB_SEARCH)
            {
                items.addAll(searchTabStacks.stream().map(s -> s.toStack(self)).collect(Collectors.toList()));
            }
            else if (perTabStacks.containsKey(tab))
            {
                items.addAll(perTabStacks.get(tab).stream().map(s -> s.toStack(self)).collect(Collectors.toList()));
            }
        }

        public static void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected,
                                         @This IFlexItem self, @SuperCall Runnable doSuper)
        {
            InteractionResultHolder<ItemStack> result = self.runEvent("update",
                    FlexEventContext.of(worldIn, entityIn, stack).with(FlexEventContext.SLOT, itemSlot).with(FlexEventContext.SELECTED, isSelected),
                    () -> {
                        doSuper.run();
                        return new InteractionResultHolder<>(InteractionResult.PASS, stack);
                    });
            if (result.getObject() != stack)
            {
                entityIn.getSlot(itemSlot).set(result.getObject());
            }
        }

        /*
        private static ActionResult<ItemStack> doContainerItem(ItemStack stack, IFlexItem self, BooleanSupplier doHas, Function<ItemStack, ItemStack> doGet)
        {
            return self.runEvent("get_container_item", FlexEventContext.of(stack), () -> {
                ActionResultType typeIn = doHas.getAsBoolean() ? ActionResultType.SUCCESS : ActionResultType.PASS;
                if (typeIn == ActionResultType.SUCCESS)
                    return new ActionResult<>(typeIn, doGet.apply(stack));
                return new ActionResult<>(typeIn, stack);
            });
        }

        public static boolean hasContainerItem(ItemStack stack, @This IFlexItem self, @SuperCall Callable<Boolean> doHas)
        {
            containerResult = doContainerItem(stack);
            return containerResult.getResult() == ActionResultType.SUCCESS;
        }

        public static ItemStack getContainerItem(ItemStack itemStack, @FieldProxy("containerResult") INullableFieldAccessor<ActionResult<ItemStack>> containerResult)
        {
            try
            {
                if (containerResult.get() != null)
                    return containerResult.get().getObject();
                return doContainerItem(itemStack).getObject();
            }
            finally {
                containerResult.set(null);
            }
        }*/

        public static Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot,
                                                                                   ItemStack stack,
                                                                                   @FieldValue("attributeModifiers")
                                                                                           Map<EquipmentSlot, Multimap<Attribute, AttributeModifier>> attributeModifiers)
        {
            return Utils.orElse(attributeModifiers.get(slot), HashMultimap::create);
        }
    }

    public interface INullableFieldAccessor<T>
    {
        @Nullable
        T get();

        void set(@Nullable T value);
    }

    public interface IIntFieldAccessor
    {
        int get();

        void set(int value);
    }

    public interface IFieldAccessor<T>
    {
        T get();

        void set(T value);
    }
}

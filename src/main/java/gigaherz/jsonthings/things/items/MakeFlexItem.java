package gigaherz.jsonthings.things.items;

import com.google.common.collect.*;
import com.google.common.io.Files;
import cpw.mods.modlauncher.api.INameMappingService;
import gigaherz.jsonthings.things.IFlexItem;
import gigaherz.jsonthings.things.builders.CompletionMode;
import gigaherz.jsonthings.things.builders.StackContext;
import gigaherz.jsonthings.things.events.FlexEventContext;
import gigaherz.jsonthings.things.events.ItemEventHandler;
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
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.extensions.IForgeItem;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.loading.FMLPaths;

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

import static net.bytebuddy.implementation.MethodCall.Composable;
import static net.bytebuddy.implementation.MethodCall.invoke;
import static net.bytebuddy.implementation.MethodDelegation.to;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

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
        return (Class<? extends T>)flexSubclasses.computeIfAbsent(clsIn, cls -> {
            DynamicType.Builder<T> built = byteBuddy
                    .subclass(clsIn, ConstructorStrategy.Default.IMITATE_SUPER_CLASS_PUBLIC)
                    .implement(IFlexItem.class)
                    .implement(IForgeItem.class)
                    .defineField("perTabStacks", Multimap.class, Visibility.PRIVATE, FieldManifestation.FINAL)
                    .defineField("searchTabStacks", List.class, Visibility.PRIVATE, FieldManifestation.FINAL)
                    .defineField("tooltipStrings", List.class, Visibility.PRIVATE, FieldManifestation.FINAL)
                    .defineField("attributeModifiers", Map.class, Visibility.PRIVATE, FieldManifestation.FINAL)
                    .defineField("eventHandlers", Map.class, Visibility.PRIVATE, FieldManifestation.FINAL)
                    .defineField("useAction", UseAction.class, Visibility.PRIVATE)
                    .defineField("useTime", int.class, Visibility.PRIVATE)
                    .defineField("useFinishMode", CompletionMode.class, Visibility.PRIVATE)
                    .defineField("containerResult", ActionResult.class, Visibility.PRIVATE)
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
                    .method(mapped("func_195939_a", "useOn")).intercept(toExactly(ItemOverrides.class))
                    .method(mapped("func_77615_a", "releaseUsing")).intercept(toExactly(ItemOverrides.class))
                    .method(mapped("func_77654_b", "finishUsingItem")).intercept(toExactly(ItemOverrides.class))
                    .method(mapped("func_77659_a", "use")).intercept(toExactly(ItemOverrides.class))
                    .method(mapped("func_195967_a", "appendHoverText")).intercept(toExactly(ItemOverrides.class))
                    .method(mapped("func_150895_a", "fillItemCategory")).intercept(toExactly(ItemOverrides.class))
                    .method(mapped("func_77663_a", "inventoryTick")).intercept(toExactly(ItemOverrides.class))
                    .method(named("getAttributeModifiers")).intercept(toExactly(ItemOverrides.class));
            for(Constructor<?> c : constructors)
            {
                try
                {
                    built = built
                            .constructor(takesArguments(c.getParameterTypes()))
                            .intercept(
                                    SuperMethodCall.INSTANCE
                                            .andThen(assignField(named("perTabStacks"),invoke(ArrayListMultimap.class.getMethod("create"))))
                                            .andThen(assignField(named("searchTabStacks"),invoke(Lists.class.getMethod("newArrayList"))))
                                            .andThen(assignField(named("tooltipStrings"),invoke(Lists.class.getMethod("newArrayList"))))
                                            .andThen(assignField(named("attributeModifiers"),invoke(Maps.class.getMethod("newHashMap"))))
                                            .andThen(assignField(named("eventHandlers"),invoke(Maps.class.getMethod("newHashMap"))))
                                            .andThen(invoke(named("initializeFlex")))
                            );
                }
                catch (NoSuchMethodException e)
                {
                    e.printStackTrace();
                }
            }
            DynamicType.Unloaded<T> make = built.make();

            if(debugDumpSubclasses)
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

            if (source.equals(target)) {
                return StackManipulation.Trivial.INSTANCE;
            }

            if (source.isPrimitive() && target.isPrimitive()) {
                return PrimitiveWideningDelegate.forPrimitive(source).widenTo(target);
            }

            if (source.isPrimitive()) {
                return PrimitiveBoxingDelegate.forPrimitive(source).assignBoxedTo(target, this, typing);
            }

            if (target.isPrimitive()) {
                return PrimitiveUnboxingDelegate.forReferenceType(source).assignUnboxedTo(target, this, typing);
            }

            if (source.asErasure().isAssignableTo(target.asErasure())) {
                return StackManipulation.Trivial.INSTANCE;
            }

            if (typing.isDynamic()) {
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
                                          @FieldValue("attributeModifiers") Map<EquipmentSlotType, Multimap<Attribute, AttributeModifier>> attributeModifiers)
        {
            for (EquipmentSlotType slot1 : EquipmentSlotType.values())
            {
                Multimap<Attribute, AttributeModifier> multimap = ArrayListMultimap.create();
                multimap.putAll(self.getAttributeModifiers(EquipmentSlotType.CHEST, ItemStack.EMPTY));
                attributeModifiers.put(slot1, multimap);
            }
        }
    }

    public static class FlexItemImplementation
    {
        public static void addEventHandler(String eventName, ItemEventHandler eventHandler,
                                           @FieldValue("eventHandlers") Map<String, ItemEventHandler> eventHandlers)
        {
            eventHandlers.put(eventName, eventHandler);
        }

        public static ItemEventHandler getEventHandler(String eventName,
                                                       @FieldValue("eventHandlers") Map<String, ItemEventHandler> eventHandlers)
        {
            return eventHandlers.get(eventName);
        }

        public static void addCreativeStack(StackContext stack, Iterable<ItemGroup> tabs,
                                     @FieldValue("perTabStacks") Multimap<ItemGroup, StackContext> perTabStacks,
                                     @FieldValue("searchTabStacks") List<StackContext> searchTabStacks)
        {
            for (ItemGroup tab : tabs)
            {
                perTabStacks.put(tab, stack);
            }
            searchTabStacks.add(stack);
        }

        public static void addAttributeModifier(@Nullable EquipmentSlotType slot, Attribute attribute, AttributeModifier modifier,
                                                @FieldValue("attributeModifiers") Map<EquipmentSlotType, Multimap<Attribute, AttributeModifier>> attributeModifiers)
        {
            if (slot != null)
            {
                attributeModifiers.get(slot).put(attribute, modifier);
            }
            else
            {
                for (EquipmentSlotType slot1 : EquipmentSlotType.values())
                { attributeModifiers.get(slot1).put(attribute, modifier); }
            }
        }
    }

    public static class ItemOverrides
    {
        public static ActionResultType useOn(ItemUseContext context, @This IFlexItem self, @SuperCall Callable<ActionResultType> doSuper) throws Exception
        {
            ItemStack heldItem = context.getItemInHand();

            ActionResult<ItemStack> result = self.runEventThrowing("use_on_block", FlexEventContext.of(context), () -> new ActionResult<>(doSuper.call(), heldItem));

            if (result.getObject() != heldItem)
            {
                context.getPlayer().setItemInHand(context.getHand(), result.getObject());
            }

            return result.getResult();
        }

        public static void releaseUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft, @This IFlexItem self, @SuperCall Runnable doSuper)
        {
            self.runEvent("stopped_using",
                    FlexEventContext.of(worldIn, entityLiving, stack).with(FlexEventContext.TIME_LEFT, timeLeft),
                    () -> {
                        doSuper.run();
                        return new ActionResult<>(ActionResultType.PASS, stack);
                    });
        }

        public static ItemStack finishUsingItem(ItemStack heldItem, World worldIn, LivingEntity entityLiving, @This IFlexItem self, @SuperCall Callable<ItemStack> doSuper) throws Exception
        {
            Callable<ActionResult<ItemStack>> resultSupplier = () -> new ActionResult<>(ActionResultType.SUCCESS, doSuper.call());

            ActionResult<ItemStack> result = self.runEventThrowing("end_using", FlexEventContext.of(worldIn, entityLiving, heldItem), resultSupplier);
            if (result.getResult() != ActionResultType.SUCCESS)
                return result.getObject();

            return self.runEventThrowing("use", FlexEventContext.of(worldIn, entityLiving, heldItem), resultSupplier).getObject();
        }

        public static ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn,
                                                  @FieldValue("useTime") int useTime, @This IFlexItem self, @SuperCall Callable<ActionResult<ItemStack>> doSuper) throws Exception
        {
            ItemStack heldItem = playerIn.getItemInHand(handIn);
            if (useTime > 0)
                return self.runEventThrowing("begin_using", FlexEventContext.of(worldIn, playerIn, handIn, heldItem), doSuper);
            else
                return self.runEventThrowing("use_on_air", FlexEventContext.of(worldIn, playerIn, handIn, heldItem), doSuper);
        }

        public static void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn,
                                           @FieldValue("tooltipStrings") List<ITextComponent> tooltipStrings,
                                           @SuperCall Runnable doSuper)
        {
            doSuper.run();
            tooltip.addAll(tooltipStrings);
        }

        public static void fillItemCategory(ItemGroup tab, NonNullList<ItemStack> items,
                                            @This Item self,
                                            @FieldValue("perTabStacks") Multimap<ItemGroup, StackContext> perTabStacks,
                                            @FieldValue("searchTabStacks") List<StackContext> searchTabStacks)
        {
            if (tab == ItemGroup.TAB_SEARCH)
            {
                items.addAll(searchTabStacks.stream().map(s -> s.toStack(self)).collect(Collectors.toList()));
            }
            else if (perTabStacks.containsKey(tab))
            {
                items.addAll(perTabStacks.get(tab).stream().map(s -> s.toStack(self)).collect(Collectors.toList()));
            }
        }

        public static void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected,
                                         @This IFlexItem self, @SuperCall Runnable doSuper)
        {
            ActionResult<ItemStack> result = self.runEvent("update",
                    FlexEventContext.of(worldIn, entityIn, stack).with(FlexEventContext.SLOT, itemSlot).with(FlexEventContext.SELECTED, isSelected),
                    () -> {
                        doSuper.run();
                        return new ActionResult<>(ActionResultType.PASS, stack);
                    });
            if (result.getObject() != stack)
            {
                entityIn.setSlot(itemSlot, result.getObject());
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

        public static Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot,
                                                                                   ItemStack stack,
                                                                                   @FieldValue("attributeModifiers")
                                                                                           Map<EquipmentSlotType, Multimap<Attribute, AttributeModifier>> attributeModifiers)
        {
            return IFlexItem.orElse(attributeModifiers.get(slot), HashMultimap::create);
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

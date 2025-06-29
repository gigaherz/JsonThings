package dev.gigaherz.jsonthings.things.builders;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import dev.gigaherz.jsonthings.JsonThings;
import dev.gigaherz.jsonthings.things.StackContext;
import dev.gigaherz.jsonthings.things.ThingRegistries;
import dev.gigaherz.jsonthings.things.UseFinishMode;
import dev.gigaherz.jsonthings.things.events.IEventRunner;
import dev.gigaherz.jsonthings.things.parsers.ThingParser;
import dev.gigaherz.jsonthings.things.serializers.FlexItemType;
import dev.gigaherz.jsonthings.things.serializers.IItemFactory;
import dev.gigaherz.jsonthings.things.serializers.ItemVariantProvider;
import dev.gigaherz.jsonthings.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.fml.util.thread.EffectiveSide;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ItemBuilder extends BaseBuilder<Item, ItemBuilder> implements ItemVariantProvider
{
    public static ItemBuilder begin(ThingParser<Item, ItemBuilder> ownerParser, ResourceLocation registryName)
    {
        return new ItemBuilder(ownerParser, registryName);
    }

    private final Map<EquipmentSlotGroup, Multimap<ResourceLocation, AttributeModifier>> attributeModifiers = Maps.newHashMap();

    private FlexItemType<?> itemType;

    private Integer maxStackSize = null;
    private Integer maxDamage = null;

    private Boolean isFireResistant;

    private ResourceKey<CreativeModeTab> group = null;
    private final Multimap<ResourceKey<CreativeModeTab>, StackContext> creativeMenuStacks = ArrayListMultimap.create();

    private Supplier<@NotNull FoodProperties> foodDefinition = null;

    public Integer useTime = null;
    public ItemUseAnimation useAnim = null;
    public UseFinishMode useFinishMode = null;

    private ResourceLocation containerItem = null;

    private String colorHandler = null;

    private String[] toolActions;

    private List<Component> lore;

    private Integer burnDuration;

    private JsonObject components;

    private IItemFactory<? extends Item> factory;

    private ItemBuilder(ThingParser<Item, ItemBuilder> ownerParser, ResourceLocation registryName)
    {
        super(ownerParser, registryName);
    }

    @Override
    protected String getThingTypeDisplayName()
    {
        return "Item";
    }

    public void setType(String typeName)
    {
        if (this.itemType != null) throw new RuntimeException("Item type already set.");
        this.itemType = ThingRegistries.ITEM_TYPE.getOptional(ResourceLocation.parse(typeName)).orElseThrow(() -> new IllegalStateException("No known block type with name " + typeName));
    }

    public void setType(FlexItemType<?> type)
    {
        if (ThingRegistries.ITEM_TYPE.getKey(type) == null)
            throw new IllegalStateException("Item type not registered!");
        this.itemType = type;
    }

    public void setMaxStackSize(int maxStackSize)
    {
        if (this.maxStackSize != null) throw new RuntimeException("Max stack size already set.");
        this.maxStackSize = maxStackSize;
    }

    public void setGroup(ResourceLocation group)
    {
        if (!this.creativeMenuStacks.isEmpty())
            throw new RuntimeException("Creative menu stacks have been added, do not call setGroup if you intend on adding creative menu stacks.");
        this.group = ResourceKey.create(Registries.CREATIVE_MODE_TAB, group);
    }

    public void withCreativeMenuStack(StackContext stackContext, ResourceLocation[] tabs)
    {
        if (this.group != null)
            throw new RuntimeException("An item group name has been defined, do not call setGroup if you intend on adding creative menu stacks.");
        for (var tab : tabs)
        {
            creativeMenuStacks.put(ResourceKey.create(Registries.CREATIVE_MODE_TAB, tab), stackContext);
        }
    }

    public void withAttributeModifier(EquipmentSlotGroup slot, ResourceLocation attribute, ResourceLocation id, double amount, AttributeModifier.Operation op)
    {
        var mod = new AttributeModifier(id, amount, op);
        attributeModifiers.computeIfAbsent(slot, _slot -> ArrayListMultimap.create()).put(attribute, mod);
    }

    public void setMaxDamage(int maxDamage)
    {
        if (this.maxDamage != null) throw new RuntimeException("Damageable already set.");
        this.maxDamage = maxDamage;
    }

    public void setFireResistant(boolean isFireResistant)
    {
        this.isFireResistant = isFireResistant;
    }

    public void setFood(ResourceLocation foodName)
    {
        if (this.foodDefinition != null) throw new RuntimeException("Food info already set.");
        this.foodDefinition = () -> ThingRegistries.FOOD
                .getOptional(foodName)
                .orElseGet(() -> JsonThings.foodPropertiesParser.getOrCrash(foodName).get());
    }

    public void setFood(FoodProperties food)
    {
        if (this.foodDefinition != null) throw new RuntimeException("Food info already set.");
        this.foodDefinition = () -> food;
    }

    public void setUseTime(int useTime)
    {
        this.useTime = useTime;
    }

    public void setUseAnim(ItemUseAnimation useAnim)
    {
        this.useAnim = useAnim;
    }

    public void setUseFinishMode(UseFinishMode finishMode)
    {
        this.useFinishMode = finishMode;
    }

    public void setContainerItem(ResourceLocation resourceLocation)
    {
        if (this.containerItem != null) throw new RuntimeException("Container Item already set.");
        this.containerItem = resourceLocation;
    }

    public void setToolActions(String[] stringValues)
    {
        toolActions = stringValues;
    }

    public void setColorHandler(String colorHandler)
    {
        this.colorHandler = colorHandler;
    }

    public void setLore(List<Component> lore)
    {
        this.lore = lore;
    }

    public void setBurnDuration(int burnTime)
    {
        this.burnDuration = burnTime;
    }

    public void setComponents(JsonObject dataComponentPatch)
    {
        this.components = dataComponentPatch;
    }

    @Override
    protected Item buildInternal()
    {
        Item.Properties properties = new Item.Properties();
        properties.setId(ResourceKey.create(Registries.ITEM, getRegistryName()));

        var ms = getMaxStackSize();
        if (ms != null)
        {
            properties = properties.stacksTo(ms);
        }

        var md = getMaxDamage();
        if (md != null)
        {
            properties = properties.durability(md);
        }

        var ci = getContainerItem();
        if (ci != null)
        {
            properties = properties.craftRemainder(Utils.getOrCrash(BuiltInRegistries.ITEM, ci));
        }

        Supplier<@NotNull FoodProperties> foodDefinition = getFoodDefinition();
        if (foodDefinition != null)
        {
            properties = properties.food(foodDefinition.get());
        }

        var fr = getIsFireResistant();
        if (fr != null && fr)
        {
            properties = properties.fireResistant();
        }

        var components = getComponents();
        if (components != null)
        {
            var parsedComponents = parseDataComponents(components);
            var props = properties;
            for(var entry : parsedComponents.entrySet())
            {
                @SuppressWarnings("rawtypes")
                DataComponentType key = entry.getKey();
                entry.getValue().ifPresent(value -> {
                    //noinspection unchecked
                    props.component(key, value);
                });
            }
        }

        Item item = factory.construct(properties, this);

        if (item instanceof IEventRunner eventRunner)
            constructEventHandlers(eventRunner);

        return item;
    }

    private DataComponentPatch parseDataComponents(JsonObject components)
    {
        return DataComponentPatch.CODEC.decode(RegistryOps.create(JsonOps.INSTANCE, getLookup()), components).getOrThrow().getFirst();
    }

    private RegistryOps.RegistryInfoLookup getLookup()
    {
        //Holder.Reference.createStandAlone

        return new RegistryOps.RegistryInfoLookup()
        {
            @SuppressWarnings({"unchecked", "rawtypes"})
            @Override
            public <T> Optional<RegistryOps.RegistryInfo<T>> lookup(ResourceKey<? extends Registry<? extends T>> registryKey)
            {
                var registry = (WritableRegistry<T>) BuiltInRegistries.REGISTRY.getValueOrThrow((ResourceKey) registryKey);
                return Optional.of(new RegistryOps.RegistryInfo(registry, new HolderGetter<T>()
                {
                    @Override
                    public Optional<Holder.Reference<T>> get(ResourceKey<T> resourceKey)
                    {
                        Optional<T> optional = registry.getOptional(resourceKey);
                        Holder.Reference<T> holder = optional.map(obj -> (Holder.Reference<T>)registry.wrapAsHolder(obj))
                                .orElseGet(() -> {
                                    var holder1 = DeferredHolder.create(resourceKey);
                                    validationPending.add(holder1);
                                    return wrapAsReference(registry, holder1);
                                });
                        return Optional.of(holder);
                    }

                    @Override
                    public Optional<HolderSet.Named<T>> get(TagKey tagKey)
                    {
                        return Optional.empty();
                    }
                }, registry.registryLifecycle()));
            }
        };
    }

    private <T> Holder.Reference<T> wrapAsReference(HolderOwner<T> owner, DeferredHolder<T, T> holder1)
    {
        return new Holder.Reference<>(Holder.Reference.Type.STAND_ALONE, owner, holder1.getKey(), null)
        {
            @Override
            public ResourceKey<T> key()
            {
                return holder1.getKey();
            }

            @Override
            public T value()
            {
                return holder1.value();
            }
        };
    }

    public void provideVariants(ResourceKey<CreativeModeTab> tabKey, CreativeModeTab.Output output, CreativeModeTab.ItemDisplayParameters parameters, @Nullable ItemBuilder _context, boolean explicit)
    {
        var context = Objects.requireNonNullElse(_context, this);

        if(explicit)
        {
            factory.provideVariants(tabKey, output, parameters, context, explicit);
        }
        else if (group != null)
            {
            if (group.equals(tabKey))
            {
                factory.provideVariants(tabKey, output, parameters, context, explicit);
            }
        }
        else if (!creativeMenuStacks.isEmpty())
        {
            creativeMenuStacks.get(tabKey).forEach(stack -> output.accept(stack.toStack(context.get())));
        }
        else if (getParent() != null)
        {
            getParent().provideVariants(tabKey, output, parameters, context, explicit);
        }
    }

    @Nullable
    public Integer getMaxDamage()
    {
        return getValue(maxDamage, ItemBuilder::getMaxDamage);
    }

    @Nullable
    public Integer getMaxStackSize()
    {
        return getValue(maxStackSize, ItemBuilder::getMaxStackSize);
    }

    @Nullable
    public Boolean getIsFireResistant()
    {
        return getValue(isFireResistant, ItemBuilder::getIsFireResistant);
    }

    @Nullable
    public ResourceLocation getContainerItem()
    {
        return getValue(containerItem, ItemBuilder::getContainerItem);
    }

    @Nullable
    public Supplier<@NotNull FoodProperties> getFoodDefinition()
    {
        return getValue(foodDefinition, ItemBuilder::getFoodDefinition);
    }

    @Nullable
    public FlexItemType<?> getTypeRaw()
    {
        return getValue(itemType, ItemBuilder::getTypeRaw);
    }

    public FlexItemType<?> getType()
    {
        return Utils.orElse(getTypeRaw(), FlexItemType.PLAIN);
    }

    public boolean hasType()
    {
        return getValueOrElse(itemType != null, ItemBuilder::hasType, false);
    }

    @Nullable
    public String getColorHandler()
    {
        return getValue(colorHandler, ItemBuilder::getColorHandler);
    }

    public void setFactory(IItemFactory<?> factory)
    {
        this.factory = factory;
    }

    @Nullable
    public ItemUseAnimation getUseAnim()
    {
        return getValue(useAnim, ItemBuilder::getUseAnim);
    }

    @Nullable
    public Integer getUseTime()
    {
        return getValue(useTime, ItemBuilder::getUseTime);
    }

    @Nullable
    public UseFinishMode getUseFinishMode()
    {
        return getValue(useFinishMode, ItemBuilder::getUseFinishMode);
    }

    @Nullable
    public List<Component> getLore()
    {
        return getValueOrElseGet(lore, ItemBuilder::getLore, List::of);
    }

    @Nullable
    public Integer getBurnDuration()
    {
        return getValue(burnDuration, ItemBuilder::getBurnDuration);
    }

    public ItemAttributeModifiers getAttributeModifiers()
    {
        var mods = getAttributeModifiersRaw();
        if (mods == null) return ItemAttributeModifiers.EMPTY;

        var builder = ItemAttributeModifiers.builder();

        for (var slotEntries : mods.entrySet())
        {
            for (var attributeEntries : slotEntries.getValue().entries())
            {
                var attr = DeferredHolder.create(Registries.ATTRIBUTE, attributeEntries.getKey());
                validationPending.add(attr);
                builder.add(attr, attributeEntries.getValue(), slotEntries.getKey());
            }
        }

        return builder.build();
    }

    @Nullable
    private Map<EquipmentSlotGroup, Multimap<ResourceLocation, AttributeModifier>> getAttributeModifiersRaw()
    {
        return getValue(attributeModifiers, ItemBuilder::getAttributeModifiersRaw);
    }

    @Nullable
    public String[] getToolActionsRaw()
    {
        return getValue(toolActions, ItemBuilder::getToolActionsRaw);
    }

    @Nullable
    public Set<ItemAbility> getToolActions()
    {
        var raw = getToolActionsRaw();
        if (raw == null)
            return null;
        return Arrays.stream(raw).map(ItemAbility::get).collect(Collectors.toSet());
    }

    @Nullable
    public JsonObject getComponents()
    {
        return getValue(components, ItemBuilder::getComponents);
    }

    private Queue<Holder> validationPending = new ArrayDeque<>();

    @Override
    public void validate()
    {
        while(!validationPending.isEmpty())
        {
            var holder = validationPending.remove();
            holder.value();
        }
    }
}



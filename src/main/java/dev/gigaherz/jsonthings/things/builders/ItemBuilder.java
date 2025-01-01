package dev.gigaherz.jsonthings.things.builders;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Pair;
import dev.gigaherz.jsonthings.JsonThings;
import dev.gigaherz.jsonthings.things.StackContext;
import dev.gigaherz.jsonthings.things.ThingRegistries;
import dev.gigaherz.jsonthings.things.UseFinishMode;
import dev.gigaherz.jsonthings.things.events.IEventRunner;
import dev.gigaherz.jsonthings.things.parsers.ThingParser;
import dev.gigaherz.jsonthings.things.serializers.FlexItemType;
import dev.gigaherz.jsonthings.things.serializers.IItemFactory;
import dev.gigaherz.jsonthings.util.Utils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ItemBuilder extends BaseBuilder<Item, ItemBuilder>
{
    public static ItemBuilder begin(ThingParser<ItemBuilder> ownerParser, ResourceLocation registryName)
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
    public UseAnim useAnim = null;
    public UseFinishMode useFinishMode = null;

    private ResourceLocation containerItem = null;

    private String colorHandler = null;

    private String[] toolActions;

    private List<MutableComponent> lore;

    private Integer burnDuration;

    private IItemFactory<? extends Item> factory;

    private ItemBuilder(ThingParser<ItemBuilder> ownerParser, ResourceLocation registryName)
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
        FlexItemType<?> itemType = ThingRegistries.ITEM_TYPES.get(ResourceLocation.parse(typeName));
        if (itemType == null)
            throw new IllegalStateException("No known block type with name " + typeName);
        this.itemType = itemType;
    }

    public void setType(FlexItemType<?> type)
    {
        if (ThingRegistries.ITEM_TYPES.getKey(type) == null)
            throw new IllegalStateException("Item type not registered!");
        this.itemType = type;
    }

    public void setMaxStackSize(int maxStackSize)
    {
        if (this.maxStackSize != null) throw new RuntimeException("Max stack size already set.");
        this.maxStackSize = maxStackSize;
    }

    public void setGroup(ResourceLocation group) {
        if (!this.creativeMenuStacks.isEmpty()) throw new RuntimeException("Creative menu stacks have been added, do not call setGroup if you intend on adding creative menu stacks.");
        this.group = ResourceKey.create(Registries.CREATIVE_MODE_TAB, group);
    }

    @Deprecated(forRemoval = true)
    public void withCreativeMenuStack(StackContext stackContext, String[] tabs)
    {
        creativeMenuStacks.add(Pair.of(stackContext, tabs));
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
        this.foodDefinition = () -> ThingRegistries.FOODS
                .getOptional(foodName)
                .orElseGet(() -> JsonThings.foodParser.getOrCrash(foodName).get());
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

    public void setUseAnim(UseAnim useAnim)
    {
        this.useAnim = useAnim;
    }

    public void setUseFinishMode(UseFinishMode finishMode)
    {
        this.useFinishMode = finishMode;
    }

    @Deprecated
    public void makeContainer(String emptyItem)
    {
        if (emptyItem.contains(":"))
            setContainerItem(ResourceLocation.parse(emptyItem));
        else
            setContainerItem(ResourceLocation.fromNamespaceAndPath(getRegistryName().getNamespace(), emptyItem));
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

    public void setLore(List<MutableComponent> lore)
    {
        this.lore = lore;
    }

    public void setBurnDuration(int burnTime)
    {
        this.burnDuration = burnTime;
    }

    @Override
    protected Item buildInternal()
    {
        Item.Properties properties = new Item.Properties();

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

        Item item = factory.construct(properties, this);

        if (item instanceof IEventRunner eventRunner)
            constructEventHandlers(eventRunner);

        return item;
    }

    public void fillItemVariants(BuildCreativeModeTabContentsEvent event, ItemBuilder context)
    {
        if (group != null)
        {
            if (group.equals(event.getTabKey()))
            {
                factory.provideVariants(event, context);
            }
            return;
        }

        if (!creativeMenuStacks.isEmpty())
        {
            creativeMenuStacks.get(event.getTabKey()).forEach(stack -> event.accept(stack.toStack(context.get().self())));
        }

        if (getParent() != null)
        {
            getParent().fillItemVariants(event, context);
        }
    }

    @Deprecated(forRemoval = true)
    public List<Pair<StackContext, String[]>> getCreativeMenuStacks()
    {
        if (creativeMenuStacks.size() > 0)
            return creativeMenuStacks;

        if (getParent() != null)
            return getParent().getCreativeMenuStacks();

        return creativeMenuStacks;
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
    public UseAnim getUseAnim()
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
    public List<MutableComponent> getLore()
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
                var attr = Utils.getHolderOrCrash(BuiltInRegistries.ATTRIBUTE, attributeEntries.getKey());
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
}



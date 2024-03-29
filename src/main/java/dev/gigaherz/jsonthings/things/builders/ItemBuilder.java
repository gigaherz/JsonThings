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
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.UseAnim;
import net.neoforged.neoforge.common.ToolAction;
import net.neoforged.neoforge.common.util.NonNullSupplier;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class ItemBuilder extends BaseBuilder<Item, ItemBuilder>
{
    public static ItemBuilder begin(ThingParser<ItemBuilder> ownerParser, ResourceLocation registryName)
    {
        return new ItemBuilder(ownerParser, registryName);
    }

    private final Map<EquipmentSlot, Multimap<ResourceLocation, AttributeModifier>> attributeModifiers = Maps.newHashMap();

    private FlexItemType<?> itemType;

    private Integer maxStackSize = null;
    private Integer maxDamage = null;

    private Boolean isFireResistant;

    private final List<Pair<StackContext, String[]>> creativeMenuStacks = Lists.newArrayList();

    private NonNullSupplier<FoodProperties> foodDefinition = null;

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
        FlexItemType<?> itemType = ThingRegistries.ITEM_TYPES.get(new ResourceLocation(typeName));
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

    public void withCreativeMenuStack(StackContext stackContext, String[] tabs)
    {
        creativeMenuStacks.add(Pair.of(stackContext, tabs));
    }

    public void withAttributeModifier(EquipmentSlot slot, ResourceLocation attribute, @Nullable UUID uuid, String name, double amount, int op)
    {
        AttributeModifier.Operation operation = AttributeModifier.Operation.fromValue(op);
        var mod = uuid != null ?
                new AttributeModifier(uuid, name, amount, operation) :
                new AttributeModifier(name, amount, operation);
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
            setContainerItem(new ResourceLocation(emptyItem));
        else
            setContainerItem(new ResourceLocation(getRegistryName().getNamespace(), emptyItem));
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

        NonNullSupplier<FoodProperties> foodDefinition = getFoodDefinition();
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
    public NonNullSupplier<FoodProperties> getFoodDefinition()
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

    public Map<EquipmentSlot, Multimap<Attribute, AttributeModifier>> getAttributeModifiers()
    {
        var mods = getAttributeModifiersRaw();
        if (mods == null) return Map.of();

        Map<EquipmentSlot, Multimap<Attribute, AttributeModifier>> modifiers = new HashMap<>();

        for (var kv : mods.entrySet())
        {
            var map = modifiers.computeIfAbsent(kv.getKey(), slot -> ArrayListMultimap.create());
            for (var kv1 : kv.getValue().entries())
            {
                var attr = Utils.getOrCrash(BuiltInRegistries.ATTRIBUTE, kv1.getKey());
                map.put(attr, kv1.getValue());
            }
        }

        return modifiers;
    }

    @Nullable
    private Map<EquipmentSlot, Multimap<ResourceLocation, AttributeModifier>> getAttributeModifiersRaw()
    {
        return getValue(attributeModifiers, ItemBuilder::getAttributeModifiersRaw);
    }

    @Nullable
    public String[] getToolActionsRaw()
    {
        return getValue(toolActions, ItemBuilder::getToolActionsRaw);
    }

    @Nullable
    public Set<ToolAction> getToolActions()
    {
        var raw = getToolActionsRaw();
        if (raw == null)
            return null;
        return Arrays.stream(raw).map(ToolAction::get).collect(Collectors.toSet());
    }
}



package dev.gigaherz.jsonthings.things.builders;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import dev.gigaherz.jsonthings.JsonThings;
import dev.gigaherz.jsonthings.things.CompletionMode;
import dev.gigaherz.jsonthings.things.IFlexItem;
import dev.gigaherz.jsonthings.things.StackContext;
import dev.gigaherz.jsonthings.things.ThingRegistries;
import dev.gigaherz.jsonthings.things.serializers.ItemType;
import dev.gigaherz.jsonthings.util.Utils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.UseAnim;
import net.minecraftforge.common.util.NonNullSupplier;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ItemBuilder extends BaseBuilder<IFlexItem>
{
    private final List<AttributeModifier> attributeModifiers = Lists.newArrayList();

    private JsonObject jsonSource;

    private ResourceLocation parentBuilder;
    private ItemBuilder parentBuilderObj;

    private ItemType<?> itemType;

    private Integer maxStackSize = null;
    private Integer maxDamage = null;

    private final List<Pair<StackContext, String[]>> creativeMenuStacks = Lists.newArrayList();

    private NonNullSupplier<FoodProperties> foodDefinition = null;

    private DelayedUse delayedUse = null;
    private ContainerInfo containerInfo = null;

    private String colorHandler = null;

    private List<MutableComponent> lore = List.of();

    private ItemBuilder(ResourceLocation registryName, JsonObject data)
    {
        super(registryName);
        this.jsonSource = data;
    }

    @Override
    protected String getThingTypeDisplayName()
    {
        return "Item";
    }

    public static ItemBuilder begin(ResourceLocation registryName, JsonObject data)
    {
        return new ItemBuilder(registryName, data);
    }

    public void setParent(ResourceLocation parentBuilder)
    {
        this.parentBuilder = parentBuilder;
    }

    public void setType(String typeName)
    {
        if (this.itemType != null) throw new RuntimeException("Item type already set.");
        ItemType<?> itemType = ThingRegistries.ITEM_TYPES.get(new ResourceLocation(typeName));
        if (itemType == null)
            throw new IllegalStateException("No known block type with name " + typeName);
        this.itemType = itemType;
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

    public void withAttributeModifier(@Nullable UUID uuid, String name, double amount, int op)
    {
        AttributeModifier.Operation operation = AttributeModifier.Operation.fromValue(op);
        attributeModifiers.add(uuid != null ?
                new AttributeModifier(uuid, name, amount, operation) :
                new AttributeModifier(name, amount, operation));
    }

    public void setMaxDamage(int maxDamage)
    {
        if (this.maxDamage != null) throw new RuntimeException("Damageable already set.");
        this.maxDamage = maxDamage;
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

    public void makeDelayedUse(int useTicks, String useType, String completeAction)
    {
        if (this.delayedUse != null) throw new RuntimeException("Delayed use already set.");
        this.delayedUse = new DelayedUse(useTicks, useType, completeAction);
    }

    public void makeContainer(String emptyItem)
    {
        if (this.containerInfo != null) throw new RuntimeException("Delayed use already set.");
        this.containerInfo = new ContainerInfo(getRegistryName(), emptyItem);
    }

    public void setColorHandler(String colorHandler)
    {
        this.colorHandler = colorHandler;
    }

    public void setLore(List<MutableComponent> lore)
    {
        this.lore = lore;
    }

    @Override
    protected IFlexItem buildInternal()
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

        var ci = getContainerInfo();
        if (ci != null)
        {
            properties = properties.craftRemainder(Utils.getItemOrCrash(ci.emptyItem));
        }

        NonNullSupplier<FoodProperties> foodDefinition = getFoodDefinition();
        if (foodDefinition != null)
        {
            properties = properties.food(foodDefinition.get());
        }

        var factory = Utils.orElse(getItemType(), ItemType.PLAIN).getFactory(jsonSource);

        jsonSource = null;

        IFlexItem flexItem = factory.construct(properties, this);

        var du = getDelayedUse();
        if (du != null)
        {
            flexItem.setUseAction(delayedUse.useAction);
            flexItem.setUseTime(delayedUse.useTicks);
            flexItem.setUseFinishMode(delayedUse.onComplete);
        }

        flexItem.setLore(lore);

        var stacks = getCreativeMenuStacks();
        if (stacks.size() > 0)
        {
            for (Pair<StackContext, String[]> tabEntries : stacks)
            {
                StackContext ctx = tabEntries.getFirst();
                String[] tabs = tabEntries.getSecond();

                Set<CreativeModeTab> tabsIterable = Arrays.stream(tabs).map(this::findCreativeTab).filter(Objects::nonNull).collect(Collectors.toSet());
                flexItem.addCreativeStack(ctx, tabsIterable);
            }
        }

        return flexItem;
    }

    @Nullable
    private CreativeModeTab findCreativeTab(String label)
    {
        var rl = new ResourceLocation(label);
        for (CreativeModeTabBuilder builder : JsonThings.creativeModeTabParser.getBuilders())
        {
            if (builder.getRegistryName().equals(rl))
                return builder.get();
        }
        for (CreativeModeTab tab : CreativeModeTab.TABS)
        {
            if (tab.getRecipeFolderName().equals(label))
                return tab;
        }
        return null;
    }

    public ItemBuilder getParentBuilder()
    {
        if (parentBuilder == null)
            throw new IllegalStateException("The item requires a parent to be assigned, but no \"parent\" key is present.");
        if (parentBuilderObj == null)
        {
            parentBuilderObj = JsonThings.itemParser.getBuildersMap().get(parentBuilder);
        }
        if (parentBuilderObj == null)
            throw new IllegalStateException("The item specifies a parent "+parentBuilder+", but no such parent was found.");
        return parentBuilderObj;
    }

    @Nullable
    private <T> T getValueWithParent(@Nullable T thisValue, Function<ItemBuilder, T> parentGetter)
    {
        if (thisValue != null) return thisValue;
        if (parentBuilder != null)
        {
            ItemBuilder parent = getParentBuilder();
            return parentGetter.apply(parent);
        }
        return null;
    }

    public List<Pair<StackContext, String[]>> getCreativeMenuStacks()
    {
        if (creativeMenuStacks.size() > 0)
            return creativeMenuStacks;

        if (parentBuilder != null)
            return getParentBuilder().getCreativeMenuStacks();

        return creativeMenuStacks;
    }

    @Nullable
    public Integer getMaxDamage()
    {
        return getValueWithParent(maxDamage, ItemBuilder::getMaxDamage);
    }

    @Nullable
    public Integer getMaxStackSize()
    {
        return getValueWithParent(maxStackSize, ItemBuilder::getMaxStackSize);
    }

    @Nullable
    public ContainerInfo getContainerInfo()
    {
        return getValueWithParent(containerInfo, ItemBuilder::getContainerInfo);
    }

    @Nullable
    public NonNullSupplier<FoodProperties> getFoodDefinition()
    {
        return getValueWithParent(foodDefinition, ItemBuilder::getFoodDefinition);
    }

    @Nullable
    public ItemType<?> getItemType()
    {
        return getValueWithParent(itemType, ItemBuilder::getItemType);
    }

    @Nullable
    public DelayedUse getDelayedUse()
    {
        return getValueWithParent(delayedUse, ItemBuilder::getDelayedUse);
    }

    @Nullable
    public String getColorHandler()
    {
        return getValueWithParent(colorHandler, ItemBuilder::getColorHandler);
    }

    static class ContainerInfo
    {
        public ResourceLocation emptyItem;

        public ContainerInfo(ResourceLocation registryName, String emptyItem)
        {
            if (emptyItem.contains(":"))
                this.emptyItem = new ResourceLocation(emptyItem);
            else
                this.emptyItem = new ResourceLocation(registryName.getNamespace(), emptyItem);
        }
    }

    static class DelayedUse
    {
        public int useTicks;
        public UseAnim useAction;
        public CompletionMode onComplete;

        public DelayedUse(int useTicks, String useAction, String completeAction)
        {
            this.useTicks = useTicks;
            this.useAction = UseAnim.valueOf(useAction.toUpperCase());
            this.onComplete = CompletionMode.valueOf(completeAction.toUpperCase());
        }
    }

    static class PlantInfo
    {
        public ResourceLocation crops;
        public ResourceLocation soil;
    }
}



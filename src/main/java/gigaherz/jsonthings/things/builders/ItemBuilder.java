package gigaherz.jsonthings.things.builders;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import gigaherz.jsonthings.JsonThings;
import gigaherz.jsonthings.things.CompletionMode;
import gigaherz.jsonthings.things.IFlexItem;
import gigaherz.jsonthings.things.StackContext;
import gigaherz.jsonthings.things.ThingRegistries;
import gigaherz.jsonthings.things.items.*;
import gigaherz.jsonthings.things.serializers.BlockType;
import gigaherz.jsonthings.things.serializers.IItemFactory;
import gigaherz.jsonthings.things.serializers.ItemType;
import gigaherz.jsonthings.util.Utils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ItemBuilder
{
    private final List<AttributeModifier> attributeModifiers = Lists.newArrayList();

    private IFlexItem builtItem = null;

    private final ResourceLocation registryName;

    private ResourceLocation parentBuilder;
    private ItemBuilder parentBuilderObj;

    private ItemType<?> itemType;
    private IItemFactory<?> factory;

    private Integer maxStackSize = null;
    private Integer maxDamage = null;

    private final List<Pair<StackContext, String[]>> creativeMenuStacks = Lists.newArrayList();

    private FoodProperties foodInfo = null;

    private DelayedUse delayedUse = null;
    private ContainerInfo containerInfo = null;

    private String colorHandler = null;

    private ItemBuilder(ResourceLocation registryName, @Nullable ResourceLocation parentBuilder)
    {
        this.registryName = registryName;
        this.parentBuilder = parentBuilder;
    }

    public static ItemBuilder begin(ResourceLocation registryName, @Nullable ResourceLocation parentBuilder)
    {
        return new ItemBuilder(registryName, parentBuilder);
    }

    public ItemBuilder withType(String typeName, JsonObject data)
    {
        if (this.itemType != null) throw new RuntimeException("Item type already set.");
        ItemType<?> itemType = ThingRegistries.ITEM_TYPES.get(new ResourceLocation(typeName));
        if (itemType == null)
            throw new IllegalStateException("No known block type with name " + typeName);
        this.itemType = itemType;
        this.factory = itemType.getFactory(data);
        return this;
    }

    public ItemBuilder withMaxStackSize(int maxStackSize)
    {
        if (this.maxStackSize != null) throw new RuntimeException("Max stack size already set.");
        this.maxStackSize = maxStackSize;
        return this;
    }

    public ItemBuilder withCreativeMenuStack(StackContext stackContext, String[] tabs)
    {
        creativeMenuStacks.add(Pair.of(stackContext, tabs));
        return this;
    }

    public ItemBuilder withAttributeModifier(@Nullable UUID uuid, String name, double amount, int op)
    {
        AttributeModifier.Operation operation = AttributeModifier.Operation.fromValue(op);
        attributeModifiers.add(uuid != null ?
                new AttributeModifier(uuid, name, amount, operation) :
                new AttributeModifier(name, amount, operation));
        return this;
    }

    public ItemBuilder makeDamageable(int maxDamage)
    {
        if (this.maxDamage != null) throw new RuntimeException("Damageable already set.");
        this.maxDamage = maxDamage;
        return this;
    }
/*
    public ItemBuilder makeBlock(ResourceLocation blockName)
    {
        if (this.blockInfo != null) throw new RuntimeException("Block info already set.");
        if (this.foodInfo != null) throw new RuntimeException("An item cannot be block and food at the same time.");
        if (this.toolInfos.size() > 0) throw new RuntimeException("An item cannot be block and tool at the same time.");
        if (this.armorInfo != null) throw new RuntimeException("An item cannot be block and armor at the same time.");
        this.blockInfo = new BlockInfo(blockName);
        return this;
    }

    public ItemBuilder withTool(String toolType, ResourceLocation tierName)
    {
        if (this.blockInfo != null) throw new RuntimeException("An item can not be tool and block at the same time.");
        if (this.armorInfo != null) throw new RuntimeException("An item cannot be tool and armor at the same time.");
        if (!ThingRegistries.ITEM_TIERS.containsKey(tierName))
            throw new RuntimeException("No known item tier definition with name '" + tierName + "'");
        Tier tier = ThingRegistries.ITEM_TIERS.get(tierName);
        if (tier == null)
            throw new IllegalStateException("Property with name " + tierName + " not found in ThingRegistries.ITEM_TIERS");
        this.toolInfos.add(new ToolInfo(toolType, tier));
        return this;
    }

    public ItemBuilder makeArmor(String equipmentSlot, String material)
    {
        if (this.armorInfo != null) throw new RuntimeException("Armor info already set.");
        if (this.blockInfo != null) throw new RuntimeException("An item can not be armor and block at the same time.");
        if (this.toolInfos.size() > 0) throw new RuntimeException("An item cannot be armor and tool at the same time.");
        this.armorInfo = new ArmorInfo(equipmentSlot, material);
        return this;
    }
*/

    public ItemBuilder makeFood(ResourceLocation foodName)
    {
        if (this.foodInfo != null) throw new RuntimeException("Food info already set.");
        if (!ThingRegistries.FOODS.containsKey(foodName))
            throw new RuntimeException("No known food definition with name '" + foodName + "'");
        FoodProperties foodInfo = ThingRegistries.FOODS.get(foodName);
        if (foodInfo == null)
            throw new IllegalStateException("Property with name " + foodName + " not found in ThingRegistries.FOODS");
        this.foodInfo = foodInfo;
        return this;
    }

    public ItemBuilder makeFood(FoodProperties food)
    {
        if (this.foodInfo != null) throw new RuntimeException("Food info already set.");
        this.foodInfo = food;
        return this;
    }

    public ItemBuilder makeDelayedUse(int useTicks, String useType, String completeAction)
    {
        if (this.delayedUse != null) throw new RuntimeException("Delayed use already set.");
        this.delayedUse = new DelayedUse(useTicks, useType, completeAction);
        return this;
    }

    public ItemBuilder makeContainer(String emptyItem)
    {
        if (this.containerInfo != null) throw new RuntimeException("Delayed use already set.");
        this.containerInfo = new ContainerInfo(registryName, emptyItem);
        return this;
    }

    public ItemBuilder withColorHandler(String colorHandler)
    {
        this.colorHandler = colorHandler;
        return this;
    }

    public IFlexItem build()
    {
        Item.Properties properties = new Item.Properties();

        if (maxDamage != null)
        {
            properties = properties.durability(maxDamage);
        }

        if (containerInfo != null)
        {
            properties = properties.craftRemainder(Utils.getItemOrCrash(containerInfo.emptyItem));
        }

        if (foodInfo != null)
        {
            properties = properties.food(foodInfo);
        }

        IFlexItem flexItem = factory.construct(properties, this);

        if (delayedUse != null)
        {
            flexItem.setUseAction(delayedUse.useAction);
            flexItem.setUseTime(delayedUse.useTicks);
            flexItem.setUseFinishMode(delayedUse.onComplete);
        }

        for (Pair<StackContext, String[]> tabEntries : creativeMenuStacks)
        {
            StackContext ctx = tabEntries.getFirst();
            String[] tabs = tabEntries.getSecond();

            Set<CreativeModeTab> tabsIterable = Arrays.stream(tabs).map(this::findCreativeTab).collect(Collectors.toSet());
            flexItem.addCreativeStack(ctx, tabsIterable);
        }

        builtItem = flexItem;
        return flexItem;
    }

    @Nullable
    private CreativeModeTab findCreativeTab(String label)
    {
        for (CreativeModeTab tab : CreativeModeTab.TABS)
        {
            if (tab.getRecipeFolderName().equals(label))
                return tab;
        }
        return null;
    }

    public IFlexItem getBuiltItem()
    {
        if (builtItem == null)
            return build();
        return builtItem;
    }

    public ItemBuilder getParentBuilder()
    {
        if (parentBuilder == null)
            throw new IllegalStateException("Parent builder not set");
        if (parentBuilderObj == null)
        {
            parentBuilderObj = JsonThings.itemParser.getBuildersMap().get(parentBuilder);
        }
        if (parentBuilderObj == null)
            throw new IllegalStateException("Parent builder not found");
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

    @Nullable
    public String getColorHandler()
    {
        return getValueWithParent(colorHandler, ItemBuilder::getColorHandler);
    }

    public ResourceLocation getRegistryName()
    {
        return registryName;
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



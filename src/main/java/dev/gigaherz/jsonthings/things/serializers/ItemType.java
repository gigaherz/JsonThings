package dev.gigaherz.jsonthings.things.serializers;

import com.google.gson.JsonObject;
import dev.gigaherz.jsonthings.things.IFlexItem;
import dev.gigaherz.jsonthings.things.ThingRegistries;
import dev.gigaherz.jsonthings.things.items.*;
import dev.gigaherz.jsonthings.util.Utils;
import joptsimple.internal.Strings;
import net.minecraft.block.Block;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.TieredItem;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

import java.util.Objects;

@SuppressWarnings("ClassCanBeRecord")
public class ItemType<T extends Item & IFlexItem>
{
    public static final ItemType<FlexItem> PLAIN = register("plain", (data) -> (props, builder) -> new FlexItem(props));

    public static final ItemType<FlexBlockItem> BLOCK = register("block", data -> {
        String name = JSONUtils.getAsString(data, "places", null);
        ResourceLocation blockName = name != null ? new ResourceLocation(name) : null;
        boolean useBlockName = JSONUtils.getAsBoolean(data, "use_block_name", true);
        return (props, builder) -> new FlexBlockItem(Utils.getBlockOrCrash(blockName != null ? blockName : builder.getRegistryName()), useBlockName, props);
    });

    public static final ItemType<FlexArmorItem> ARMOR = register("armor", data -> {

        String slotName;
        if (data.has("equipment_slot"))
        {
            String str = data.get("equipment_slot").getAsString();
            if (!Strings.isNullOrEmpty(str))
            {
                slotName = str;
            }
            else
            {
                throw new RuntimeException("Armor equipment slot must be a non-empty string.");
            }
        }
        else
        {
            throw new RuntimeException("Armor info must have a non-empty 'equipment_slot' string.");
        }

        String materialName;
        if (data.has("material"))
        {
            String str = data.get("material").getAsString();
            if (!Strings.isNullOrEmpty(str))
            {
                materialName = str;
            }
            else
            {
                throw new RuntimeException("Armor material must be a non-empty string.");
            }
        }
        else
        {
            throw new RuntimeException("Armor info must have a non-empty 'material' string.");
        }

        final EquipmentSlotType slot = EquipmentSlotType.byName(slotName);
        final ArmorMaterial material = ArmorMaterial.valueOf(materialName.toUpperCase());

        return (props, builder) -> new FlexArmorItem(material, slot, props);
    });

    public static final ItemType<FlexSwordItem> SWORD = register("sword", makeToolSerializer2(FlexSwordItem::new));
    public static final ItemType<FlexShovelItem> SHOVEL = register("shovel", makeToolSerializer(FlexShovelItem::new));
    public static final ItemType<FlexAxeItem> AXE = register("axe", makeToolSerializer(FlexAxeItem::new));
    public static final ItemType<FlexPickaxeItem> PICKAXE = register("pickaxe", makeToolSerializer2(FlexPickaxeItem::new));
    public static final ItemType<FlexHoeItem> HOE = register("hoe", makeToolSerializer2(FlexHoeItem::new));

    public static final ItemType<FlexDiggerItem> DIGGER = register("digger", data -> {

        String tier = parseTier(data);

        float damage = JSONUtils.getAsInt(data, "damage");
        float speed = JSONUtils.getAsFloat(data, "speed");

        return (props, builder) -> new FlexDiggerItem(getTier(tier), damage, speed, props);
    });

    private static <T extends TieredItem & IFlexItem> IItemSerializer<T> makeToolSerializer(DiggerFactory<T> factory)
    {
        return data -> {

            String tier = parseTier(data);

            float damage = JSONUtils.getAsFloat(data, "damage");
            float speed = JSONUtils.getAsFloat(data, "speed");

            return (props, builder) -> factory.create(getTier(tier), damage, speed, props);
        };
    }

    private static <T extends TieredItem & IFlexItem> IItemSerializer<T> makeToolSerializer2(DiggerFactory2<T> factory)
    {
        return data -> {

            String tier = parseTier(data);

            int damage = JSONUtils.getAsInt(data, "damage");
            float speed = JSONUtils.getAsFloat(data, "speed");

            return (props, builder) -> factory.create(getTier(tier), damage, speed, props);
        };
    }

    private static <T extends TieredItem & IFlexItem> IItemSerializer<T> makeTieredSerializer(TieredFactory<T> factory)
    {
        return data -> {

            String tier = parseTier(data);

            return (props, builder) -> factory.create(getTier(tier), props);
        };
    }

    @FunctionalInterface
    public interface DiggerFactory<T extends TieredItem & IFlexItem>
    {
        T create(IItemTier tier, float damage, float speed, Item.Properties properties);
    }

    @FunctionalInterface
    public interface DiggerFactory2<T extends TieredItem & IFlexItem>
    {
        T create(IItemTier tier, int damage, float speed, Item.Properties properties);
    }

    @FunctionalInterface
    public interface TieredFactory<T extends TieredItem & IFlexItem>
    {
        T create(IItemTier tier, Item.Properties properties);
    }

    private static IItemTier getTier(String tierName)
    {
        return Utils.getOrCrash(ThingRegistries.ITEM_TIERS, new ResourceLocation(tierName));
    }

    private static String parseTier(JsonObject data)
    {
        String tierName;
        if (data.has("tier"))
        {
            String str = data.get("tier").getAsString();
            if (!Strings.isNullOrEmpty(str))
            {
                tierName = str;
            }
            else
            {
                throw new RuntimeException("Tool tier must be a non-empty string.");
            }
        }
        else
        {
            throw new RuntimeException("Tool info must have a non-empty 'tier' string.");
        }

        return tierName;
    }

    public static void init()
    {
        /* do nothing */
    }

    public static <T extends Item & IFlexItem> ItemType<T> register(String name, IItemSerializer<T> factory)
    {
        return Registry.register(ThingRegistries.ITEM_TYPES, name, new ItemType<>(factory));
    }

    private final IItemSerializer<T> factory;

    private ItemType(IItemSerializer<T> factory)
    {
        this.factory = factory;
    }

    public IItemFactory<T> getFactory(JsonObject data)
    {
        return factory.createFactory(data);
    }

    public String toString()
    {
        return "ItemType{" + ThingRegistries.ITEM_TYPES.getKey(this) + "}";
    }
}

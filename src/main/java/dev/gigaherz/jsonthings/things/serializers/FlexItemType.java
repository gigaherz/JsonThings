package dev.gigaherz.jsonthings.things.serializers;

import com.google.gson.JsonObject;
import dev.gigaherz.jsonthings.things.ThingRegistries;
import dev.gigaherz.jsonthings.things.builders.ItemBuilder;
import dev.gigaherz.jsonthings.things.items.*;
import dev.gigaherz.jsonthings.util.Utils;
import dev.gigaherz.jsonthings.util.parse.JParse;
import dev.gigaherz.jsonthings.util.parse.JParseException;
import joptsimple.internal.Strings;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.List;
import java.util.function.Supplier;

@SuppressWarnings("ClassCanBeRecord")
public class FlexItemType<T extends Item>
{
    public static final FlexItemType<FlexItem> PLAIN = register("plain", (data) -> FlexItem::new);

    public static final FlexItemType<FlexBlockItem> BLOCK = register("block", data -> {
        final String name = GsonHelper.getAsString(data, "places", null);
        boolean useBlockName = GsonHelper.getAsBoolean(data, "use_block_name", true);
        return (props, builder) -> {
            ResourceLocation blockName = name != null ? ResourceLocation.parse(name) : builder.getRegistryName();
            var block = Utils.getOrCrash(BuiltInRegistries.BLOCK, blockName);
            return new FlexBlockItem(block, useBlockName, props, builder);
        };
    });

    public static final FlexItemType<FlexDrinkableBottleItem> DRINKABLE_BOTTLE = register("drinkable_bottle", (data) -> {
        MutableObject<ResourceLocation> baseItemName = new MutableObject<>();
        JParse.begin(data)
                .ifKey("base_item", val -> val.string().map(ResourceLocation::parse).handle(baseItemName::setValue));
        return (props, builder) -> {
            Supplier<Item> baseItem = baseItemName.getValue() != null
                    ? DeferredHolder.create(Registries.ITEM, baseItemName.getValue())
                    : () -> Items.GLASS_BOTTLE;
            return new FlexDrinkableBottleItem(baseItem, props, builder);
        };
    });

    public static final FlexItemType<FlexBucketItem> BUCKET = register("bucket", data -> {
        String name = GsonHelper.getAsString(data, "fluid", null);
        return (props, builder) ->
        {
            ResourceLocation fluidName;
            if (name != null)
            {
                fluidName = ResourceLocation.parse(name);
            }
            else
            {
                var thisName = builder.getRegistryName();
                var path = thisName.getPath();
                if (path.endsWith("_bucket")) path = path.substring(0, path.length() - "_bucket".length());
                fluidName = ResourceLocation.fromNamespaceAndPath(thisName.getNamespace(), path);
            }
            return new FlexBucketItem(Lazy.of(() -> Utils.getOrCrash(BuiltInRegistries.FLUID, fluidName)), props, builder);
        };
    });

    public static final FlexItemType<FlexArmorItem> ARMOR = register("armor", data -> {

        MutableObject<ArmorItem.Type> armorType = new MutableObject<>();
        MutableObject<ResourceLocation> materialName = new MutableObject<>();

        JParse.begin(data)
                .requireExactlyOne(List.of("equipment_slot", "armor_type"), () -> new JParseException("Amor item must have an 'armor_type' key, or for backward compatibility, a 'slotName' key."))
                .ifKey("equipment_slot", val -> val.string().map(Utils::armorTypeByEquipmentSlotName).handle(armorType::setValue))
                .ifKey("armor_type", val -> val.string().map(Utils::armorTypeByName).handle(armorType::setValue))
                .key("material", val -> val.string().map(ResourceLocation::parse).handle(materialName::setValue));

        return (props, builder) -> {
            var material = Utils.getHolderOrCrash(BuiltInRegistries.ARMOR_MATERIAL, materialName.getValue());
            return new FlexArmorItem(material, armorType.getValue(), props, builder);
        };
    });

    public static final FlexItemType<FlexSwordItem> SWORD = register("sword", makeToolSerializer(FlexSwordItem::new));
    public static final FlexItemType<FlexShovelItem> SHOVEL = register("shovel", makeToolSerializer(FlexShovelItem::new));
    public static final FlexItemType<FlexAxeItem> AXE = register("axe", makeToolSerializer(FlexAxeItem::new));
    public static final FlexItemType<FlexPickaxeItem> PICKAXE = register("pickaxe", makeToolSerializer(FlexPickaxeItem::new));
    public static final FlexItemType<FlexHoeItem> HOE = register("hoe", makeToolSerializer(FlexHoeItem::new));

    public static final FlexItemType<FlexDiggerItem> DIGGER = register("digger", data -> {

        String tier = parseTier(data);

        String tagName = GsonHelper.getAsString(data, "mineable");

        TagKey<Block> tag = Utils.blockTag(tagName);

        float damage = GsonHelper.getAsInt(data, "damage");
        float speed = GsonHelper.getAsFloat(data, "speed");

        return (props, builder) -> {
            var tier_ = getTier(tier);
            props = props.attributes(createToolAttributes(tier_, damage, speed));
            return new FlexDiggerItem(getTier(tier), tag, props, builder);
        };
    });

    private static <T extends TieredItem> IItemSerializer<T> makeToolSerializer(DiggerFactory<T> factory)
    {
        return data -> {

            String tier = parseTier(data);

            float damage = GsonHelper.getAsFloat(data, "damage");
            float speed = GsonHelper.getAsFloat(data, "speed");

            return (props, builder) -> {
                var tier_ = getTier(tier);
                props = props.attributes(createToolAttributes(tier_, damage, speed));
                return factory.create(getTier(tier), props, builder);
            };
        };
    }

    public static final FlexItemType<FlexTieredItem> TIERED = register("tiered", makeTieredSerializer(FlexTieredItem::new));

    private static <T extends TieredItem> IItemSerializer<T> makeTieredSerializer(TieredFactory<T> factory)
    {
        return data -> {

            String tier = parseTier(data);

            return (props, builder) -> factory.create(getTier(tier), props, builder);
        };
    }

    @FunctionalInterface
    public interface DiggerFactory<T extends TieredItem>
    {
        T create(Tier tier, Item.Properties properties, ItemBuilder builder);
    }

    @FunctionalInterface
    public interface DiggerFactory2<T extends TieredItem>
    {
        T create(Tier tier, Item.Properties properties, ItemBuilder builder);
    }

    @FunctionalInterface
    public interface TieredFactory<T extends TieredItem>
    {
        T create(Tier tier, Item.Properties properties, ItemBuilder builder);
    }

    private static Tier getTier(String tierName)
    {
        return Utils.getOrCrash(ThingRegistries.TIERS, ResourceLocation.parse(tierName));
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

    public static <T extends Item> FlexItemType<T> register(String name, IItemSerializer<T> factory)
    {
        return Registry.register(ThingRegistries.ITEM_TYPES, name, new FlexItemType<>(factory));
    }

    private final IItemSerializer<T> factory;

    private FlexItemType(IItemSerializer<T> factory)
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

    public static ItemAttributeModifiers createToolAttributes(Tier tier, float damage, float speed)
    {
        return ItemAttributeModifiers.builder()
                .add(
                        Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(
                                Item.BASE_ATTACK_DAMAGE_ID,
                                damage + tier.getAttackDamageBonus(),
                                AttributeModifier.Operation.ADD_VALUE
                        ),
                        EquipmentSlotGroup.MAINHAND
                )
                .add(
                        Attributes.ATTACK_SPEED,
                        new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, speed, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
                .build();
    }
}

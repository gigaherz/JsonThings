# Item Definitions

This format applies to effect instances as defined in other json files. There is no names definitions for effect instances
at this point.

## Basic structure of the JSON file

```json
{
  "parent": "minecraft:string",
  "type": "plain",
  "max_stack_size": 64,
  "max_damage": 50,
  "food": {
    "saturation": 5
  },
  "group": "decorations",
  "creative_menu_stacks": [
    {
      "nbt": {}
    }
  ],
  "attribute_modifiers": [
    {
      "nbt": {}
    }
  ],
  "color_handler": "foliage"  
}
```

## "parent"

Defines another item to copy properties from.

Optional. Default: no parent.

Must be a resource location string like `"string"`, or `"minecraft:stick"`. Like on recipe jsons and other vanilla files,
if the namespace (the part before the colon) is missing "minecraft" is implied.

## "type"

Defines the type of item to construct. Each type has additional properties.

Optional. Default: plain type without additional properties.

Must be a resource location string like `"block"`, or `"minecraft:sword"`. Like on recipe jsons and other vanilla files,
if the namespace (the part before the colon) is missing "minecraft" is implied.

## "max_stack_size"

Defines how much the item stacks.

Optional. Default: 0.

The default means no amplification, and will do the standard effect.

Must be a positive integer between 1 and 127. Values above 64 are not well-defined and may fail.

## "max_damage"

Defines how many uses (without Unbreaking) the item has before breaking.

Optional. Default: 0 (no durability bar), except for item types like tools and armor.

Must be a positive integer or zero. Zero means the item will not be damageable.


## "group"

Defines which creative tab to have the item in.

Cannot be used at the same time as `"creative_menu_stacks"`.

Optional. Default: not shown in the creative menu.

Must be a string with the name of a creative menu tab.

## "creative_menu_stacks"

Defines which stacks of this item will be added to the creative menu. Each stack can be added to one or more tabs.

Cannot be used at the same time as `"group"`.

Optional. Default: not shown in the creative menu.

Must be a json array (`[]`) containing json objects (`{}`).

Each json object corresponds to the combination of a tab list and information about the stack:

```json
    {
      "tabs": [ "tools" ],
      "nbt": { "tmp": 0 }
    }
```

The `"tabs"` key is required and must be a json array (`[]`) containing strings.

The rest of the object are values defining the itemstack.

For details on the definition of itemstacks in json, see the [ItemStack Definitions](./misc/ItemStack.md) page.

## "food"

Defines the item to be edible.

Optional. Default: not food.

For details on the values used to define food, see the [Food Definitions](./Food.md) page.

## "attribute_modifiers"

Defines the attribute changes that are applied when this item is equipped. Eg.: +20% speed

Optional. Default: no attribute modifiers.

Must be a json array (`[]`) containing json objects (`{}`). Each object must describe one attribute modifier.

The syntax for attribute modifiers is described in the [Attribute Modifiers](./misc/AttributeModifiers.md) page.

## "color_handler"

Defines a color handler for the stack. Color handler provides tint values based on context.

Optional. Default: no tinting.

Must be a resource location string like `"foliage"`, or `"minecraft:tall_grass"`. Like on recipe jsons and other vanilla files,
if the namespace (the part before the colon) is missing "minecraft" is implied.

By default, only 2 color handlers are defined: `"foliage"` and `"tall_grass"`. More can be defined by mod code, and in the future it will be possible to define them via scripting.

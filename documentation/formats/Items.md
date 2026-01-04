# Item Definitions

Items define things that can be in your inventory, and in your hands. 

Item definitions go in the `item` directory in the thing pack.

E.g.
```
/things/examplepack/item/cheese_stick.json
```

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
  "container": "bowl",
  "fire_resistant": true,
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
  "color_handler": "foliage",
  "lore": [
    "Hello",
    {"text": "Hi", "italic": true, "color": "gray" }
  ],
  "tool_actions": ["shovel_dig"],
  "burn_duration": 20,
  "delayed_use": {
    "duration": 20,
    "animation": "EAT",
    "on_complete": "USE_ITEM"
  }
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

See the available item types in the [Item Types](./ItemTypes.md) page.

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

The rest of the object are values defining the item stack.

For details on the definition of item stacks in json, see the [ItemStack Definitions](./ItemStack.md) page.

## "container"

Defines the item that will be left behind in the crafting table after crafting, or in the furnace when the fuel is spent.

Optional. Default: no container.

## "food"

Defines the item to be edible.

Optional. Default: not food.

For details on the values used to define food, see the [Food Definitions](./Food.md) page.

## "attribute_modifiers"

Defines the attribute changes that are applied when this item is equipped. E.g.: +20% speed

Optional. Default: no attribute modifiers.

Must be a json array (`[]`) containing json objects (`{}`). Each object must describe one attribute modifier.

The syntax for attribute modifiers is described in the [Attribute Modifiers](./AttributeModifiers.md) page.

## "color_handler"

Defines a color handler for the stack. Color handler provides tint values based on context.

Optional. Default: no tinting.

Must be a resource location string like `"foliage"`, or `"minecraft:tall_grass"`. Like on recipe jsons and other vanilla files,
if the namespace (the part before the colon) is missing "minecraft" is implied.

By default, only 2 color handlers are defined: `"foliage"` and `"tall_grass"`. More can be defined by mod code, and in the future it will be possible to define them via scripting.

## "lore"

Defines a list of lines of lore text to show in the tooltip box.

Optional. Default: no lore.

Must be a json array (`[]`) containing strings and json-formatted text components, as they would be used in commands such as `/tellraw`.

## "tool_actions"

Defines a list of tool actions to present to the game. This doesn't make an item able to perform those actions unless it's scripted to do so, without scripting it only declares the item as a kind of tool. 

Optional. Default: depends on item type.

Must be a json array (`[]`) containing strings.

## "burn_duration"

Defines the amount of time this item can burn when used as fuel for a furnace.

Optional. Default: use vanilla default handling (depends on item type).

Must be an integer. A value of 0 prevents the item from being considered fuel.
(a value of 0 makes game crash exactly)

## "fire_resistant"

Defines if the item can survive fire and lava.

Optional. Default: false (item burns in lava and fire).

Must be a boolean (`false`  or `true`).

## "delayed_use"

EXPERIMENTAL.

Defines if you can use the item continuously.

Optional.

'"duration"' is required, difines how many ticks you should use.
'"animation"' is required. Allowed values:     
    NONE,
    EAT,
    DRINK,
    BLOCK,
    BOW,
    SPEAR,
    CROSSBOW,
    SPYGLASS,
    TOOT_HORN,
    BRUSH,
    CUSTOM
'"on_complete"' is optional. Allowed values: 
  USE_ITEM,       (like food)
  CONTINUE        (like a bow)

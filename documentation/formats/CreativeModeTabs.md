# Creative Mode Tab Definition

Creative Mode tab definitions allow adding new tabs to the creative mode menu.

Creative Mode tab definitions go in the `creative_mode_tab` directory in the thing pack.

See ["group"](./Items.md#group) and ["creative_menu_stacks"](./Items.md#creative_menu_stacks) in the Item definitions for an example of where this is used.

## Basic structure of the JSON file

```json
{
  "icon": "minecraft:stick",
}
```

## "icon"

Defines the item to be used as the icon for the tab.

Required.

Must be a resource location string like `"string"`, or `"minecraft:stick"`. Like on model jsons and other vanilla files,
if the namespace (the part before the colon) is missing "minecraft" is implied.

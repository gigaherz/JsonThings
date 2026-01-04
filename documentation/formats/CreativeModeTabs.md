# Creative Mode Tab Definition

Creative Mode tab definitions allow adding new tabs to the creative mode menu.

Creative Mode tab definitions go in the `creative_mode_tab` directory in the thing pack.

See ["group"](./Items.md#group) and ["creative_menu_stacks"](./Items.md#creative_menu_stacks) in the Item definitions for an example of where this is used.

## Basic structure of the JSON file

```json
{
  "icon": "minecraft:stick",
  "translation_key": "namespace:tab_name",
  "right_side": false,
  "items":["minecraft:apple"],
  "before":"",
  "after":""
}
```

## "icon"

Defines the item to be used as the icon for the tab.

Required.

Must be a resource location string like `"string"`, or `"minecraft:stick"`, or a json object (`{}`) containing a stack definition.

## "translation_key"

The translation key of this tab.

Optional.

Default value: <mod namespace>:<tab id>

## "items"

Defines the contents of the tab. Additional json items can add themselves to a tab via their own json files. 

Required.

Must be a list of item stacks. Each entry must be either resource location strings like `"string"`, or `"minecraft:stick"`, or a json object (`{}`) containing a stack definition.

## "before"

Lists creative mode tabs that must be placed to the right of this tab.

Optional.

Must be a list of resource location strings like `"string"`, or `"minecraft:stick"`. Like on model jsons and other vanilla files,
if the namespace (the part before the colon) is missing "minecraft" is implied.

## "after"

Lists creative mode tabs that must be placed to the left of this tab.

Optional.

Must be a list of resource location strings like `"string"`, or `"minecraft:stick"`. Like on model jsons and other vanilla files,
if the namespace (the part before the colon) is missing "minecraft" is implied.

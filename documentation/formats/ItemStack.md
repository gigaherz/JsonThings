# Item Stack Definition

Item Stacks link an item with a count and optional tags, they are the essential object through which Items are used.

Item Stack definitions are not named, but rather contained within other json files.

See [Items](./Items.md#creative_menu_stacks) for an example of where this is used.

## Basic structure of the JSON file

```json
{
  "item": "minecraft:stick",
  "count": 10,
  "nbt": { }  
}
```

## "item"

Defines the item to be used in the stack.

Not supported in the Item's "creative_menu_stacks".

Required when supported. Default: item provided by context.

Must be a resource location string like `"string"`, or `"minecraft:stick"`. Like on recipe jsons and other vanilla files,
if the namespace (the part before the colon) is missing "minecraft" is implied.

## "count"

Defines the number of items in the stack.

Not supported in the Item's "creative_menu_stacks". Creative menu stacks should always be 1.

Optional. Default: 1.

Must be a positive integer bigger than zero.

## "nbt"

Defines the NBT Tag attached to the stack.

Optional. Default: no NBT.

Can have 2 formats:

* A string with the tag format as specified in the ingame `/give` command.
    ```
      "{Damage:10b}"
    ```
* A json object (`{}`) with the tag hierarchy defined explicitly.
    ```json
      {
        "Damage": 10
      }
    ```

The string version allows using explicit types, which may be necessary in some cases.

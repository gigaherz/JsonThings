# Block Definitions

Fluid definitions let you define liquids that can be placed in buckets and other fluid containers, and/or placed in the world.

Fluid definitions go in the `fluid` directory in the thing pack.

E.g.
```
/things/examplepack/fluid/mud.json
```

## Basic structure of the JSON file

```json
{
  "parent": "another:fluid",
  "type": "plain",
  "properties": {
    "facing": "horizontal_facing",
    "powered": { "type": "boolean" },
    "str": { "type": "string", "values": [ "value1", "value2" ] },
    "num": { "type": "int", "min": 0, "max": 5 }
  },
  "default_state": {
    "facing": "east",
    "powered": false,
    "str": "value1",
    "num": 0
  },
  "bucket": true,
  "still_texture": "minecraft:block/water_still",
  "flowing_texture": "minecraft:block/water_flow",
  "side_texture": "minecraft:block/water_overlay",
  "rarity": "epic",
  "color": [255,0,255],
  "density": 100,
  "luminosity": 15,
  "temperature": 100,
  "viscosity": 100,
  "gaseous": false,
  "fill_sound": "resource:location",
  "empty_sound": "resource:location"
}
```

# "parent"

Defines another fluid to copy properties from.

Optional. Default: no parent.

Must be a resource location string like `"water"`, or `"minecraft:lava"`. Like on loot table jsons and other vanilla files,
if the namespace (the part before the colon) is missing "minecraft" is implied.

## "type"

Defines the type of block to construct. Each type has additional properties.

Optional. Default: plain type without additional properties.

Must be a resource location string like `"plain"`, or `"minecraft:stairs"`. Like on tag jsons and other vanilla files,
if the namespace (the part before the colon) is missing "minecraft" is implied.

See the available item types in the [Fluid Types](./FluidTypes.md) page.

## "properties"

Defines the fluidstate properties contained in the mod.

Optional. Default: no properties, single state.

Must be a json object (`{}`) containing keys for the property names.

The values in the object can be of 2 types:
* A String: the name of a stock property provided by vanilla minecraft. New stock properties can be added via mod code.
* A json object (`{}`) containing some of these keys:
    ```json
      {
        "type": "string",
        "values": []
      } 
    ```
  * `"type"`: One of the property implementations available. Required.
    * `"boolean"`: The values will be `false` and `true`.
    * `"int"`: The values will be a set of integers. The valid range must be specified in the `min` and `max` keys.
    * `"string"`: The values will be a set of strings. The valid strings must be specified in the `values` list.
    * `"direction"`: The values will be cardinal directions. A subset of the directions can be specified in the `values` list.
    * `"enum"`: The values will be the values in the enum given by the `class` key. A subset of the directions can be specified in the `values` list.
  * `"values"`: For `string`, `direction` and `enum` properties, a json array (`[]`) containing the strings corresponding to the allowed values. Required for `string` properties only.
  * `"min"` and `"max"`: For `int` properties, the range of integers available. Required for `int` properties.
  * `"class"`: For `enum` properties, the fully qualified name of the Enum class. Required for `enum` properties.

## "default_state"

Defines the default value of each property, when the value is not provided explicitly.

Optional. Default: the property's first valid value. The default is subject to change unexpectedly and not recommended.

Must be a json object (`{}`) containing property names as keys, with valid values for those properties.

```json
{
  "facing": "east",
  "age": 3
}
```

## "bucket"

Defines that the block is see-through and neighbour face culling should not be performed.

Optional. Default: false (solid).

Must be a boolean (`false` or `true`).

## "item"

Defines the fluids's bucket item.

Optional. Default: no item. If not used, the fluid will have no bucket defined. Other fluid containers will be able to be used still.

Can be one of 2 types:
* A Boolean: If `true`, the fluid bucket will have all the default properties.
* A json object (`{}`) containing the definition of an item, as seen in the [Items](./Items.md) page.

## "still_texture"

Defines the texture used for the top face of source block and flowing blocks that are in equilibrium. Also used for display on dynamic buckets and other fluid containers.

Required.

Must be a resource location string like `"water"`, or `"minecraft:block/water_still"`. Like on tag jsons and other vanilla files,
if the namespace (the part before the colon) is missing "minecraft" is implied.

## "flowing_texture"

Defines the texture used for the top face of flowing blocks that flow toward a lower point. Also used for display on dynamic buckets and other fluid containers.

Required.

Must be a resource location string like `"water"`, or `"minecraft:block/water_flowing"`. Like on tag jsons and other vanilla files,
if the namespace (the part before the colon) is missing "minecraft" is implied.

## "side_texture"

Defines the texture used for side of the fluid block when seen through a non-solid block. Sometimes referred to as the overlay texture.

Optional. Default: no side texture.

Must be a resource location string like `"water"`, or `"minecraft:block/water_overlay"`. Like on tag jsons and other vanilla files,
if the namespace (the part before the colon) is missing "minecraft" is implied.

## "rarity"

Defines the rarity color that will be used when displaying the name of the fluid.

Optional. Default: "common".

Must be one of these strings:

 * "common"
 * "uncommon" 
 * "rare"
 * "epic"

## "color"

Defines a color tint that will be multiplied with the texture colors.

Optional. Default: white (no tint).

Can be:
1. A number between 0 and 2^32-1
2. A string containing a hex color code, such as "#FFAABBCC". Short form colors ("#ABC") are NOT supported.
3. A json array containing elements for either `[a,r,g,b]` or `[r,g,b]`.
4. A json object containing values for "r", "g", "b", and optionally "a".

    Eg:
```json
{ "r": 255, "g": 0, "b": 255 }
```

## "luminosity"

Defines a luminosity value for the fluid. Not used by vanilla fluid block.

Optional. Default: 0.

Must be an integer.

## "temperature"

Defines a tmeperature value for the fluid. Not used by vanilla fluid flowing mechanics.

Optional. Default: 300.

Must be an integer.

## "density"

Defines a density value for the fluid. Not used by vanilla fluid flowing mechanics.

Optional. Default: 1000.

Must be an integer.

## "viscosity"

Defines a viscosity value for the fluid. Not used by vanilla fluid flowing mechanics.

Optional. Default: 1000.

Must be an integer.

## "gaseous"

Defines that the fluid should be considered gaseous. Not used by vanilla fluid flowing mechanics.
Mods are encouraged to display gaseous fluids using transparency instead of height.

Optional. Default: not gaseous.

Must be a boolean (`false` or `true`).

## "fill_sound"

Defines the sound that will be played when filling a bucket.

Optional. Default: no sound will be played when filling a bucket.

Must be a resource location string like `"water"`, or `"minecraft:block/water_still"`. Like on tag jsons and other vanilla files,
if the namespace (the part before the colon) is missing "minecraft" is implied.

## "empty_sound"

Defines the sound that will be played when emptying a bucket.

Optional. Default: no sound will be played when emptying a bucket.

Must be a resource location string like `"water"`, or `"minecraft:block/water_flowing"`. Like on tag jsons and other vanilla files,
if the namespace (the part before the colon) is missing "minecraft" is implied.

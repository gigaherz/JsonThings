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
  "fluid_type": "some:fluid_type",
  "fluid_type": { fluid type definition ... },
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
  "bucket": true
}
```

# "parent"

Defines another fluid to copy properties from.

Optional. Default: no parent.

Must be a resource location string like `"water"`, or `"minecraft:lava"`. Like on loot table jsons and other vanilla files,
if the namespace (the part before the colon) is missing "minecraft" is implied.

## "type"

Defines the fluid factory to use for constructing the fluid. Each factory can have additional properties.

Optional. Default: plain type without additional properties.

Must be a resource location string like `"plain"`, or `"minecraft:stairs"`. Like on tag jsons and other vanilla files,
if the namespace (the part before the colon) is missing "minecraft" is implied.

See the available fluid factory types in the [Fluid Factories](./FluidFactories.md) page.

## "fluid_type"

Defines the fluid type, which is a separate object that defines the attributes of the fluid, such as texture, or temperature.

Required.

Can be either a json object `{}`, or a resource location string like `"plain"`, or `"minecraft:stairs"`. Like on tag jsons and other vanilla files,
if the namespace (the part before the colon) is missing "minecraft" is implied.

See the available fluid types in the [Fluid Types](./FluidTypes.md) page.

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

# Fluid Types

Fluids in 1.19 take a FluidType object which is registered separately. For convenience, Json Things lets you declare your fluid types in the fluid.

The FluidType contains the basic properties of a fluid, such as its texture, or temperature.

Named FluidType definitions go in the `fluid_type` directory in the thing pack.

E.g.
```
/things/examplepack/fluid_type/mud.json
```

## Basic structure of the JSON file

```json
{
  "parent": "another:fluid_type",
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

  "motion_scale": 1.0,
  "fall_distance_modifier": 1.0,

  "can_push_entity": false,
  "can_swim": false,
  "can_drown": false,
  "can_extinguish": false,
  "can_hydrate": false,
  "can_convert_to_source": false,
  "supports_boating": false,
  
  "sounds": {
    "bucket_fill": "resource:location",
    "bucket_empty": "resource:location",
    "fluid_vaporize": "resource:location"
  }
}
```

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

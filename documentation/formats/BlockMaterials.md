# Armor Materials

Armor Materials define the tier and material properties of armor items.

Named item armor materials go in the `armor_material` directory in the thing pack.

Eg.
```
/things/examplepack/armor_material/rubber.json
```

## Basic structure of the JSON file

```json
{
  "map_color": "dirt",
  "liquid": false,
  "flammable": false,
  "replaceable": false,
  "solid": true,
  "blocks_motion": true,
  "solid_blocking": true,
  "push_reaction": "normal"
}
```

## "map_color"

Defines the color displayed in a map, from the choices available in the map color palette.

Required.

Must be either a string, or a positive integer between 0 and 63 (inclusive).

## "liquid"

Defines if the block is a liquid. Should only be used for fluid blocks.

Optional. Default: false.

Must be a boolean (`false` or `true`).

## "flammable"

Defines if the block can catch fire.

Optional. Default: false.

Must be a boolean (`false` or `true`).

## "replaceable"

Defines if the block can be replaced when placing another block. E.g. grass.

Optional. Default: false.

Must be a boolean (`false` or `true`).

## "solid"

Defines if the block is to be considered solid opaque. When false, the block will not cull nearby block faces.

Set this to `false` to prevent "X-Ray" effect on blocks with see-through textures.

Optional. Default: true.

Must be a boolean (`false` or `true`).

## "blocks_motion"

Defines if the block prevents entities from walking through it. When false, the block does not block movement. E.g. signs.

Optional. Default: true.

Must be a boolean (`false` or `true`).

## "solid_blocking"

Defines if the block is to be considered physically solid. When false, redstone will not pass through the block, and Iron Golems will not spawn on top.

Optional. Default: true.

Must be a boolean (`false` or `true`).

## "push_reaction"

Defines how the block reacts when pushed by a piston.

Optional. Default: normal.

Must be one of these strings:

* `"normal"`: The block can be pushed by pistons.
* `"block"`: The block cannot be pushed by pistons.
* `"destroy"`: The block breaks and drops its loot when pushed by pistons.

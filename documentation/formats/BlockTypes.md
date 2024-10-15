# Block Types

Blocks come in many types. Some common blocks require special handling such as special superclasses to be used in code.

To support those special blocks, there's a number of block types that can be specified in the json.

More types will be added in the future as needed.

## "plain"

Default block type.

Default render layer: solid.

No default blockstate properties.

## "falling"

A plain block that falls when not supported by a block underneath.

Default render layer: solid.

No default blockstate properties.

Parameters:
* `"dust_color"`: The color of the dust particle that appears when the block is unsupported but not yet falling. Can be an integer or an object. If it's an object it must have `"r"`, `"g"`, `"b"` and optionally `"a"`.
    * Optional. Default: white (r=g=b=255).

## "directional"

A block that can be placed in multiple cardinal directions, including up and down.

Default render layer: solid.

Default blockstate properties: facing

## "horizontal_directional"

A block that can be placed in multiple cardinal directions, but only the horizontal ones.

Default render layer: solid.

Default blockstate properties: facing

## "rotated_pillar"

A block that can be placed in axis directions, like logs and pillars, where opposing cardinal directions look the same.

Default render layer: solid.

Default blockstate properties: axis

## "slab"

A block that has the properties of a slab, including waterlogging.

Default render layer: solid.

Default blockstate properties: type, waterlogged

## "stairs"

A block that has the properties of a stair block, including waterlogging.

Default render layer: solid.

Default blockstate properties: facing, half, shape, waterlogged

Note: Parent block is required for this type.

## "wall"

A block that has the properties of a wall, including waterlogging.

Default render layer: solid.

Default blockstate properties: up, east_wall, north_wall, south_wall, west_wall, waterlogged

## "fence"

A block that has the properties of a fence, including waterlogging.

Default render layer: solid.

Default blockstate properties: east, north, south, west, waterlogged

## "fence_gate"

A block that has the properties of a fence gate.

Default render layer: solid.

Default blockstate properties: open, powered, in_wall

## "leaves"

A block that has the properties of leaves.

Default render layer: cutout_mipped. Also defaults to not solid.

Default blockstate properties: distance, persistent

## "door"

A block that has the properties of a wooden door.

Default render layer: cutout. Also defaults to not solid.

Default blockstate properties: facing, open, hinge, powered, half

## "trapdoor"

A block that has the properties of a wooden trapdoor, including waterlogging.

Default render layer: cutout. Also defaults to not solid.

Default blockstate properties: open, half, powered, waterlogged

## "sapling"

A sapling for a tree. Only supports single sapling growth, no 2x2 "mega tree".

Default render layer: cutout.

No default blockstate properties.

Parameters:
* `"tree_feature"`: The registry name of the feature this sapling will spawn once it grows.
    * Required.

## "liquid"

A block representing a fluid contained in the block grid.

Default render layer: translucent.

Default blockstate properties: level.

Parameters:
* `"fluid"`: The registry name of the contained fluid.
    * Required.

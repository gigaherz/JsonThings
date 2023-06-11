# Updating from Block Materials

Block Materials used to define the map color and some basic properties of blocks.

They have been removed in 1.20. Existing material definitions need to be moved to the block json 

## "map_color"

The existing map color property in the block needs to be used instead. The format is the same.

## "liquid"

I have decided not to provide a replacement for this. It is implied in the liquid block type.

## "flammable"

Use the "ignited_by_lava" property of the block.

## "replaceable"

The property has been moved as-is to the block.

## "solid"

Set the "not_solid" property of the block to true.

## "blocks_motion"

The property has been moved as-is to the block.

## "solid_blocking"

This property has no equivalent, it's defined by the voxel shape, and overriden by the "force_solid" and "blocks_motion" properties.

## "push_reaction"

The property has been moved as-is to the block.
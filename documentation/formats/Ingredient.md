# Ingredient definitions

Ingredients are not named, but rather used inside other json files.

Due to initialization timing, ingredients used in _Things_ cannot be custom (mod-defined) ingredients. Only `item` and `tag` ingredients are supported.

Item ingredients contain an `"item"` key with a resource location indicating the item registry name.

```json
{
  "item": "minecraft:clay"
}
```

Tag ingredients contain a `"tag"` key with a resource location indicating the item registry name.

```json
{
  "tag": "forge:string"
}
```
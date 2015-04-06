CC's Factory Manager
================

Allows [ComputerCraft](http://www.computercraft.info/) to handle items, fluids and RF similar to [Steve's Factory Manager](http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/1293066-1-7-2-steves-factory-manager) but with the power of lua.

Though you can, please don't just pastebin code for this peripheral. It can be very over powered and you are meant to work for it by spending time on programming. This is also the reason why there is only a very basic quick-start guide and the peripheral documentation.

Tutorials, Spotlights and "how-to"-guides are very welcome - just don't post finished code.


Quick-Start
----------------

1. Place a Factory Controller next to a Turtle
2. Use a few Factory Cables to connect the controller to an empty chest or place a chest besides the controller.
3. Right-click the Controller to open its GUI and name the turtle "hello" and the chest "world"
4. Put a distinctive item in the turtles first slot, open a lua shell on the turtle and run:

```lua
factory = peripheral.wrap("front")
factory.transferItem("hello", 1, "world", 1, 0)
```

5. It should say 1, because it transferred 1 item from the first slot of "hello" to the first-best slot (0) of the target "world", i.e.: ``` transferItem(source, source-slot, target, quantity, target-slot) ```


Permissions
-----------------

Feel free to use in any modpack. I won't answer messages/issues about this, please don't feel insulted.


Recipes
-----------------

Factory Controller         |  Factory Cable
:-------------------------:|:-------------------------:
![](/readme-images/FactoryController.png)  |  ![](/readme-images/FactoryCable.png)


Peripheral Documentation
-----------------

### Relative positions and Names

#### setName(x, y, z, name)
Sets the name of a position relative to the Factory Controller. This can also be done via the GUI of the Factory Controller block. Note that names are unique and setting a new name for a position might overwrite a old one.
The name is used by other Factory methods to refer to the block in that position.

#### getName(x, y, z)
Returns the name of the position.

#### getTarget(name)
Returns the position a name is refering to.

#### resetNames()
Clears the name of all positions. Useful for lua programs which are naming their blocks themselves.



### Targets and types

#### getTargets()
Returns a table containing details about all Blocks capable of handling items, fluids and/or energy, e.g. when connected to a single Casting Table:
```lua
{
  {
    Pos = { x = 5, y = 0, z = 1 },
    Types = { "item", "fluid" },
    Meta = 0,
    Block = "TConstruct:SearedBlock",
    Name = "Casting Table",
    Target = "IngotCast",
  },
}
```

#### getCapabilities(target-name), getCapabilitiesByPos(x, y, z)
Returns a table with the capabilities provided by the given target, e.g. for above Casting Table:
```lua
{
  item = true,
  fluid = true
}
```



### Inventory Handling

#### transferItem(source-name, source-slot, target-name, quantity, target-slot)
Returns the number of transferred items. If ```target-slot``` is 0 the first matching/free slot will be used.

#### getInventorySize(source-name)
Returns the number of slots of the inventory at ```source-name```.

#### getAllItems(source-name), getAllItemsByPos(x, y, z)
Returns a table containing all items stored in the inventory at that position. E.g.:
```lua
{
  {
    mod_id = "minecraft",
    raw_name = "tile.stone",
    max_size = 64,
    max_dmg = 0,
    qty = 32,
    dmg = 0,
    id = "minecraft:stone",
    ore_dict = { stone = true, },
    display_name = "Stone",
    name = "stone",
  },
  [ 11 ] = {
    display_name = "Enchanted Book",
    raw_name = "item.enchantedbook",
    max_size = 1,
    max_dmg = 0,
    qty = 1,
    dmg = 0,
    id = "minecraft:enchanted_book",
    nbt_id = "258bbd4a6be0fef5da627018fb8cc9b1",
    name = "enchanted_book",
    mod_id = "minecraft",
  },
}
```



### Fluid Handling

#### transferFluid(source, source-side, source-tank-id, target, target-side, amount)
Side is specified as "north", "up", "down" etc.
Tank-Id is the number of the tank stored in the block; this is usually ```1``` as most blocks only have one tank.
Returns the amount of mB transferred.

#### getTankInfo(source, side), getTankInfoByPos(x, y, z, side)
Returns details about the tank accessible from the given side for the given block. If side is ```""``` the details about all sides will be returned. E.g. for querying any specific side of an Extra-Utilities Drum filled with one bucket of Lava:
```
{
  {
    capacity = 256000,
    contents = {
      rawName = "Lava",
      amount = 1000,
      name = "lava",
      id = 2,
    },
  },
}
```



### RF/Energy Handling

#### transferEnergy(source, source-side, target, target-side, amount)
Side is specified as "north", "up", "down" etc.
Returns the amount of RF transferred.

#### getEnergyInfo(source, side), getEnergyInfoByPos(x, y, z, side)
Returns details about the RF stored at the given side of the given block.
```
{
  stored = 25200,
  capacity = 500000,
}
```

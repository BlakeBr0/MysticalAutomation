<recipetype:mysticalautomation:farmer>.addRecipe("test_farmer_no_crux",
  <item:mysticalagriculture:prosperity_seed_base>, <item:mysticalagriculture:inferium_farmland>, 3,
  {
    <item:minecraft:carrot>: 1.0 as float?,
    <item:minecraft:potato>: 0.25 as float?
  }
);

<recipetype:mysticalautomation:farmer>.addRecipe("test_farmer_crux",
  <item:mysticalagriculture:prosperity_seed_base>, <item:mysticalagriculture:prudentium_farmland>, <item:minecraft:cobblestone>, 5,
  {
    <item:minecraft:carrot>: 1.0 as float?,
    <item:minecraft:potato>: 0.25 as float?
  }
);

var recipes = <recipetype:mysticalautomation:farmer>.allRecipes;

println("There are " + recipes.length + " farmer recipes");
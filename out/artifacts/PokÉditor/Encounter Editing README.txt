What can be edited using PokÉditor
Author: Turtleisaac

Starting off with the personal data narc: all base stats, type 1, type 2, catch rate, exp yield, all EV yields (maximum value of 3 per EV), uncommon held item, rare held item, gender ratio (0-255, representing the number of encounters out of 254 that will be female. 0 is male only, 254 is female only. 255 is no gender.), hatch multiplier (number multiplied by 255 that represents amount of steps needed to hatch this species), base happiness, growth rate, egg group 1, egg group 2, ability 1, ability 2, run chance (safari zone only), a column I have lovingly labeled as "DO NOT TOUCH" (as Hiro's docs are clearly incorrect), and the TM learnset information for all TM's and HM's for this species.

Next up is the learnset narc, which kind of speaks for itself. Each entry is defined by a move, followed by the level it is to be learned at. All species have a maximum learnset move count of 20, and all entries after the end of a mon's learnset should just be left blank.

<<<<<<< HEAD
The next narc is the evolutions narc.  You can implement the method of evolution, what is required to evolve (representing a level, item, species, move, etc... depending on the selected method), and the resulting species. Each species has a maximum of 7 evolutions. If a pokemon has no evolution in a slot, it should have "None" as the defined method, the "Required" column should be left blank, and the "Result" column should contain "~" (tilde, the squiggly line)

Lastly is the final narc (for now), the encounters narc. With it, you can firstly change (on a by-area basis) (editing map header data is required to edit the associated encounter data for maps using that header) the master encounter rates for field encounters, water encounters (individual rates for each rod and surf), and rock smash encounters (all rates are out of 255).
=======
The next narc is the Evolutions narc.  You can implement the method of evolution, what is required to evolve (representing a level, item, species, move, etc... depending on the selected method), and the resulting species. Each species has a maximum of 7 evolutions. If a pokemon has no evolution in a slot, it should have "None" as the defined method, the "Required" column should be left blank, and the "Result" column should contain "~" (tilde, the squiggly line)

Lastly is the final narc (for now), the Encounters narc. With it, you can firstly change (on a by-area basis) (editing map header data is required to edit the associated encounter data for maps using that header) the master encounter rates for field encounters, water encounters (individual rates for each rod and surf), and rock smash encounters (all rates are out of 255).
>>>>>>> refs/remotes/origin/master

For field encounters you can edit the species that appears in any of 36 encounter slots: 12 for morning, 12 for day, 12 for night. Each slot for all three times share a hard-coded encounter rate and user-defined level (see program output to see what I mean). To make one species appear more frequently and at varying levels, simply put it in more encounter slots and vary the levels for those slots.

For water encounters, there are five encounter slots each for surf, old rod, good rod, and super rod. Each has a hard-coded encounter rate, and you are able to define a range of levels for a pokemon to appear at by defining a minimum and maximum level.

For rock smash encounters, there are two encounter slots, having hard-coded rates of 90% and 10%, respectively. You can define a range of levels once again using a minimum and maximum level.

Mass Outbreak/ Swarm data has four entries per-area, representing a species that will appear in a field encounter, surf encounter, good rod encounter, or super rod encounter. The levels for these encounters are defined by the first encounter slot for their respective encounter methods.

Pastoral/ Meridian Sound (Hoenn/ Sinnoh Sound) editing is super simple, all you need is to define species that appear in two slots for each area. The levels of these slots are defined by the field encounter slot 1 and slot 2 levels.

To finish off the encounter data editor, if you wish to have no encounter present in a slot, enter in "~" (tilde, the squiggly line) as the pokemon name. Level/ min level/ max level should be set to 0 for those null entries.
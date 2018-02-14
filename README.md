EvolutionEmulator 2.1
=====================
Aquatic Mastery Productions  
Jonathan Augustine
________________________________

A very basic attempt at emulating Earthen evolution with object based programming.

Emulating DNA passage from organism to offspring through 
	* Paired mating
	* Asexual replication
	* Neucliotide mutations 

	Package Tree

com.ampro.Evolution

1.)DNA
1.a.)-<Codon
1.b.)Gene
-<Trait(K)
->interpreter
->-<DNAReader
->-<Interpreter
>Environment
->Resources

>Populations
->Organisms
->->Descriptors
->->->Type
->->Predators

>util
	
# Evëmu: Evolution Emulator 3.0 (Kotlin)
#### Jonathan Augustine
#### Aquatic Mastery Productions
------------------------------

This program is a simple emulation of earthen evolution. It aims to propose how
fully random sequences of ribonucleic acids might lead to capable organisms.

<a name='PROC'></a>
## The Process
1. A randomly generated population is introduced to the environment

2. The population goes through 1 mating season of random mating pairs
    1. a The population of offspring should be Y organisms larger than the parent
population

3. A die-off event occurs and Y organisms are killed based off their "fitness"

4. opt-1) Repeat steps 2-3 Z number of times, adding each offspring generation
to the F1 population

5. After Z additions to genF1, go through a die-off event

6.
    1. Repeat steps 2-5 with each successive Fn generation until K number of
organisms exhibit the "perfect" or near-perfect sequence
    2. Change the Trait scores after V number of Fn generations


<a name='RMP'> Roadmap </a>
## Roadmap
- Genetics
    + Introns/Exons
    + Modular Trait base length?
    + Add depth to traits (What does what when where and negitive traits)
    + Each trait will serve different purposes (diet, sight, height, etc)
    + Mutations : proabability & location
- Male Female?
    + Sexual Selection? Maybe through types
- "Inept" modifier?
- Environments
    + Resource Pools
        + The resources will determine the avalibity of certain traits
        + Each codon will be in different availabilities based on the Environment?
     + Predator organisms that need to eat(kill) X number of lower/weaker
     organisms to survive
     + Changing environment in which certain traits may change from good
     to neutral or bad

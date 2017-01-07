##CSGO BET SIMULATOR
This program finds a strategy to bet on CSGO matches on the betting site CSGO-Lounge.
Old matches are parsed from the website with jsoup and stored locally in a Java Object file.

Disclosure: The CSGO Lounge maintainers probably don't approve of running this and I don't think it would work after a few updates to the website.

### Settings
The odds are calculated as a combination of different metrics
Currently the best combination of the metrics:
* The decrease of influence of a previous match with time passed is weighted as _0.0f_ (ignored). This means old matches count as much as new matches.
* The number of matches played by a team overall is weighted as _0.65f_
* Results of previous direct encounters is weighted _0.8f_


The difference of the calculated  to the official odds is should be _0.2f_ before a bet is placed.

###Install
With Maven installed, run "mvn install" to get the dependencies from the Maven repository.

###Run
Run Main.main() to run the simulator for 4 months with the current settings and see how much money you would make.
Run Main.main(n) to load the newest matches with n being the offset from the first match ever stored on the website.
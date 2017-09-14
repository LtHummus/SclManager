# SCL Manager #

This is an online service for managing the SpyParty Competitive League (SCL). This repo is the backend REST service for a frontend (not written by me) to interact with: upload completed games, retrieve lists of games, and get player standings. There is also an endpoint for the draft tool (the SpyPartyDraft repo) to interact with and post draft information so this code can link up map drafts to matches.

This project is built in Scala on the Scalatra web framework. [jOOQ](https://www.jooq.org/) is used for database access, [HikariCP](https://github.com/brettwooldridge/HikariCP) is used for connection pooling (which is admitedly overkill for a small-scale project like this), [Typesafe Config](https://github.com/typesafehub/config) is used for configuration file reading (with an AWS Key Management Service layer I built on top for storing secure data).

## Parsing Matches ##

I used my previous work in reverse engineering the SpyParty replay format (with an assist by Chris Hecker, the SpyParty dev) to build this system. By design, there is no authentication here. Player names are stored in the SpyParty replay themselves, so we can use that information to look up match data in the database. All of the code for parsing is located in the `com.lthummus.sclmanager.parsing` package, the main entry point for parsing an indivdual file is the `fromInputStream` function in the file `Replay.scala`.  Note that this code makes extensive use of scalaz disjunctions (the `\/` class).

## TODO List ##

While SclManager has been battle-tested in Season 3 of the SpyParty Competitive League, there are some things I would like to do better.

1. Add a stats page for interesting statistics gathered throughout the tournament.
1. Automatic creation of playoff/demotion matches. Right now their creation is manual.
1. Cleanup API and database calls. There's definitely some optimizations that can be made (especially on the db), but since the tournament is small (for now), they weren't high priority.
1. Build an admin UI for the tournament administrators. Right now, everything is mostly done by hand.
1. Clean up some mismatched terminology
  1. This is mostly caused because I did the db design first, forgetting that `match` is a reserved word in Scala. You'll see things referred to as a "match" in some places, and a "bout" in others.  Same thing happened with `type`

## The `com.lthummus.sclmanager.util` Package ##

Everything in here was made for either initializing the database for the season, for modifying a large number of matches (one player in a league dropped out and we almost completely re-arranged the schedule), or some other one-time purpose.

## Thank You List ##

* aforgottentune -- Author of the [front-end]([https://github.com/tfavazza/SCL-Manager)
* krazycaley, catnip -- SCL Administrators
* Chris Hecker -- small assist with the SpyParty replay format, making the game SpyParty

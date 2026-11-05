# ChaCuN

A digital adaptation of a tile-laying board game, written in Java with a JavaFX GUI. This was the semester project for EPFL CS-108 (Practice of Object-Oriented Programming), spring 2024, by Imad Bechlaghem and Tino Bianconi.

- `src/ch/epfl/chacun/`: game model. It covers tiles, zones and zone partitions, the board, scoring, the message board, the game state machine, and a Base32 action encoder for sharing moves between players.
- `src/ch/epfl/chacun/gui/`: JavaFX interface (board, decks, players, message board, actions).
- `test/`: JUnit tests.
- `resources/`: tile images and stylesheets supplied by the course.

## Run
Open the folder in IntelliJ with JDK 21+ and a JavaFX SDK on the module path, then run `ch.epfl.chacun.gui.Main`. Player names go in as program arguments.

Project specification and tile artwork © EPFL CS-108 course staff.

# ChaCuN

A digital version of a prehistoric-themed tile-laying board game for 2–5 players, written in Java with a JavaFX interface. Players draw and place landscape tiles, put down hunters and huts to claim forests, meadows, rivers and lakes, and score when areas close.

Semester project for EPFL CS-108 (Practice of Object-Oriented Programming), spring 2024, by Imad Bechlaghem and Tino Bianconi.

## Highlights
- **Immutable game model:** tiles, zones, areas and union-find-style zone partitions, plus a `GameState` state machine that drives every turn
- **Scoring engine:** handles closed forests and rivers, meadow hunts with animal cancellation, lake and river networks, and special tile powers
- **Message board:** a running log of every point scored, in French via `TextMakerFr`
- **Action encoding:** each move compresses to a short Base32 code (`ActionEncoder`), so players on separate machines can play by exchanging codes
- **JavaFX GUI:** board view with tile placement and rotation, decks, player panel, message board and action input
- **22 JUnit test classes** covering the model

## Layout
```
src/ch/epfl/chacun/        game model and rules
src/ch/epfl/chacun/gui/    JavaFX views (BoardUI, DecksUI, PlayersUI, …) and Main
test/ch/epfl/chacun/       JUnit 5 tests
resources/                 tile images and CSS (course-provided)
```

## Run
Open the folder in IntelliJ IDEA with JDK 21+ and the JavaFX SDK configured. Run `ch.epfl.chacun.gui.Main` with 2–5 player names as program arguments, for example `Alice Bob Carol`. An optional `--seed=<n>` makes the tile shuffle reproducible.

Game specification and tile artwork © EPFL CS-108 course staff.

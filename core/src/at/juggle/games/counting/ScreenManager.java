/*
 * This project and its source code is licensed under
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 * Copyright (c) 2017 Mathias Lux, mathias@juggle.at
 */

package at.juggle.games.counting;

import com.badlogic.gdx.Screen;

import at.juggle.games.counting.screens.CountingGameScreen;
import at.juggle.games.counting.screens.CreditsScreen;
import at.juggle.games.counting.screens.LoadingScreen;
import at.juggle.games.counting.screens.MenuScreen;
import at.juggle.games.counting.screens.SortingGameScreen;

/**
 * Created by Mathias Lux, mathias@juggle.at, on 04.02.2016.
 */
public class ScreenManager {
    public enum ScreenState {Loading, Menu, CountingGame, Credits, Help, SortingGame, GameOver};
    private Screen currentScreen;
    private ScreenState currentState;
    private CountingGame parentGame;

    public ScreenManager(CountingGame game) {
        this.parentGame = game;
        currentScreen = new LoadingScreen(game);
        currentState = ScreenState.Loading;
    }

    public Screen getCurrentScreen() {
        return currentScreen;
    }

    public ScreenState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(ScreenState state) {
        if (state != currentState) { // only if state changes.
            currentState = state;
            if (state == ScreenState.Menu) {
                currentScreen = new MenuScreen(parentGame);
                // parentGame.getSoundManager().startSong("main"); // starts the main theme.
            } else if (state == ScreenState.CountingGame) {
                parentGame.getSoundManager().fadeOut(); // fade out music ...
                currentScreen = new CountingGameScreen(parentGame);
            } else if (state == ScreenState.SortingGame) {
                parentGame.getSoundManager().fadeOut(); // fade out music ...
                currentScreen = new SortingGameScreen(parentGame);
            } else if (state == ScreenState.Credits) {
                parentGame.getSoundManager().fadeOut(); // fade out music ...
                currentScreen = new CreditsScreen(parentGame);
            }
        }
    }

    public CountingGame getParentGame() {
        return parentGame;
    }

    public void setParentGame(CountingGame parentGame) {
        this.parentGame = parentGame;
    }
}

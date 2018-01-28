package net.leejjon.bluffpoker.state;

import com.badlogic.gdx.Gdx;

import net.leejjon.bluffpoker.BluffPokerGame;
import net.leejjon.bluffpoker.logic.Player;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GameStateTest extends GdxTest {

    @Test
    public void testConsoleLogging_threeMessages_success() {
        GameState gameState = GameState.get();

        try {
            gameState.logGameConsoleMessage("Blabla");
            fail("Should not be able to log if UI is not initialized.");
        } catch (NullPointerException e) {}

        initializeUI(gameState);

        assertTrue(gameState.isNewGameState());

        String a = "a";
        String b = "b";
        String c = "c";

        gameState.logGameConsoleMessage(a);

        assertFalse(gameState.isNewGameState());
        verify(preferences, times(1)).flush();

        assertEquals(a, latestOutputLabel.getText().toString());
        assertEquals(a, gameState.latestOutput);

        gameState.logGameConsoleMessage(b);
        gameState.logGameConsoleMessage(c);

        assertEquals(a, thirdLatestOutputLabel.getText().toString());
        assertEquals(b, secondLatestOutputLabel.getText().toString());
        assertEquals(c, latestOutputLabel.getText().toString());
        assertEquals(a, gameState.thirdLatestOutput);
        assertEquals(b, gameState.secondLatestOutput);
        assertEquals(c, gameState.latestOutput);

        verify(preferences, times(3)).flush();
    }

    @Test
    public void testConstructPlayers() {
        loadDefaultPlayers();
        GameState gameState = GameState.get();
        assertEquals(2, gameState.getPlayers().length);

        Player currentPlayer = gameState.getCurrentPlayer();
        assertNotNull(currentPlayer);
        assertEquals("Leon", currentPlayer.getName());
        assertEquals(3, currentPlayer.getLives());
    }

    @Test
    public void loadDices() {
//        GameState gameState = GameState.get();
//        initializeUI(gameState);
//
//        gameState.saveGame();
//        String logMessageWithState = logMessages.get(0);
//        String state = logMessageWithState.substring(GameState.SAVED_GAMESTATE.length());
//
//        gameState.resetToNull();
//        when(preferences.getString(SelectPlayersStageState.KEY)).thenReturn(state);
//
//        gameState = GameState.get();
//        initializeUI(gameState);
    }

    private void loadDefaultPlayers() {
        List<String> playersList = Arrays.asList("Leon", "Dirk");
        GameState.get().constructPlayers(playersList, 3);
    }

    @Test
    public void testLogging() {
        String logMesssage = "Hoi";
        Gdx.app.log(BluffPokerGame.TAG, logMesssage);

        assertEquals(logMesssage, logMessages.get(0));
    }
}

package net.leejjon.bluffpoker.state;

import net.leejjon.bluffpoker.actors.CupActor;
import net.leejjon.bluffpoker.interfaces.Lockable;

import lombok.Data;

@Data
public class Cup implements Lockable {
    private boolean believing = false;
    private boolean watchingOwnThrow = false;
    private boolean locked = false;

    private transient CupActor cupActor;

    @Override
    public void lock() {
        locked = true;
        GameState.state().saveGame();
        cupActor.getLockImage().setVisible(true);
    }

    @Override
    public void unlock() {
        locked = false;
        GameState.state().saveGame();
        cupActor.getLockImage().setVisible(false);
    }

    public void believe() {
        believing = true;
        GameState.state().saveGame();
        cupActor.open();
    }

    public void doneBelieving() {
        believing = false;
        GameState.state().saveGame();
        cupActor.close();
    }

    public void watchOwnThrow() {
        watchingOwnThrow = true;
        GameState.state().saveGame();
        cupActor.open();
    }

    public void doneWatchingOwnThrow() {
        watchingOwnThrow = false;
        GameState.state().saveGame();
        cupActor.close();
    }

    public void update() {
        if (believing || watchingOwnThrow) {
            cupActor.open();
        } else {
            cupActor.close();
            if (locked) {
                cupActor.getLockImage().setVisible(true);
            }
        }
    }
}
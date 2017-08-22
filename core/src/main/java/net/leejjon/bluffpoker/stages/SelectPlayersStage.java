package net.leejjon.bluffpoker.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import net.leejjon.bluffpoker.BluffPokerGame;
import net.leejjon.bluffpoker.actors.BlackBoard;
import net.leejjon.bluffpoker.assets.TextureKey;
import net.leejjon.bluffpoker.dialogs.AddNewPlayerDialog;
import net.leejjon.bluffpoker.dialogs.PlayersFromPhonebookDialog;
import net.leejjon.bluffpoker.dialogs.WarningDialog;
import net.leejjon.bluffpoker.interfaces.ContactsRequesterInterface;
import net.leejjon.bluffpoker.interfaces.StageInterface;
import net.leejjon.bluffpoker.listener.ModifyPlayerListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeSet;

public class SelectPlayersStage extends AbstractStage implements ModifyPlayerListener {
    private java.util.List<String> players;
    private List<String> playerList;

    private WarningDialog playerAlreadyExistsWarning;
    private WarningDialog playerNameInvalid;
    private WarningDialog minimalTwoPlayersRequired;
    private final PlayersFromPhonebookDialog playersFromPhonebookDialog;

    public SelectPlayersStage(Skin uiSkin, final StageInterface stageInterface, final ContactsRequesterInterface contactsRequester) {
        super(false);

        playerAlreadyExistsWarning = new WarningDialog(uiSkin);
        playerNameInvalid = new WarningDialog("Player name empty or too long!", uiSkin);
        minimalTwoPlayersRequired = new WarningDialog("Select at least two players!", uiSkin);
        final AddNewPlayerDialog addNewPlayerDialog = new AddNewPlayerDialog(this);
        playersFromPhonebookDialog = new PlayersFromPhonebookDialog(uiSkin, this);

        players = new ArrayList<>();

        players.add(contactsRequester.getDeviceOwnerName());

        final float padding = 7f;

        List.ListStyle ls = uiSkin.get(List.ListStyle.class);
        ls.selection = addBordersToTextArea(ls.selection);
        ls.fontColorSelected = new Color(1f, 1f, 1f, 1.0f);
        playerList = new List<>(ls);
        playerList.setItems(players.toArray(new String[players.size()]));

        Texture callBoardTexture = stageInterface.getTexture(TextureKey.CALL_BOARD);
        BlackBoard choosePlayersBackground = new BlackBoard(callBoardTexture);

        Label chooseLabel = new Label("Choose", uiSkin, "arial32", Color.WHITE);
        Label playersLabel = new Label("players", uiSkin, "arial32", Color.WHITE);

        Table topTable = new Table();
        topTable.setFillParent(true);
        topTable.center();
        topTable.top();
        topTable.add(chooseLabel).colspan(2).padTop(chooseLabel.getHeight() - padding).padBottom(padding);
        topTable.row();
        topTable.add(playersLabel).colspan(2);
        topTable.row();

        ScrollPane playersScrollPane = new ScrollPane(playerList, uiSkin);
        playersScrollPane.setScrollingDisabled(true, false);

        int width = Gdx.graphics.getWidth() / BluffPokerGame.getDivideScreenByThis();
        int height = Gdx.graphics.getHeight() / BluffPokerGame.getDivideScreenByThis();

        table.center();
        table.bottom();

        // Take 50% of the screen.
        float fiftyPercentOfScreen = (width * 100) / 170;
        table.add(playersScrollPane).width(fiftyPercentOfScreen)
                .height((height * 100) / 200)
                .padBottom(padding);
        table.row();

        ImageButton up = new ImageButton(uiSkin, "up");
        up.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                swapPlayerUp();
            }
        });
        ImageButton down = new ImageButton(uiSkin, "down");
        down.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                swapPlayerDown();
            }
        });
        ImageButton remove = new ImageButton(uiSkin, "minus");
        remove.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                removeSelectedPlayer();
            }
        });
        ImageButton addButton = new ImageButton(uiSkin, "plus");
        addButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.input.getTextInput(addNewPlayerDialog, "Insert new player name", "", "Enter name here.");
            }
        });
        ImageButton phonebook = new ImageButton(uiSkin, "phonebook");
        phonebook.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                contactsRequester.initiateSelectContacts(SelectPlayersStage.this, new TreeSet<>(players));
            }
        });
        TextButton startGame = new TextButton("Start game", uiSkin);
        startGame.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                startGame(stageInterface);
            }
        });

        Table buttonTable = new Table();
        Cell<ImageButton> upCell = buttonTable.add(up).height(startGame.getHeight());
        Cell<ImageButton> downCell = buttonTable.add(down).height(startGame.getHeight());
        Cell<ImageButton> addCell = buttonTable.add(addButton).right().height(startGame.getHeight());
        Cell<ImageButton> removeCell = buttonTable.add(remove).right().height(startGame.getHeight());
        Cell<ImageButton> phoneBookCell = buttonTable.add(phonebook).right().height(startGame.getHeight());

        // Calculate the padding to make sure the buttons are nicely alligned with the list.
        float widthOfAllButtons = upCell.getMinWidth() + downCell.getMinWidth() + addCell.getMinWidth() + removeCell.getMinWidth() + phoneBookCell.getMinWidth();
        float buttonPadding = (fiftyPercentOfScreen - (widthOfAllButtons)) / 8;

        upCell.padRight(buttonPadding);
        downCell.padRight(buttonPadding * 5);
        removeCell.padLeft(buttonPadding);
        phoneBookCell.padLeft(buttonPadding);

        table.add(buttonTable).width(fiftyPercentOfScreen).center();
        table.row();
        table.add(startGame).center().pad(padding * 4);;

        addActor(choosePlayersBackground);
        addActor(topTable);
        addActor(table);
    }

    private Drawable addBordersToTextArea(Drawable drawable) {
        drawable.setLeftWidth(2f);
        drawable.setTopHeight(2f);
        drawable.setBottomHeight(2f);
        return drawable;
    }

    protected void startGame(StageInterface changeScreen) {
        if (players.size() < 2) {
            minimalTwoPlayersRequired.show(this);
        } else {
            changeScreen.startGame(players);
        }
    }

    private void swapPlayerUp() {
        int selectedIndex = playerList.getSelectedIndex();
        if (selectedIndex > 0) {
            Collections.swap(players, selectedIndex, selectedIndex - 1);
            playerList.setItems(players.toArray(new String[players.size()]));
        }
    }

    private void swapPlayerDown() {
        int selectedIndex = playerList.getSelectedIndex();
        if (selectedIndex > -1 && selectedIndex < players.size() - 1 && players.size() > 1) {
            Collections.swap(players, selectedIndex, selectedIndex + 1);
            playerList.setItems(players.toArray(new String[players.size()]));
        }
    }

    private void removeSelectedPlayer() {
        String selectedPlayer = playerList.getSelected();
        if (selectedPlayer != null) {
            players.remove(selectedPlayer);
            playerList.setItems(players.toArray(new String[players.size()]));
        }
    }

    private void addPlayersToGame(String ... playerNames) {
        final int maxNameLength = 16;

        for (String playerName : playerNames) {
            if (playerName.length() > maxNameLength && playerName.contains(" ")) {
                final String[] split = playerName.split("\\s+");
                playerName = split[0];

                // TODO: Add first letter of last name maybe? will suck if people have a lot of initials...
            }

            if (playerName.length() > 0 && !playerName.isEmpty() && playerName.length() <= maxNameLength) {
                if (!players.contains(playerName)) {
                    players.add(playerName);
                } else {
                    playerAlreadyExistsWarning.setRuntimeSpecificWarning("Player " + playerName + " already exists.");
                    playerAlreadyExistsWarning.show(this);
                }
            } else {
                playerNameInvalid.show(this);
            }
        }

        // Update the actual UI list with the new players.
        playerList.setItems(players.toArray(new String[players.size()]));
    }

    @Override
    public void addContactsToGame(String... playerNames) {
        addPlayersToGame(playerNames);
    }

    @Override
    public void selectFromPhoneBook(String... phoneBookContactNames) {
        playersFromPhonebookDialog.show(this);
        playersFromPhonebookDialog.addNewPlayer(phoneBookContactNames);
    }
}
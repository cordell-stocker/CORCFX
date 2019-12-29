/*
 * Copyright 2019, Cordell Stocker (cordellstocker@gmail.com)
 * All rights reserved.
 *
 * This file is part of CORCFX.
 *
 *     CORCFX is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     CORCFX is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with CORCFX.  If not, see <https://www.gnu.org/licenses/>.
 */
package corcfx.experimental;

import corc.core.Logger;
import corc.structure.CardsetListener;
import corc.structure.ICard;
import java.util.HashMap;
import java.util.List;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

/**
 *
 */
public class VisualHand extends BorderPane {

    private final ObservableList<Node> HAND_PANE_CHILDREN;
    private final HashMap<ICard, CardImageView> HASH_MAP = new HashMap<>();

    private volatile boolean addingCards;
    private volatile boolean removingCards;

    public VisualHand(Pane handPane) {
        this.HAND_PANE_CHILDREN = handPane.getChildren();
        this.setCenter(handPane);
    }

    public <C extends ICard> CardsetListener<C> makeListener(Class<C> clazz) {
        return new CardsetListener<>() {

            @Override
            public void cardsAdded(List<? extends C> cards) {
                addingCards = true;
                Platform.runLater(() -> {
                    addCards(cards);
                    addingCards = false;
                    startNotifyAll();
                });

                startWaitForAdding();
            }

            @Override
            public void cardsRemoved(List<? extends C> cards) {
                removingCards = true;
                Platform.runLater(() -> {
                    removeCards(cards);
                    removingCards = false;
                    startNotifyAll();
                });

                startWaitForRemoving();
            }
        };
    }

    protected void addCards(List<? extends ICard> cards) {
        for (ICard card : cards) {
            this.addCardImageView(card, new CardImageView(card));
        }
    }

    protected void removeCards(List<? extends ICard> cards) {
        for (ICard card : cards) {
            this.removeCardImageView(card);
        }
    }

    /**
     * By default, this method will add the CardImageView to
     * both the saved list and the handpane's children.
     *
     * @param cardImageView the CardImageView to be added.
     */
    protected void addCardImageView(ICard card, CardImageView cardImageView) {
        this.addToDisplayOnly(cardImageView);
        this.addToSavedOnly(card, cardImageView);
    }

    /**
     * By default, this method will remove the CardImageView
     * from both the saved list and the handpane's children.
     *
     * @param card the CardImageView to be removed.
     */
    protected void removeCardImageView(ICard card) {
        this.removeFromDisplayOnly(card);
        this.removeFromSavedOnly(card);
    }

    protected final void addToDisplayOnly(CardImageView cardImageView) {
        this.HAND_PANE_CHILDREN.remove(cardImageView);
        this.HAND_PANE_CHILDREN.add(cardImageView);
    }

    protected final void removeFromDisplayOnly(ICard card) {
        this.HAND_PANE_CHILDREN.remove(this.HASH_MAP.get(card));
    }

    protected final void addToSavedOnly(ICard card, CardImageView civ) {
        this.HASH_MAP.put(card, civ);
    }

    protected final void removeFromSavedOnly(ICard card) {
        this.HASH_MAP.remove(card);
    }

    protected final CardImageView getStoredCardImageView(ICard card) {
        return this.HASH_MAP.get(card);
    }

    /**
     * synchronized forces the caller to own this
     * object's monitor first, before performing
     * any actions.
     */
    private synchronized void startWaitForAdding() {
        /*
         * Why still use a boolean flag? Because a Thread can be
         * woken up before the condition is met.
         */
        while (addingCards) {
            try {
                wait();
            } catch (InterruptedException e) {
                Logger.logFatal(Thread.currentThread().getName() + " was Interrupted while waiting for Cards " +
                        "to be added.\n", e);
            }
        }
    }

    private synchronized void startWaitForRemoving() {
        while (removingCards) {
            try {
                wait();
            } catch (InterruptedException e) {
                Logger.logFatal(Thread.currentThread().getName() + " was Interrupted while waiting for Cards " +
                        "to be removed.\n", e);
            }
        }
    }

    private synchronized void startNotifyAll() {
        notifyAll();
    }

}

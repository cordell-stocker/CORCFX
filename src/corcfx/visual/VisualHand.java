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
package corcfx.visual;

import corc.core.Logger;
import corc.structure.CardsetListener;
import corc.structure.ICard;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;

public class VisualHand extends BorderPane {

    private final ObservableList<Node> HAND_PANE_CHILDREN;
    private final List<CardImageView> SAVED_CARD_IMAGE_VIEWS = new ArrayList<>();

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
            this.addCardImageView(new CardImageView(card));
        }
    }

    protected void removeCards(List<? extends ICard> cards) {
        List<CardImageView> toRemove = new ArrayList<>();
        for (ICard card : cards) {
            for (CardImageView cardImageView : this.getSavedCardImageViews()) {
                if (cardImageView.getCard() == card) {
                    toRemove.add(cardImageView);
                    break;
                }
            }
        }
        for (CardImageView cardImageView : toRemove) {
            this.removeCardImageView(cardImageView);
        }
    }

    /**
     * By default, this method will add the CardImageView to
     * both the saved list and the handpane's children.
     *
     * @param cardImageView the CardImageView to be added.
     */
    protected void addCardImageView(CardImageView cardImageView) {
        this.addToSavedOnly(cardImageView);
        this.addToDisplayOnly(cardImageView);
    }

    /**
     * By default, this method will remove the CardImageView
     * from both the saved list and the handpane's children.
     *
     * @param cardImageView the CardImageView to be removed.
     */
    protected void removeCardImageView(CardImageView cardImageView) {
        this.removeFromSavedOnly(cardImageView);
        this.removeFromDisplayOnly(cardImageView);
    }

    protected final void addToDisplayOnly(CardImageView cardImageView) {
        this.HAND_PANE_CHILDREN.remove(cardImageView);
        this.HAND_PANE_CHILDREN.add(cardImageView);
    }

    protected final void removeFromDisplayOnly(CardImageView cardImageView) {
        this.HAND_PANE_CHILDREN.remove(cardImageView);
    }

    protected final void addToSavedOnly(CardImageView cardImageView) {
        this.SAVED_CARD_IMAGE_VIEWS.add(cardImageView);
    }

    protected final void removeFromSavedOnly(CardImageView cardImageView) {
        this.SAVED_CARD_IMAGE_VIEWS.remove(cardImageView);
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

    protected List<CardImageView> getSavedCardImageViews() {
        return this.SAVED_CARD_IMAGE_VIEWS;
    }

}

/*
Copyright 2019, Cordell Stocker (cordellstocker@gmail.com)
All rights reserved.

This file is part of CORCFX.

    CORCFX is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    CORCFX is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with CORCFX.  If not, see <https://www.gnu.org/licenses/>.
*/
package corcfx.structure;

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

public abstract class AbstractVisualHand<C extends ICard, CIV extends AbstractCardImageView<C>> extends BorderPane {

    private final Pane HAND_PANE;
    private final ObservableList<Node> HAND_PANE_CHILDREN;
    private final List<CIV> SAVED_CARD_IMAGE_VIEWS = new ArrayList<>();

    private volatile boolean addingCards;
    private volatile boolean removingCards;

    private CardsetListener<C> listener = new CardsetListener<>() {
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

    public AbstractVisualHand(Pane handPane) {
        this.HAND_PANE = handPane;
        this.HAND_PANE_CHILDREN = handPane.getChildren();
        this.setCenter(handPane);
    }

    protected abstract void addCards(List<? extends C> cards);

    protected abstract void removeCards(List<? extends C> cards);

    /**
     * By default, this method will add the CardImageView to
     * both the saved list and the handpane's children.
     *
     * @param cardImageView the CardImageView to be added.
     */
    protected void addCardImageView(CIV cardImageView) {
        this.addToSavedOnly(cardImageView);
        this.addToDisplayOnly(cardImageView);
    }

    /**
     * By default, this method will remove the CardImageView
     * from both the saved list and the handpane's children.
     *
     * @param cardImageView the CardImageView to be removed.
     */
    protected void removeCardImageView(CIV cardImageView) {
        this.removeFromSavedOnly(cardImageView);
        this.removeFromDisplayOnly(cardImageView);
    }

    protected final void addToDisplayOnly(CIV cardImageView) {
        this.HAND_PANE_CHILDREN.remove(cardImageView);
        this.HAND_PANE_CHILDREN.add(cardImageView);
    }

    protected final void removeFromDisplayOnly(CIV cardImageView) {
        this.HAND_PANE_CHILDREN.remove(cardImageView);
    }

    protected final void addToSavedOnly(CIV cardImageView) {
        this.SAVED_CARD_IMAGE_VIEWS.add(cardImageView);
    }

    protected final void removeFromSavedOnly(CIV cardImageView) {
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

    public CardsetListener<C> getCardsetListener() {
        return this.listener;
    }

    public Pane getHandPane() {
        return this.HAND_PANE;
    }

    protected List<CIV> getSavedCardImageViews() {
        return this.SAVED_CARD_IMAGE_VIEWS;
    }

}

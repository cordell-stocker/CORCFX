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
 * Displays multiple {@link CardImageView}s that are typically
 * associated with cards passed through an
 * {@link corc.structure.ListenableCardset}.
 * <p>
 * A {@link CardsetListener} linked to this (an instance), which will
 * handle adding and removing cards to and from this is provided,
 * such that the CardImageViews displayed will be a direct
 * representation of the associated Cardset.
 *
 * @param <C>
 */
public class VisualHand<C extends ICard> extends BorderPane {

    /**
     * A predefined {@link CardsetListener} to handle the addition
     * and removal of respective {@link CardImageView}s to this.
     * <p>
     * The listener will call {@link Thread#wait()} on the calling
     * {@link Thread} until the process of adding or removing cards
     * has finished. The Thread calling the methods of the listener
     * MUST NOT be the FXThread.
     * <p>
     * This listener SHOULD only be given to a single Cardset. So that
     * this VisualHand displays a direct representation of the
     * associated Cardset.
     */
    private final CardsetListener<C> cardsetListener = new CardsetListener<>() {
        @Override
        public void cardsAdded(List<? extends C> cards) {
            addingCards = true;
            Platform.runLater(() -> {
                addCards(cards);
                addingCards = false;
                startNotifyAll();
            });

            startWaitForAddingFinished();
        }

        @Override
        public void cardsRemoved(List<? extends C> cards) {
            removingCards = true;
            Platform.runLater(() -> {
                removeCards(cards);
                removingCards = false;
                startNotifyAll();
            });

            startWaitForRemovingFinished();
        }
    };

    private final HashMap<C, CardImageView<C>> hashMap = new HashMap<>();
    private final ObservableList<Node> handPaneChildren;
    private final CardUrlResolver<C> urlResolver;

    private volatile boolean addingCards;
    private volatile boolean removingCards;

    /**
     * Constructs a {@link BorderPane} capable of handling the visual
     * representation of cards.
     * <p>
     * The specified {@link Pane} is stored as the center Pane.
     *
     * @param handPane    the Pane that will store
     *                    {@link CardImageView}s
     * @param urlResolver the CardUrlResolver to be used to obtain
     *                    String URLS for the front and back images
     *                    of cards added to this.
     */
    public VisualHand(Pane handPane, CardUrlResolver<C> urlResolver) {
        this.setCenter(handPane);
        this.handPaneChildren = handPane.getChildren();
        this.urlResolver = urlResolver;
    }

    /**
     * Handles the addition of cards to this.
     * <p>
     * WARNING: This method will be called inside of the FXThread by
     * the built-in {@link CardsetListener} whenever cards are added
     * through the listener.
     * <p>
     * Creates the {@link CardImageView}s from the specified list of
     * cards as well as passes the created CardImageViews to the
     * {@link VisualHand#addCardImageView(CardImageView)} method.
     * <p>
     * If this method is overridden, the implementor MUST still
     * handle the process described above.
     *
     * @param cards the cards to have their visual representations
     *              added to this.
     */
    protected void addCards(List<? extends C> cards) {
        for (C card : cards) {
            CardImageView<C> civ = new CardImageView<>(card, urlResolver);
            addCardImageView(civ);
        }
    }

    /**
     * Handles the addition of {@link CardImageView}s to this.
     * <p>
     * By default, this method only calls
     * {@link VisualHand#addCardImageViewToHashMap(CardImageView)}
     * and {@link VisualHand#addCardImageViewToHandPane(CardImageView)}
     * in that order.
     * <p>
     * This method MAY be overridden to support additional logic such
     * as animations. If this method is overridden, the new method
     * MUST call both the methods described above in the same order
     * which are called by this default method.
     *
     * @param civ the CardImageView being added to this.
     */
    protected void addCardImageView(CardImageView<C> civ) {
        this.addCardImageViewToHashMap(civ);
        this.addCardImageViewToHandPane(civ);
    }

    protected final void addCardImageViewToHashMap(CardImageView<C> civ) {
        this.hashMap.put(civ.getCard(), civ);
    }

    protected final void addCardImageViewToHandPane(CardImageView<C> civ) {
        this.handPaneChildren.remove(civ); // Prevents duplicate children errors.
        this.handPaneChildren.add(civ);
    }

    /**
     * Handles the removal of cards from this.
     * <p>
     * WARNING: This method will be called inside of the FXThread by
     * the built-in {@link CardsetListener} whenever cards are removed
     * through the listener.
     * <p>
     * Gets the associated {@link CardImageView}s in this from the
     * specified list of cards and passes the CardImageViews to the
     * {@link VisualHand#removeCardImageView(CardImageView)} method.
     * <p>
     * If this method is overridden, the implementor MUST still
     * handle the process described above.
     *
     * @param cards the cards to have their visual representations
     *              removed from this.
     */
    protected void removeCards(List<? extends C> cards) {
        for (C card : cards) {
            this.removeCardImageView(this.hashMap.get(card));
        }
    }

    /**
     * Handles the removal of {@link CardImageView}s from this.
     * <p>
     * By default, this method only calls
     * {@link VisualHand#removeCardImageViewFromHandPane(CardImageView)}
     * and {@link VisualHand#removeCardImageViewFromHashMap(CardImageView)}
     * in that order.
     * <p>
     * This method MAY be overridden to support additional logic such
     * as animations. If this method is overridden, the new method
     * MUST call both the methods described above in the same order
     * which are called by this default method.
     *
     * @param civ the CardImageView being removed this.
     */
    protected void removeCardImageView(CardImageView<C> civ) {
        this.removeCardImageViewFromHandPane(civ);
        this.removeCardImageViewFromHashMap(civ);
    }

    protected final CardImageView<C> removeCardImageViewFromHashMap(CardImageView<C> civ) {
        return this.hashMap.remove(civ.getCard());
    }

    protected final void removeCardImageViewFromHandPane(CardImageView<C> civ) {
        this.handPaneChildren.remove(civ);
    }

    protected CardImageView<C> getCardImageViewFromHashMap(C card) {
        return this.hashMap.get(card);
    }

    /**
     * Returns the predefined {@link CardsetListener} linked to this.
     * <p>
     * The listener will call {@link Thread#wait()} on the calling
     * {@link Thread} until the process of adding or removing cards
     * has finished. The Thread calling the methods of the listener
     * MUST NOT be the FXThread.
     * <p>
     * If the Thread processing the adding or removing of cards is
     * interrupted, an exception's stack trace will be printed to the
     * standard error stream at the point of failure. This will not
     * stop the program, but the result of changes to this VisualHand
     * is undefined behavior.
     * <p>
     * This listener SHOULD only be given to a single Cardset. So that
     * this VisualHand displays a direct representation of the
     * associated Cardset.
     *
     * @return the listener linked with this.
     */
    public CardsetListener<C> getCardsetListener() {
        return this.cardsetListener;
    }

    private synchronized void startWaitForAddingFinished() {
        while (addingCards) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.err.println(Thread.currentThread().getName() + " was Interrupted while" +
                        "waiting for Cards to be added.");
                e.printStackTrace();
            }
        }
    }

    private synchronized void startWaitForRemovingFinished() {
        while (removingCards) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.err.println(Thread.currentThread().getName() + " was Interrupted while" +
                        "waiting for Cards to be removed.");
                e.printStackTrace();
            }
        }
    }

    private synchronized void startNotifyAll() {
        notifyAll();
    }
}

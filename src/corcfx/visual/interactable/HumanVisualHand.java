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

package corcfx.visual.interactable;

import corc.structure.ICard;
import corcfx.visual.CardImageView;
import corcfx.visual.CardUrlResolver;
import corcfx.visual.VisualHand;

/**
 * A {@link VisualHand} that uses an {@link OrganizablePane} to hold
 * its children.
 *
 * @param <C>
 */
public abstract class HumanVisualHand<C extends ICard> extends VisualHand<C> {

    private final OrganizablePane HAND_PANE;

    /**
     * Creates a {@link VisualHand} with an {@link OrganizablePane}
     * to hold its children.
     *
     * @param handPane the OrganizablePane to use.
     * @param resolver the CardUrlResolver to be used to obtain
     *                 String URLS for the front and back images
     *                 of cards added to this.
     */
    public HumanVisualHand(OrganizablePane handPane, CardUrlResolver<C> resolver) {
        super(handPane, resolver);
        this.HAND_PANE = handPane;
    }

    /**
     * A wrapper method for the {@link OrganizablePane} to get the
     * card that has been selected.
     *
     * If an error occurs while getting the selected card, the
     * exception will be printed to the standard error stream and
     * the method will return null.
     *
     * @return the card that has been selected.
     */
    public C getSelectedCard() {
        try {
            //noinspection unchecked
            CardImageView<C> cardImageView = (CardImageView<C>) this.HAND_PANE.getSelectedNode();
            return cardImageView.getCard();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Deselects the currently selected card.
     *
     * A wrapper method for the {@link OrganizablePane#deselectNode()}.
     */
    public void deselectCard() {
        this.HAND_PANE.deselectNode();
    }

}

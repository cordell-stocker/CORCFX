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
import corcfx.visual.VisualHand;

public class HumanVisualHand extends VisualHand {

    private final OrganizablePane HAND_PANE;

    public HumanVisualHand(OrganizablePane handPane) {
        super(handPane);
        this.HAND_PANE = handPane;
    }

    public ICard getSelectedCard() {
        try {
            CardImageView cardImageView = (CardImageView) this.HAND_PANE.getSelectedNode();
            return cardImageView.getCard();
        } catch (NullPointerException ex) {
            return null;
        }
    }

    public void deselectCard() {
        this.HAND_PANE.clearSelectedNode();
    }

    @Override
    protected void addCardImageView(corcfx.visual.CardImageView CIV) {
        this.addToSavedOnly(CIV);
        HAND_PANE.addNodeAfterLast(CIV);
    }

    @Override
    protected void removeCardImageView(corcfx.visual.CardImageView CIV) {
        this.removeFromSavedOnly(CIV);
        HAND_PANE.getChildren().remove(CIV);
        HAND_PANE.orderChildren();
    }
}

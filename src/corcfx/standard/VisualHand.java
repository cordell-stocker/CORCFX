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
package corcfx.standard;

import corc.javafxextend.structure.AbstractVisualHand;
import corc.standard.Card;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;

public class VisualHand extends AbstractVisualHand<Card, CardImageView> {

    public VisualHand(Pane handPane) {
        super(handPane);
    }

    @Override
    protected synchronized void addCards(List<? extends Card> cards) {
        List<CardImageView> toAdd = new ArrayList<>();
        for (Card card : cards) {
            CardImageView cardImageView = new CardImageView(card);
            toAdd.add(cardImageView);
        }
        for (CardImageView cardImageView : toAdd) {
            this.addCardImageView(cardImageView);
        }
    }

    @Override
    protected synchronized void removeCards(List<? extends Card> cards) {
        List<CardImageView> toRemove = new ArrayList<>();
        for (Card card : cards) {
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

}

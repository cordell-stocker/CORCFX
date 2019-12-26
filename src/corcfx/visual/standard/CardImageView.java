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
package corcfx.visual.standard;

import corc.standard.Card;

public class CardImageView extends corcfx.visual.CardImageView {

    public CardImageView(Card card) {
        super(card);
    }

    public CardImageView(CardImageView civ) {
        super(civ);
    }

    public CardImageView(Card card, boolean isFaceUp) {
        super(card, isFaceUp);
    }

    @Override
    public Card getCard() {
        return (Card) super.getCard();
    }
}

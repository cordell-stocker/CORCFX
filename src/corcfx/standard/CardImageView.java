/*
Copyright 2019, Cordell Stocker (cordellstocker@gmail.com)
All rights reserved.

This file is part of CORC.

    CORC is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    CORC is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with CORC.  If not, see <https://www.gnu.org/licenses/>.
*/
package corcfx.standard;

import corc.javafxextend.structure.AbstractCardImageView;
import corc.standard.Card;
import corc.standard.Face;
import corc.standard.Suit;

public class CardImageView extends AbstractCardImageView<Card> {

    private static final String RES_PATH = "corcfx/resources/";
    private static final String IMG_EXT = ".png";
    private static final String BACK_URL = CardImageView.RES_PATH + "back_blue_vertical" + CardImageView.IMG_EXT;

    public CardImageView() {
        super(new Card(Face.ACE, Suit.SPADES), CardImageView.BACK_URL, CardImageView.BACK_URL, true);
    }

    public CardImageView(Card card) {
        this(card, true);
    }

    public CardImageView(Card card, boolean isFaceUp) {
        super(card, CardImageView.getFrontUrlFromCard(card), CardImageView.BACK_URL, isFaceUp);
    }

    public <I extends AbstractCardImageView<Card>> CardImageView(I cardImageView) {
        super(cardImageView);
    }

    private static String getFrontUrlFromCard(Card card) {
        return CardImageView.RES_PATH + card.getFace().getName().toLowerCase() + "_" +
                card.getSuit().getName().toLowerCase() + CardImageView.IMG_EXT;
    }

    @Override
    public int compareTo(AbstractCardImageView<Card> o) {
        return this.getCard().compareTo(o.getCard());
    }
}

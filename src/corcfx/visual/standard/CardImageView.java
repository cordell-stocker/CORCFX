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
import corc.standard.Face;
import corc.standard.Suit;
import corcfx.visual.CardUrlResolver;

/**
 * A wrapper class for {@link corcfx.experimental.CardImageView} that
 * uses the standard {@link Card}.
 */
public class CardImageView extends corcfx.visual.CardImageView<Card> {

    /**
     * Creates a default CardImageView that uses the ACE of SPADES
     * from the standard {@link Card}, and is face-down.
     */
    public CardImageView() {
        super(new Card(Face.ACE, Suit.SPADES), CardUrlResolver.STANDARD_CARD_RESOLVER, false);
    }

    /**
     * Copy Constructor wrapper for {@link corcfx.experimental.CardImageView}.
     *
     * @param civ the CardImageView to be copied.
     */
    public CardImageView(CardImageView civ) {
        super(civ);
    }

    /**
     * Creates a CardImageView representing the specified {@link Card}.
     * This CardImageView will appear face-up.
     *
     * @param card the Card to represent.
     */
    public CardImageView(Card card) {
        super(card, CardUrlResolver.STANDARD_CARD_RESOLVER);
    }

    /**
     * Creates a CardImageView representing the specified {@link Card}.
     *
     * @param card     the Card to represent.
     * @param isFaceUp true if the front {@link javafx.scene.image.Image}
     *                 will show, false for the back Image.
     */
    public CardImageView(Card card, boolean isFaceUp) {
        super(card, CardUrlResolver.STANDARD_CARD_RESOLVER, isFaceUp);
    }

}

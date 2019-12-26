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

import corc.standard.Card;
import corc.structure.ICard;

/**
 * Defines the rules for obtaining a String URL for the location of the
 * front and back images of a subclass of {@link ICard}.
 *
 * A {@link CardURLResolver} is provided for the {@link Card} class.
 *
 * @param <C> the subclass of ICard being used.
 */
public interface CardURLResolver<C extends ICard> {

    String STANDARD_CARD_PATH = "corcfx/resources/";
    String STANDARD_CARD_EXTENSION = ".png";
    CardURLResolver<Card> STANDARD_CARD_RESOLVER = new CardURLResolver<>() {
        @Override
        public String getCardFrontURL(Card card) {
            return String.format("%s%s_%s%s", STANDARD_CARD_PATH, card.getFace().getName().toLowerCase(),
                    card.getSuit().getName().toLowerCase(), STANDARD_CARD_EXTENSION);
        }

        @Override
        public String getCardBackURL(Card card) {
            return String.format("%sback_blue_vertical%s", STANDARD_CARD_PATH, STANDARD_CARD_EXTENSION);
        }
    };

    /**
     * Gets the String URL for the front image of the card.
     *
     * Handles the logic of obtaining a String URL from
     * a card object to be used to instantiate an
     * {@link javafx.scene.image.Image}
     *
     * @param card the card to be used.
     * @return the String URL for the front image.
     */
    String getCardFrontURL(C card);

    /**
     * Gets the String URL for the back image of the card.
     *
     * Handles the logic of obtaining a String URL from
     * a card object to be used to instantiate an
     * {@link javafx.scene.image.Image}
     *
     * @param card the card to be used.
     * @return the String URL for the back image.
     */
    String getCardBackURL(C card);
}

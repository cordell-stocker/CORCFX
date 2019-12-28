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
 * Provides methods to obtain String URLs for the front and back
 * images of a card.
 * <p>
 * Implementations MUST NOT contain any members except those specified
 * by this. The implementation MUST be fully immutable and stateless.
 * <p>
 * For an example implementation see
 * {@link CardUrlResolver#STANDARD_CARD_RESOLVER}.
 * <p>
 * A {@link CardUrlResolver} is provided for the {@link Card} class;
 * The path and extension for the included images are also provided.
 * The file names follow the naming convention of "face_suit" all
 * lowercase, so a full example would be
 * "corcfx/resources/ace_spades.png".
 * Exceptions include: back_blue_horizontal.png,
 * back_blue_vertical.png, back_red_horizontal.png,
 * back_red_vertical.png, joker_black.png, and joker_red.png.
 *
 * @param <C>
 */
public interface CardUrlResolver<C extends ICard> {

    String STANDARD_CARD_PATH = "corcfx/resources/";
    String STANDARD_CARD_EXTENSION = ".png";

    /**
     * Default resolver for the {@link Card} class.
     */
    CardUrlResolver<Card> STANDARD_CARD_RESOLVER = new CardUrlResolver<>() {
        @Override
        public String getCardFrontURL(Card card) {
            return String.format(
                    "%s%s_%s%s",
                    STANDARD_CARD_PATH,
                    card.getFace().getName().toLowerCase(),
                    card.getSuit().getName().toLowerCase(),
                    STANDARD_CARD_EXTENSION
            );
        }

        @Override
        public String getCardBackURL(Card card) {
            return String.format(
                    "%sback_blue_vertical%s",
                    STANDARD_CARD_PATH,
                    STANDARD_CARD_EXTENSION
            );
        }
    };

    /**
     * Returns the cards's String URL for the front image.
     * <p>
     * The implementation SHOULD return a valid URL which
     * can be used with the {@link javafx.scene.image.Image}
     * constructor.
     *
     * @param card the card.
     * @return the String URL for the front image.
     */
    String getCardFrontURL(C card);

    /**
     * Returns the cards's String URL for the back image.
     * <p>
     * The implementation SHOULD return a valid URL which
     * can be used with the {@link javafx.scene.image.Image}
     * constructor.
     *
     * @param card the card.
     * @return the String URL for the back image.
     */
    String getCardBackURL(C card);
}

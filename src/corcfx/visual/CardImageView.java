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

import corc.structure.ICard;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Visual card representation.
 * <p>
 * Uses a front image and a back image that can be switched
 * between to represent a card as either face-up or face-down.
 * <p>
 * If either {@link Image} is unable to be created, no image
 * will be shown by this. An exception stack trace will be
 * printed to the standard error stream.
 *
 * @param <C>
 */
public class CardImageView<C extends ICard> extends ImageView {

    private final C card;
    private final CardUrlResolver<C> urlResolver;
    private Image frontImage;
    private Image backImage;
    private boolean isFaceUp;

    /**
     * Copy constructor.
     * <p>
     * Uses the same card and CardURLResolver instances as the
     * specified CardImageView as well as the same face-up value.
     *
     * @param civ the CardImageView to be copied.
     */
    public CardImageView(CardImageView<C> civ) {
        this(civ.card, civ.urlResolver, civ.isFaceUp);
    }

    /**
     * Constructs this using the specified values.
     * <p>
     * The CardImageView will show the front image.
     *
     * @param card        the card to represent.
     * @param urlResolver the CardUrlResolver to be used to obtain
     *                    String URLS for the front and back images
     *                    of the specified card.
     */
    public CardImageView(C card, CardUrlResolver<C> urlResolver) {
        this(card, urlResolver, true);
    }

    /**
     * Constructs this using the specified values.
     *
     * @param card        the card to represent.
     * @param urlResolver the CardUrlResolver to be used to obtain
     *                    String URLS for the front and back images
     *                    of the specified card.
     * @param isFaceUp    true to show the front image, false to show
     *                    the back image.
     */
    public CardImageView(C card, CardUrlResolver<C> urlResolver, boolean isFaceUp) {
        this.card = card;
        this.urlResolver = urlResolver;
        this.isFaceUp = isFaceUp;

        try {
            frontImage = new Image(urlResolver.getCardFrontURL(card));
            backImage = new Image(urlResolver.getCardBackURL(card));
        } catch (IllegalArgumentException e) {
            this.frontImage = null;
            this.backImage = null;
            System.err.println("Invalid URL or resource not found: " + card.getDescription());
            e.printStackTrace();
        }

        update();
    }

    public void setFaceUp(boolean isFaceUp) {
        // Only perform actions if the call to this
        // method would result in a change of state.
        if (this.isFaceUp != isFaceUp) {
            this.isFaceUp = isFaceUp;
            this.update();
        }
    }

    public boolean isFaceUp() {
        return this.isFaceUp;
    }

    public C getCard() {
        return this.card;
    }

    public CardUrlResolver<C> getUrlResolver() {
        return this.urlResolver;
    }

    private void update() {
        Image image = this.isFaceUp ? this.frontImage : this.backImage;
        this.setImage(image);
    }

}

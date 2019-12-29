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

package corcfx.experimental;

import static corcfx.visual.CardUrlResolver.STANDARD_CARD_RESOLVER;

import corc.standard.Card;
import corc.structure.ICard;
import corcfx.visual.CardUrlResolver;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * The visual representation of an {@link ICard} object using an {@link ImageView}.
 * <p>
 * This class provides support for the standard {@link Card}s by default.
 * To add support for different types of ICards, a {@link CardUrlResolver} will
 * need to be created for the subclass and added using the
 * {@link CardImageView#addCardURLResolver(Class, CardUrlResolver)}.
 * <p>
 * The card specified in the constructor will be ran against the known {@link CardUrlResolver}s
 * in this class. If both the front and back {@link Image}s cannot be made using the
 * CardURLResolvers, a {@link RuntimeException} will be thrown.
 * <p>
 * If a CardURLResolver exists specifically for the subclass of {@link ICard} given,
 * only that CardURLResolver will be attempted to generate the images. Otherwise
 * all the CardURLResolvers will be searched through to attempt applying CardURLResolvers
 * of known superclasses of the specified card. If no working CardURLResolver is found, a
 * RunTimeException will be thrown.
 */
public class CardImageView extends ImageView implements Comparable<CardImageView> {

    private static final Map<Class<? extends ICard>, CardUrlResolver> DEFAULT_RESOLVERS = Map.ofEntries(
            Map.entry(Card.class, STANDARD_CARD_RESOLVER)
    );

    private static final HashMap<Class<? extends ICard>, CardUrlResolver> CARD_RESOLVERS =
            new HashMap<>(DEFAULT_RESOLVERS);

    /**
     * Adds support for a subclass of {@link ICard} to be created by this class.
     *
     * @param clazz        the subclass of ICard to have support added for.
     * @param nameResolver the {@link CardUrlResolver} to be associated with the specified {@link Class}.
     * @param <T>          the subclass of ICard being used.
     */
    public static <T extends ICard> void addCardURLResolver(Class<T> clazz, CardUrlResolver<T> nameResolver) {
        if (!CardImageView.CARD_RESOLVERS.containsKey(clazz)) {
            CardImageView.CARD_RESOLVERS.put(clazz, nameResolver);
        } else {
            CardImageView.CARD_RESOLVERS.replace(clazz, nameResolver);
        }
    }

    /**
     * The card specified will be ran against the known {@link CardUrlResolver}s
     * in this class. If both the front and back {@link Image}s cannot be made using the
     * CardURLResolvers, a {@link RuntimeException} will be thrown.
     * <p>
     * If a CardURLResolver exists specifically for the subclass of {@link ICard} given,
     * only that CardURLResolver will be attempted to generate the images. Otherwise
     * all the CardURLResolvers will be searched through to attempt applying CardURLResolvers
     * of known superclasses of the specified card. If no working CardURLResolver is found, a
     * RunTimeException will be thrown.
     *
     * @param card the card to get String the URL from.
     * @param <C>  the subclass of ICard of the specified card.
     * @return the String URL of the card.
     */
    public static <C extends ICard> String getCardFrontURL(C card) {
        if (CardImageView.CARD_RESOLVERS.containsKey(card.getClass())) {
            //noinspection unchecked
            return CardImageView.CARD_RESOLVERS.get(card.getClass()).getCardFrontURL(card);
        }

        List<Class> superClasses = getSuperClassesInResolves(card.getClass());
        CardUrlResolver resolver;
        try {
            resolver = getUsableResolver(superClasses, card);
        } catch (RuntimeException ex) {
            return card.getDescription();
        }
        //noinspection unchecked
        return resolver.getCardFrontURL(card);
    }


    /**
     * The card specified will be ran against the known {@link CardUrlResolver}s
     * in this class. If both the front and back {@link Image}s cannot be made using the
     * CardURLResolvers, a {@link RuntimeException} will be thrown.
     * <p>
     * If a CardURLResolver exists specifically for the subclass of {@link ICard} given,
     * only that CardURLResolver will be attempted to generate the images. Otherwise
     * all the CardURLResolvers will be searched through to attempt applying CardURLResolvers
     * of known superclasses of the specified card. If no working CardURLResolver is found, a
     * RunTimeException will be thrown.
     *
     * @param card the card to get String the URL from.
     * @param <C>  the subclass of ICard of the specified card.
     * @return the String URL of the card.
     */
    public static <C extends ICard> String getCardBackURL(C card) {
        if (CardImageView.CARD_RESOLVERS.containsKey(card.getClass())) {
            //noinspection unchecked
            return CardImageView.CARD_RESOLVERS.get(card.getClass()).getCardBackURL(card);
        }

        List<Class> superClasses = getSuperClassesInResolves(card.getClass());
        CardUrlResolver resolver;
        try {
            resolver = getUsableResolver(superClasses, card);
        } catch (RuntimeException ex) {
            return card.getDescription();
        }
        //noinspection unchecked
        return resolver.getCardBackURL(card);
    }

    private static List<Class> getSuperClassesInResolves(Class subclazz) {
        List<Class> superClasses = new ArrayList<>();
        for (Class clazz : CARD_RESOLVERS.keySet()) {
            //noinspection unchecked
            if (clazz.isAssignableFrom(subclazz)) {
                superClasses.add(clazz);
            }
        }
        return superClasses;
    }

    private static CardUrlResolver getUsableResolver(List<Class> superClasses, ICard card) throws RuntimeException {
        if (superClasses.size() == 1) {
            return CardImageView.CARD_RESOLVERS.get(superClasses.get(0));
        } else if (superClasses.size() > 1) {
            for (Class clazz : superClasses) {
                try {
                    // If both front and back images can be made, high chance
                    // that resolver is the one we want.
                    CardUrlResolver resolver = CardImageView.CARD_RESOLVERS.get(clazz);
                    //noinspection unchecked
                    new Image(resolver.getCardFrontURL(card));
                    //noinspection unchecked
                    new Image(resolver.getCardBackURL(card));
                    //noinspection unchecked
                    return resolver;
                } catch (Exception ex) {
                    // Try next one.
                }
            }
        }
        throw new RuntimeException("Failed to find usable CardURLResolver.");
    }

    private Image frontImage = null;
    private Image backImage = null;
    private final ICard CARD;
    private boolean isFaceUp;

    /**
     * Creates a CardImageView of a card.
     * <p>
     * The CardImageView will be face-up.
     *
     * @param card the card to be displayed.
     * @see CardImageView#CardImageView(ICard, boolean).
     */
    public CardImageView(ICard card) {
        this(card, true);
    }

    /**
     * Copy constructor.
     * <p>
     * Will create new Images to be used for the front and back
     * to avoid duplicate children exceptions.
     *
     * @param civ the CardImageView to copy.
     */
    public CardImageView(CardImageView civ) {
        this.CARD = civ.CARD;
        this.isFaceUp = civ.isFaceUp;
        try {
            this.frontImage = new Image(getCardFrontURL(this.CARD));
            this.backImage = new Image(getCardBackURL(this.CARD));
        } catch (Exception e) {
            System.err.println("Failed to construct CardImageView with Card: " + this.CARD.getDescription());
            e.printStackTrace();
        }
        this.update();
    }

    /**
     * Creates a CardImageView out of a card.
     * <p>
     * The card will be ran against the known {@link CardUrlResolver}s in this class.
     * If both the front and back {@link Image}s cannot be made using the CardURLResolvers,
     * a {@link RuntimeException} will be thrown.
     * <p>
     * If a CardURLResolver exists specifically for the subclass of {@link ICard} given,
     * only that CardURLResolver will be attempted to generate the images. Otherwise
     * all the CardURLResolvers will be searched through to attempt applying CardURLResolvers
     * of known superclasses of the specified card. If no working CardURLResolver is found, a
     * RunTimeException will be thrown.
     *
     * @param card     the card to be displayed.
     * @param isFaceUp true if the front image is to be shown, false for the back image.
     */
    public CardImageView(ICard card, boolean isFaceUp) {
        this.CARD = card;
        this.isFaceUp = isFaceUp;
        try {
            this.frontImage = new Image(getCardFrontURL(card));
            this.backImage = new Image(getCardBackURL(card));
        } catch (Exception e) {
            throw new RuntimeException("Failed to construct CardImageView with Card: " + card.getDescription());
        }
        this.update();
    }

    private void update() {
        Image image = this.isFaceUp ? this.frontImage : this.backImage;
        this.setImage(image);
    }

    /**
     * Sets whether the front or back image is to be shown.
     *
     * @param isFaceUp true for the front image, false for the back image.
     */
    public void setIsFaceUp(boolean isFaceUp) {
        if (this.isFaceUp != isFaceUp) {
            this.isFaceUp = isFaceUp;
            this.update();
        }
    }

    /**
     * Gets whether the front or back image is currently being shown.
     *
     * @return true for the front image, false for the back image.
     */
    public boolean isFaceUp() {
        return this.isFaceUp;
    }

    /**
     * Gets the {@link ICard} that was used to make this.
     *
     * @return the ICard used to make this.
     */
    public ICard getCard() {
        return this.CARD;
    }

    /**
     * If the CardImageViews do not contain the same subclass
     * of {@link ICard}, the returned value will be "0".
     *
     * @param o the other CardImageView.
     * @return positive if this comes before, negative for after, or 0 for equal ordering.
     */
    @Override
    public int compareTo(CardImageView o) {
        if (this.CARD.getClass() == o.CARD.getClass()) {
            //noinspection unchecked
            return this.CARD.compareTo(o.getCard());
        } else {
            return 0;
        }
    }
}

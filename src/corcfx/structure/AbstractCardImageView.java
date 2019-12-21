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
package corcfx.structure;

import corc.core.Logger;
import corc.structure.ICard;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public abstract class AbstractCardImageView<C extends ICard> extends ImageView implements Comparable<AbstractCardImageView<C>> {

    private final String FRONT_URL, BACK_URL;
    private final C CARD;

    private boolean isFaceUp;

    public AbstractCardImageView(C card, String frontUrl, String backUrl, boolean isFaceUp) {
        this.CARD = card;
        this.FRONT_URL = frontUrl;
        this.BACK_URL = backUrl;
        this.isFaceUp = isFaceUp;
        this.update();
    }

    public <T extends AbstractCardImageView<C>> AbstractCardImageView(T cardImageView) {
        this(
                cardImageView.getCard(),
                cardImageView.getFrontUrl(),
                cardImageView.getBackUrl(),
                cardImageView.isFaceUp()
        );
    }

    private void update() {
        String url = this.isFaceUp ? this.FRONT_URL : this.BACK_URL;
        try {
            Image image = new Image(url);
            this.setImage(image);
            Logger.logInfo(
                    "Created CardImageView with Card: " + this.CARD + ", using URLS: " +
                            this.FRONT_URL + " :: " + this.BACK_URL
            );
        } catch (IllegalArgumentException e) {
            Logger.logSevere("Failed to create CardImageView with Card: " + this.CARD +
                    ", using URLS: " + this.FRONT_URL + " :: " + this.BACK_URL);
        }
    }

    public void setIsFaceUp(boolean isFaceUp) {
        if (this.isFaceUp != isFaceUp) {
            this.isFaceUp = isFaceUp;
            this.update();
        }
    }

    public boolean isFaceUp() {
        return this.isFaceUp;
    }

    public C getCard() {
        return this.CARD;
    }

    public String getFrontUrl() {
        return this.FRONT_URL;
    }

    public String getBackUrl() {
        return this.BACK_URL;
    }

}

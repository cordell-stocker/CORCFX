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

package corcfx.visual.standard.interactable;

import corc.base.standard.Card;
import corcfx.visual.CardUrlResolver;
import corcfx.visual.interactable.OrganizablePane;

/**
 * A wrapper class for {@link corcfx.visual.interactable.HumanVisualHand}
 * that uses the standard {@link Card}.
 */
public class HumanVisualHand extends corcfx.visual.interactable.HumanVisualHand<Card> {

    /**
     * Constructs a {@link corcfx.visual.interactable.HumanVisualHand}
     * that uses the standard {@link Card}.
     *
     * @param handPane    the {@link OrganizablePane} that will store
     *                    {@link corcfx.visual.standard.CardImageView}s
     */
    public HumanVisualHand(OrganizablePane handPane) {
        super(handPane, CardUrlResolver.STANDARD_CARD_RESOLVER);
    }

}

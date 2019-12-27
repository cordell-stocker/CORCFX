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
package corcfx.visual.interactable;

import javafx.scene.layout.VBox;

/**
 * A panel that serves no major function other than to pause
 * model code until "Continue" is clicked.
 */
public class ContinuePanel extends OptionPanel<String> {

    private static String[] options = new String[]{"Continue"};

    public ContinuePanel(String headerStyle, String buttonStyle, double minButtonWidth) {
        super(new VBox(), "", ContinuePanel.options, headerStyle, buttonStyle, minButtonWidth);
    }

    public void getContinue() {
        super.getOptionClicked();
    }
}

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

import corc.core.Logger;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * A {@link Pane} that will display a list of options as {@link Button}s.
 *
 * This is meant to be treated as a sort-of "Menu Screen" callable by a
 * Model {@link Thread} in the Model-View-Controller paradigm.
 *
 * When using this, it is RECOMMENDED to add/remove this from the display as needed
 * using the {@link Pane#getChildren()} of the parent Pane.
 *
 * @param <T> the class of the options.
 */
public abstract class OptionPanel<T> extends Pane {

    public static final String DEFAULT_HEADER_STYLE =
            "-fx-background-color: WHITE; " +
            "-fx-border-color: GRAY; " +
            "-fx-padding: 4px; " +
            "-fx-font-size: 20px"; // Default
    public static final String DEFAULT_BUTTON_STYLE = "DEFAULT";
    public static final String DEFAULT_TITLE = "";
    public static final double MIN_BUTTON_WIDTH = 50;

    private final Pane CONTAINER;
    private T optionClicked;
    private volatile boolean clicked;

    /**
     * Creates a {@link Pane} that will contain a list of {@link Button}s.
     * This constructor will use the options given with all remaining values
     * using the default values defined in {@link OptionPanel}.
     *
     * @param options the options to be displayed as Buttons
     */
    public OptionPanel(T[] options) {
        this(new VBox(), DEFAULT_TITLE, options, DEFAULT_HEADER_STYLE, DEFAULT_BUTTON_STYLE, MIN_BUTTON_WIDTH);
    }

    /**
     * Creates a {@link Pane} that will contain a list of {@link Button}s.
     *
     * The options will have {@link Object#toString()} called to create
     * the text that will appear on the buttons.
     *
     * @param pane the Pane that will contain the title and option Buttons.
     * @param title the text for the title {@link Label}.
     * @param options the options to be displayed as Buttons.
     * @param titleStyle the css-like style to be used for the title Label.
     * @param buttonStyle the css-like style to be used for the Buttons
     * @param minButtonWidth the minimum width of the Buttons
     */
    public OptionPanel(Pane pane, String title, T[] options, String titleStyle, String buttonStyle, double minButtonWidth) {
        this.CONTAINER = pane;
        this.getChildren().add(pane);
        OptionPanel<T> that = this; // Prevents confusion in lambdas.

        if (title.length() != 0) {
            Label header = new Label(title);
            header.setStyle(titleStyle);
            this.getChildren().add(header);
        }

        for (T option : options) {
            Button button = new Button(option.toString());
            button.setMinWidth(minButtonWidth);
            if (!buttonStyle.equals(DEFAULT_BUTTON_STYLE)) {
                button.setStyle(buttonStyle);
            }
            button.setOnAction(e -> {
                that.optionClicked = option;
                that.clicked = true;
                that.startNotifyAll();
            });
            this.getChildren().add(button);
        }
    }

    /**
     * Waits for one of the option {@link Button}s to be clicked.
     *
     * WARNING: MUST NOT be called on the FXThread. The calling {@link Thread}
     * will have {@link Thread#wait()} called on it.
     *
     * @return the option that was clicked.
     */
    protected T getOptionClicked() {
        this.clicked = false;
        startWaitForClick();
        return this.optionClicked;
    }

    private synchronized void startWaitForClick() {
        /*
         * Why still use a boolean flag? Because a Thread can be
         * woken up before the condition is met.
         */
        while (!clicked) {
            try {
                wait();
            } catch (InterruptedException e) {
                Logger.logWarning("Option Pane interrupted while waiting");
            }
        }
    }

    private synchronized void startNotifyAll() {
        notifyAll();
    }

}

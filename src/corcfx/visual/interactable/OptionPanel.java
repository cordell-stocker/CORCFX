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
 * Displays a list of options that the user can click.
 * <p>
 * The options will appear as {@link Button}s.
 * <p>
 * This is meant to be treated as a sort-of "Menu Screen" callable by
 * a Model {@link Thread} in the Model-View-Controller paradigm.
 * <p>
 * When using this, it is RECOMMENDED to add/remove this from the
 * display as needed using the {@link Pane#getChildren()} of the
 * parent Pane.
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

    private T optionClicked;
    private volatile boolean clicked;

    /**
     * This constructor will use the options given and then uses the
     * default values defined in {@link OptionPanel} for the other
     * values in the
     * {@link OptionPanel#OptionPanel(Pane, String, Object[], String, String, double)}.
     * constructor; using a {@link VBox} for the Pane argument.
     *
     * @param options the options to be displayed.
     */
    public OptionPanel(T[] options) {
        this(new VBox(), DEFAULT_TITLE, options, DEFAULT_HEADER_STYLE, DEFAULT_BUTTON_STYLE, MIN_BUTTON_WIDTH);
    }

    /**
     * Constructs {@link Button}s for the specified options which will
     * be contained in the specified {@link Pane}.
     * <p>
     * The options will have {@link Object#toString()} called to create
     * the text that will appear on the buttons.
     * <p>
     * The order child {@link javafx.scene.Node}s are added to the Pane
     * is as follows: the title as a {@link Label} (if a non-empty
     * String is passed), then each specified option as a Button in
     * array index order.
     *
     * @param pane           the Pane that will contain the title and
     *                       option Buttons.
     * @param title          the text for the title {@link Label}.
     * @param options        the options to be displayed as Buttons.
     * @param titleStyle     the css-like style to be used for the
     *                       title Label.
     * @param buttonStyle    the css-like style to be used for the Buttons
     * @param minButtonWidth the minimum width of the Buttons
     */
    public OptionPanel(Pane pane, String title, T[] options, String titleStyle, String buttonStyle, double minButtonWidth) {
        this.getChildren().add(pane);
        OptionPanel<T> that = this; // Prevents confusion in lambdas.

        if (title.length() != 0) {
            Label header = new Label(title);
            header.setStyle(titleStyle);
            pane.getChildren().add(header);
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
            pane.getChildren().add(button);
        }
    }

    /**
     * Waits for one of the option {@link Button}s to be clicked.
     * <p>
     * WARNING: MUST NOT be called on the FXThread. The calling
     * {@link Thread} will have {@link Thread#wait()} called on it
     * until a button is clicked.
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

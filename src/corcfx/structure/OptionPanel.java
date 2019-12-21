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

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public abstract class OptionPanel<T> extends VBox {

    public static final String DEFAULT_HEADER_STYLE = "-fx-background-color: WHITE; " +
            "-fx-border-color: GRAY; -fx-padding: 4px; -fx-font-size: 20px"; // Default
    public static final String DEFAULT_BUTTON_STYLE = "DEFAULT";
    private T optionClicked;
    private volatile boolean clicked;

    public OptionPanel(String title, T[] options, String headerStyle, String buttonStyle) {
        double MIN_BUTTON_WIDTH = 50;
        OptionPanel<T> that = this; // Prevents confusion in lambdas.

        this.setAlignment(Pos.CENTER);

        if (title.length() != 0) {
            Label header = new Label(title);
            header.setStyle(headerStyle);
            this.getChildren().add(header);
        }

        for (T option : options) {
            Button button = new Button(option.toString());
            button.setMinWidth(MIN_BUTTON_WIDTH);
            if (!buttonStyle.equals(DEFAULT_BUTTON_STYLE)) {
                button.setStyle(buttonStyle);
            }
            button.setOnAction(e -> {
                that.optionClicked = option;
                that.clicked = true;
            });
            this.getChildren().add(button);
        }
    }

    protected T getOptionClicked() {
        this.clicked = false;
        while (!this.clicked) {
            try {
                long SLEEP_TIME = 100;
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return this.optionClicked;
    }

}

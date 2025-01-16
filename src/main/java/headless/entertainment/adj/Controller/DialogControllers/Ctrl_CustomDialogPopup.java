/*
 * Copyright (c) 2022. Headless-Entertainment - Adrian Domenic Walter Weidig. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * Copies, Modifications, Merges, Publications, Distributions or Sublicense of the
 * Software has to grant the same permissions as stated in this license.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package headless.entertainment.adj.Controller.DialogControllers;

import headless.entertainment.adj.Enums.Enum_CstmDiag;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Popup;

/**
 * Controller class for all CustomDialogPopups
 * needed by JPRO, because Alerts with
 * showandwait() are ont allowed.
 *
 * @author Adrian Domenic Walter Weidig
 * @version 0.1
 * @since 0.1
 */
public class Ctrl_CustomDialogPopup {


    /* -------------------------------- */
    /* ------ FXML Attributes    ------ */
    /* -------------------------------- */

    @FXML
    private Button fx_ButtonConfirm, fx_ButtonCancel;

    @FXML
    public AnchorPane fx_DialogPane;

    @FXML
    private Text fx_Caption;

    @FXML
    private Text fx_Description;

    @FXML
    private ImageView fx_Icon;

    /* -------------------------------- */
    /* ------ Other Attributes   ------ */
    /* -------------------------------- */

    private Popup pop;

    private final BooleanProperty isConfirmed = new SimpleBooleanProperty(false);

    /* -------------------------------- */
    /* ------ FXML Methods       ------ */
    /* -------------------------------- */

    /**
     * Hides the popup when pressing cancel button.
     *
     * @param event the event when pressing the button
     * @since 0.1
     */
    @FXML
    void onCancel(ActionEvent event) {
        this.pop.hide();
    }

    /**
     * Sets isConfirmed to true and activates
     * Listeners bound with isConfirmed and
     * hides the Popup.
     *
     * @param event the event when pressing the button
     * @since 0.1
     */
    @FXML
    void onConfirm(ActionEvent event) {
        this.isConfirmed.setValue(true);
        this.pop.hide();

    }

    /**
     * Initializes the Dialog and saves
     * the parent popup.
     *
     * @param pop the popup
     * @since 0.1
     */
    /* -------------------------------- */
    /* ------ Other Methods      ------ */
    /* -------------------------------- */
    public void initCstmDiag(Popup pop) {
        this.pop = pop;
    }

    /* -------------------------------- */
    /* ------ Getter and Setter  ------ */
    /* -------------------------------- */

    /**
     * TODO: EDIT JAVA-DOC
     * Gets is confirmed.
     *
     * @return the is confirmed
     * @since 0.1
     */
    public BooleanProperty getIsConfirmed() {
        return this.isConfirmed;
    }

    /**
     * TODO: EDIT JAVA-DOC
     * Sets fx description.
     *
     * @param fx_Description the fx description
     * @since 0.1
     */
    public void setFx_Description(String fx_Description) {
        this.fx_Description.setText(fx_Description);
    }

    /**
     * TODO: EDIT JAVA-DOC
     * Sets fx caption.
     *
     * @param fx_Caption the fx caption
     * @since 0.1
     */
    public void setFx_Caption(String fx_Caption) {
        this.fx_Caption.setText(fx_Caption);
    }

    /**
     * TODO: EDIT JAVA-DOC
     * Sets fx icon.
     *
     * @param diagType the diag type
     * @since 0.1
     */
    public void setFx_Icon(Enum_CstmDiag diagType) {
        this.fx_Icon.setImage(new Image(diagType.getImagePath()));
    }

    /**
     * TODO: EDIT JAVA-DOC
     * Gets fx button cancel.
     *
     * @return the fx button cancel
     * @since 0.1
     */
    public Button getFx_ButtonCancel() {
        return this.fx_ButtonCancel;
    }

    /**
     * TODO: EDIT JAVA-DOC
     * Gets fx button confirm.
     *
     * @return the fx button confirm
     * @since 0.1
     */
    public Button getFx_ButtonConfirm() {
        return this.fx_ButtonConfirm;
    }
}



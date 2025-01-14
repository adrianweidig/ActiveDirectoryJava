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

package headless.entertainment.adj.Model;

import headless.entertainment.adj.Controller.DialogControllers.Ctrl_CustomDialogPopup;
import headless.entertainment.adj.Enums.Enum_CstmDiag;
import headless.entertainment.adj.Handler.ADJ_Translator;
import javafx.beans.property.BooleanProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Popup;

import java.io.IOException;

/**
 * JPRO cant use blocking JavaFX Alerts.
 * Thats why we use specialized Popups.
 *
 * @author Adrian Domenic Walter Weidig
 * @version 0.1
 * @since 0.1
 */
public class ADJ_Alert extends Popup {

    private final Ctrl_CustomDialogPopup ctrl_customDialogPopup;

    private final ADJ_Translator translator;

    /**
     * Instantiates a new Cstm alert and
     * allows usage of specialized caption
     * and content for further usage.
     *
     * @param enum_cstmDiag the enum type
     * @param caption       the caption
     * @param content       the content
     * @since 0.1
     */
    public ADJ_Alert(final Enum_CstmDiag enum_cstmDiag, final String caption, final String content, final ADJ_Translator translator) {
        this.translator = translator;

        FXMLLoader loader = new FXMLLoader();

        Parent fxmlParent = null;
        try {
            fxmlParent = loader.load(this.getClass().getResourceAsStream("/headless/entertainment/adj/fxml/CustomDialogPopup.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.ctrl_customDialogPopup = loader.getController();

        this.getContent().add(fxmlParent);
        this.setAutoHide(false);
        this.setHideOnEscape(true);

        this.ctrl_customDialogPopup.initCstmDiag(this);
        this.ctrl_customDialogPopup.setFx_Icon(enum_cstmDiag);
        this.ctrl_customDialogPopup.setFx_Caption(caption);
        this.ctrl_customDialogPopup.setFx_Description(content);

        this.ctrl_customDialogPopup.getFx_ButtonCancel().setText(translator.get("CustomDialogPopup.cancel"));
    }

    /**
     * Instantiates a new Cstm alert and
     * uses the caption and text from
     * the Enum itself.
     *
     * @param enum_cstmDiag the enum type
     * @since 0.1
     */
    public ADJ_Alert(final Enum_CstmDiag enum_cstmDiag, final ADJ_Translator translator) {
        this.translator = translator;

        FXMLLoader loader = new FXMLLoader();

        Parent fxmlParent = null;
        try {
            fxmlParent = loader.load(this.getClass().getResourceAsStream("/headless/entertainment/adj/fxml/CustomDialogPopup.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.ctrl_customDialogPopup = loader.getController();

        this.getContent().add(fxmlParent);
        this.setAutoHide(false);
        this.setHideOnEscape(true);

        this.ctrl_customDialogPopup.initCstmDiag(this);
        this.ctrl_customDialogPopup.setFx_Icon(enum_cstmDiag);
        this.ctrl_customDialogPopup.setFx_Caption(enum_cstmDiag.name());
        this.ctrl_customDialogPopup.setFx_Description(enum_cstmDiag.getMessage());

        this.ctrl_customDialogPopup.getFx_ButtonCancel().setText(translator.get("CustomDialogPopup.cancel"));
    }

    /**
     * Changes some properties of the
     * popup to use it as a dialogue with
     * return values.
     *
     * @return the confirmation property
     * @since 0.1
     */
    public BooleanProperty getConfirmationProperty() {
        this.ctrl_customDialogPopup.getFx_ButtonConfirm().setText(this.translator.get("CustomDialogPopup.confirm"));
        return this.ctrl_customDialogPopup.getIsConfirmed();
    }

}

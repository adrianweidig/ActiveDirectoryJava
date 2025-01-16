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

package headless.entertainment.adj.Controller;

import com.jpro.webapi.WebAPI;
import headless.entertainment.adj.Client.Client_LDAP;
import headless.entertainment.adj.Handler.ADJ_Translator;
import javafx.animation.PauseTransition;
import javafx.application.HostServices;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.naming.NamingException;
import javax.naming.directory.InvalidSearchFilterException;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * View Controller for User Login.
 *
 * @author Adrian Domenic Walter Weidig
 * @version 0.1
 * @since 0.1
 */
public class Ctrl_Login implements Initializable {

    /* -------------------------------- */
    /* ------ FXML Attributes    ------ */
    /* -------------------------------- */

    @FXML
    private VBox fx_MainBox;

    @FXML
    private Button fx_LgnBtn;

    @FXML
    private PasswordField fx_password;

    @FXML
    private TextField fx_username;

    // TextProperties of fx_invalidLoginCredentials is bound
    // for live language switching
    @FXML
    private Label fx_invalidLoginCredentials, fx_Copyright;

    /* -------------------------------- */
    /* ------ Other Attributes   ------ */
    /* -------------------------------- */

    private HostServices hostServices = null;

    private WebAPI webAPI = null;

    private boolean alreadyCaught = false;

    private int wrongCounter = 0;

    private final ADJ_Translator translator = new ADJ_Translator();

    /* -------------------------------- */
    /* ------ Override Methods   ------ */
    /* -------------------------------- */

    /**
     * Initializes BEFORE the view is generated and
     * validates the Login-Input.
     *
     * @param location  the location
     * @param resources the resources
     * @since 0.1
     */
    @Override
    public void initialize(final URL location, final ResourceBundle resources) {

        // Checks if password and username is filled and binds
        // this state to the disabledProperty of the button.
        BooleanBinding fields_filled = new BooleanBinding() {
            {
                super.bind(Ctrl_Login.this.fx_password.textProperty(), Ctrl_Login.this.fx_username.textProperty());
            }

            @Override
            protected boolean computeValue() {
                return Ctrl_Login.this.fx_password.getText().equals("") || Ctrl_Login.this.fx_username.getText().equals("");
            }
        };

        this.fx_LgnBtn.disableProperty().bind(fields_filled);
    }

    /* -------------------------------- */
    /* ------ FXML Methods       ------ */
    /* -------------------------------- */

    /**
     * Fires the LoginButton when
     * Enter is pressed.
     *
     * @param event the event
     * @since 0.1
     */
    @FXML
    void onKeyPressed(final KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            try {
                this.fx_LgnBtn.fire();
            } catch (Error npe) {
                System.out.println("This is a current JPRO bug that will be fixed by JPRO devs in future.");
            }
        }
    }

    /**
     * Tries to log in the user after he put in
     * all the required information. If not  a
     * message is shown with the problem occurred
     * and stays for 5 seconds. Login is still
     * available during the 5 seconds.
     *
     * @param event the event
     * @since 0.1
     */
    @FXML
    void onLoginButtonClick(final ActionEvent event) {
        StringProperty invTextProp = this.fx_invalidLoginCredentials.textProperty();

        int maxWrongLogins = 3;

        if (this.wrongCounter < maxWrongLogins) {

            // Together with "alreadyCaught" works as the
            // sign that the problem with button already
            // has been caught or now got caught and
            // shows the message for 5seconds. This mechanism
            // prevents from spamming app with "setMessage"
            // and binding issues.
            boolean catched = false;

            try {
                Client_LDAP client_ldap = new Client_LDAP(this.fx_password.getText(), this.fx_username.getText(), this.translator);

                FXMLLoader loader = new FXMLLoader();
                Parent fxmlScene = loader.load(this.getClass().getResourceAsStream("/headless/entertainment/adj/fxml/ADJ.fxml"));
                Ctrl_ADJ ctrl_adj = loader.getController();

                Stage mainStage = (Stage) this.fx_MainBox.getScene().getWindow();

                

                mainStage.setScene(new Scene(fxmlScene));


                ctrl_adj.initialize(client_ldap, this.hostServices, this.translator);
                ctrl_adj.internationalize();
                ctrl_adj.generateTable();

            } catch (InvalidSearchFilterException e) {
                invTextProp.bind(this.translator.getStringBinding("Login.exception.wrongFilter"));
                if (!this.alreadyCaught) {
                    catched = true;
                }
                this.wrongCounter++;
            } catch (NamingException e) {
                invTextProp.bind(this.translator.getStringBinding("Login.exception.wrongCredentials"));
                if (!this.alreadyCaught) {
                    catched = true;
                }
                this.wrongCounter++;
            } catch (IOException e) {
                invTextProp.bind(this.translator.getStringBinding("Login.exception.wrongPath"));
                if (!this.alreadyCaught) {
                    catched = true;
                }
                this.wrongCounter++;
            } finally {

                // Finally - Block checks for catches happened
                // and guides the InvalidMessage showing.

                if (catched) {
                    // to prevent deadlock catching
                    this.alreadyCaught = true;

                    PauseTransition hideInvalidMessage = new PauseTransition();

                    // Hides the error message after 5s
                    hideInvalidMessage.setOnFinished(e -> {
                        invTextProp.unbind();
                        this.fx_invalidLoginCredentials.setText("");
                        this.alreadyCaught = false;
                    });

                    hideInvalidMessage.setDelay(Duration.seconds(5.0));
                    hideInvalidMessage.play();
                }
            }
        }

        // Checks the amount of wrong logins and if the warning message is already bound or not
        if (this.wrongCounter >= maxWrongLogins && !invTextProp.isBound()) {
            this.fx_invalidLoginCredentials.setText(this.translator.get("Login.exception.locked"));
        }
    }

    /* -------------------------------- */
    /* ------ Other Methods      ------ */
    /* -------------------------------- */

    /**
     * Initializes the login screen and defines
     * the default locale connected to the supported
     * ones and whether the application is started as
     * browser or desktop application.
     *
     * @since 0.1
     */
    public void initView() {
        // webApi.getLanguage() returns Strings like en, de and so on
        // we convert it with translator to en_EN, de_DE and so on.
        if (this.webAPI != null) {
            this.translator.setLanguage(new Locale.Builder().setLanguage(this.webAPI.getLanguage()).build());
        } else {
            this.translator.setLanguage(this.translator.getDefaultLocale());
        }

        this.fx_username.promptTextProperty().bind(this.translator.getStringBinding("Login.fx_username"));
        this.fx_password.promptTextProperty().bind(this.translator.getStringBinding("Login.fx_password"));
        this.fx_Copyright.textProperty().bind(this.translator.getStringBinding("Login.fx_Copyright"));
        this.fx_LgnBtn.textProperty().bind(this.translator.getStringBinding("Login.fx_LgnBtn"));
    }

    /* -------------------------------- */
    /* ------ Getter and Setter  ------ */
    /* -------------------------------- */

    /**
     * TODO: EDIT JAVA-DOC
     * Sets host services.
     *
     * @param hostServices the host services
     * @since 0.1
     */
// We don't want those things to be reinitialized
    public void setHostServices(final HostServices hostServices) {
        if (this.hostServices == null) {
            this.hostServices = hostServices;
        }
    }

    /**
     * TODO: EDIT JAVA-DOC
     * Sets web api.
     *
     * @param webAPI the web api
     * @since 0.1
     */
    public void setWebAPI(final WebAPI webAPI) {
        if (this.webAPI == null) {
            this.webAPI = webAPI;
        }
    }
}




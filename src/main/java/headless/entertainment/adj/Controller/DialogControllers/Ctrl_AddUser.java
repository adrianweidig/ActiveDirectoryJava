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

import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Group;
import com.dlsc.formsfx.model.structure.StringField;
import com.dlsc.formsfx.model.util.BindingMode;
import com.dlsc.formsfx.view.renderer.FormRenderer;
import headless.entertainment.adj.Controller.Ctrl_ADJ;
import headless.entertainment.adj.Enums.Enum_CstmDiag;
import headless.entertainment.adj.Model.ADJ_Alert;
import headless.entertainment.adj.Model.LDAPTableView;
import headless.entertainment.adj.Model.LDAPWindow;
import headless.entertainment.adj.Settings.Config;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import org.apache.commons.lang3.StringUtils;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;

/**
 * View Controller of AddUser Dialogue.
 *
 * @author Adrian Domenic Walter Weidig
 * @version 0.1
 * @since 0.1
 */
public class Ctrl_AddUser {

    /* -------------------------------- */
    /* ------ FXML Attributes    ------ */
    /* -------------------------------- */

    @FXML
    private Button fx_ButtonAddUser, fx_ButtonCancel;

    @FXML
    private HBox fx_ButtonBox;

    @FXML
    private VBox fx_UserAddOptionsBox;

    /* -------------------------------- */
    /* ------ Other Attributes   ------ */
    /* -------------------------------- */

    private Ctrl_ADJ controller;

    private Popup pop;

    private final Attributes userAttributes = new BasicAttributes();

    /* -------------------------------- */
    /* ------ FXML Methods       ------ */
    /* -------------------------------- */

    /**
     * Hides the popup if button is pressed.
     *
     * @param event the event activating the button
     * @since 0.1
     */
    @FXML
    private void onCancel(ActionEvent event) {
        this.pop.hide();
    }

    /**
     * Prepares the information of the Add User dialogue
     * to give Client_ldap needed information for adding
     * a new user.
     *
     * @param event the event activating the button
     * @see headless.entertainment.adj.Client.Client_LDAP#addUser(Attributes, String)
     * @since 0.1
     */
    @FXML
    private void onUserAdd(ActionEvent event) {
        try {
            this.pop.getScene().setCursor(Cursor.WAIT);

            //Converts all Properties to simple Strings at the moment we press the button
            NamingEnumeration<? extends Attribute> userAttrs = this.userAttributes.getAll();
            while (userAttrs.hasMoreElements()) {
                Attribute attr = userAttrs.next();
                if (attr.get() instanceof SimpleStringProperty) {
                    this.userAttributes.remove(attr.getID());
                    String content = ((SimpleStringProperty) attr.get()).get();
                    if (content.equals("")) {
                        content = "empty";
                    }
                    this.userAttributes.put(attr.getID(), content);
                }
            }

            // Builds the complete hierarchical LDAP order for the last selected TreeItem
            StringBuilder LDAPString = new StringBuilder();

            TreeItem<String> tmpLastTreeItem = this.controller.getLastTreeItem();

            while (tmpLastTreeItem.getParent() != null) {
                LDAPString.append("OU=").append(tmpLastTreeItem.getValue().replaceAll(" ", "")).append(",");
                tmpLastTreeItem.setExpanded(true);
                tmpLastTreeItem = tmpLastTreeItem.getParent();
            }

            LDAPString.append(tmpLastTreeItem.getValue());

            Enum_CstmDiag result = this.controller.getClient_ldap().addUser(this.userAttributes, LDAPString.toString());

            if (result != null) {
                ADJ_Alert alert = new ADJ_Alert(result, result.toString(), result.getMessage(), this.controller.getTranslator());
                alert.show(this.controller.getFx_TablePane().getScene().getWindow());
            }

            this.pop.hide();

            if (this.controller.getLastTreeItem() != null) {
                this.controller.getFx_ScrollPane().setContent(null);
                this.controller.getFx_TablePane().getChildren().removeAll(this.controller.getFx_TablePane().getChildren());

                LDAPWindow bldr_table = new LDAPWindow(this.controller);
                bldr_table.generateLDAPWindow();

                LDAPTableView LDAPTableView = new LDAPTableView(bldr_table);
                LDAPTableView.generateLDAPTableView(this.controller.getLastTreeItem());
            }

        } catch (NamingException e) {
            this.pop.hide();

            ADJ_Alert alert = new ADJ_Alert(Enum_CstmDiag.ERROR, "Error", this.controller.getTranslator().get("AddUser.exception.userCouldNotBeAdded"), this.controller.getTranslator());
            alert.show(this.controller.getFx_TablePane(), this.controller.getScreenMiddleX(), this.controller.getScreenMiddleY());
        } finally {
            // this.pop.getScene().setCursor(Cursor.DEFAULT);
        }
    }

    /* -------------------------------- */
    /* ------ Other Methods      ------ */
    /* -------------------------------- */

    /**
     * Initializes the AddUser dialogue.
     *
     * @param controller the main controller
     * @param pop        the popup object to use it for hiding
     * @since 0.1
     */
    public void initDialog(Ctrl_ADJ controller, Popup pop) {
        String[] requiredAttributes = Config.getInstance().getRequired_User_Add_Attributes().replaceAll("/n", "").split(",");
        String[] optionalAttributes = Config.getInstance().getOptional_User_Add_Attributes().replaceAll("/n", "").split(",");

        this.pop = pop;
        this.controller = controller;
        this.fx_UserAddOptionsBox.getChildren().add(this.createForm(requiredAttributes, optionalAttributes));
        this.fx_ButtonBox.toFront();

        this.fx_ButtonAddUser.setText(this.controller.getTranslator().get("AddUser.buttons.confirm"));
        this.fx_ButtonCancel.setText(this.controller.getTranslator().get("AddUser.buttons.cancel"));
    }

    /**
     * Creates the form in connection to the
     * required and optional attributes stated
     * in config.xml.
     *
     * @param requiredAttributes the required attributes
     * @param optionalAttributes the optional attributes
     * @return the form renderer
     * @since 0.1
     */
    private FormRenderer createForm(String[] requiredAttributes, String[] optionalAttributes) {
        //-------- Fixed add of username and LDAP specific user objectClass, because we will definitely NEED it
        Attribute objectClassAttribute = new BasicAttribute("objectClass");
        objectClassAttribute.add("top");
        objectClassAttribute.add("user");
        objectClassAttribute.add("person");
        objectClassAttribute.add("organizationalPerson");

        this.userAttributes.put(objectClassAttribute);
        // Form Generation
        Form userAddForm = Form.of().title(this.controller.getTranslator().get("AddUser.form.addUser"));
        Group userAddGroupReqAttrs = Group.of();

        StringProperty stringProperty = new SimpleStringProperty("");
        this.userAttributes.put("name", stringProperty);

        StringField sf = Field.ofStringType(stringProperty)
                .label("Name")
                .required(this.controller.getTranslator().get("AddUser.form.required"))
                .placeholder(StringUtils.capitalize("Name"));

        sf.setBindingMode(BindingMode.CONTINUOUS);
        sf.validate();

        // Boolean Binding for all required Values
        BooleanBinding booleanBinding = new SimpleBooleanProperty(true).and(sf.validProperty());
        userAddGroupReqAttrs.getElements().add(sf);

        for (String requiredAttribute : requiredAttributes) {

            //-------- Ignoring fixed additions if user added it to xml
            if (!requiredAttribute.equals("name") && !requiredAttribute.equals("objectClass")) {
                StringProperty attributeProperty = new SimpleStringProperty("");
                this.userAttributes.put(requiredAttribute, attributeProperty);

                sf = Field.ofStringType(attributeProperty)
                        .label(StringUtils.capitalize(requiredAttribute))
                        .required(this.controller.getTranslator().get("AddUser.form.required"))
                        .placeholder(StringUtils.capitalize(requiredAttribute));

                sf.setBindingMode(BindingMode.CONTINUOUS);
                sf.validate();
                booleanBinding = booleanBinding.and(sf.validProperty());
                userAddGroupReqAttrs.getElements().add(sf);
            }

        }

        this.fx_ButtonAddUser.disableProperty().bind(booleanBinding.not());

        userAddForm.getGroups().add(userAddGroupReqAttrs);

        Group userAddGroupOptAttrs = Group.of();
        for (String optionalAttribute : optionalAttributes) {
            //-------- Ignoring fixed additions if user added it to xml
            if (!optionalAttribute.equals("name") && !optionalAttribute.equals("objectClass")) {
                StringProperty attributeProperty = new SimpleStringProperty("");
                this.userAttributes.put(optionalAttribute, attributeProperty);

                sf = Field.ofStringType(attributeProperty)
                        .placeholder(StringUtils.capitalize(optionalAttribute))
                        .label(StringUtils.capitalize(optionalAttribute))
                        .required(false);
                sf.setBindingMode(BindingMode.CONTINUOUS);
                userAddGroupOptAttrs.getElements().add(sf);
            }
        }

        userAddForm.getGroups().add(userAddGroupOptAttrs);

        return new FormRenderer(userAddForm);
    }

}


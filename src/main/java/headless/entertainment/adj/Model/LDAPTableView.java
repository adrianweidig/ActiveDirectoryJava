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

import headless.entertainment.adj.Controller.Ctrl_ADJ;
import headless.entertainment.adj.Enums.Enum_CstmDiag;
import headless.entertainment.adj.Handler.ADJ_Translator;
import headless.entertainment.adj.Settings.Config;
import javafx.animation.PauseTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.util.Duration;
import org.controlsfx.control.table.TableFilter;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Class used to generate the TableView
 * with configured LDAP Attributes.
 * //TODO: Make LDAPTableView inherit from TableView
 *
 * @author Adrian Domenic Walter Weidig
 * @version 0.1
 * @see Config
 * @since 0.1
 */
public class LDAPTableView {

    private final ADJ_Translator translator;

    private final Ctrl_ADJ controller;

    /**
     * Used so people can't generate huge amount of TVs
     * in given time.
     */
    private boolean useable = true;

    /**
     * Instantiates a new Ldap table view
     * and transmits the used LDAP Window.
     *
     * @param LDAPWindow the ldap window
     * @since 0.1
     */
    public LDAPTableView(final LDAPWindow LDAPWindow) {
        this.translator = LDAPWindow.getController().getTranslator();
        this.controller = LDAPWindow.getController();
    }

    /**
     * Generates the View Part after
     * initialized by the constructor.
     *
     * @param selected_TreeItem The currently selected Tree Item
     * @since 0.1
     */
    public void generateLDAPTableView(TreeItem<String> selected_TreeItem) {

        if (this.useable) {
            this.useable = false;

            PauseTransition dontGenerateNewTableview = new PauseTransition();
            dontGenerateNewTableview.setOnFinished(event -> this.useable = true);
            dontGenerateNewTableview.setDelay(Duration.millis(100));

            // Unload existing Table Views
            if (!this.controller.getFx_TablePane().getChildren().isEmpty()) {
                this.controller.getFx_TablePane().getChildren().remove(0);
            }

            TableView<Attributes> newTable = new TableView<>();
            this.controller.setUseTable(newTable);

            newTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            newTable.setPrefHeight(this.controller.getFx_TablePane().getHeight());
            newTable.setPrefWidth(this.controller.getFx_TablePane().getWidth());
            newTable.setEditable(true);
            newTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


            this.controller.getFx_TablePane().getChildren().add(newTable);

            // Builds the complete hierarchical LDAP order for the last selected TreeItem
            StringBuilder LDAPString = new StringBuilder();

            while (selected_TreeItem.getParent() != null) {
                LDAPString.append("OU=").append(selected_TreeItem.getValue()).append(",");
                selected_TreeItem = selected_TreeItem.getParent();
            }

            LDAPString.append(selected_TreeItem.getValue());

            this.generateTableColumns(LDAPString, newTable);

            dontGenerateNewTableview.play();
        }
    }

    /**
     * Generates the table columns.
     *
     * @param LDAPString the ldap string
     * @param ldap_Table the ldap table
     * @since 0.1
     */
    private void generateTableColumns(final StringBuilder LDAPString, final TableView<Attributes> ldap_Table) {
        try {
            String[] permissionList = Config.getInstance().getPermitted_Attributes().replaceAll("/n", "").split(",");

            // There are no LDAP Filter possibilities for filtering specific categories. So we have to "filter" them on our own.
            NamingEnumeration<SearchResult> all_Searchresults = this.controller.getClient_ldap().search(LDAPString.toString(), "(!(objectClass=organizationalUnit))");

            ArrayList<String> possibleAttributes = new ArrayList<>();

            // permissionList.length <= 0 means no-one is permitted
            while (all_Searchresults.hasMoreElements() && permissionList.length > 0) {
                SearchResult searchresult = all_Searchresults.next();
                Attributes all_Attributes = searchresult.getAttributes();

                if (all_Attributes != null) {
                    NamingEnumeration<? extends Attribute> ae = all_Attributes.getAll();
                    boolean isFiltered = false;
                    //First check if distinguished name contains one of the Group_Filters.
                    //If so it shouldn't be displayed.

                    String[] prohibitionList = Config.getInstance().getGroup_Filter().replaceAll("/n", "").split(",");

                    while (ae.hasMoreElements()) {
                        Attribute atr = ae.next();
                        String attributeID = atr.getID();

                        if (attributeID.equals("distinguishedName")) {
                            Attribute distinguishedName = all_Attributes.get(attributeID);
                            for (String s : prohibitionList) {
                                if (distinguishedName.get().toString().contains(s)) {
                                    isFiltered = true;
                                    break;
                                }
                            }
                        }
                    }

                    if (!isFiltered) {

                        ae = all_Attributes.getAll();

                        while (ae.hasMoreElements()) {
                            boolean permitted = false;
                            Attribute atr = ae.next();
                            String attributeID = atr.getID();

                            for (String s : permissionList) {
                                if (attributeID.equals(s)) {
                                    permitted = true;
                                    break;
                                }
                            }

                            // If the attribute is not even on the permission list. Let's remove it from the whole AttributeList
                            if (!permitted) {
                                all_Attributes.remove(attributeID);
                            } else if (!possibleAttributes.contains(attributeID)) {
                                //Prevent duplicates
                                possibleAttributes.add(attributeID);

                                TableColumn<Attributes, String> attributeColumn = new TableColumn<>(attributeID);
                                attributeColumn.setEditable(true);

                                attributeColumn.setCellValueFactory(cellValue -> {

                                    SimpleStringProperty sp = new SimpleStringProperty("");
                                    if (cellValue.getValue().get(attributeID) != null) {
                                        sp.set(cellValue.getValue().get(attributeID).toString().replaceAll(attributeID, "").substring(1));
                                    }

                                    return sp;
                                });

                                ldap_Table.getColumns().add(attributeColumn);

                            }

                        }

                        // If there were no permitted attributes added the size is 0, so
                        // we don't add it to our table.
                        if (all_Attributes.size() > 0) {
                            ldap_Table.getItems().add(all_Attributes);
                        }

                    }

                }

            }

            TableFilter.forTableView(ldap_Table).apply();
            ldap_Table.setPlaceholder(new Label(this.translator.get("TableViewPlaceholder")));

            // Adds the context Menu only when user is member of configured admin group even if he would've rights
            if (this.controller.getClient_ldap().isMemberOf(Config.getInstance().getLDAP_Admin_Group_Filter()).equals(Enum_CstmDiag.SUCCESS) || Config.getInstance().getLDAP_Admin_Group_Filter().equals("")) {

                this.addContextMenu(ldap_Table);
            }
        } catch (NamingException e) {
            ADJ_Alert alert = new ADJ_Alert(Enum_CstmDiag.ERROR, "Error", this.translator.get("LDAPTableView.noEntries"), this.translator);
            alert.show(this.controller.getFx_TablePane(), this.controller.getScreenMiddleX(), this.controller.getScreenMiddleY());
            System.out.println();
        }
    }

    /**
     * Adds the context Menu on the TableView
     *
     * @param tv the TableView
     * @since 0.1
     */
    private void addContextMenu(final TableView<Attributes> tv) {
        ObservableList<Attributes> obLi = tv.getSelectionModel().getSelectedItems();

        ContextMenu contextMenu = new ContextMenu();
        MenuItem addUser = new MenuItem(this.translator.get("LDAPTableView.contextMenu.addUser"));
        addUser.setOnAction(e -> {
            try {
                this.controller.UserAdd();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        MenuItem removeUser = new MenuItem(this.translator.get("LDAPTableView.contextMenu.deleteUser"));
        removeUser.setDisable(true);
        removeUser.setOnAction(e -> this.controller.UserDelete(obLi));

        // Observe the possibility to remove a user and deactivates the option if there is no possibility
        obLi.addListener((ListChangeListener<Attributes>) c -> removeUser.setDisable(obLi.isEmpty()));

        contextMenu.getItems().addAll(addUser, removeUser);

        tv.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                contextMenu.show(tv, e.getScreenX(), e.getScreenY());
            } else {
                contextMenu.hide();
            }
        });
    }

}
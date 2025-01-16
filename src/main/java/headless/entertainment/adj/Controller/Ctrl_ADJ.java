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
import headless.entertainment.adj.Client.Client_ITOP;
import headless.entertainment.adj.Client.Client_LDAP;
import headless.entertainment.adj.Controller.DialogControllers.Ctrl_AddUser;
import headless.entertainment.adj.Enums.Enum_CstmDiag;
import headless.entertainment.adj.Handler.ADJ_Translator;
import headless.entertainment.adj.Model.ADJ_Alert;
import headless.entertainment.adj.Model.LDAPTableView;
import headless.entertainment.adj.Model.LDAPWindow;
import javafx.application.HostServices;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Main controller class. Maintaining the whole
 * buildup process of the views after login in.
 *
 * @author Adrian Domenic Walter Weidig
 * @version 0.1
 * @since 0.1
 */
public class Ctrl_ADJ {

    /* -------------------------------- */
    /* ------ FXML Attributes    ------ */
    /* -------------------------------- */
    @FXML
    private VBox fx_TablePane;

    @FXML
    private ScrollPane fx_ScrollPane;

    @FXML
    private MenuItem fx_LogOut, fx_Export, fx_Documentation, fx_Reload, fx_Report;

    @FXML
    private SeparatorMenuItem fx_Separator;

    @FXML
    private Menu fx_Menu, fx_HelpMenu;

    /* -------------------------------- */
    /* ------ Other Attributes   ------ */
    /* -------------------------------- */

    private Client_LDAP client_ldap;

    private Client_ITOP client_itop;

    private TableView<Attributes> useTable;

    private TreeItem<String> lastTreeItem;

    private HostServices hostServices;

    private WebAPI webAPI;

    private ADJ_Translator translator;

    private double screenMiddleX;

    private double screenMiddleY;
    /* -------------------------------- */
    /* ------ FXML Methods       ------ */
    /* -------------------------------- */

    /**
     * Shows / downloads the Documentation.pdf
     *
     * @param event the event when menuItem was selected
     * @since 0.1
     */
    @FXML
    private void OnAction_Documentation(ActionEvent event) {
        try {
            // WebInterface
            if (this.webAPI != null) {
                this.webAPI.downloadURL(new File("./docs/Documentation.pdf").toURI().toURL());
            }

            // Native Desktop Mode
            if (this.hostServices != null && this.webAPI == null) {
                this.hostServices.showDocument(System.getProperty("user.dir") + "docs/Documentation.pdf");
            }

            // Wrong something is young padawan
            if (this.hostServices == null && this.webAPI == null) {
                ADJ_Alert documentationNotFound = new ADJ_Alert(Enum_CstmDiag.ERROR, Enum_CstmDiag.ERROR.name(), this.translator.get("Ctrl_ADJ.documentationNotFound"), this.translator);
                documentationNotFound.show(this.fx_TablePane.getScene().getWindow(), this.screenMiddleX, this.screenMiddleY);
            }

        } catch (MalformedURLException e) {
            // If someone deleted the .pdf file
            ADJ_Alert documentationNotFound = new ADJ_Alert(Enum_CstmDiag.ERROR, Enum_CstmDiag.ERROR.name(), this.translator.get("Ctrl_ADJ.documentationNotFound"), this.translator);
            documentationNotFound.show(this.fx_TablePane.getScene().getWindow(), this.screenMiddleX, this.screenMiddleY);
        }
    }

    /**
     * Exports the shown TableView to an .xlsx
     * file and opens it with system standard.
     *
     * @param event the event when menuItem was selected
     * @since 0.1
     */
    @FXML
    private void OnAction_Export(ActionEvent event) {
        String currentTreeItemValue = this.lastTreeItem.getValue();

        // Only if a TreeItem is already selected
        if (this.lastTreeItem != null) {
            TableView<Attributes> tv = this.useTable;

            Workbook workbook = new XSSFWorkbook();
            Sheet spreadsheet = workbook.createSheet(currentTreeItemValue);
            Row row = spreadsheet.createRow(0);


            // Iterates through the current table and writes them into
            // the spreadsheet
            for (int i = 0; i < tv.getColumns().size(); i++) {
                row.createCell(i).setCellValue(tv.getColumns().get(i).getText());
            }

            for (int i = 0; i < tv.getItems().size(); i++) {
                row = spreadsheet.createRow(i + 1);
                for (int j = 0; j < tv.getColumns().size(); j++) {
                    if (tv.getColumns().get(j).getCellData(i) != null) {
                        row.createCell(j).setCellValue(tv.getColumns().get(j).getCellData(i).toString());
                    } else {
                        row.createCell(j).setCellValue("");
                    }
                }

                spreadsheet.autoSizeColumn(i + 1);
            }

            try {
                File exportPath = new File("./exports/" + currentTreeItemValue + ".xlsx");

                // Checks whether the file is readable or not.
                // Readable means existing OR maybe opened.
                boolean fileExistingOrOpened = exportPath.canRead();

                // Not existing or opened means we can create a new one
                if (!fileExistingOrOpened) {
                    FileOutputStream fileOut = new FileOutputStream(exportPath);
                    workbook.write(fileOut);
                    fileOut.close();
                }

                // WebInterface
                if (this.webAPI != null) {
                    this.webAPI.downloadURL(exportPath.toURI().toURL());
                }

                // Native Desktop Mode
                if (this.hostServices != null && this.webAPI == null) {
                    this.hostServices.showDocument(System.getProperty("user.dir") + "/exports/" + currentTreeItemValue + ".xlsx");
                }

                // Wrong something is young padawan
                if (this.hostServices == null && this.webAPI == null) {
                    ADJ_Alert notOpenable = new ADJ_Alert(Enum_CstmDiag.ERROR, Enum_CstmDiag.ERROR.name(), this.translator.get("Ctrl_ADJ.export.notOpenable"), this.translator);
                    notOpenable.show(this.fx_TablePane.getScene().getWindow(), this.screenMiddleX, this.screenMiddleY);
                }


            } catch (IOException e) {
                // If it is not possible to open the exported file
                ADJ_Alert exportError = new ADJ_Alert(Enum_CstmDiag.ERROR, Enum_CstmDiag.ERROR.name(), this.translator.get("Ctrl_ADJ.export.error"), this.translator);
                exportError.show(this.fx_TablePane.getScene().getWindow(), this.screenMiddleX, this.screenMiddleY);
                e.printStackTrace();
            }
        }

        // If nothing is selected yet
        if (this.lastTreeItem == null) {
            ADJ_Alert noTableSelected = new ADJ_Alert(Enum_CstmDiag.ERROR, Enum_CstmDiag.ERROR.name(), this.translator.get("Ctrl_ADJ.export.noTableSelected"), this.translator);
            noTableSelected.show(this.fx_TablePane.getScene().getWindow(), this.screenMiddleX, this.screenMiddleY);
        }
    }

    /**
     * Reloads the browser-page quiting the session and
     * makes the user to login again.
     *
     * @param event the event when menuItem was selected
     * @since 0.1
     */
    @FXML
    private void OnAction_LogOut(ActionEvent event) {
        if (this.getFx_TablePane().getScene().getWindow().getScene() != null) {
            this.webAPI.executeScript("location.reload()");
        }
    }

    /**
     * Reloads the TableView<br>
     * <br>
     * Known Bug: <br>
     * TreeView not expanding to the same TableViewPoint,
     * but collapsing completely.
     *
     * @param event the event when menuItem was selected
     * @see javax.naming.directory.DirContext#search(String, String, SearchControls) javax.naming.directory.DirContext#search(String, String, SearchControls)
     * @since 0.1
     */
    @FXML
    private void OnAction_Reload(ActionEvent event) {
        if (this.lastTreeItem != null) {
            this.fx_ScrollPane.setContent(null);
            this.fx_TablePane.getChildren().removeAll(this.fx_TablePane.getChildren());

            LDAPWindow bldr_table = new LDAPWindow(this);
            Enum_CstmDiag windowCreationSuccess = bldr_table.generateLDAPWindow();

            LDAPTableView LDAPTableView = new LDAPTableView(bldr_table);
            LDAPTableView.generateLDAPTableView(this.lastTreeItem);

            // Shows an alert if something happened during window initialization
            if (windowCreationSuccess.equals(Enum_CstmDiag.ERROR)) {
                ADJ_Alert windowCreationAlert = new ADJ_Alert(windowCreationSuccess, this.translator);
                windowCreationAlert.show(this.fx_TablePane.getScene().getWindow(), this.screenMiddleX, this.screenMiddleY);
            }
        }
    }

    /**
     * TODO: EDIT JAVA-DOC - TBD
     * On action report.
     *
     * @param event the event
     * @since 0.1
     */
    @FXML
    private void OnAction_Report(ActionEvent event) {

    }

    /* -------------------------------- */
    /* ------ Other Methods      ------ */
    /* -------------------------------- */

    /**
     * Initializes all needed global params.
     * HostServices will be available permanently.
     * WebApi can be null if started as desktop app.
     * <p>
     * This method is seen as the constructor of this
     * controller class, because it is mandatory to
     * call this function before working with this method.
     * <p>
     * TODO: Change other methods, so this class MUST call this method before doing something else
     *
     * @param client_ldap  the used LDAP client
     * @param hostServices the application hostServices
     * @param translator   the global translator class
     * @since 0.1
     */
    public void initialize(final Client_LDAP client_ldap, final HostServices hostServices, final ADJ_Translator translator) {
        this.client_ldap = client_ldap;
        this.hostServices = hostServices;
        this.translator = translator;
        this.webAPI = WebAPI.getWebAPI(this.fx_ScrollPane.getScene().getWindow());
    }

    /**
     * Initiates the View-Build process. You can see it
     * somehow as the original constructor.
     * (JavaFX Controller > no Constructor)
     *
     * @since 0.1
     */
    public void generateTable() {
        LDAPWindow ldapWindow = new LDAPWindow(this);

        Enum_CstmDiag windowCreationSuccess = ldapWindow.generateLDAPWindow();

        // Shows an alert if something happened during window initialization
        if (windowCreationSuccess.equals(Enum_CstmDiag.ERROR)) {
            ADJ_Alert windowCreationAlert = new ADJ_Alert(windowCreationSuccess, this.translator);
            windowCreationAlert.show(this.fx_TablePane.getScene().getWindow(), this.screenMiddleX, this.screenMiddleY);
        }
    }

    /**
     * Hides or shows and internationalizes the
     * View items depending on settings.
     *
     * @since 0.1
     */
    public void internationalize() {
        // Modifies the view when webAPI is existing
        if (this.webAPI != null) {
            this.screenMiddleX = this.webAPI.getBrowserSize().getWidth() / 2;
            this.screenMiddleY = this.webAPI.getBrowserSize().getHeight() / 2;
        } else {
            this.screenMiddleX = this.fx_TablePane.getWidth() / 2;
            this.screenMiddleY = this.fx_TablePane.getHeight() / 2;
            this.fx_LogOut.setVisible(false);
            this.fx_Separator.setVisible(false);
        }

        // Menus
        this.fx_Menu.textProperty().bind(this.translator.getStringBinding("MenuBar.menu"));
        this.fx_HelpMenu.textProperty().bind(this.translator.getStringBinding("MenuBar.help"));

        // Menu Items
        /// Menu
        this.fx_LogOut.textProperty().bind(this.translator.getStringBinding("MenuBar.menu.logout"));
        this.fx_Export.textProperty().bind(this.translator.getStringBinding("MenuBar.menu.export"));
        this.fx_Reload.textProperty().bind(this.translator.getStringBinding("MenuBar.menu.reload"));

        /// Help
        this.fx_Documentation.textProperty().bind(this.translator.getStringBinding("MenuBar.help.docu"));
        this.fx_Report.textProperty().bind(this.translator.getStringBinding("MenuBar.help.report"));
    }

    /**
     * Deletes the user from LDAP
     *
     * @param obLi the ob li
     * @since 0.1
     */
    public void UserDelete(final ObservableList<Attributes> obLi) {
        Stage stage = (Stage) this.fx_TablePane.getScene().getWindow();

        // Maybe multiple users are selected
        for (Attributes attributes : obLi) {
            ADJ_Alert deleteWarning = new ADJ_Alert(Enum_CstmDiag.WARNING, this.translator.get("Ctrl_ADJ.deleteUser.confirmationDialog"), this.translator.get("Ctrl_ADJ.deleteUser.confDiagText1") + attributes.get("name").toString().substring(6) + this.translator.get("Ctrl_ADJ.deleteUser.confDiagText2"), this.translator);

            // observes the confirmation of the alert
            deleteWarning.getConfirmationProperty().addListener((observable, oldValue, newValue) -> {

                // If change on confirmationProperty ( = BooleanProperty ) has occured
                if (newValue) {

                    // Building the LDAP String representing the used filter
                    StringBuilder LDAPString = new StringBuilder();

                    TreeItem<String> tmpLastTreeItem = this.lastTreeItem;

                    while (tmpLastTreeItem.getParent() != null) {
                        LDAPString.append("OU=").append(tmpLastTreeItem.getValue().replaceAll(" ", "")).append(",");
                        tmpLastTreeItem.setExpanded(true);
                        tmpLastTreeItem = tmpLastTreeItem.getParent();
                    }

                    LDAPString.append(tmpLastTreeItem.getValue());

                    // Deleting the user from LDAP
                    Enum_CstmDiag userDeletionSuccess = this.client_ldap.delUser(attributes.get("name").toString().substring(6), LDAPString.toString());
                    Enum_CstmDiag windowCreationSuccess = Enum_CstmDiag.ERROR;

                    ADJ_Alert successAlert = new ADJ_Alert(userDeletionSuccess, this.translator);
                    successAlert.show(stage, this.screenMiddleX, this.screenMiddleY);

                    if (this.lastTreeItem != null) {
                        this.fx_ScrollPane.setContent(null);
                        this.fx_TablePane.getChildren().removeAll(this.fx_TablePane.getChildren());

                        LDAPWindow bldr_table = new LDAPWindow(this);

                        // Enum_CstmDiag.SUCCESS if everything goes well
                        windowCreationSuccess = bldr_table.generateLDAPWindow();

                        LDAPTableView LDAPTableView = new LDAPTableView(bldr_table);
                        LDAPTableView.generateLDAPTableView(this.lastTreeItem);
                    }

                    // Shows an alert if something happened during window initialization
                    if (windowCreationSuccess.equals(Enum_CstmDiag.ERROR)) {
                        ADJ_Alert windowCreationAlert = new ADJ_Alert(windowCreationSuccess, this.translator);
                        windowCreationAlert.show(stage, this.screenMiddleX, this.screenMiddleY);
                    }
                }
            });

            deleteWarning.show(stage, this.screenMiddleX, this.screenMiddleY);
        }
    }

    /**
     * Fügt dem LDAP einen Nutzer hinzu und aktualisiert
     * anschließend die TableView durch eine neue
     * LDAP Abfrage.
     *
     * @throws IOException when .fxml file is not found
     * @since 0.1
     */
    public void UserAdd() throws IOException {

        FXMLLoader loader = new FXMLLoader();

        Parent fxmlParent = loader.load(this.getClass().getResourceAsStream("/headless/entertainment/adj/fxml/AddUserDiag.fxml"));

        Ctrl_AddUser addCtrl = loader.getController();

        Popup pop = new Popup();
        pop.getContent().add(fxmlParent);
        pop.setAutoHide(true);
        pop.setHideOnEscape(true);

        addCtrl.initDialog(this, pop);

        pop.show(this.fx_ScrollPane.getScene().getWindow(), this.screenMiddleX, this.screenMiddleY);
    }

    /* -------------------------------- */
    /* ------ Getter and Setter  ------ */
    /* -------------------------------- */

    /**
     * TODO: EDIT JAVA-DOC
     * Gets fx table pane.
     *
     * @return the fx table pane
     * @since 0.1
     */
    public VBox getFx_TablePane() {
        return this.fx_TablePane;
    }

    /**
     * TODO: EDIT JAVA-DOC
     * Gets fx scroll pane.
     *
     * @return the fx scroll pane
     * @since 0.1
     */
    public ScrollPane getFx_ScrollPane() {
        return this.fx_ScrollPane;
    }

    /**
     * TODO: EDIT JAVA-DOC
     * Gets client ldap.
     *
     * @return the client ldap
     * @since 0.1
     */
    public Client_LDAP getClient_ldap() {
        return this.client_ldap;
    }

    /**
     * TODO: EDIT JAVA-DOC
     * Gets last tree item.
     *
     * @return the last tree item
     * @since 0.1
     */
    public TreeItem<String> getLastTreeItem() {
        return this.lastTreeItem;
    }

    /**
     * TODO: EDIT JAVA-DOC
     * Gets screen middle x.
     *
     * @return the screen middle x
     * @since 0.1
     */
    public double getScreenMiddleX() {
        return this.screenMiddleX;
    }

    /**
     * TODO: EDIT JAVA-DOC
     * Gets screen middle y.
     *
     * @return the screen middle y
     * @since 0.1
     */
    public double getScreenMiddleY() {
        return this.screenMiddleY;
    }

    /**
     * TODO: EDIT JAVA-DOC
     * Gets translator.
     *
     * @return the translator
     * @since 0.1
     */
    public ADJ_Translator getTranslator() {
        return this.translator;
    }

    /**
     * TODO: EDIT JAVA-DOC
     * Sets use table.
     *
     * @param useTable the use table
     * @since 0.1
     */
    public void setUseTable(TableView<Attributes> useTable) {
        this.useTable = useTable;
    }

    /**
     * TODO: EDIT JAVA-DOC
     * Sets last tree item.
     *
     * @param lastTreeItem the last tree item
     * @since 0.1
     */
    public void setLastTreeItem(TreeItem<String> lastTreeItem) {
        this.lastTreeItem = lastTreeItem;
    }


}



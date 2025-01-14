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
import headless.entertainment.adj.Settings.Config;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import java.util.*;

/**
 * Generates the whole window skeleton for
 * left Part TreeView and right Part
 * TableView.
 *
 * @author Adrian Domenic Walter Weidig
 * @version 0.1
 * @since 0.1
 */
public class LDAPWindow {

    private final LDAPTableView LDAPTableView;

    private final Ctrl_ADJ controller;

    public LDAPWindow(final Ctrl_ADJ controller) {
        this.controller = controller;
        this.LDAPTableView = new LDAPTableView(this);
    }

    /* -------------------------------- */
    /* ------ TreeView Elements  ------ */
    /* -------------------------------- */

    /**
     * Generates the whole View with the
     * left TreeView and right TableView
     * part.
     *
     * @return CstmDiag Enum for Logging or Dialog purposes
     * @see javax.naming.directory.DirContext#search(String, String, SearchControls)
     * @since 0.1
     */
    public Enum_CstmDiag generateLDAPWindow() {
        Enum_CstmDiag result = Enum_CstmDiag.SUCCESS;

        TreeItem<String> tree_Root = new TreeItem<>(Config.getInstance().getDomain());

        TreeView<String> ou_Tree = new TreeView<>();
        ou_Tree.setShowRoot(false);
        ou_Tree.setEditable(true);
        ou_Tree.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        ou_Tree.setRoot(tree_Root);
        //TODO: Performance Boosting!
        //TODO: Maybe input own TextField Class with image and use onClicks
        ou_Tree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            this.LDAPTableView.generateLDAPTableView(newValue);
            this.controller.setLastTreeItem(newValue);
        });

        this.controller.getFx_ScrollPane().setContent(ou_Tree);

        List<String> prohibitionList = Arrays.asList(Config.getInstance().getGroup_Filter().replaceAll("/n", "").split(","));

        //TODO: Komprimieren und vereinfachen
        ArrayList<String> organizationalUnits = new ArrayList<>();

        // Case there's only 1 or no element inside the prohibitionList
        StringBuilder filter = new StringBuilder("(objectClass=organizationalUnit)");

        if (!prohibitionList.isEmpty()) {
            if (!(prohibitionList.size() == 1 && prohibitionList.get(0).equals(""))) {
                filter = new StringBuilder("(&(objectClass=organizationalUnit)(!(|");
                for (String s : prohibitionList) {
                    filter.append("(").append(s).append(")");
                }
                filter.append(")))");
            } else {
                prohibitionList = new ArrayList<>();
            }
        }

        try {
            NamingEnumeration<SearchResult> all_Searchresults = this.controller.getClient_ldap().search(Config.getInstance().getDomain(), ("objectClass=organizationalUnit"));
            while (all_Searchresults.hasMoreElements()) {
                try {
                    boolean prohibited = false;

                    SearchResult searchresult = all_Searchresults.next();

                    Attributes all_Attributes = searchresult.getAttributes();

                    if (all_Attributes != null && all_Attributes.get("distinguishedName") != null) {

                        String attribute = all_Attributes.get("distinguishedName").toString().substring(19).replaceAll(Config.getInstance().getDomain(), "");

                        //TODO: Workaround entfernen.
                        // Aktuell filtert der obige LDAP Filter alles, was in der config.xml eingegeben wurde.
                        // Seltsamerweise macht er das eben NICHT für alle entsprechenden Einträge.
                        for (String s : prohibitionList) {
                            if (attribute.contains(s)) {
                                prohibited = true;
                                break;
                            }
                        }

                        if (!prohibited) {
                            organizationalUnits.add(attribute.replaceAll("OU=", ""));
                        }
                    }

                } catch (NamingException e) {
                    result = Enum_CstmDiag.ERROR;
                }
            }

            ArrayList<String[]> treeList = this.sortOrganizationUnits(organizationalUnits);

            LDAPTreeView ldapTreeView = new LDAPTreeView();

            ldapTreeView.generateLDAPTreeView(tree_Root, treeList);

        } catch (NamingException e) {
            result = Enum_CstmDiag.ERROR;
        }

        return result;
    }


    /**
     * Sort organization units array list.
     *
     * @param organizationalUnits the organizational units
     * @return the array list
     * @since 0.1
     */
    private ArrayList<String[]> sortOrganizationUnits(final ArrayList<String> organizationalUnits) {
        TreeSet<String> sortList = new TreeSet<>();

        for (String ou : organizationalUnits) {
            String[] sortArray = ou.split(",");
            Collections.reverse(Arrays.asList(sortArray));

            String tmpString = Arrays.toString(sortArray).trim();

            // Yeah. I forget what this regex means.
            tmpString = tmpString.replaceAll("\\[(.*?)]", "$1");
            sortList.add(tmpString);
        }

        ArrayList<String[]> treeList = new ArrayList<>();

        for (String s : sortList) {
            String[] sortArray = s.split(",");
            treeList.add(sortArray);
        }
        return treeList;
    }

    /* -------------------------------- */
    /* ------ Setter und Getter   ----- */
    /* -------------------------------- */

    Ctrl_ADJ getController() {
        return this.controller;
    }
}
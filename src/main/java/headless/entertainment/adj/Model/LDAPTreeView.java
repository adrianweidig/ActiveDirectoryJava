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

import javafx.scene.control.TreeItem;

import java.util.ArrayList;

/**
 * Generates the Left Window Part with the
 * LDAP attributes being configured.
 *
 * @author Adrian Domenic Walter Weidig
 * @version 0.1
 * @see headless.entertainment.adj.Settings.Config
 * @since 0.1
 */
public class LDAPTreeView {

    /**
     * Uses the Root (Normally the domain) and the
     * treeList with all found LDAP entries and
     * builds the TreeView recursively.
     *
     * @param tree_Root the tree root
     * @param treeList  the tree list
     * @since 0.1
     */
    void generateLDAPTreeView(final TreeItem<String> tree_Root, final ArrayList<String[]> treeList) {
        for (String[] ouStrings : treeList) {
            TreeItem<String> TI = tree_Root;
            TreeItem<String> TI_Parent = tree_Root;
            int value = 0;

            while (TI != null) {
                TI = this.searchItem(TI, ouStrings[value]);
                if (TI == null) {
                    TI = new TreeItem<>(ouStrings[value]);
                    TI_Parent.getChildren().add(TI);
                    TI = null;
                } else {
                    TI_Parent = TI;
                    value++;
                }

            }
        }
    }

    /**
     * Recursive Method to generate the
     * TreeItem used in TreeView.
     *
     * @param root        the root
     * @param searchValue the search value
     * @return the tree item
     * @since 0.1
     */
    private TreeItem<String> searchItem(final TreeItem<String> root, final String searchValue) {
        TreeItem<String> foundItem = null;

        if (root.getValue().equals(searchValue)) {
            foundItem = root;
        } else if (!root.getChildren().isEmpty()) {
            for (TreeItem<String> childItem : root.getChildren()) {
                foundItem = this.searchItem(childItem, searchValue);
            }
        }
        return foundItem;

    }
}
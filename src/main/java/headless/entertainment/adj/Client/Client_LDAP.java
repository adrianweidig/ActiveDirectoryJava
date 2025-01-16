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

package headless.entertainment.adj.Client;

import headless.entertainment.adj.Enums.Enum_CstmDiag;
import headless.entertainment.adj.Handler.ADJ_Translator;
import headless.entertainment.adj.Settings.Config;

import javax.naming.Context;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.util.Properties;

/**
 * Client class for communication with LDAP.
 *
 * @author Adrian Domenic Walter Weidig
 * @version 0.1
 * @since 0.1
 */
public class Client_LDAP {

    private final DirContext context;

    private final SearchControls searchCtrls = new SearchControls();

    private final String username;

    private final ADJ_Translator translator;

    /**
     * Instantiates a new Client ldap with given
     * password, username.
     *
     * @param password   the LDAP password
     * @param username   the LDAP username
     * @param translator the translator
     * @throws NamingException if password or username is not existing / wrong.
     * @since 0.1
     */
    public Client_LDAP(String password, String username, ADJ_Translator translator) throws NamingException {
        this.username = username;
        this.translator = translator;

        Properties login_Properties = new Properties();
        login_Properties.put(Context.INITIAL_CONTEXT_FACTORY, Config.getInstance().getInit_Context_Factory());
        login_Properties.put(Context.PROVIDER_URL, Config.getInstance().getProvider_Url());
        login_Properties.put(Context.SECURITY_AUTHENTICATION, "simple");
        login_Properties.put(Context.SECURITY_PRINCIPAL, username);
        login_Properties.put(Context.SECURITY_CREDENTIALS, password);

        this.context = new InitialDirContext(login_Properties);

        this.searchCtrls.setSearchScope(SearchControls.SUBTREE_SCOPE);
    }

    /* -------------------------------- */
    /* ------ Other Methods      ------ */
    /* -------------------------------- */

    /**
     * Adds a user to AD.
     *
     * @param attrs      the attributes adding to the user in LDAP.
     * @param LDAPString the LDAP Groups as specialized String.
     * @return whether the operation was successfully or not.
     * @since 0.1
     */
    public Enum_CstmDiag addUser(Attributes attrs, String LDAPString) {
        Enum_CstmDiag result = Enum_CstmDiag.SUCCESS;
        result.setMessage(this.translator.get("Client_LDAP.userSuccessfullyCreated"));

        // Converts the existing StringProperties inside attrs to simple Strings
        try {
            // We cut the first 6 letters: "name: "
            String username = attrs.get("name").toString().substring(6);

            this.context.createSubcontext("cn=" + username + "," + LDAPString, attrs);
        } catch (NameAlreadyBoundException e) {
            result = Enum_CstmDiag.ERROR;
            result.setMessage(this.translator.get("Client_LDAP.exception.nameAlreadyUsed"));
        } catch (InvalidAttributesException e) {
            result = Enum_CstmDiag.ERROR;
            result.setMessage(this.translator.get("Client_LDAP.exception.invalidAttributes"));
        } catch (NamingException e) {
            result = Enum_CstmDiag.ERROR;
            result.setMessage(this.translator.get("Client_LDAP.exception.wrongNamingConvention"));
        }

        return result;
    }

    /**
     * Deletes the given user.
     *
     * @param uname      the username.
     * @param LDAPString the LDAP Group as a specialized String.
     * @return the enum cstm diag
     * @since 0.1
     */
    public Enum_CstmDiag delUser(String uname, String LDAPString) {
        Enum_CstmDiag result = Enum_CstmDiag.SUCCESS;
        result.setMessage(this.translator.get("Client_LDAP.userSuccessfullyDeleted"));

        try {
            this.context.destroySubcontext("cn=" + uname + "," + LDAPString);
        } catch (NamingException e) {
            result = Enum_CstmDiag.ERROR;
            result.setMessage(this.translator.get("Client_LDAP.exception.userNotDeletable"));
        }
        return result;
    }

    /**
     * Searches inside the AD.
     *
     * @param distinguishedName the distinguished name
     * @param filter            the filter
     * @return The results.
     * @throws NamingException if a naming exception is encountered
     * @see javax.naming.directory.DirContext#search(String, String, SearchControls) javax.naming.directory.DirContext#search(String, String, SearchControls)
     * @since 0.1
     */
    public NamingEnumeration<SearchResult> search(String distinguishedName, String filter) throws NamingException {
        return this.context.search(distinguishedName, filter, this.searchCtrls);
    }

    /**
     * Returns whether the user is inside the OU or not.
     * You can use this method to check whether a person
     * is member of admin group or not.
     * <p>
     *
     * @param ouGroup the OU you want to check
     * @return whether the user has admin rights or not.
     * @see #search(String, String) #search(String, String)
     * @since 0.1
     */
    public Enum_CstmDiag isMemberOf(String ouGroup) {
        Enum_CstmDiag isMember;

        try {
            isMember = Enum_CstmDiag.SUCCESS;

            String filter = "(&(objectClass=*)(userPrincipalName=" + this.username + ")(memberOf=" + ouGroup + "))";
            NamingEnumeration<SearchResult> values = this.search(Config.getInstance().getDomain(), filter);
            if (values.hasMoreElements()) {
                isMember = Enum_CstmDiag.INFO;
            }
        } catch (NamingException e) {
            isMember = Enum_CstmDiag.ERROR;
        }
        return isMember;
    }

    /* -------------------------------- */
    /* ------ Getter and Setter  ------ */
    /* -------------------------------- */
}
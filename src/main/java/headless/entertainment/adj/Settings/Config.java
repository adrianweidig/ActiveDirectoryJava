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

package headless.entertainment.adj.Settings;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Static JPRO shared Config usable
 * by all JPRO Sessions.
 *
 * @author Adrian Domenic Walter Weidig
 * @version 0.1
 * @since 0.1
 */
public class Config {

    private final static String CFG_PATH = "/headless/entertainment/adj/cfg/config.xml";

    private static volatile Config instance;

    private final static Properties CFG_PROPERTIES = new Properties();

    /**
     * Loads all the XML information from config.xml
     *
     * @since 0.1
     */
    private Config() {
        try {
            CFG_PROPERTIES.loadFromXML(this.getClass().getResourceAsStream(CFG_PATH));
        } catch (FileNotFoundException e) {
            System.out.println("Config File nicht gefunden!");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static Config getInstance() {
        return instance == null ? new Config() : instance;
    }

    public String getDomain() {
        return CFG_PROPERTIES.getProperty("Domain");
    }

    public String getInit_Context_Factory() {
        return CFG_PROPERTIES.getProperty("Init_Context_Factory");
    }

    public String getProvider_Url() {
        return CFG_PROPERTIES.getProperty("Provider_Url");
    }

    public String getLDAP_Admin_Group_Filter() {
        return CFG_PROPERTIES.getProperty("LDAP_Admin_Group_Filter");
    }

    public String getGroup_Filter() {
        return CFG_PROPERTIES.getProperty("Group_Filter");
    }

    public String getPermitted_Attributes() {
        return CFG_PROPERTIES.getProperty("Permitted_Attributes");
    }

    public String getRequired_User_Add_Attributes() {
        return CFG_PROPERTIES.getProperty("Required_User_Add_Attributes");
    }

    public String getOptional_User_Add_Attributes() {
        return CFG_PROPERTIES.getProperty("Optional_User_Add_Attributes");
    }


}

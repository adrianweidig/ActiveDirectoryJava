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

package headless.entertainment.adj.Handler;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Translator class used for changing View-language
 * on fly.
 *
 * @author Adrian Domenic Walter Weidig
 * @version 0.1
 * @since 0.1
 */
public class ADJ_Translator {

    /* Important to know that JPRO shares all statics, but not final only
       means that we save memory, because all user sessions will use this
       constant Path String.  */

    private final static String RES_BUNDLE_PATH = "headless/entertainment/adj/I18n";

    // Watch out: Default Locale is set to en_EN, de_DE most of the time. de_DE is Locale.GERMANY not Locale.GERMAN
    private final ArrayList<Locale> supportedLanguage = new ArrayList<>(Arrays.asList(Locale.ENGLISH, Locale.GERMANY));

    private final ObjectProperty<Locale> locale = new SimpleObjectProperty<>(this.getDefaultLocale());

    /**
     * Formats the string with given locale and returns
     * the formatted string with information from
     * Resource Bundle.
     *
     * @param key  the text information
     * @param args the additional args for Resource Bundle usage
     * @return the formatted string from Resource Bundle
     * @since 0.1
     */
    public String get(final String key, final Object... args) {
        // Call-By-Reference, because RB.getBundle gives same
        ResourceBundle bundle = ResourceBundle.getBundle(RES_BUNDLE_PATH, this.getLanguage());
        return MessageFormat.format(bundle.getString(key), args);
    }

    /**
     * Creates a String Binding from given information to the
     * get method in connection to the used language.
     *
     * @param key  the text information
     * @param args the additional args for Resource Bundle usage
     * @return the finished string binding
     * @since 0.1
     */
    public StringBinding getStringBinding(final String key, final Object... args) {
        return Bindings.createStringBinding(() -> this.get(key, args), this.locale);
    }

    /* -------------------------------- */
    /* ------ Getter and Setter  ------ */
    /* -------------------------------- */

    /**
     * Checks for the System default language
     * and the program specific support. If the
     * default language ist not supported English
     * is used.
     * <p>
     *
     * @return System default language or English
     */
    public Locale getDefaultLocale() {
        return this.supportedLanguage.contains(Locale.getDefault()) ? Locale.getDefault() : Locale.ENGLISH;
    }

    public Locale getLanguage() {
        return this.locale.get();
    }

    public void setLanguage(final Locale locale) {
        this.locale.set(locale);
    }

}

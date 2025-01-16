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

package headless.entertainment.adj.Enums;

import headless.entertainment.adj.Model.ADJ_Alert;

/**
 * Enum usable by Errors to show special popup
 * dialogues needed by JPRO instead of normal
 * blocking alerts.
 * <p>
 * You can add a message to return more
 * potential information OR use message
 * directly when creating ADJ_Alert.
 *
 * @see ADJ_Alert
 * @since 0.1
 */
public enum Enum_CstmDiag {

    SUCCESS("headless/entertainment/adj/images/dialog_icons/success.png", ""),

    ERROR("headless/entertainment/adj/images/dialog_icons/error.png", ""),

    INFO("headless/entertainment/adj/images/dialog_icons/info.png", ""),

    WARNING("headless/entertainment/adj/images/dialog_icons/warning.png", "");

    private final String imagePath;

    private String message;

    /**
     * Instantiates a new Enum
     *
     * @param imagePath the image path
     * @param message   the message
     * @since 0.1
     */
    Enum_CstmDiag(final String imagePath, final String message) {
        this.imagePath = imagePath;
        this.message = message;
    }

    /* -------------------------------- */
    /* ------ Getter and Setter  ------ */
    /* -------------------------------- */

    /**
     * TODO: EDIT JAVA-DOC
     * Gets image path.
     *
     * @return the image path
     * @since 0.1
     */
    public String getImagePath() {
        return this.imagePath;
    }

    /**
     * TODO: EDIT JAVA-DOC
     * Sets message.
     *
     * @param message the message
     * @since 0.1
     */
    public void setMessage(final String message) {
        this.message = message;
    }

    /**
     * TODO: EDIT JAVA-DOC
     * Gets message.
     *
     * @return the message
     * @since 0.1
     */
    public String getMessage() {
        return this.message;
    }
}

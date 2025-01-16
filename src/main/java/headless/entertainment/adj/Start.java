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

package headless.entertainment.adj;

import com.jpro.webapi.JProApplication;
import headless.entertainment.adj.Controller.Ctrl_Login;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;

/**
 * Main class for starting
 *
 * @author Adrian Domenic Walter Weidig
 * @version 0.1
 * @since 0.1
 */
public class Start extends JProApplication {

    /**
     * The main start position in the JavaFX program.
     *
     * @param primaryStage the primary stage / window
     * @throws Exception the exception
     * @since 0.1
     */
    @Override
    public void start(Stage primaryStage) throws Exception {


        primaryStage.setTitle("Active Directory Java");
        primaryStage.getIcons().add(new Image("headless/entertainment/adj/images/KeyLogo.png"));
        primaryStage.setResizable(true);

        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent fxmlScene = fxmlLoader.load(this.getClass().getResourceAsStream("/headless/entertainment/adj/fxml/Login.fxml"));
        Scene scene = new Scene(fxmlScene);
        primaryStage.setScene(scene);
        primaryStage.show();

        Ctrl_Login loginController = fxmlLoader.getController();

        // Only works if ANY browser is installed on system
        loginController.setHostServices(this.getHostServices());
        try {
            loginController.setWebAPI(this.getWebAPI());
            // Already issued, that RE is totally bad here by JPro
            // see .getWebApi()
        } catch (RuntimeException e) {
            System.err.println("HINT: Application running as desktop application");
        }

        loginController.initView();
    }

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     * @since 0.1
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * TODO: EDIT JAVA-DOC
     * Stop.
     *
     * @throws Exception the exception
     * @since 0.1
     */
    @Override
    public void stop() throws Exception {
        super.stop();

        File exportDirectory = new File("exports/");

        // Deletes all export Files
        deleteExportFiles(exportDirectory);

        // Deletes all occurrences of directories inside export directory
        // There should be none
        checkForExportDirectories(exportDirectory);
    }

    /**
     * Deletes all occurrences of directories inside export directory
     * There should be none!
     *
     * @param dir the dir that has to be inspected
     * @since 0.1
     */
    private static void checkForExportDirectories(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                checkForExportDirectories(f);
                // We don't need the boolean return, because
                // files which are not getting deleted are
                // protected or used
                f.delete();
            }
        }
    }

    /**
     * Deletes all export Files inside export directory
     * and possible subdirectories.
     * There should be none subdirectories!
     *
     * @param dir the dir / file that has to be inspected
     * @since 0.1
     */
    private static void deleteExportFiles(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteExportFiles(f);
                } else {
                    // We don't need the boolean return, because
                    // files which are not getting deleted are
                    // protected or used
                    f.delete();
                }
            }
        }
    }
}

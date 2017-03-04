//rando
//PDF Merging Utility
//Uses Apache's PDFBox Jar

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

public class Main extends Application {

    ArrayList<String> fileList = new ArrayList<String>();
    String savePath = "";
    String fileName = "";
    TextField fileNameHolder = new TextField();
    TextArea area = new TextArea();
    int counter = 0;

    public static void main(String[] args) throws IOException {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.getIcons().add(new Image("PDF.png"));
        stage.setResizable(false);
        fileNameHolder.setPrefColumnCount(20);
        fileNameHolder.setPromptText("Name of file to be saved.");

        area.setPromptText("Selected Files.");
        area.setPrefColumnCount(60);
        area.setPrefRowCount(12);
        area.setEditable(false);

        Button open = new Button();
        open.setText("Select File");
        open.setOnAction((t) -> {
            FileChooser choose = new FileChooser();
            choose.setTitle("Choose PDF to Edit");
            List<File> file = choose.showOpenMultipleDialog(stage);
            if (file != null) {
                for (File f : file) {
                    fileList.add(f.getPath());
                }
                area.setText("");
                for (String f : fileList) {
                    area.setText(area.getText() + f + "\n");
                }
            }
        });

        Button merge = new Button();
        merge.setText("Merge Files");
        merge.setOnAction((t) -> {
            PDFMergerUtility merger = new PDFMergerUtility();

            fileName = fileNameHolder.getText();

            if (fileList.size() > 1) {
                if (savePath.length() > 0) {
                    if (fileName.length() > 0) {
                        for (String s : fileList) {
                            try {
                                merger.setDestinationFileName(savePath + fileName + ".pdf");
                                merger.addSource(s);
                            } catch (FileNotFoundException ex) {
                                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        try {
                            merger.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
                            area.setText("File saved in: " + savePath);
                        } catch (IOException ex) {
                            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Please enter file name to be saved.");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a folder to save the file.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please select at least two files to merge.");
            }
        });

        Button convert = new Button();
        convert.setText("IMG -> PDF");
        convert.setOnAction((t) -> {
            fileName = fileNameHolder.getText();
            if (fileList.size() > 0) {
                if (savePath.length() > 0) {
                    if (fileName.length() > 0) {
                        for (String s : fileList) {
                            PDDocument doc = new PDDocument();
                            try {
                                InputStream in = new FileInputStream(s);
                                BufferedImage bImg = ImageIO.read(in);
                                float width = bImg.getWidth();
                                float height = bImg.getHeight();
                                PDPage page = new PDPage(new PDRectangle(width, height));
                                doc.addPage(page);
                                PDImageXObject img = PDImageXObject.createFromFile(s, doc);
                                PDPageContentStream cStream = new PDPageContentStream(doc, page);
                                cStream.drawImage(img, 0, 0);
                                cStream.close();
                                in.close();
                                if (counter > 0) {
                                    doc.save(savePath + fileName + counter + ".pdf");
                                } else {
                                    doc.save(savePath + fileName + ".pdf");
                                }
                                doc.close();
                                counter++;
                            } catch (FileNotFoundException ex) {
                                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (IOException ex) {
                                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        counter = 0;
                    } else {
                        JOptionPane.showMessageDialog(null, "Please enter file name to be saved.");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a folder to save the file.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please select at least two files to merge.");
            }
            area.setText("PDF saved in: " + savePath);
        });

        Button saveDirectory = new Button();
        saveDirectory.setText("Choose Dir");
        saveDirectory.setOnAction((t) -> {
            DirectoryChooser choose = new DirectoryChooser();
            choose.setTitle("Choose Save Directory");
            File directory = choose.showDialog(stage);
            savePath = directory.getPath() + "\\";
        });

        Button clear = new Button();
        clear.setText("Clear Files");
        clear.setOnAction((t) -> {
            clearFiles();
        }
        );

        GridPane root = new GridPane();
        GridPane.setConstraints(open,
                0, 0);
        GridPane.setConstraints(merge,
                1, 0);
        GridPane.setConstraints(convert, 2, 0);
        GridPane.setConstraints(saveDirectory, 3, 0);
        GridPane.setConstraints(clear,
                4, 0);
        GridPane.setConstraints(fileNameHolder, 0, 1, 4, 1);
        GridPane.setConstraints(area, 0, 2, 4, 1);
        root.setHgap(
                5);
        root.setVgap(
                5);
        root.getChildren()
                .addAll(open, merge, convert, saveDirectory, clear, fileNameHolder, area);

        Scene scene = new Scene(root, 550, 300);

        stage.setTitle(
                "PDF Edit");
        stage.setScene(scene);

        stage.show();
    }

    private void clearFiles() {
        if (fileList.size() > 0) {
            fileList.clear();
            area.setText("Files Cleared!");
        } else {
            area.setText("No Files to Clear!");
        }
    }

}

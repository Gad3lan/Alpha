package com.alpha;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.transform.Transform;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class Controller implements Initializable {

    public Label contrastVal;
    public Slider contrast;
    public Label brightnessVal;
    public Slider brightness;
    public Label saturationVal;
    public Slider saturation;
    public Label opacityVal;
    public Slider opacity;
    public ImageView imagePreview;
    public AnchorPane imagePane;
    public ImageView exportPreview;
    public Label fileName;
    public Label thresholdVal;
    public Slider threshold;
    public ToggleGroup Color;
    public ToggleGroup AlphaType;
    public Button undoButton;
    public Button redoButton;
    public Button resetButton;
    public Button convertButton;
    public ColorAdjust colorAdjust = new ColorAdjust();
    public App.AlphaColor alphaType;
    public String path;
    public boolean whiteAlpha = true;
    public File selectedFile;
    public ArrayList<Image> history;
    public int historyIdx = 0;
    public ResourceBundle bundle;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bundle = resources;
    }

    public void updateContrast() {
        contrastVal.setText(Math.round(contrast.getValue()) + " %");
        if (imagePreview.getImage() != null) {
            colorAdjust.setContrast(contrast.getValue()/100);
            imagePreview.setEffect(colorAdjust);
            exportPreview.setEffect(colorAdjust);
        }
    }

    public void updateBrightness() {
        brightnessVal.setText(Math.round(brightness.getValue()) + " %");
        if (imagePreview.getImage() != null) {
            colorAdjust.setBrightness(brightness.getValue()/100);
            imagePreview.setEffect(colorAdjust);
            exportPreview.setEffect(colorAdjust);
        }
    }

    public void updateSaturation() {
        saturationVal.setText(Math.round(saturation.getValue()) + " %");
        if (imagePreview.getImage() != null) {
            colorAdjust.setSaturation(saturation.getValue()/100);
            imagePreview.setEffect(colorAdjust);
            exportPreview.setEffect(colorAdjust);
        }
    }

    public void updateOpacity() {
        opacityVal.setText(Math.round(opacity.getValue()) + " %");
        if (imagePreview.getImage() != null) {
            imagePreview.setOpacity(opacity.getValue()/100);
            exportPreview.setOpacity(opacity.getValue()/100);
        }
    }

    public void updateThreshold() {
        thresholdVal.setText(Math.round(threshold.getValue()) + " %");
    }

    public void importImage(ActionEvent actionEvent) throws FileNotFoundException {
        Button b = (Button) actionEvent.getSource();
        Window w = b.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(bundle.getString("fileChooser.open"));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
        selectedFile = fileChooser.showOpenDialog(w);
        if (selectedFile != null) {
            path = selectedFile.getAbsolutePath();
            FileInputStream img = new FileInputStream(selectedFile.getPath());
            Image image = new Image(img);
            imagePreview.setImage(image);
            exportPreview.setImage(image);
            fileName.setText("alpha_" + selectedFile.getName());
            history = new ArrayList<>();
            history.add(image);
            historyIdx++;
            resetSliders();
        }
        imagePreview.fitWidthProperty().bind(imagePane.widthProperty().subtract(30));
        imagePreview.fitHeightProperty().bind(imagePane.heightProperty().subtract(30));
        convertButton.setDisable(false);
    }

    public Image getConvertableImage(boolean isExport) {
        SnapshotParameters parameters = new SnapshotParameters();
        double scale;
        if (isExport) {
            if (imagePreview.getImage().getWidth() > imagePreview.getImage().getHeight()) {
                scale = history.get(0).getWidth() / imagePreview.getFitWidth();
            } else {
                scale = history.get(0).getHeight() / imagePreview.getFitHeight();
            }
        } else {
            if (imagePreview.getImage().getWidth() > imagePreview.getImage().getHeight()) {
                scale = imagePreview.getImage().getWidth() / imagePreview.getFitWidth();
            } else {
                scale = imagePreview.getImage().getHeight() / imagePreview.getFitHeight();
            }
            scale = Math.round(scale);
        }
        parameters.setTransform(Transform.scale(scale, scale));
        parameters.setFill(javafx.scene.paint.Color.TRANSPARENT);
        if (isExport) {
            WritableImage writableImage = new WritableImage((int) history.get(0).getWidth(), (int) history.get(0).getHeight());
            return imagePreview.snapshot(parameters, writableImage);
        }
        return imagePreview.snapshot(parameters, null);
    }

    public void exportImage(ActionEvent actionEvent) {
        Button b = (Button) actionEvent.getSource();
        Window w = b.getScene().getWindow();
        if (selectedFile != null) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(selectedFile.getParentFile());
            String fileName = selectedFile.getName();
            if (fileName.indexOf(".") > 0) {
                fileName = fileName.substring(0, fileName.lastIndexOf("."));
            }
            fileChooser.setInitialFileName("alpha_" + fileName + ".png");
            fileChooser.setTitle(bundle.getString("fileChooser.save"));
            File outputFile = fileChooser.showSaveDialog(w);
            if (outputFile != null) {
                String name = outputFile.getName();
                if (name.length() < 4 || !name.endsWith(".png")) {
                    outputFile = new File(outputFile.getAbsoluteFile() + ".png");
                }
                Image image = getConvertableImage(true);
                BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
                try {
                    ImageIO.write(bImage, "png", outputFile);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void setAlphaType() throws Exception {
        RadioButton b = (RadioButton) Color.getSelectedToggle();
        switch (b.getId()) {
            case "RGB" -> alphaType = App.AlphaColor.RGB;
            case "Red" -> alphaType = App.AlphaColor.R;
            case "Green" -> alphaType = App.AlphaColor.G;
            case "Blue" -> alphaType = App.AlphaColor.B;
            default -> throw new Exception("Unknown alpha type");
        }
    }

    public void convertImage() {
        if (!convertButton.isDisable()) {
            Image image = getConvertableImage(false);
            AlphaConverter converter = new AlphaConverter(image, alphaType, threshold.getValue(), whiteAlpha);
            Image convertedImage = converter.convert();
            if (historyIdx < history.size()) {
                history = new ArrayList<>(history.subList(0, historyIdx));
                redoButton.setDisable(true);
            }
            history.add(convertedImage);
            historyIdx++;
            if (undoButton.isDisable()) {
                undoButton.setDisable(false);
                resetButton.setDisable(false);
            }
            imagePreview.setImage(convertedImage);
            exportPreview.setImage(convertedImage);
        }
    }

    public void selectAlphaColor() {
        RadioButton b = (RadioButton) AlphaType.getSelectedToggle();
        whiteAlpha = b.getId().equals("white");
    }

    public void undo() {
        if (historyIdx > 1) {
            historyIdx--;
            imagePreview.setImage(history.get(historyIdx-1));
            exportPreview.setImage(history.get(historyIdx-1));
            if (historyIdx < 2) {
                undoButton.setDisable(true);
            }
            if (redoButton.isDisable()) {
                redoButton.setDisable(false);
            }
        }
    }

    public void redo() {
        if (historyIdx < history.size()) {
            imagePreview.setImage(history.get(historyIdx));
            exportPreview.setImage(history.get(historyIdx));
            historyIdx++;
            if (historyIdx > history.size() - 1) {
                redoButton.setDisable(true);
            }
            if (undoButton.isDisable()) {
                undoButton.setDisable(false);
            }
        }
    }

    public void reset() {
        historyIdx = 1;
        imagePreview.setImage(history.get(0));
        exportPreview.setImage(history.get(0));
        history = new ArrayList<>(history.subList(0, 1));
        undoButton.setDisable(true);
        resetButton.setDisable(true);
    }

    public void resetSliders() {
        contrast.setValue(0);
        contrastVal.setText("0 %");
        brightness.setValue(0);
        brightnessVal.setText("0 %");
        saturation.setValue(0);
        saturationVal.setText("0 %");
        opacity.setValue(100);
        opacityVal.setText("100 %");
    }
}

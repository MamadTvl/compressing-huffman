import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Editor {
    @FXML
    private ProgressBar bar;
    @FXML
    private Button Browse, DecodeB;
    @FXML
    private TextArea TextEditor;
    @FXML
    private CheckBox Choose;
    private String OrginalAddress, CompressedAddress;
    private HuffmanTree huffmanTree;
    private Alert alert;

    public void ShowText() throws IOException {
        if (CompressedAddress != null) {
            TextEditor.setText("");
            FileReader fileReader = new FileReader(CompressedAddress);
            StringBuilder text = new StringBuilder();
            int i;
            while ((i = fileReader.read()) != -1)
                text.append((char) i);
            fileReader.close();
            TextEditor.setText(text.toString());
        } else {
            Show();
        }
    }

    private void Show() throws IOException {
        if (Choose.isSelected()) {
            FileReader fileReader = new FileReader(OrginalAddress);
            StringBuilder text = new StringBuilder();
            int i;
            while ((i = fileReader.read()) != -1)
                text.append((char) i);
            fileReader.close();
            TextEditor.setText(text.toString());
        }
    }

    public void ChooseFile() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("src/EditorFiles"));
        fileChooser.setTitle("select your File");
        File selectedDirectory = fileChooser.showOpenDialog(null);
        OrginalAddress = String.valueOf(selectedDirectory);
        if (OrginalAddress != null)
            ShowText();
    }

    public void Decode() throws IOException {
        if (huffmanTree.Address.equals(OrginalAddress)) {
            bar.setProgress(0);
            huffmanTree.Decoding(CompressedAddress, bar);
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setContentText("The file is Decoded Successfully !");
            alert.showAndWait();
            bar.setProgress(0);
            DecodeB.setDisable(true);
        }
        else {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("You Can't decoding !");
            alert.showAndWait();
        }

    }

    public void Encode() throws IOException {
        if (Choose.isSelected()) {
            Encoding();
            bar.setProgress(0);
        } else {
            OrginalAddress = Save();
            if (!OrginalAddress.equals("null")) {
                FileWriter fileWriter = new FileWriter(OrginalAddress);
                String texteditor = TextEditor.getText();
                for (int j = 0; j < texteditor.length(); j++) {
                    fileWriter.write(texteditor.charAt(j));
                }
                fileWriter.close();
                Encoding();
                bar.setProgress(0);
            } else {
                alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Mmm...");
                alert.setContentText("What ?!\nClick Ok and Go Away");
                alert.showAndWait();
            }
        }

    }

    private void Encoding() throws IOException {
        if (OrginalAddress != null) {
            StringBuilder address = new StringBuilder();
            address.append(OrginalAddress);
            address.delete(address.length() - 4, address.length());
            address.append("-Compressed.txt");
            huffmanTree = new HuffmanTree(OrginalAddress);
            huffmanTree.Print();
            CompressedAddress = address.toString();
            huffmanTree.Encoding(CompressedAddress, bar);

            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setContentText("The file is Encoded Successfully !");
            alert.showAndWait();
            DecodeB.setDisable(false);
        } else {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Something Wrong!\nCheck that checkbox");
            alert.showAndWait();
        }

    }

    public String Save() throws IOException {
        String address = null;
        if (!TextEditor.getText().equals("")) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(new File("src/EditorFiles"));
            fileChooser.setTitle("select your File");
            File selectedDirectory = fileChooser.showOpenDialog(null);
            address = String.valueOf(selectedDirectory);
        } else {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Oops...");
            alert.setContentText("Type something first !");
            alert.showAndWait();
        }
        assert address != null;
        if (!address.equals("null"))
            Save(address);
        return address;
    }

    private void Save(String address) throws IOException {
        if (address != null) {
            FileWriter fileWriter = new FileWriter(address);
            String texteditor = TextEditor.getText();
            for (int j = 0; j < texteditor.length(); j++) {
                fileWriter.write(texteditor.charAt(j));
            }
            fileWriter.close();
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Good News");
            alert.setContentText("File Saved!");
            alert.showAndWait();
        }
    }

    public void BrowseOn() {
        if (Choose.isSelected()) {
            Browse.setDisable(false);
            TextEditor.setEditable(false);
        }
        else {
            Browse.setDisable(true);
            TextEditor.setEditable(true);
        }

    }
}

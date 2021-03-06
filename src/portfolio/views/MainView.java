package portfolio.views;

import com.sun.deploy.util.SystemUtils;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedAreaChart;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.MenuBar;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.*;
import java.time.LocalDate;
import java.util.*;

import javafx.stage.StageStyle;
import javafx.util.Callback;
import portfolio.models.PoolPairModel;
import portfolio.models.TransactionModel;
import portfolio.controllers.MainViewController;

public class MainView implements Initializable {
    @FXML
    public NumberAxis yAxis;
    @FXML
    public AnchorPane mainAnchorPane;
    @FXML
    public AnchorPane leftAnchorPane;
    @FXML
    public Button btnRawData;
    @FXML
    public Button btnAnalyse;
    @FXML
    public Button btnUpdateDatabase;
    @FXML
    public Pane anchorPanelAnalyse, anchorPanelRawData;
    @FXML
    public Label strCurrentBlockLocally, strCurrentBlockOnBlockchain, strLastUpdate;
    @FXML
    public ComboBox<String> cmbCoins, cmbIntervall, cmbFiat, cmbPlotCurrency, cmbCoinsCom, cmbIntervallCom, cmbFiatCom, cmbPlotCurrencyCom, cmbIntervallOver;
    @FXML
    public ImageView coinImageRewards, coinImageCommissions;
    @FXML
    public DatePicker dateFrom = new DatePicker();
    @FXML
    public DatePicker dateTo = new DatePicker();
    @FXML
    public DatePicker dateFromCom = new DatePicker();
    @FXML
    public DatePicker dateToCom = new DatePicker();
    @FXML
    public DatePicker dateFromOver = new DatePicker();
    @FXML
    public DatePicker dateToOver = new DatePicker();
    @FXML
    public TabPane tabPane = new TabPane();
    @FXML
    public LineChart<Number, Number> plotRewards, plotCommissions1, plotCommissions2;
    @FXML
    public StackedAreaChart<Number, Number> plotOverview;
    @FXML
    public TableView<TransactionModel> rawDataTable;
    @FXML
    public TableView<PoolPairModel> plotTable;
    @FXML
    public TableColumn<TransactionModel, Long> blockTimeColumn;
    @FXML
    public TableColumn<TransactionModel, String> typeColumn;
    @FXML
    public TableColumn<TransactionModel, Double> cryptoValueColumn;
    @FXML
    public TableColumn<TransactionModel, String> cryptoCurrencyColumn;
    @FXML
    public TableColumn<TransactionModel, String> blockHashColumn;
    @FXML
    public TableColumn<TransactionModel, Integer> blockHeightColumn;
    @FXML
    public TableColumn<TransactionModel, String> poolIDColumn;
    @FXML
    public TableColumn<TransactionModel, String> ownerColumn;
    @FXML
    public TableColumn<TransactionModel, Double> fiatValueColumn;
    @FXML
    public TableColumn<TransactionModel, String> fiatCurrencyColumn;
    @FXML
    public TableColumn<TransactionModel, String> transactionColumn;
    @FXML
    public TableColumn<PoolPairModel, String> timeStampColumn;
    @FXML
    public TableColumn<PoolPairModel, Double> crypto1Column;
    @FXML
    public TableColumn<PoolPairModel, Double> crypto2Column;
    @FXML
    public TableColumn<PoolPairModel, Double> fiatColumn;
    @FXML
    public TableColumn<PoolPairModel, String> poolPairColumn;
    @FXML
    public Label CurrentBlock;
    public Label CurrentBlockChain;
    public Label LastUpdate;
    public Tab Rewards;
    public Tab Commissions;
    public Tab Overview;
    public Label StartDate;
    public Label EndDate;
    public Label EndDateCom;
    public Label StartDateCom;
    public Label StartDateOver;
    public Label EndDateOver;

    public MenuItem menuItemCopySelected = new MenuItem("Copy");
    public MenuItem menuItemCopyHeaderSelected = new MenuItem("Copy with header");
    public MenuItem menuItemExportSelected = new MenuItem("Export selected to CSV");
    public MenuItem menuItemExportAllSelected = new MenuItem("Export all to CSV");
    public MenuItem menuItemCopySelectedPlot = new MenuItem("Copy");
    public MenuItem menuItemCopyHeaderSelectedPlot = new MenuItem("Copy with header");
    public MenuItem menuItemExportSelectedPlot = new MenuItem("Export selected to CSV");
    public MenuItem menuItemExportAllSelectedPlot = new MenuItem("Export all to CSV");


    public Stage settingsStage, helpStage, donateStage;
    public boolean init = true;
    public Button btnSettings;
    public Button btnHelp;
    public Button btnDonate;
    public Label connectionLabel;
    public Button btnConnect;
    MainViewController mainViewController = new MainViewController();

    public MainView() {
    }

    public void btnAnalysePressed() {
        this.anchorPanelAnalyse.toFront();
        this.fiatColumn.setVisible(!this.tabPane.getSelectionModel().getSelectedItem().getText().equals(this.mainViewController.settingsController.translationList.getValue().get("Rewards")));
        if (!this.init) {
            this.mainViewController.plotUpdate(this.tabPane.getSelectionModel().getSelectedItem().getText());
            if (tabPane.getSelectionModel().getSelectedItem().getText().equals(this.mainViewController.settingsController.translationList.getValue().get(this.mainViewController.settingsController.translationList.getValue().get("Overview")))) {
                crypto1Column.setText(this.mainViewController.settingsController.translationList.getValue().get("Rewards") + " (" + mainViewController.settingsController.selectedFiatCurrency.getValue() + ")");
                crypto2Column.setText(this.mainViewController.settingsController.translationList.getValue().get("Commissions") + " (" + mainViewController.settingsController.selectedFiatCurrency.getValue() + ")");
            }
            if (tabPane.getSelectionModel().getSelectedItem().getText().equals(this.mainViewController.settingsController.translationList.getValue().get(this.mainViewController.settingsController.translationList.getValue().get("Rewards")))) {
                crypto1Column.setText(mainViewController.settingsController.selectedCoin.getValue().split("-")[1]);
                crypto2Column.setText(mainViewController.settingsController.selectedCoin.getValue().split("-")[1] + "(" + mainViewController.settingsController.selectedFiatCurrency.getValue() + ")");
            }
            if (tabPane.getSelectionModel().getSelectedItem().getText().equals(this.mainViewController.settingsController.translationList.getValue().get(this.mainViewController.settingsController.translationList.getValue().get("Commissions")))) {
                crypto1Column.setText(mainViewController.settingsController.selectedCoin.getValue().split("-")[1]);
                crypto2Column.setText(mainViewController.settingsController.selectedCoin.getValue().split("-")[0]);
            }
        }
    }

    public void btnRawDataPressed() {
        this.anchorPanelRawData.toFront();
    }

    public void helpPressed() throws IOException {

        if (helpStage != null) helpStage.close();
        final Delta dragDelta = new Delta();
        Parent root = FXMLLoader.load(getClass().getResource("HelpView.fxml"));
        Scene scene = new Scene(root);

        helpStage = new Stage();
        helpStage.initStyle(StageStyle.UNDECORATED);
        scene.setOnMousePressed(mouseEvent -> {
            // record a delta distance for the drag and drop operation.
            dragDelta.x = helpStage.getX() - mouseEvent.getScreenX();
            dragDelta.y = helpStage.getY() - mouseEvent.getScreenY();
        });
        scene.setOnMouseDragged(mouseEvent -> {
            helpStage.setX(mouseEvent.getScreenX() + dragDelta.x);
            helpStage.setY(mouseEvent.getScreenY() + dragDelta.y);
        });
        helpStage.getIcons().add(new Image(new File(System.getProperty("user.dir") + "/defi-portfolio/src/icons/help.png").toURI().toString()));
        helpStage.setTitle((this.mainViewController.settingsController.translationList.getValue().get("HelpTitle").toString()));
        helpStage.setScene(scene);
        ChangeListener<Number> widthListener = (observable, oldValue, newValue) -> {
            double stageWidth = newValue.doubleValue();
            helpStage.setX(mainAnchorPane.getScene().getWindow().getX() + mainAnchorPane.getScene().getWindow().getWidth() / 2 - stageWidth / 2);
        };
        ChangeListener<Number> heightListener = (observable, oldValue, newValue) -> {
            double stageHeight = newValue.doubleValue();
            helpStage.setY(mainAnchorPane.getScene().getWindow().getY() + mainAnchorPane.getScene().getWindow().getHeight() / 2 - stageHeight / 2);
        };

        helpStage.widthProperty().addListener(widthListener);
        helpStage.heightProperty().addListener(heightListener);

        helpStage.setOnShown(e -> {
            helpStage.widthProperty().removeListener(widthListener);
            helpStage.heightProperty().removeListener(heightListener);
        });
        helpStage.show();

        java.io.File darkMode = new File(System.getProperty("user.dir") + "/defi-portfolio/src/portfolio/styles/darkMode.css");
        java.io.File lightMode = new File(System.getProperty("user.dir") + "/defi-portfolio/src/portfolio/styles/lightMode.css");
        if (this.mainViewController.settingsController.selectedStyleMode.getValue().equals("Dark Mode")) {
            helpStage.getScene().getStylesheets().add(darkMode.toURI().toString());
        } else {
            helpStage.getScene().getStylesheets().add(lightMode.toURI().toString());
        }
    }

    public void openAccountInformation() throws IOException {
        if (donateStage != null) donateStage.close();
        final Delta dragDelta = new Delta();
        Parent root = FXMLLoader.load(getClass().getResource("DonateView.fxml"));
        Scene scene = new Scene(root);
        donateStage = new Stage();
        donateStage.initStyle(StageStyle.UNDECORATED);
        scene.setOnMousePressed(mouseEvent -> {
            // record a delta distance for the drag and drop operation.
            dragDelta.x = donateStage.getX() - mouseEvent.getScreenX();
            dragDelta.y = donateStage.getY() - mouseEvent.getScreenY();
        });
        scene.setOnMouseDragged(mouseEvent -> {
            donateStage.setX(mouseEvent.getScreenX() + dragDelta.x);
            donateStage.setY(mouseEvent.getScreenY() + dragDelta.y);
        });
        donateStage.getIcons().add(new Image(new File(System.getProperty("user.dir") + "/defi-portfolio/src/icons/donate.png").toURI().toString()));
        donateStage.setTitle(this.mainViewController.settingsController.translationList.getValue().get("Donate").toString());
        donateStage.setScene(scene);
        ChangeListener<Number> widthListener = (observable, oldValue, newValue) -> {
            double stageWidth = newValue.doubleValue();
            donateStage.setX(mainAnchorPane.getScene().getWindow().getX() + mainAnchorPane.getScene().getWindow().getWidth() / 2 - stageWidth / 2);
        };
        ChangeListener<Number> heightListener = (observable, oldValue, newValue) -> {
            double stageHeight = newValue.doubleValue();
            donateStage.setY(mainAnchorPane.getScene().getWindow().getY() + mainAnchorPane.getScene().getWindow().getHeight() / 2 - stageHeight / 2);
        };

        donateStage.widthProperty().addListener(widthListener);
        donateStage.heightProperty().addListener(heightListener);

        donateStage.setOnShown(e -> {
            donateStage.widthProperty().removeListener(widthListener);
            donateStage.heightProperty().removeListener(heightListener);
        });
        donateStage.show();

        java.io.File darkMode = new File(System.getProperty("user.dir") + "/defi-portfolio/src/portfolio/styles/darkMode.css");
        java.io.File lightMode = new File(System.getProperty("user.dir") + "/defi-portfolio/src/portfolio/styles/lightMode.css");
        if (this.mainViewController.settingsController.selectedStyleMode.getValue().equals("Dark Mode")) {
            donateStage.getScene().getStylesheets().add(darkMode.toURI().toString());
        } else {
            donateStage.getScene().getStylesheets().add(lightMode.toURI().toString());
        }
    }

    public void openSettingPressed() throws IOException {

        if (settingsStage != null) settingsStage.close();
        final Delta dragDelta = new Delta();
        Parent root = FXMLLoader.load(getClass().getResource("SettingsView.fxml"));
        Scene scene = new Scene(root);
        settingsStage = new Stage();
        settingsStage.initStyle(StageStyle.UNDECORATED);
        scene.setOnMousePressed(mouseEvent -> {
            // record a delta distance for the drag and drop operation.
            dragDelta.x = settingsStage.getX() - mouseEvent.getScreenX();
            dragDelta.y = settingsStage.getY() - mouseEvent.getScreenY();
        });
        scene.setOnMouseDragged(mouseEvent -> {
            settingsStage.setX(mouseEvent.getScreenX() + dragDelta.x);
            settingsStage.setY(mouseEvent.getScreenY() + dragDelta.y);
        });
        settingsStage.getIcons().add(new Image(new File(System.getProperty("user.dir") + "/defi-portfolio/src/icons/settings.png").toURI().toString()));
        settingsStage.setTitle(this.mainViewController.settingsController.translationList.getValue().get("Settings").toString());
        settingsStage.setScene(scene);

        ChangeListener<Number> widthListener = (observable, oldValue, newValue) -> {
            double stageWidth = newValue.doubleValue();
            settingsStage.setX(mainAnchorPane.getScene().getWindow().getX() + mainAnchorPane.getScene().getWindow().getWidth() / 2 - stageWidth / 2);
        };
        ChangeListener<Number> heightListener = (observable, oldValue, newValue) -> {
            double stageHeight = newValue.doubleValue();
            settingsStage.setY(mainAnchorPane.getScene().getWindow().getY() + mainAnchorPane.getScene().getWindow().getHeight() / 2 - stageHeight / 2);
        };

        settingsStage.widthProperty().addListener(widthListener);
        settingsStage.heightProperty().addListener(heightListener);

        settingsStage.setOnShown(e -> {
            settingsStage.widthProperty().removeListener(widthListener);
            settingsStage.heightProperty().removeListener(heightListener);
        });

        settingsStage.show();

        java.io.File darkMode = new File(System.getProperty("user.dir") + "/defi-portfolio/src/portfolio/styles/darkMode.css");
        java.io.File lightMode = new File(System.getProperty("user.dir") + "/defi-portfolio/src/portfolio/styles/lightMode.css");
        if (this.mainViewController.settingsController.selectedStyleMode.getValue().equals("Dark Mode")) {
            settingsStage.getScene().getStylesheets().add(darkMode.toURI().toString());
        } else {
            settingsStage.getScene().getStylesheets().add(lightMode.toURI().toString());
        }
    }

    public void connectDefid(ActionEvent actionEvent) {
        this.mainViewController.transactionController.startServer();
        this.mainViewController.startTimer();
    }

    static class Delta {
        double x, y;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        this.mainViewController.mainView = this;
        this.anchorPanelRawData.toFront();
        this.btnRawData.fire();
        updateLanguage();

        coinImageRewards.setImage(new Image(new File(System.getProperty("user.dir") + "/defi-portfolio/src/icons/" + mainViewController.settingsController.selectedCoin.getValue().split("-")[0].toLowerCase() + "-icon.png").toURI().toString()));
        coinImageCommissions.setImage(new Image(new File(System.getProperty("user.dir") + "/defi-portfolio/src/icons/" + mainViewController.settingsController.selectedCoin.getValue().split("-")[0].toLowerCase() + "-icon.png").toURI().toString()));
        updateStylesheet();

        this.mainViewController.settingsController.selectedStyleMode.addListener(style -> updateStylesheet());
        final Delta dragDelta = new Delta();

        this.btnConnect.disableProperty().bind(this.mainViewController.bDataBase.not());


        this.btnUpdateDatabase.disableProperty().bindBidirectional(this.mainViewController.bDataBase);
        this.connectionLabel.visibleProperty().bindBidirectional(this.mainViewController.bDataBase);
        this.strCurrentBlockLocally.textProperty().bindBidirectional(this.mainViewController.strCurrentBlockLocally);
        this.strCurrentBlockOnBlockchain.textProperty().bindBidirectional(this.mainViewController.strCurrentBlockOnBlockchain);
        this.strLastUpdate.textProperty().bindBidirectional(this.mainViewController.strLastUpdate);
        this.btnUpdateDatabase.setOnAction(e -> {
            mainViewController.btnUpdateDatabasePressed();
            if (!this.init) mainViewController.plotUpdate(this.tabPane.getSelectionModel().getSelectedItem().getText());
        });

        tabPane.getSelectionModel().selectedItemProperty().addListener(
                (ov, t, t1) -> {
                    if (!this.init)
                        mainViewController.plotUpdate(tabPane.getSelectionModel().getSelectedItem().getText());
                    cmbCoins.setVisible(true);
                    cmbFiat.setVisible(true);
                    cmbPlotCurrency.setVisible(true);
                    cmbCoinsCom.setVisible(true);
                    cmbFiatCom.setVisible(true);
                    cmbPlotCurrencyCom.setVisible(true);

                    if (tabPane.getSelectionModel().getSelectedItem().getText().equals(this.mainViewController.settingsController.translationList.getValue().get("Overview"))) {
                        crypto1Column.setText(this.mainViewController.settingsController.translationList.getValue().get("Rewards") + " (" + mainViewController.settingsController.selectedFiatCurrency.getValue() + ")");
                        crypto2Column.setText(this.mainViewController.settingsController.translationList.getValue().get("Commissions") + " " + mainViewController.settingsController.selectedFiatCurrency.getValue() + ")");
                    }
                    if (tabPane.getSelectionModel().getSelectedItem().getText().equals(this.mainViewController.settingsController.translationList.getValue().get("Rewards"))) {
                        crypto1Column.setText(mainViewController.settingsController.selectedCoin.getValue().split("-")[1]);
                        crypto2Column.setText(mainViewController.settingsController.selectedCoin.getValue().split("-")[1] + "(" + mainViewController.settingsController.selectedFiatCurrency.getValue() + ")");
                    }
                    if (tabPane.getSelectionModel().getSelectedItem().getText().equals(this.mainViewController.settingsController.translationList.getValue().get("Commissions"))) {
                        crypto1Column.setText(mainViewController.settingsController.selectedCoin.getValue().split("-")[1]);
                        crypto2Column.setText(mainViewController.settingsController.selectedCoin.getValue().split("-")[0]);
                    }
                    fiatColumn.setVisible(!tabPane.getSelectionModel().getSelectedItem().getText().equals(this.mainViewController.settingsController.translationList.getValue().get("Rewards")));
                }
        );

        this.cmbIntervall.valueProperty().bindBidirectional(this.mainViewController.settingsController.selectedIntervall);
        this.cmbIntervall.valueProperty().addListener((ov, oldValue, newValue) -> {
            if (newValue != null) {

                switch (newValue) {
                    case "Daily":
                    case "Täglich":
                        this.mainViewController.settingsController.selectedIntervallInt = "Daily";
                        break;
                    case "Weekly":
                    case "Wöchentlich":
                        this.mainViewController.settingsController.selectedIntervallInt = "Weekly";
                        break;
                    case "Monthly":
                    case "Monatlich":
                        this.mainViewController.settingsController.selectedIntervallInt = "Monthly";
                        break;
                    case "Yearly":
                    case "Jährlich":
                        this.mainViewController.settingsController.selectedIntervallInt = "Yearly";
                        break;
                    default:
                        break;
                }
            }
            if (!this.init) mainViewController.plotUpdate(tabPane.getSelectionModel().getSelectedItem().getText());
            this.mainViewController.settingsController.saveSettings();
        });

        this.cmbIntervallCom.valueProperty().bindBidirectional(this.mainViewController.settingsController.selectedIntervall);
        this.cmbIntervallOver.valueProperty().bindBidirectional(this.mainViewController.settingsController.selectedIntervall);

        this.cmbCoins.getItems().addAll(this.mainViewController.settingsController.cryptoCurrencies);
        this.cmbCoins.valueProperty().bindBidirectional(this.mainViewController.settingsController.selectedCoin);
        this.cmbCoins.valueProperty().addListener((ov, oldValue, newValue) -> {

            if (!this.init) mainViewController.plotUpdate(tabPane.getSelectionModel().getSelectedItem().getText());

            if (tabPane.getSelectionModel().getSelectedItem().getText().equals(this.mainViewController.settingsController.translationList.getValue().get("Overview"))) {
                crypto1Column.setText(this.mainViewController.settingsController.translationList.getValue().get("Rewards") + " (" + mainViewController.settingsController.selectedFiatCurrency.getValue() + ")");
                crypto2Column.setText(this.mainViewController.settingsController.translationList.getValue().get("Commissions") + " (" + mainViewController.settingsController.selectedFiatCurrency.getValue() + ")");
                cmbCoins.setVisible(false);
                cmbFiat.setVisible(false);
                cmbPlotCurrency.setVisible(false);
            }
            if (tabPane.getSelectionModel().getSelectedItem().getText().equals(this.mainViewController.settingsController.translationList.getValue().get("Rewards"))) {
                crypto1Column.setText(mainViewController.settingsController.selectedCoin.getValue().split("-")[1]);
                crypto2Column.setText(mainViewController.settingsController.selectedCoin.getValue().split("-")[1] + "(" + mainViewController.settingsController.selectedFiatCurrency.getValue() + ")");
            }
            if (tabPane.getSelectionModel().getSelectedItem().getText().equals(this.mainViewController.settingsController.translationList.getValue().get("Commissions"))) {
                crypto1Column.setText(mainViewController.settingsController.selectedCoin.getValue().split("-")[1]);
                crypto2Column.setText(mainViewController.settingsController.selectedCoin.getValue().split("-")[0]);
            }

            fiatColumn.setVisible(!tabPane.getSelectionModel().getSelectedItem().getText().equals(this.mainViewController.settingsController.translationList.getValue().get("Rewards")));
            this.mainViewController.settingsController.saveSettings();
            coinImageRewards.setImage(new Image(new File(System.getProperty("user.dir") + "/defi-portfolio/src/icons/" + mainViewController.settingsController.selectedCoin.getValue().split("-")[0].toLowerCase() + "-icon.png").toURI().toString()));
            coinImageCommissions.setImage(new Image(new File(System.getProperty("user.dir") + "/defi-portfolio/src/icons/" + mainViewController.settingsController.selectedCoin.getValue().split("-")[0].toLowerCase() + "-icon.png").toURI().toString()));
        });

        this.cmbCoinsCom.getItems().addAll(this.mainViewController.settingsController.cryptoCurrencies);
        this.cmbCoinsCom.valueProperty().bindBidirectional(this.mainViewController.settingsController.selectedCoin);

        this.fiatColumn.setText(this.fiatColumn.getText() + " (" + mainViewController.settingsController.selectedFiatCurrency.getValue() + ")");
        this.crypto1Column.setText(this.mainViewController.settingsController.translationList.getValue().get("Rewards") + " (" + mainViewController.settingsController.selectedFiatCurrency.getValue() + ")");
        this.crypto2Column.setText(this.mainViewController.settingsController.translationList.getValue().get("Commissions") + " (" + mainViewController.settingsController.selectedFiatCurrency.getValue() + ")");

        this.mainViewController.settingsController.selectedFiatCurrency.addListener((ov, oldValue, newValue) -> {
            if (!oldValue.equals(newValue) & this.plotRewards != null) {
                if (!this.init) mainViewController.plotUpdate(tabPane.getSelectionModel().getSelectedItem().getText());
                this.mainViewController.settingsController.saveSettings();
                this.fiatColumn.setText(this.fiatColumn.getText() + " (" + newValue + ")");
                if (tabPane.getSelectionModel().getSelectedItem().getText().equals(this.mainViewController.settingsController.translationList.getValue().get("Overview"))) {
                    crypto1Column.setText(this.mainViewController.settingsController.translationList.getValue().get("Rewards") + " (" + mainViewController.settingsController.selectedFiatCurrency.getValue() + ")");
                    crypto2Column.setText(this.mainViewController.settingsController.translationList.getValue().get("Commissions") + " (" + mainViewController.settingsController.selectedFiatCurrency.getValue() + ")");
                }
                if (tabPane.getSelectionModel().getSelectedItem().getText().equals(this.mainViewController.settingsController.translationList.getValue().get("Rewards"))) {
                    crypto1Column.setText(mainViewController.settingsController.selectedCoin.getValue().split("-")[1]);
                    crypto2Column.setText(mainViewController.settingsController.selectedCoin.getValue().split("-")[1] + "(" + mainViewController.settingsController.selectedFiatCurrency.getValue() + ")");
                }
                if (tabPane.getSelectionModel().getSelectedItem().getText().equals(this.mainViewController.settingsController.translationList.getValue().get("Commissions"))) {
                    crypto1Column.setText(mainViewController.settingsController.selectedCoin.getValue().split("-")[1]);
                    crypto2Column.setText(mainViewController.settingsController.selectedCoin.getValue().split("-")[0]);
                }

                this.fiatColumn.setVisible(!tabPane.getSelectionModel().getSelectedItem().getText().equals(this.mainViewController.settingsController.translationList.getValue().get("Rewards")));
            }

        });


        this.cmbFiatCom.getItems().addAll(this.mainViewController.settingsController.plotCurrency);
        this.cmbFiatCom.valueProperty().bindBidirectional(this.mainViewController.settingsController.selectedPlotCurrency);

        this.cmbFiat.getItems().addAll(this.mainViewController.settingsController.plotCurrency);
        this.cmbFiat.valueProperty().bindBidirectional(this.mainViewController.settingsController.selectedPlotCurrency);
        this.cmbFiat.valueProperty().addListener((ov, oldValue, newValue) -> {
            if (!this.init) mainViewController.plotUpdate(tabPane.getSelectionModel().getSelectedItem().getText());
            this.mainViewController.settingsController.saveSettings();

            if (tabPane.getSelectionModel().getSelectedItem().getText().equals(this.mainViewController.settingsController.translationList.getValue().get("Overview"))) {
                crypto1Column.setText(this.mainViewController.settingsController.translationList.getValue().get("Rewards") + " (" + mainViewController.settingsController.selectedFiatCurrency.getValue() + ")");
                crypto2Column.setText(this.mainViewController.settingsController.translationList.getValue().get("Commissions") + " (" + mainViewController.settingsController.selectedFiatCurrency.getValue() + ")");
                cmbCoins.setVisible(false);
                cmbFiat.setVisible(false);
                cmbPlotCurrency.setVisible(false);
            }
            if (tabPane.getSelectionModel().getSelectedItem().getText().equals(this.mainViewController.settingsController.translationList.getValue().get("Rewards"))) {
                crypto1Column.setText(mainViewController.settingsController.selectedCoin.getValue().split("-")[1]);
                crypto2Column.setText(mainViewController.settingsController.selectedCoin.getValue().split("-")[1] + "(" + mainViewController.settingsController.selectedFiatCurrency.getValue() + ")");
            }
            if (tabPane.getSelectionModel().getSelectedItem().getText().equals(this.mainViewController.settingsController.translationList.getValue().get("Commissions"))) {
                crypto1Column.setText(mainViewController.settingsController.selectedCoin.getValue().split("-")[1]);
                crypto2Column.setText(mainViewController.settingsController.selectedCoin.getValue().split("-")[0]);
            }

            fiatColumn.setVisible(!tabPane.getSelectionModel().getSelectedItem().getText().equals(this.mainViewController.settingsController.translationList.getValue().get("Rewards")));
        });

        this.mainViewController.settingsController.selectedDecimal.addListener((ov, oldValue, newValue) -> {
            if (!oldValue.equals(newValue) & this.plotRewards != null) {
                mainViewController.plotUpdate(tabPane.getSelectionModel().getSelectedItem().getText());
            }
        });

        this.cmbPlotCurrency.valueProperty().bindBidirectional(this.mainViewController.settingsController.selectedPlotType);
        this.cmbPlotCurrency.valueProperty().addListener((ov, oldValue, newValue) -> {
            if (!this.init) mainViewController.plotUpdate(tabPane.getSelectionModel().getSelectedItem().getText());
        });

        this.cmbPlotCurrencyCom.valueProperty().bindBidirectional(this.mainViewController.settingsController.selectedPlotType);

        this.dateFrom.valueProperty().bindBidirectional(this.mainViewController.settingsController.dateFrom);
        this.dateFrom.valueProperty().addListener((ov, oldValue, newValue) -> {
            if (!this.init) mainViewController.plotUpdate(tabPane.getSelectionModel().getSelectedItem().getText());
        });

        this.dateFromCom.valueProperty().bindBidirectional(this.mainViewController.settingsController.dateFrom);
        this.dateFromOver.valueProperty().bindBidirectional(this.mainViewController.settingsController.dateFrom);

        this.dateFrom.setValue(LocalDate.now().minusDays(30L));
        this.dateFrom.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || date.compareTo(today) > 0);
            }
        });

        this.dateFromCom.setValue(LocalDate.now().minusDays(30L));
        this.dateFromCom.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || date.compareTo(today) > 0);
            }
        });

        this.dateFromOver.setValue(LocalDate.now().minusDays(30L));
        this.dateFromOver.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || date.compareTo(today) > 0);
            }
        });

        this.mainViewController.settingsController.selectedLanguage.addListener((ov, oldValue, newValue) -> this.updateLanguage());

        this.dateTo.valueProperty().bindBidirectional(this.mainViewController.settingsController.dateTo);
        this.dateTo.valueProperty().addListener((ov, oldValue, newValue) -> {
            if (!this.init) mainViewController.plotUpdate(tabPane.getSelectionModel().getSelectedItem().getText());
        });
        this.dateTo.setValue(LocalDate.now());
        this.dateTo.setDayCellFactory(picker -> new DateCell() {

            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || date.compareTo(today) > 0);
            }
        });

        this.dateToOver.valueProperty().bindBidirectional(this.mainViewController.settingsController.dateTo);
        this.dateToOver.setValue(LocalDate.now());
        this.dateToOver.setDayCellFactory(picker -> new DateCell() {

            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || date.compareTo(today) > 0);
            }
        });

        this.dateToCom.valueProperty().bindBidirectional(this.mainViewController.settingsController.dateTo);
        this.dateToCom.setValue(LocalDate.now());
        this.dateToCom.setDayCellFactory(picker -> new DateCell() {

            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || date.compareTo(today) > 0);
            }
        });

        initializeTableViewContextMenu();
        initPlotTableContextMenu();

        rawDataTable.itemsProperty().set(this.mainViewController.getTransactionTable());
        rawDataTable.getSelectionModel().setSelectionMode(
                SelectionMode.MULTIPLE
        );

        plotTable.itemsProperty().set(this.mainViewController.getPlotData());
        plotTable.getSelectionModel().setSelectionMode(
                SelectionMode.MULTIPLE
        );

        timeStampColumn.setCellValueFactory(param -> param.getValue().getBlockTime());
        crypto1Column.setCellValueFactory(param -> param.getValue().getCryptoValue1().asObject());
        crypto2Column.setCellValueFactory(param -> param.getValue().getCryptoValue2().asObject());
        fiatColumn.setCellValueFactory(param -> param.getValue().getFiatValue().asObject());
        poolPairColumn.setCellValueFactory(param -> param.getValue().getPoolPair());
        ownerColumn.setCellValueFactory(param -> param.getValue().getOwner());
        blockTimeColumn.setCellValueFactory(param -> param.getValue().getBlockTime().asObject());
        typeColumn.setCellValueFactory(param -> param.getValue().getType());
        cryptoCurrencyColumn.setCellValueFactory(param -> param.getValue().getCrypto());
        cryptoValueColumn.setCellValueFactory(param -> param.getValue().getCryptoValue().asObject());
        blockHashColumn.setCellValueFactory(param -> param.getValue().getBlockHash());
        blockHeightColumn.setCellValueFactory(param -> param.getValue().getBlockHeight().asObject());
        poolIDColumn.setCellValueFactory(param -> param.getValue().getPoolID());
        fiatValueColumn.setCellValueFactory(param -> param.getValue().getFiat().asObject());
        fiatCurrencyColumn.setCellValueFactory(param -> param.getValue().getFiatCurrency());
        transactionColumn.setCellValueFactory(param -> param.getValue().getTxID());
        Callback<TableColumn<TransactionModel, String>, TableCell<TransactionModel, String>> cellFactory0
                = (final TableColumn<TransactionModel, String> entry) -> new TableCell<TransactionModel, String>() {

            Hyperlink hyperlink = new Hyperlink();

            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    TransactionModel tempParam = rawDataTable.getItems().get(getIndex());
                    hyperlink.setText(item);
                    hyperlink.setOnAction((event) -> {
                        try {
                            if (mainViewController.settingsController.getPlatform() == "linux") {
                                // Workaround for Linux because "Desktop.getDesktop().browse()" doesn't work on some Linux implementations
                                if (Runtime.getRuntime().exec(new String[]{"which", "xdg-open"}).getInputStream().read() != -1) {
                                    Runtime.getRuntime().exec(new String[]{"xdg-open", "https://mainnet.defichain.io/#/DFI/mainnet/block/" + tempParam.getBlockHashValue()});
                                } else {
                                    System.out.println("xdg-open is not supported!");
                                }
                            } else {
                                Desktop.getDesktop().browse(new URL("https://mainnet.defichain.io/#/DFI/mainnet/block/" + tempParam.getBlockHashValue()).toURI());
                            }
                        } catch (IOException | URISyntaxException e) {
                            e.printStackTrace();
                        }
                    });
                    setGraphic(hyperlink);
                }
                setText(null);
            }
        };
        blockHashColumn.setCellFactory(cellFactory0);

        Callback<TableColumn<TransactionModel, String>, TableCell<TransactionModel, String>> cellFactory1
                = (final TableColumn<TransactionModel, String> entry) -> new TableCell<TransactionModel, String>() {

            Hyperlink hyperlink = new Hyperlink();

            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    TransactionModel tempParam = rawDataTable.getItems().get(getIndex());
                    hyperlink.setText(item);
                    hyperlink.setOnAction((event) -> {
                        try {
                            if (mainViewController.settingsController.getPlatform() == "linux") {
                                // Workaround for Linux because "Desktop.getDesktop().browse()" doesn't work on some Linux implementations
                                if (Runtime.getRuntime().exec(new String[]{"which", "xdg-open"}).getInputStream().read() != -1) {
                                    Runtime.getRuntime().exec(new String[]{"xdg-open", "https://mainnet.defichain.io/#/DFI/mainnet/address/" + tempParam.getOwnerValue()});
                                } else {
                                    System.out.println("xdg-open is not supported!");
                                }
                            } else {
                                Desktop.getDesktop().browse(new URL("https://mainnet.defichain.io/#/DFI/mainnet/address/" + tempParam.getOwnerValue()).toURI());
                            }
                        } catch (IOException | URISyntaxException e) {
                            e.printStackTrace();
                        }
                    });
                    setGraphic(hyperlink);
                }
                setText(null);
            }
        };
        ownerColumn.setCellFactory(cellFactory1);

        Callback<TableColumn<TransactionModel, Integer>, TableCell<TransactionModel, Integer>> cellFactory2
                = (final TableColumn<TransactionModel, Integer> entry) -> new TableCell<TransactionModel, Integer>() {

            Hyperlink hyperlink = new Hyperlink();

            @Override
            public void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    TransactionModel tempParam = rawDataTable.getItems().get(getIndex());
                    hyperlink.setText(item.toString());
                    hyperlink.setOnAction((event) -> {
                        try {
                            if (mainViewController.settingsController.getPlatform() == "linux") {
                                // Workaround for Linux because "Desktop.getDesktop().browse()" doesn't work on some Linux implementations
                                if (Runtime.getRuntime().exec(new String[]{"which", "xdg-open"}).getInputStream().read() != -1) {
                                    Runtime.getRuntime().exec(new String[]{"xdg-open", "https://mainnet.defichain.io/#/DFI/mainnet/block/" + tempParam.getBlockHeightValue()});
                                } else {
                                    System.out.println("xdg-open is not supported!");
                                }
                            } else {
                                Desktop.getDesktop().browse(new URL("https://mainnet.defichain.io/#/DFI/mainnet/block/" + tempParam.getBlockHeightValue()).toURI());
                            }
                        } catch (IOException | URISyntaxException e) {
                            e.printStackTrace();
                        }
                    });
                    setGraphic(hyperlink);
                }
                setText(null);
            }
        };
        blockHeightColumn.setCellFactory(cellFactory2);

        Callback<TableColumn<TransactionModel, String>, TableCell<TransactionModel, String>> cellFactory3
                = (final TableColumn<TransactionModel, String> entry) -> new TableCell<TransactionModel, String>() {

            Hyperlink hyperlink = new Hyperlink();

            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    TransactionModel tempParam = rawDataTable.getItems().get(getIndex());

                    if (tempParam.getTxIDValue().equals("\"\"")) {
                        setText("-");
                        setGraphic(null);
                    } else {

                        hyperlink.setText(item);
                        hyperlink.setOnAction((event) -> {
                            try {
                                if (mainViewController.settingsController.getPlatform() == "linux") {
                                    // Workaround for Linux because "Desktop.getDesktop().browse()" doesn't work on some Linux implementations
                                    if (Runtime.getRuntime().exec(new String[]{"which", "xdg-open"}).getInputStream().read() != -1) {
                                        Runtime.getRuntime().exec(new String[]{"xdg-open", "https://mainnet.defichain.io/#/DFI/mainnet/tx/" + tempParam.getTxIDValue()});
                                    } else {
                                        System.out.println("xdg-open is not supported!");
                                    }
                                } else {
                                    Desktop.getDesktop().browse(new URL("https://mainnet.defichain.io/#/DFI/mainnet/tx/" + tempParam.getTxIDValue()).toURI());
                                }
                            } catch (IOException | URISyntaxException e) {
                                e.printStackTrace();
                            }
                        });

                        setText(null);
                        setGraphic(hyperlink);
                    }
                }
            }
        };
        transactionColumn.setCellFactory(cellFactory3);


        poolIDColumn.setCellFactory(tc -> new TableCell<TransactionModel, String>() {
            @Override
            protected void updateItem(String poolID, boolean empty) {
                super.updateItem(poolID, empty);
                if (empty) {
                    setText(null);
                } else {

                    String pool = "-";

                    switch (poolID) {
                        case "4":
                            pool = "ETH-DFI";
                            break;
                        case "5":
                            pool = "BTC-DFI";
                            break;
                        case "6":
                            pool = "USDT-DFI";
                            break;
                        case "8":
                            pool = "DOGE-DFI";
                            break;
                        case "10":
                            pool = "LTC-DFI";
                            break;
                        default:
                            break;
                    }

                    setText(pool);
                }
            }
        });

        fiatCurrencyColumn.setCellFactory(tc -> new TableCell<TransactionModel, String>() {
            @Override
            protected void updateItem(String fiatCurrency, boolean empty) {
                super.updateItem(fiatCurrency, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(mainViewController.settingsController.selectedFiatCurrency.getValue());
                }
            }
        });
        fiatColumn.setCellFactory(tc -> new TableCell<PoolPairModel, Double>() {
            @Override
            protected void updateItem(Double fiatValue, boolean empty) {
                super.updateItem(fiatValue, empty);
                if (empty) {
                    setText(null);
                } else {

                    Locale localeDecimal = Locale.GERMAN;
                    if (mainViewController.settingsController.selectedDecimal.getValue().equals(".")) {
                        localeDecimal = Locale.US;
                    }
                    setText(String.format(localeDecimal, "%.8f", fiatValue));
                }
            }
        });

        fiatValueColumn.setCellFactory(tc -> new TableCell<TransactionModel, Double>() {
            @Override
            protected void updateItem(Double fiatValue, boolean empty) {
                super.updateItem(fiatValue, empty);
                if (empty) {
                    setText(null);
                } else {

                    Locale localeDecimal = Locale.GERMAN;
                    if (mainViewController.settingsController.selectedDecimal.getValue().equals(".")) {
                        localeDecimal = Locale.US;
                    }
                    setText(String.format(localeDecimal, "%.8f", fiatValue));
                }
            }
        });

        crypto1Column.setCellFactory(tc -> new TableCell<PoolPairModel, Double>() {
            @Override
            protected void updateItem(Double cryptoValue, boolean empty) {
                super.updateItem(cryptoValue, empty);
                if (empty) {
                    setText(null);
                } else {

                    Locale localeDecimal = Locale.GERMAN;
                    if (mainViewController.settingsController.selectedDecimal.getValue().equals(".")) {
                        localeDecimal = Locale.US;
                    }
                    setText(String.format(localeDecimal, "%.8f", cryptoValue));
                }
            }
        });

        crypto2Column.setCellFactory(tc -> new TableCell<PoolPairModel, Double>() {
            @Override
            protected void updateItem(Double cryptoValue, boolean empty) {
                super.updateItem(cryptoValue, empty);
                if (empty) {
                    setText(null);
                } else {

                    Locale localeDecimal = Locale.GERMAN;
                    if (mainViewController.settingsController.selectedDecimal.getValue().equals(".")) {
                        localeDecimal = Locale.US;
                    }
                    setText(String.format(localeDecimal, "%.8f", cryptoValue));
                }
            }
        });

        cryptoValueColumn.setCellFactory(tc -> new TableCell<TransactionModel, Double>() {
            @Override
            protected void updateItem(Double cryptoValue, boolean empty) {
                super.updateItem(cryptoValue, empty);
                if (empty) {
                    setText(null);
                } else {

                    Locale localeDecimal = Locale.GERMAN;
                    if (mainViewController.settingsController.selectedDecimal.getValue().equals(".")) {
                        localeDecimal = Locale.US;
                    }
                    setText(String.format(localeDecimal, "%.8f", cryptoValue));
                }
            }
        });

        blockTimeColumn.setCellFactory(tc -> new TableCell<TransactionModel, Long>() {
            @Override
            protected void updateItem(Long blockTime, boolean empty) {
                super.updateItem(blockTime, empty);
                if (empty) {
                    setText(null);
                } else {
                    Date date = new Date(blockTime * 1000L);
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    setText(dateFormat.format(date));
                }
            }
        });
        this.init = false;
    }

    private void initializeTableViewContextMenu() {


        ContextMenu contextMenuRawData = new ContextMenu();

        //Init context menu of raw data table
        menuItemCopySelected.setOnAction(event -> mainViewController.copySelectedRawDataToClipboard(rawDataTable.selectionModelProperty().get().getSelectedItems(), false));
        menuItemCopyHeaderSelected.setOnAction(event -> mainViewController.copySelectedRawDataToClipboard(rawDataTable.selectionModelProperty().get().getSelectedItems(), true));
        menuItemExportSelected.setOnAction(event -> mainViewController.exportTransactionToExcel(rawDataTable.selectionModelProperty().get().getSelectedItems()));
        menuItemExportAllSelected.setOnAction(event -> mainViewController.exportTransactionToExcel(rawDataTable.getItems()));

        contextMenuRawData.getItems().add(menuItemCopySelected);
        contextMenuRawData.getItems().add(menuItemCopyHeaderSelected);
        contextMenuRawData.getItems().add(menuItemExportSelected);
        contextMenuRawData.getItems().add(menuItemExportAllSelected);

        this.rawDataTable.contextMenuProperty().set(contextMenuRawData);
    }

    private void initPlotTableContextMenu() {

        //Init context menu of plot table

        ContextMenu contextMenuPlotData = new ContextMenu();
        menuItemCopySelectedPlot.setOnAction(event -> mainViewController.copySelectedDataToClipboard(plotTable.selectionModelProperty().get().getSelectedItems(), false));
        menuItemCopyHeaderSelectedPlot.setOnAction(event -> mainViewController.copySelectedDataToClipboard(plotTable.selectionModelProperty().get().getSelectedItems(), true));
        menuItemExportSelectedPlot.setOnAction(event -> mainViewController.exportPoolPairToExcel(plotTable.selectionModelProperty().get().getSelectedItems(), this.tabPane.getSelectionModel().getSelectedItem().getText()));
        menuItemExportAllSelectedPlot.setOnAction(event -> mainViewController.exportPoolPairToExcel(plotTable.getItems(), this.tabPane.getSelectionModel().getSelectedItem().getText()));

        contextMenuPlotData.getItems().add(menuItemCopySelectedPlot);
        contextMenuPlotData.getItems().add(menuItemCopyHeaderSelectedPlot);
        contextMenuPlotData.getItems().add(menuItemExportSelectedPlot);
        contextMenuPlotData.getItems().add(menuItemExportAllSelectedPlot);

        this.plotTable.contextMenuProperty().set(contextMenuPlotData);
    }

    private void updateStylesheet() {

        java.io.File darkMode = new File(System.getProperty("user.dir") + "/defi-portfolio/src/portfolio/styles/darkMode.css");
        java.io.File lightMode = new File(System.getProperty("user.dir") + "/defi-portfolio/src/portfolio/styles/lightMode.css");
        this.mainAnchorPane.getStylesheets().clear();
        if (this.helpStage != null) this.helpStage.getScene().getStylesheets().clear();
        if (this.settingsStage != null) this.settingsStage.getScene().getStylesheets().clear();
        if (this.donateStage != null) this.donateStage.getScene().getStylesheets().clear();

        if (this.mainViewController.settingsController.selectedStyleMode.getValue().equals("Dark Mode")) {
            this.mainAnchorPane.getStylesheets().add(darkMode.toURI().toString());
            if (this.helpStage != null)
                this.helpStage.getScene().getStylesheets().add(darkMode.toURI().toString());
            if (this.settingsStage != null)
                this.settingsStage.getScene().getStylesheets().add(darkMode.toURI().toString());
            if (this.donateStage != null)
                this.donateStage.getScene().getStylesheets().add(darkMode.toURI().toString());
        } else {
            this.mainAnchorPane.getStylesheets().add(lightMode.toURI().toString());
            if (this.helpStage != null)
                this.helpStage.getScene().getStylesheets().add(lightMode.toURI().toString());
            if (this.settingsStage != null)
                this.settingsStage.getScene().getStylesheets().add(lightMode.toURI().toString());
            if (this.donateStage != null)
                this.donateStage.getScene().getStylesheets().add(lightMode.toURI().toString());
        }
    }

    private void updateLanguage() {
        this.mainViewController.settingsController.updateLanguage();
        this.btnRawData.textProperty().setValue(this.mainViewController.settingsController.translationList.getValue().get("RawData").toString());
        this.menuItemCopySelected.setText(this.mainViewController.settingsController.translationList.getValue().get("Copy").toString());
        this.menuItemCopyHeaderSelected.setText(this.mainViewController.settingsController.translationList.getValue().get("CopyHeader").toString());
        this.menuItemExportSelected.setText(this.mainViewController.settingsController.translationList.getValue().get("ExportSelected").toString());
        this.menuItemExportAllSelected.setText(this.mainViewController.settingsController.translationList.getValue().get("ExportAll").toString());
        this.menuItemCopySelectedPlot.setText(this.mainViewController.settingsController.translationList.getValue().get("Copy").toString());
        this.menuItemCopyHeaderSelectedPlot.setText(this.mainViewController.settingsController.translationList.getValue().get("CopyHeader").toString());
        this.menuItemExportSelectedPlot.setText(this.mainViewController.settingsController.translationList.getValue().get("ExportSelected").toString());
        this.menuItemExportAllSelectedPlot.setText(this.mainViewController.settingsController.translationList.getValue().get("ExportAll").toString());
        this.CurrentBlock.setText(this.mainViewController.settingsController.translationList.getValue().get("CurrentBlock").toString());
        this.CurrentBlockChain.setText(this.mainViewController.settingsController.translationList.getValue().get("CurrentBlockBC").toString());
        this.LastUpdate.setText(this.mainViewController.settingsController.translationList.getValue().get("LastUpdate").toString());
        this.btnSettings.setText(this.mainViewController.settingsController.translationList.getValue().get("Settings").toString());
        this.btnDonate.setText(this.mainViewController.settingsController.translationList.getValue().get("Donate").toString());
        this.Rewards.setText(this.mainViewController.settingsController.translationList.getValue().get("Rewards").toString());
        this.Commissions.setText(this.mainViewController.settingsController.translationList.getValue().get("Commissions").toString());
        this.Overview.setText(this.mainViewController.settingsController.translationList.getValue().get("Overview").toString());
        this.StartDate.setText(this.mainViewController.settingsController.translationList.getValue().get("StartDate").toString());
        this.EndDate.setText(this.mainViewController.settingsController.translationList.getValue().get("EndDate").toString());
        this.StartDateCom.setText(this.mainViewController.settingsController.translationList.getValue().get("StartDate").toString());
        this.EndDateCom.setText(this.mainViewController.settingsController.translationList.getValue().get("EndDate").toString());
        this.StartDateOver.setText(this.mainViewController.settingsController.translationList.getValue().get("StartDate").toString());
        this.EndDateOver.setText(this.mainViewController.settingsController.translationList.getValue().get("EndDate").toString());
        this.btnAnalyse.setText(this.mainViewController.settingsController.translationList.getValue().get("AnalyseData").toString());
        this.btnUpdateDatabase.setText(this.mainViewController.settingsController.translationList.getValue().get("UpdateData").toString());
        if (this.cmbIntervall.getItems().size() > 0) {

            this.cmbIntervallCom.getItems().set(0, this.mainViewController.settingsController.translationList.getValue().get("Daily").toString());
            this.cmbIntervallCom.getItems().set(1, this.mainViewController.settingsController.translationList.getValue().get("Weekly").toString());
            this.cmbIntervallCom.getItems().set(2, this.mainViewController.settingsController.translationList.getValue().get("Monthly").toString());
            this.cmbIntervallCom.getItems().set(3, this.mainViewController.settingsController.translationList.getValue().get("Yearly").toString());
            this.cmbIntervallOver.getItems().set(0, this.mainViewController.settingsController.translationList.getValue().get("Daily").toString());
            this.cmbIntervallOver.getItems().set(1, this.mainViewController.settingsController.translationList.getValue().get("Weekly").toString());
            this.cmbIntervallOver.getItems().set(2, this.mainViewController.settingsController.translationList.getValue().get("Monthly").toString());
            this.cmbIntervallOver.getItems().set(3, this.mainViewController.settingsController.translationList.getValue().get("Yearly").toString());
            this.cmbIntervall.getItems().set(0, this.mainViewController.settingsController.translationList.getValue().get("Daily").toString());
            this.cmbIntervall.getItems().set(1, this.mainViewController.settingsController.translationList.getValue().get("Weekly").toString());
            this.cmbIntervall.getItems().set(2, this.mainViewController.settingsController.translationList.getValue().get("Monthly").toString());
            this.cmbIntervall.getItems().set(3, this.mainViewController.settingsController.translationList.getValue().get("Yearly").toString());
            this.cmbPlotCurrency.getItems().set(0, this.mainViewController.settingsController.translationList.getValue().get("Individual").toString());
            this.cmbPlotCurrency.getItems().set(1, this.mainViewController.settingsController.translationList.getValue().get("Cumulated").toString());
            this.cmbPlotCurrencyCom.getItems().add(0, this.mainViewController.settingsController.translationList.getValue().get("Individual").toString());
            this.cmbPlotCurrencyCom.getItems().add(1, this.mainViewController.settingsController.translationList.getValue().get("Cumulated").toString());

        } else {

            this.cmbIntervallCom.getItems().add(this.mainViewController.settingsController.translationList.getValue().get("Daily").toString());
            this.cmbIntervallCom.getItems().add(this.mainViewController.settingsController.translationList.getValue().get("Weekly").toString());
            this.cmbIntervallCom.getItems().add(this.mainViewController.settingsController.translationList.getValue().get("Monthly").toString());
            this.cmbIntervallCom.getItems().add(this.mainViewController.settingsController.translationList.getValue().get("Yearly").toString());
            this.cmbIntervallOver.getItems().add(this.mainViewController.settingsController.translationList.getValue().get("Daily").toString());
            this.cmbIntervallOver.getItems().add(this.mainViewController.settingsController.translationList.getValue().get("Weekly").toString());
            this.cmbIntervallOver.getItems().add(this.mainViewController.settingsController.translationList.getValue().get("Monthly").toString());
            this.cmbIntervallOver.getItems().add(this.mainViewController.settingsController.translationList.getValue().get("Yearly").toString());
            this.cmbIntervall.getItems().add(this.mainViewController.settingsController.translationList.getValue().get("Daily").toString());
            this.cmbIntervall.getItems().add(this.mainViewController.settingsController.translationList.getValue().get("Weekly").toString());
            this.cmbIntervall.getItems().add(this.mainViewController.settingsController.translationList.getValue().get("Monthly").toString());
            this.cmbIntervall.getItems().add(this.mainViewController.settingsController.translationList.getValue().get("Yearly").toString());
            this.cmbPlotCurrency.getItems().add(this.mainViewController.settingsController.translationList.getValue().get("Individual").toString());
            this.cmbPlotCurrency.getItems().add(this.mainViewController.settingsController.translationList.getValue().get("Cumulated").toString());
            this.cmbPlotCurrencyCom.getItems().add(this.mainViewController.settingsController.translationList.getValue().get("Individual").toString());
            this.cmbPlotCurrencyCom.getItems().add(this.mainViewController.settingsController.translationList.getValue().get("Cumulated").toString());
        }
        this.btnConnect.setText(this.mainViewController.settingsController.translationList.getValue().get("ConnectNode").toString());
        this.connectionLabel.getTooltip().setText(this.mainViewController.settingsController.translationList.getValue().get("UpdateTooltip").toString());
        this.blockTimeColumn.setText(this.mainViewController.settingsController.translationList.getValue().get("Date").toString());
        this.timeStampColumn.setText(this.mainViewController.settingsController.translationList.getValue().get("Date").toString());
        this.typeColumn.setText(this.mainViewController.settingsController.translationList.getValue().get("Operation").toString());
        this.cryptoValueColumn.setText(this.mainViewController.settingsController.translationList.getValue().get("CryptoValue").toString());
        this.cryptoCurrencyColumn.setText(this.mainViewController.settingsController.translationList.getValue().get("CryptoCurrency").toString());
        this.fiatValueColumn.setText(this.mainViewController.settingsController.translationList.getValue().get("FIATValue").toString());
        this.fiatCurrencyColumn.setText(this.mainViewController.settingsController.translationList.getValue().get("FIATCurrency").toString());
        this.poolIDColumn.setText(this.mainViewController.settingsController.translationList.getValue().get("PoolPair").toString());
        this.poolPairColumn.setText(this.mainViewController.settingsController.translationList.getValue().get("PoolPair").toString());
        this.blockHeightColumn.setText(this.mainViewController.settingsController.translationList.getValue().get("BlockHeight").toString());
        this.blockHashColumn.setText(this.mainViewController.settingsController.translationList.getValue().get("BlockHash").toString());
        this.ownerColumn.setText(this.mainViewController.settingsController.translationList.getValue().get("Owner").toString());
        this.transactionColumn.setText(this.mainViewController.settingsController.translationList.getValue().get("TransactionHash").toString());
        this.fiatColumn.setText(this.mainViewController.settingsController.translationList.getValue().get("Total").toString());
        this.mainViewController.donateController.strDonateText.setValue(this.mainViewController.settingsController.translationList.getValue().get("DonateLabel").toString());
        this.mainViewController.helpController.strHelpText.setValue(this.mainViewController.settingsController.translationList.getValue().get("ContactUS").toString());
        this.mainViewController.helpController.strCloseText.setValue(this.mainViewController.settingsController.translationList.getValue().get("Close").toString());
        this.mainViewController.settingsController.selectedPlotType.setValue(this.mainViewController.settingsController.translationList.getValue().get("Individual").toString());
        this.mainViewController.settingsController.selectedIntervall.setValue(this.mainViewController.settingsController.translationList.getValue().get("Daily").toString());
    }

}

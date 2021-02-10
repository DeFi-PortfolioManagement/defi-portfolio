package portfolio.controllers;

import javafx.animation.PauseTransition;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import portfolio.models.PoolPairModel;
import portfolio.models.PortfolioModel;
import portfolio.models.TransactionModel;
import portfolio.services.ExportService;
import portfolio.views.MainView;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;
import java.util.Timer;

public class MainViewController {

    public StringProperty strCurrentBlockLocally = new SimpleStringProperty("0");
    public StringProperty strCurrentBlockOnBlockchain = new SimpleStringProperty("No connection");
    public StringProperty strLastUpdate = new SimpleStringProperty("-");
    public StringProperty strProgressbar = new SimpleStringProperty("");

    //View
    public MainView mainView;
    public JFrame frameUpdate;
    public Frame frameDefid;

    //Table and plot lists
    public List<PoolPairModel> poolPairModelList = new ArrayList<>();
    public ObservableList<TransactionModel> transactionList;
    public ObservableList<PoolPairModel> poolPairList;

    //Init all controller and services
    public SettingsController settingsController = SettingsController.getInstance();
    public CoinPriceController coinPriceController = new CoinPriceController(this.settingsController.strPathAppData + this.settingsController.strCoinPriceData);
    public TransactionController transactionController = new TransactionController(this.settingsController.strPathAppData + this.settingsController.strTransactionData, this.settingsController, this.coinPriceController, this.settingsController.strCookiePath);
    public ExportService expService;

    public MainViewController() {

        this.transactionController.startServer();

        // generate folder //defi-portfolio if no one exists
        File directory = new File(this.settingsController.strPathAppData);
        if (!directory.exists()) {
            directory.mkdir();
        }
        // init all relevant lists for tables and plots
        this.transactionList = FXCollections.observableArrayList(this.transactionController.getTransactionList());
        this.poolPairList = FXCollections.observableArrayList(this.poolPairModelList);
        this.expService = new ExportService(this.coinPriceController, this.transactionController, this.settingsController);

        // get last block locally
        this.strCurrentBlockLocally.set(Integer.toString(transactionController.getLocalBlockCount()));

        //start timer for getting last block on blockchain
        startTimer();
        //Add listener to Fiat
        this.settingsController.selectedFiatCurrency.addListener(
                (ov, t, t1) -> {
                    this.transactionController.getPortfolioList().clear();
                    for (TransactionModel transactionModel : this.transactionController.getTransactionList()) {
                        if (!transactionModel.getCryptoCurrencyValue().contains("-")) {
                            transactionModel.setFiatCurrency(t1);
                            transactionModel.setFiatValue(transactionModel.getCryptoValueValue() * this.coinPriceController.getPriceFromTimeStamp(transactionModel.getCryptoCurrencyValue() + t1, transactionModel.getBlockTimeValue() * 1000L));
                        }

                        if (transactionModel.getTypeValue().equals("Rewards") | transactionModel.getTypeValue().equals("Commission")) {
                            this.transactionController.addToPortfolioModel(transactionModel);
                        }
                        //TODO Portfolio clear and   this.transactionController.addPortfoli...
                    }
                    this.transactionList.clear();
                    this.transactionList.addAll(this.transactionController.getTransactionList());

                }
        );

    }

    public void startTimer() {
        new Timer("Timer").scheduleAtFixedRate(new TimerController(this), 0, 30000L);
    }

    public void copySelectedRawDataToClipboard(List<TransactionModel> list, boolean withHeaders) {
        StringBuilder sb = new StringBuilder();

        Locale localeDecimal = Locale.GERMAN;
        if (settingsController.selectedDecimal.getValue().equals(".")) {
            localeDecimal = Locale.US;
        }

        if (withHeaders)
            sb.append("Date,Operation,Amount,Cryptocurrency,FIAT value,FIAT currency,Pool ID,Block Height,Block Hash,Owner,".replace(",", this.settingsController.selectedSeperator.getValue())).append("\n");

        for (TransactionModel transaction : list) {
            sb.append(this.transactionController.convertTimeStampToString(transaction.getBlockTime().getValue())).append(this.settingsController.selectedSeperator.getValue());
            sb.append(transaction.getType().getValue()).append(this.settingsController.selectedSeperator.getValue());
            String[] CoinsAndAmounts = this.transactionController.splitCoinsAndAmounts(transaction.getAmount().getValue());
            sb.append(String.format(localeDecimal, "%.8f", Double.parseDouble(CoinsAndAmounts[0]))).append(this.settingsController.selectedSeperator.getValue());
            sb.append(CoinsAndAmounts[1]).append(this.settingsController.selectedSeperator.getValue());
            sb.append(String.format(localeDecimal, "%.8f", transaction.getFiatValueValue())).append(this.settingsController.selectedSeperator.getValue());
            sb.append(this.settingsController.selectedFiatCurrency.getValue()).append(this.settingsController.selectedSeperator.getValue());
            sb.append(transaction.getPoolID().getValue()).append(this.settingsController.selectedSeperator.getValue());
            sb.append(transaction.getBlockHeight().getValue()).append(this.settingsController.selectedSeperator.getValue());
            sb.append(transaction.getBlockHash().getValue()).append(this.settingsController.selectedSeperator.getValue());
            sb.append(transaction.getOwner().getValue());
            sb.append("\n");
        }
        StringSelection stringSelection = new StringSelection(sb.toString());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    public void copySelectedDataToClipboard(List<PoolPairModel> list, boolean withHeaders) {
        StringBuilder sb = new StringBuilder();

        Locale localeDecimal = Locale.GERMAN;
        if (settingsController.selectedDecimal.getValue().equals(".")) {
            localeDecimal = Locale.US;
        }

        if (withHeaders) {
            switch (this.mainView.tabPane.getSelectionModel().getSelectedItem().getText()) {
                case "Overview":
                case "Commissions":
                    sb.append((this.mainView.plotTable.getColumns().get(0).getText() + "," + this.mainView.plotTable.getColumns().get(1).getText() + "," + this.mainView.plotTable.getColumns().get(2).getText() + "," + this.mainView.plotTable.getColumns().get(3).getText() + "," + this.mainView.plotTable.getColumns().get(4).getText()).replace(",", this.settingsController.selectedSeperator.getValue())).append("\n");
                    break;
                case "Rewards":
                    sb.append((this.mainView.plotTable.getColumns().get(0).getText() + "," + this.mainView.plotTable.getColumns().get(1).getText() + "," + this.mainView.plotTable.getColumns().get(3).getText() + "," + this.mainView.plotTable.getColumns().get(4).getText()).replace(",", this.settingsController.selectedSeperator.getValue())).append("\n");
                    break;
                default:
                    break;
            }
        }

        for (PoolPairModel poolPair : list
        ) {
            switch (this.mainView.tabPane.getSelectionModel().getSelectedItem().getText()) {
                case "Overview":
                    sb.append(poolPair.getBlockTime().getValue()).append(this.settingsController.selectedSeperator.getValue());
                    sb.append(String.format(localeDecimal, "%.8f", poolPair.getFiatValue().getValue())).append(this.settingsController.selectedSeperator.getValue());
                    sb.append(String.format(localeDecimal, "%.8f", poolPair.getCryptoValue1().getValue())).append(this.settingsController.selectedSeperator.getValue());
                    sb.append(String.format(localeDecimal, "%.8f", poolPair.getCryptoValue2().getValue()));
                    sb.append("\n");
                    break;
                case "Rewards":
                    sb.append(poolPair.getBlockTime().getValue()).append(this.settingsController.selectedSeperator.getValue());
                    sb.append(poolPair.getPoolPair().getValue()).append(this.settingsController.selectedSeperator.getValue());
                    sb.append(String.format(localeDecimal, "%.8f", poolPair.getCryptoValue1().getValue())).append(this.settingsController.selectedSeperator.getValue());
                    sb.append(String.format(localeDecimal, "%.8f", poolPair.getCryptoValue2().getValue()));
                    sb.append("\n");
                    break;
                case "Commissions":
                    sb.append(poolPair.getBlockTime().getValue()).append(this.settingsController.selectedSeperator.getValue());
                    sb.append(poolPair.getPoolPair().getValue()).append(this.settingsController.selectedSeperator.getValue());
                    sb.append(String.format(localeDecimal, "%.8f", poolPair.getFiatValue().getValue())).append(this.settingsController.selectedSeperator.getValue());
                    sb.append(String.format(localeDecimal, "%.8f", poolPair.getCryptoValue1().getValue())).append(this.settingsController.selectedSeperator.getValue());
                    sb.append(String.format(localeDecimal, "%.8f", poolPair.getCryptoValue2().getValue()));
                    sb.append("\n");
                    break;
                default:
                    break;
            }


        }
        StringSelection stringSelection = new StringSelection(sb.toString());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    public boolean updateTransactionData() {

        if (this.transactionController.checkCrp()) {
            if (new File(this.settingsController.strPathAppData + this.settingsController.strTransactionData).exists()) {
                int depth = Integer.parseInt(this.transactionController.getBlockCountRpc()) - this.transactionController.getLocalBlockCount();
                return this.transactionController.updateTransactionData(depth);
            } else {
                return this.transactionController.updateTransactionData(this.transactionController.getAccountHistoryCountRpc());
            }
        }
        return false;
    }

    public boolean checkIfDeFiAppIsRunning() {
        String line;
        StringBuilder pidInfo = new StringBuilder();
        Process p;

        try {
            p = Runtime.getRuntime().exec(System.getenv("windir") + "\\system32\\" + "tasklist.exe");
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = input.readLine()) != null) {
                pidInfo.append(line);
            }

            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return pidInfo.toString().contains("defi-app");
    }

    public void btnUpdateDatabasePressed() {

        if (!checkIfDeFiAppIsRunning()) {

            if (updateTransactionData()) {

                this.showUpdateWindow();
                this.strCurrentBlockLocally.set(Integer.toString(this.transactionController.getLocalBlockCount()));
                this.strCurrentBlockOnBlockchain.set(this.transactionController.getBlockCountRpc());
                transactionList.clear();
                transactionList.addAll(this.transactionController.getTransactionList());
                Date date = new Date(System.currentTimeMillis());
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                this.strLastUpdate.setValue(dateFormat.format(date));
                this.closeUpdateWindow();

            } else {
                if (!this.transactionController.checkCrp()) {
                    this.showDefidNotRunning();
                    this.strCurrentBlockOnBlockchain.set("No connection");
                }
            }

        } else {
            this.showDefiAppIsRunning();
        }
    }

    public void showDefiAppIsRunning() {
        JFrame frameDefid = new JFrame("DeFi App running");
        frameDefid.setLayout(null);
        ImageIcon icon = new ImageIcon(System.getProperty("user.dir") + "\\src\\icons\\process.png");
        JLabel jl = new JLabel("     The Defi-App is running! Please close it first.", icon, JLabel.CENTER);
        jl.setSize(400, 100);
        jl.setLocation(0, 0);
        frameDefid.add(jl);
        frameDefid.setSize(400, 125);
        frameDefid.setLocationRelativeTo(null);
        frameDefid.setUndecorated(true);

        JButton b = new JButton("OK");
        b.setBounds(160, 80, 80, 25);
        b.addActionListener(e -> {
            Component component = (Component) e.getSource();
            JFrame frame = (JFrame) SwingUtilities.getRoot(component);
            frame.dispose();
        });
        frameDefid.add(b);
        frameDefid.setVisible(true);
    }

    public void showDefidNotRunning() {
        this.frameDefid = new JFrame("Launch defid.exe");
        frameDefid.setLayout(null);
        ImageIcon icon = new ImageIcon(System.getProperty("user.dir") + "\\src\\icons\\connected.png");
        JLabel jl = new JLabel("     The defid.exe is not running! Please start it manually.", icon, JLabel.CENTER);
        jl.setSize(400, 100);
        jl.setLocation(0, 0);
        frameDefid.add(jl);
        frameDefid.setSize(400, 125);
        frameDefid.setLocationRelativeTo(null);
        frameDefid.setUndecorated(true);

        JButton b = new JButton("OK");
        b.setBounds(160, 80, 80, 25);
        b.addActionListener(e -> {
            Component component = (Component) e.getSource();
            JFrame frame = (JFrame) SwingUtilities.getRoot(component);
            frame.dispose();
        });
        frameDefid.add(b);
        frameDefid.setVisible(true);
    }

    public void showUpdateWindow() {
        this.frameUpdate = new JFrame("Loading Database");
        ImageIcon icon = new ImageIcon(System.getProperty("user.dir") + "\\src\\icons\\updating.png");
        JLabel jl = new JLabel("     Updating local files. Please wait...!", icon, JLabel.CENTER);
        frameUpdate.add(jl);
        frameUpdate.setSize(350, 125);
        frameUpdate.setLocationRelativeTo(null);
        frameUpdate.setUndecorated(true);
        frameUpdate.setVisible(true);
    }

    public void closeUpdateWindow() {
        this.frameUpdate.setVisible(false);
        this.frameUpdate.dispose();
    }

    public void plotUpdate(String openedTab) {
        switch (openedTab) {
            case "Overview":
                updateOverview();
                break;
            case "Rewards":
                updateRewards();
                break;
            case "Commissions":
                updateCommissions();
                break;
            default:
                break;
        }
    }

    public void updateOverview() {

        this.poolPairModelList.clear();
        this.mainView.plotOverview.setLegendVisible(true);
        this.mainView.plotOverview.getData().clear();
        this.mainView.plotOverview.getYAxis().setLabel("Total (" + this.settingsController.selectedFiatCurrency.getValue() + ")");

        double maxValue = 0;

        for (String poolPair : this.settingsController.cryptoCurrencies) {

            XYChart.Series<Number, Number> overviewSeries = new XYChart.Series();
            overviewSeries.setName(poolPair);

            if (this.transactionController.getPortfolioList().containsKey(poolPair + "-" + this.settingsController.selectedIntervall.getValue())) {

                for (HashMap.Entry<String, PortfolioModel> entry : this.transactionController.getPortfolioList().get(poolPair + "-" + this.settingsController.selectedIntervall.getValue()).entrySet()) {
                    if (entry.getValue().getDateValue().compareTo(this.transactionController.convertDateToIntervall(this.settingsController.dateFrom.getValue().toString(), this.settingsController.selectedIntervall.getValue())) >= 0 &&
                            entry.getValue().getDateValue().compareTo(this.transactionController.convertDateToIntervall(this.settingsController.dateTo.getValue().toString(), this.settingsController.selectedIntervall.getValue())) <= 0) {

                        if (poolPair.equals(entry.getValue().getPoolPairValue())) {
                            overviewSeries.getData().add(new XYChart.Data(entry.getKey(), entry.getValue().getFiatRewards1Value() + entry.getValue().getFiatCommissions1Value() + entry.getValue().getFiatCommissions2Value()));
                            this.poolPairModelList.add(new PoolPairModel(entry.getKey(), entry.getValue().getFiatRewards1Value() + entry.getValue().getFiatCommissions1Value() + entry.getValue().getFiatCommissions2Value(), entry.getValue().getFiatRewards1Value(), entry.getValue().getFiatCommissions1Value() + entry.getValue().getFiatCommissions2Value(), poolPair));
                        }
                    }
                }

                this.mainView.yAxis.setAutoRanging(false);
                if (maxValue < overviewSeries.getData().stream().mapToDouble(d -> (Double) d.getYValue()).max().getAsDouble()) {
                    this.mainView.yAxis.setUpperBound(overviewSeries.getData().stream().mapToDouble(d -> (Double) d.getYValue()).max().getAsDouble() * 1.10);
                    maxValue = overviewSeries.getData().stream().mapToDouble(d -> (Double) d.getYValue()).max().getAsDouble();
                }
                this.mainView.plotOverview.getData().add(overviewSeries);
                this.mainView.plotOverview.setCreateSymbols(true);
            }

        }
        for (XYChart.Series<Number, Number> s : this.mainView.plotOverview.getData()) {
            if (s != null) {
                for (XYChart.Data d : s.getData()) {
                    if (d != null) {
                        Tooltip t = new Tooltip(d.getYValue().toString());
                        Tooltip.install(d.getNode(), t);
                        d.getNode().setOnMouseEntered(event -> d.getNode().getStyleClass().add("onHover"));
                        d.getNode().setOnMouseExited(event -> d.getNode().getStyleClass().remove("onHover"));
                    }
                }
            }
        }

        this.poolPairModelList.sort(Comparator.comparing(PoolPairModel::getBlockTimeValue));
        this.poolPairList.clear();
        this.poolPairList.addAll(this.poolPairModelList);
    }

    public void updateRewards() {

        XYChart.Series<Number, Number> rewardsSeries = new XYChart.Series();

        this.poolPairModelList.clear();
        this.mainView.plotRewards.setLegendVisible(false);
        this.mainView.plotRewards.getData().clear();

        if (this.settingsController.selectedPlotCurrency.getValue().equals("Coin")) {
            this.mainView.plotRewards.getYAxis().setLabel(this.settingsController.selectedCoin.getValue().split("-")[1]);
        } else {
            this.mainView.plotRewards.getYAxis().setLabel(this.settingsController.selectedCoin.getValue().split("-")[1] + " (" + this.settingsController.selectedFiatCurrency.getValue() + ")");
        }

        if (this.transactionController.getPortfolioList().containsKey(this.settingsController.selectedCoin.getValue() + "-" + this.settingsController.selectedIntervall.getValue())) {

            if (this.settingsController.selectedPlotType.getValue().equals("Individual")) {

                for (HashMap.Entry<String, PortfolioModel> entry : this.transactionController.getPortfolioList().get(this.settingsController.selectedCoin.getValue() + "-" + this.settingsController.selectedIntervall.getValue()).entrySet()) {

                    if (entry.getValue().getDateValue().compareTo(this.transactionController.convertDateToIntervall(this.settingsController.dateFrom.getValue().toString(), this.settingsController.selectedIntervall.getValue())) >= 0 &&
                            entry.getValue().getDateValue().compareTo(this.transactionController.convertDateToIntervall(this.settingsController.dateTo.getValue().toString(), this.settingsController.selectedIntervall.getValue())) <= 0) {

                        if (this.settingsController.selectedPlotCurrency.getValue().equals("Coin")) {
                            rewardsSeries.getData().add(new XYChart.Data(entry.getKey(), entry.getValue().getCoinRewards1Value()));
                        } else {
                            rewardsSeries.getData().add(new XYChart.Data(entry.getKey(), entry.getValue().getFiatRewards1Value()));
                        }
                        this.poolPairModelList.add(new PoolPairModel(entry.getKey(), 1, entry.getValue().getCoinRewards1Value(), entry.getValue().getFiatRewards1Value(), this.settingsController.selectedCoin.getValue()));
                    }
                }


                if (this.mainView.plotRewards.getData().size() == 1) {
                    this.mainView.plotRewards.getData().remove(0);
                }

                this.mainView.plotRewards.getData().add(rewardsSeries);

                for (XYChart.Series<Number, Number> s : this.mainView.plotRewards.getData()) {
                    for (XYChart.Data d : s.getData()) {
                        Tooltip t = new Tooltip(d.getYValue().toString());
                        //t.setShowDelay(Duration.seconds(0));
                        Tooltip.install(d.getNode(), t);
                        d.getNode().setOnMouseEntered(event -> d.getNode().getStyleClass().add("onHover"));
                        d.getNode().setOnMouseExited(event -> d.getNode().getStyleClass().remove("onHover"));
                    }
                }
            } else {

                XYChart.Series<Number, Number> rewardsCumulated = new XYChart.Series();

                double cumulatedCoinValue = 0;
                double cumulatedFiatValue = 0;
                for (HashMap.Entry<String, PortfolioModel> entry : this.transactionController.getPortfolioList().get(this.settingsController.selectedCoin.getValue() + "-" + this.settingsController.selectedIntervall.getValue()).entrySet()) {
                    if (entry.getValue().getDateValue().compareTo(this.transactionController.convertDateToIntervall(this.settingsController.dateFrom.getValue().toString(), this.settingsController.selectedIntervall.getValue())) >= 0 &&
                            entry.getValue().getDateValue().compareTo(this.transactionController.convertDateToIntervall(this.settingsController.dateTo.getValue().toString(), this.settingsController.selectedIntervall.getValue())) <= 0) {

                        if (this.settingsController.selectedPlotCurrency.getValue().equals("Coin")) {
                            cumulatedCoinValue = cumulatedCoinValue + entry.getValue().getCoinRewards1Value();
                            rewardsCumulated.getData().add(new XYChart.Data(entry.getKey(), cumulatedCoinValue));
                        } else {
                            cumulatedFiatValue = cumulatedFiatValue + entry.getValue().getFiatRewards1Value();
                            rewardsCumulated.getData().add(new XYChart.Data(entry.getKey(), cumulatedFiatValue));
                        }

                        this.poolPairModelList.add(new PoolPairModel(entry.getKey(),  1, entry.getValue().getCoinRewards1Value(), entry.getValue().getFiatRewards1Value(), this.settingsController.selectedCoin.getValue()));
                    }
                }
                if (this.mainView.plotRewards.getData().size() == 1) {
                    this.mainView.plotRewards.getData().remove(0);
                }

                this.mainView.plotRewards.getData().add(rewardsCumulated);

                for (XYChart.Series<Number, Number> s : this.mainView.plotRewards.getData()) {
                    for (XYChart.Data d : s.getData()) {
                        Tooltip t = new Tooltip(d.getYValue().toString());
                        //t.setShowDelay(Duration.seconds(0));
                        Tooltip.install(d.getNode(), t);
                        d.getNode().setOnMouseEntered(event -> d.getNode().getStyleClass().add("onHover"));
                        d.getNode().setOnMouseExited(event -> d.getNode().getStyleClass().remove("onHover"));
                    }
                }

            }

            this.poolPairModelList.sort(Comparator.comparing(PoolPairModel::getBlockTimeValue));
            this.poolPairList.clear();
            this.poolPairList.addAll(this.poolPairModelList);
        }

    }

    public void updateCommissions() {

        XYChart.Series<Number, Number> commissionsSeries1 = new XYChart.Series();
        XYChart.Series<Number, Number> commissionsSeries2 = new XYChart.Series();
        this.mainView.plotCommissions1.getData().clear();
        this.mainView.plotCommissions2.getData().clear();
        this.poolPairModelList.clear();
        this.poolPairList.clear();
        this.mainView.plotCommissions1.setLegendVisible(false);
        this.mainView.plotCommissions2.setLegendVisible(false);

        if (this.settingsController.selectedPlotCurrency.getValue().equals("Coin")) {
            this.mainView.plotCommissions1.getYAxis().setLabel(this.settingsController.selectedCoin.getValue().split("-")[1]);
            this.mainView.plotCommissions2.getYAxis().setLabel(this.settingsController.selectedCoin.getValue().split("-")[0]);
        } else {
            this.mainView.plotCommissions1.getYAxis().setLabel(this.settingsController.selectedCoin.getValue().split("-")[1] + " (" + this.settingsController.selectedFiatCurrency.getValue() + ")");
            this.mainView.plotCommissions2.getYAxis().setLabel(this.settingsController.selectedCoin.getValue().split("-")[1] + " (" + this.settingsController.selectedFiatCurrency.getValue() + ")");
        }

        if (this.transactionController.getPortfolioList().containsKey(this.settingsController.selectedCoin.getValue() + "-" + this.settingsController.selectedIntervall.getValue())) {

            if (this.settingsController.selectedPlotType.getValue().equals("Individual")) {

                for (HashMap.Entry<String, PortfolioModel> entry : this.transactionController.getPortfolioList().get(this.settingsController.selectedCoin.getValue() + "-" + this.settingsController.selectedIntervall.getValue()).entrySet()) {
                    if (entry.getValue().getDateValue().compareTo(this.transactionController.convertDateToIntervall(this.settingsController.dateFrom.getValue().toString(), this.settingsController.selectedIntervall.getValue())) >= 0 &&
                            entry.getValue().getDateValue().compareTo(this.transactionController.convertDateToIntervall(this.settingsController.dateTo.getValue().toString(), this.settingsController.selectedIntervall.getValue())) <= 0) {

                        if (this.settingsController.selectedPlotCurrency.getValue().equals("Coin")) {
                            commissionsSeries1.getData().add(new XYChart.Data(entry.getKey(), entry.getValue().getCoinCommissions1Value()));
                            commissionsSeries2.getData().add(new XYChart.Data(entry.getKey(), entry.getValue().getCoinCommissions2Value()));
                        } else {
                            commissionsSeries1.getData().add(new XYChart.Data(entry.getKey(), entry.getValue().getFiatCommissions1Value()));
                            commissionsSeries2.getData().add(new XYChart.Data(entry.getKey(), entry.getValue().getFiatCommissions2Value()));
                        }
                        this.poolPairModelList.add(new PoolPairModel(entry.getKey(), entry.getValue().getFiatCommissions1Value() + entry.getValue().getFiatCommissions2Value(), entry.getValue().getCoinCommissions1Value(), entry.getValue().getCoinCommissions2Value(), this.settingsController.selectedCoin.getValue()));
                    }
                }


                this.mainView.plotCommissions1.getData().add(commissionsSeries1);
                this.mainView.plotCommissions2.getData().add(commissionsSeries2);

                for (XYChart.Series<Number, Number> s : this.mainView.plotCommissions1.getData()) {
                    for (XYChart.Data d : s.getData()) {
                        Tooltip t = new Tooltip(d.getYValue().toString());
                        Tooltip.install(d.getNode(), t);
                        d.getNode().setOnMouseEntered(event -> d.getNode().getStyleClass().add("onHover"));
                        d.getNode().setOnMouseExited(event -> d.getNode().getStyleClass().remove("onHover"));
                    }
                }

                for (XYChart.Series<Number, Number> s : this.mainView.plotCommissions2.getData()) {
                    for (XYChart.Data d : s.getData()) {
                        Tooltip t = new Tooltip(d.getYValue().toString());
                        Tooltip.install(d.getNode(), t);
                        d.getNode().setOnMouseEntered(event -> d.getNode().getStyleClass().add("onHover"));
                        d.getNode().setOnMouseExited(event -> d.getNode().getStyleClass().remove("onHover"));
                    }
                }

            } else {

                XYChart.Series<Number, Number> rewardsCumulated1 = new XYChart.Series();
                XYChart.Series<Number, Number> rewardsCumulated2 = new XYChart.Series();

                double cumulatedCommissions1CoinValue = 0;
                double cumulatedCommissions1FiatValue = 0;
                double cumulatedCommissions2CoinValue = 0;
                double cumulatedCommissions2FiatValue = 0;
                for (HashMap.Entry<String, PortfolioModel> entry : this.transactionController.getPortfolioList().get(this.settingsController.selectedCoin.getValue() + "-" + this.settingsController.selectedIntervall.getValue()).entrySet()) {
                    if (entry.getValue().getDateValue().compareTo(this.transactionController.convertDateToIntervall(this.settingsController.dateFrom.getValue().toString(), this.settingsController.selectedIntervall.getValue())) >= 0 &&
                            entry.getValue().getDateValue().compareTo(this.transactionController.convertDateToIntervall(this.settingsController.dateTo.getValue().toString(), this.settingsController.selectedIntervall.getValue())) <= 0) {

                        if (this.settingsController.selectedPlotCurrency.getValue().equals("Coin")) {
                            cumulatedCommissions1CoinValue = cumulatedCommissions1CoinValue + entry.getValue().getCoinCommissions1Value();
                            cumulatedCommissions2CoinValue = cumulatedCommissions2CoinValue + entry.getValue().getCoinCommissions2Value();
                            rewardsCumulated1.getData().add(new XYChart.Data(entry.getKey(), cumulatedCommissions1CoinValue));
                            rewardsCumulated2.getData().add(new XYChart.Data(entry.getKey(), cumulatedCommissions2CoinValue));
                        } else {
                            cumulatedCommissions1FiatValue = cumulatedCommissions1FiatValue + entry.getValue().getFiatCommissions1Value();
                            cumulatedCommissions2FiatValue = cumulatedCommissions2FiatValue + entry.getValue().getFiatCommissions2Value();
                            rewardsCumulated1.getData().add(new XYChart.Data(entry.getKey(), cumulatedCommissions1FiatValue));
                            rewardsCumulated2.getData().add(new XYChart.Data(entry.getKey(), cumulatedCommissions2FiatValue));
                        }

                        this.poolPairModelList.add(new PoolPairModel(entry.getKey(), entry.getValue().getFiatCommissions1Value() + entry.getValue().getFiatCommissions2Value(), entry.getValue().getCoinCommissions1Value(), entry.getValue().getCoinCommissions2Value(), this.settingsController.selectedCoin.getValue()));

                    }
                }

                if (this.mainView.plotCommissions1.getData().size() == 1) {
                    this.mainView.plotCommissions1.getData().remove(0);
                }

                if (this.mainView.plotCommissions2.getData().size() == 1) {
                    this.mainView.plotCommissions2.getData().remove(0);
                }

                this.mainView.plotCommissions1.getData().add(rewardsCumulated1);

                for (XYChart.Series<Number, Number> s : this.mainView.plotCommissions1.getData()) {
                    for (XYChart.Data d : s.getData()) {
                        Tooltip t = new Tooltip(d.getYValue().toString());
                        //t.setShowDelay(Duration.seconds(0));
                        Tooltip.install(d.getNode(), t);
                        d.getNode().setOnMouseEntered(event -> d.getNode().getStyleClass().add("onHover"));
                        d.getNode().setOnMouseExited(event -> d.getNode().getStyleClass().remove("onHover"));
                    }
                }

                this.mainView.plotCommissions2.getData().add(rewardsCumulated2);
                for (XYChart.Series<Number, Number> s : this.mainView.plotCommissions2.getData()) {
                    for (XYChart.Data d : s.getData()) {
                        Tooltip t = new Tooltip(d.getYValue().toString());
                        //t.setShowDelay(Duration.seconds(0));
                        Tooltip.install(d.getNode(), t);
                        d.getNode().setOnMouseEntered(event -> d.getNode().getStyleClass().add("onHover"));
                        d.getNode().setOnMouseExited(event -> d.getNode().getStyleClass().remove("onHover"));
                    }
                }

            }


            this.poolPairModelList.sort(Comparator.comparing(PoolPairModel::getBlockTimeValue));
            this.poolPairList.clear();
            this.poolPairList.addAll(this.poolPairModelList);
        }

    }

    public ObservableList<TransactionModel> getTransactionTable() {
        return this.transactionList;
    }

    public ObservableList<PoolPairModel> getPlotData() {
        return this.poolPairList;
    }

    public void exportTransactionToExcel(List<TransactionModel> list) {

        Locale localeDecimal = Locale.GERMAN;
        if (settingsController.selectedDecimal.getValue().equals(".")) {
            localeDecimal = Locale.US;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV files", "*.csv")
        );
        File selectedFile = fileChooser.showSaveDialog(new Stage());

        if (selectedFile != null) {
            boolean success = this.expService.exportTransactionToExcel(list, selectedFile.getPath(), localeDecimal, this.settingsController.selectedSeperator.getValue());
            if (success) {
                this.strProgressbar.setValue("Excel successfully exported!");
                PauseTransition pause = new PauseTransition(Duration.seconds(10));
                pause.setOnFinished(e -> this.strProgressbar.setValue(null));
                pause.play();
            } else {
                this.strProgressbar.setValue("Error while exporting excel!");
                PauseTransition pause = new PauseTransition(Duration.seconds(10));
                pause.setOnFinished(e -> this.strProgressbar.setValue(null));
                pause.play();
            }
        }
    }

    public void exportPoolPairToExcel(List<PoolPairModel> list, String source) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV files", "*.csv")
        );

        File selectedFile = fileChooser.showSaveDialog(new Stage());

        if (selectedFile != null) {
            boolean success = this.expService.exportPoolPairToExcel(list, selectedFile.getPath(), source, this.mainView);

            if (success) {
                this.strProgressbar.setValue("Excel successfully exported!");
                PauseTransition pause = new PauseTransition(Duration.seconds(10));
                pause.setOnFinished(e -> this.strProgressbar.setValue(null));
                pause.play();
            } else {
                this.strProgressbar.setValue("Error while exporting excel!");
                PauseTransition pause = new PauseTransition(Duration.seconds(10));
                pause.setOnFinished(e -> this.strProgressbar.setValue(null));
                pause.play();
            }
        }
    }

    public void openBlockChainExplorer(TransactionModel model) {
        try {
            Desktop.getDesktop().browse(new URL("https://mainnet.defichain.io/#/DFI/mainnet/block/" + model.getBlockHashValue()).toURI());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
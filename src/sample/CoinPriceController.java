package sample;

import com.litesoftwares.coingecko.CoinGeckoApiClient;
import com.litesoftwares.coingecko.impl.CoinGeckoApiClientImpl;

import java.io.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.TreeMap;

public class CoinPriceController {

    public CoinPriceModel coinPriceModel;
    String strCoinPriceData;

    public CoinPriceController(String strCoinPriceData) {
        this.strCoinPriceData = strCoinPriceData;
        coinPriceModel = getCoinPriceLocal(this.strCoinPriceData);
        updateCoinPriceData();
    }

    public boolean updateCoinPriceData() {

        CoinPriceModel coinPrice = getCoinPriceLocal(this.strCoinPriceData);
        CoinGeckoApiClient client = new CoinGeckoApiClientImpl();

        var currentTimeStamp = new Timestamp(System.currentTimeMillis()).getTime() / 1000L;

        if (client.getCoinMarketChartRangeById("defichain", "eur", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices().size() > 0) {

            //Update DFI
            TreeMap<String,List<List<String>>> coinPriceList = new TreeMap<>();
            coinPriceList=this.coinPriceModel.GetKeyMap();

            coinPriceList.put("DFIEUR",client.getCoinMarketChartRangeById("defichain", "eur", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());
            coinPriceList.put("DFIUSD",client.getCoinMarketChartRangeById("defichain", "usd", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());
            coinPriceList.put("DFICHF",client.getCoinMarketChartRangeById("defichain", "chf", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());

            //Update BTC
            coinPriceList.put("BTCEUR",client.getCoinMarketChartRangeById("bitcoin", "eur", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());
            coinPriceList.put("BTCUSD",client.getCoinMarketChartRangeById("bitcoin", "usd", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());
            coinPriceList.put("BTCCHF",client.getCoinMarketChartRangeById("bitcoin", "chf", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());

            //Update ETH
            coinPriceList.put("ETHEUR",client.getCoinMarketChartRangeById("ethereum", "eur", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());
            coinPriceList.put("ETHUSD",client.getCoinMarketChartRangeById("ethereum", "usd", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());
            coinPriceList.put("ETHCHF",client.getCoinMarketChartRangeById("ethereum", "chf", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());

            //Update USDT
            coinPriceList.put("USDTEUR",client.getCoinMarketChartRangeById("tether", "eur", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());
            coinPriceList.put("USDTUSD",client.getCoinMarketChartRangeById("tether", "usd", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());
            coinPriceList.put("USDTCHF",client.getCoinMarketChartRangeById("tether", "chf", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());

            //Update LTC
            coinPriceList.put("LTCEUR",client.getCoinMarketChartRangeById("litecoin", "eur", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());
            coinPriceList.put("LTCUSD",client.getCoinMarketChartRangeById("litecoin", "usd", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());
            coinPriceList.put("LTCCHF",client.getCoinMarketChartRangeById("litecoin", "chf", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());

            //Update BCH
            coinPriceList.put("BCHEUR",client.getCoinMarketChartRangeById("bitcoin-cash", "eur", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());
            coinPriceList.put("BCHUSD",client.getCoinMarketChartRangeById("bitcoin-cash", "usd", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());
            coinPriceList.put("BCHCHF",client.getCoinMarketChartRangeById("bitcoin-cash", "chf", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());

            coinPrice.SetKeyMap(coinPriceList);
            coinPrice.lastTimeStamp = Long.toString(currentTimeStamp);

            // Serialization
            try {
                //Saving of object in a file
                FileOutputStream file = new FileOutputStream(this.strCoinPriceData);
                ObjectOutputStream out = new ObjectOutputStream(file);

                // Method for serialization of object
                out.writeObject(coinPrice);
                out.close();
                file.close();
                this.coinPriceModel = coinPrice;
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            return true;
        }
        return false;
    }

    public String getLastTimeStamp(String strCoinPricePath) {
        return getCoinPriceLocal(strCoinPricePath).lastTimeStamp;
    }

    public CoinPriceModel getCoinPriceLocal(String strCoinPricePath) {
        CoinPriceModel coinPrice = new CoinPriceModel();
        if (new File(strCoinPricePath).exists()) {
            try {
                // Reading the object from a file
                FileInputStream file = new FileInputStream(strCoinPricePath);
                ObjectInputStream in = new ObjectInputStream(file);

                // Method for deserialization of object
                coinPrice = (CoinPriceModel) in.readObject();

                in.close();
                file.close();

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return coinPrice;
    }

    public double getPriceFromTimeStamp(String coinFiatPair, Long timeStamp) {
        double price=0;

        for (int i = this.coinPriceModel.GetKeyMap().get(coinFiatPair).size() - 1; i >= 0; i--)
            if(timeStamp>Long.parseLong(this.coinPriceModel.GetKeyMap().get(coinFiatPair).get(i).get(0))){
                price = Double.parseDouble(this.coinPriceModel.GetKeyMap().get(coinFiatPair).get(i).get(1));
                break;
            }

        return price;
    }
}

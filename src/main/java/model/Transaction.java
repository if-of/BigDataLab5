package model;

public class Transaction {
    private final int stockCode;
    private final int customerId;

    public Transaction(int stockCode, int customerId) {
        this.stockCode = stockCode;
        this.customerId = customerId;
    }

    public int getStockCode() {
        return stockCode;
    }

    public int getCustomerId() {
        return customerId;
    }
}

import model.Transaction;
import service.XLSXReader;

import java.io.IOException;
import java.util.List;

public class Application {

    public static void main(String[] args) throws IOException {
        XLSXReader xlsxReader = new XLSXReader();
        List<Transaction> transactions = xlsxReader.readCorrectTransaction("Online Retail.xlsx");
    }
}

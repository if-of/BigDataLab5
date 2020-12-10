package service;

import model.Transaction;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class XLSXReader {

    public List<Transaction> readCorrectTransaction(String resourceName) throws IOException {
        List<Transaction> transactions = new ArrayList<>();

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourceName);
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet datatypeSheet = workbook.getSheetAt(0);
        Iterator<Row> iterator = datatypeSheet.iterator();

        while (iterator.hasNext()) {
            Row currentRow = iterator.next();
            if (currentRow == null) {
                continue;
            }

            Cell voiceNoCell = currentRow.getCell(0);
            Cell stockCodeCell = currentRow.getCell(1);
            Cell quantityCell = currentRow.getCell(3);
            Cell customerIdCell = currentRow.getCell(6);
            if (voiceNoCell == null
                    || (voiceNoCell.getCellType() != CellType.STRING
                    && voiceNoCell.getCellType() != CellType.NUMERIC)
                    || (voiceNoCell.getCellType() == CellType.STRING
                    && voiceNoCell.getStringCellValue().startsWith("C"))

                    || stockCodeCell == null
                    || (stockCodeCell.getCellType() != CellType.STRING
                    && stockCodeCell.getCellType() != CellType.NUMERIC)

                    || quantityCell == null
                    || quantityCell.getCellType() != CellType.NUMERIC
                    || quantityCell.getNumericCellValue() <= 0

                    || customerIdCell == null
                    || customerIdCell.getCellType() != CellType.NUMERIC
            ) {
                continue;
            }

            int stockCode;
            if (stockCodeCell.getCellType() == CellType.STRING) {
                String stockCodeRaw = stockCodeCell.getStringCellValue();
                stockCodeRaw = stockCodeRaw.replaceAll("[^0-9.]", "");
                if (stockCodeRaw.isBlank()) {
                    continue;
                }
                stockCode = Integer.parseInt(stockCodeRaw);
            } else {
                stockCode = (int) stockCodeCell.getNumericCellValue();
            }
            int customerId = (int) customerIdCell.getNumericCellValue();

            transactions.add(new Transaction(stockCode, customerId));
        }

        return transactions;
    }
}

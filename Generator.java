import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Generator {

    public static void main(String[] args) {
        String inputFile = "rowData.txt"; // Giriş dosyasının adı
        String outputFile = "30thousand.txt"; // Çıkış dosyasının adı

        try {
            processDataSet(inputFile, outputFile);
            System.out.println("Veri seti işlendi ve yeni dosyaya yazıldı.");
        } catch (IOException e) {
            System.out.println("Hata oluştu: " + e.getMessage());
        }
    }

    public static void processDataSet(String inputFile, String outputFile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));

        String line;
        int lineNumber = 0;

        while ((line = reader.readLine()) != null) {
            String[] parts = line.trim().split("\\s+"); // Satırı boşluklara göre böler

            if (parts.length >= 3) {
                double number1 = Double.parseDouble(parts[1]);
                double number2 = Double.parseDouble(parts[2]);

                int convertedNumber1 = (int) number1;
                int convertedNumber2 = (int) number2;

                writer.write(lineNumber + " " + convertedNumber1 + " " + convertedNumber2 + "\n");

                lineNumber++;
            }
        }

        reader.close();
        writer.close();
    }
}

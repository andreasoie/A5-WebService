import sun.awt.windows.WPrinterJob;

public class Testing {
    public static void main(String[] args) {
        Testing t = new Testing();
        // TESTING THINGS OUT

        String tempIP = "198.92.236.0";
        String tempSUB = "255.255.252.0";
        String[] aBin = t.getBinary(tempIP).split("\\.",4);
        String[] bBin = t.getBinary(tempSUB).split("\\.", 4);
        String[] cBin = {"0", "0", "0", "0"};
        //11000110.1011100.11101100
        //11000110.01011100.11101100.00000000

        for (int i = 0; i < a; i++) {
            if (b[i].equals("255")) {
                cBin[i] = a[i];
            } else {
                cBin[i] = b[i];
            }

            System.out.println(bBin);

        }
    }

    private String getBinary(String address) {
        StringBuilder binary = new StringBuilder();

        String[] ipArray = address.split("\\.");

        for (int i = 0; i < ipArray.length; i++) {
            int temp = Integer.parseInt(ipArray[i]);
            if (temp >= 0 && temp <= 255) {


                binary.append(Integer.toBinaryString(temp));
                binary.append('.');
            }
        }
        return binary.deleteCharAt(binary.length() - 1).toString();
    }
}

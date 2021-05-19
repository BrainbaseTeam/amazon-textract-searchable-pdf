public class ConvertPDF {

    private static String OriginalPDF;
    private static String NewPDF;

    public static void main(String args[]) {
        try {
            OriginalPDF = args[0];
            NewPDF = args[1];
  
            //Generate searchable PDF from local pdf
            DemoPdfFromLocalPdf localPdf = new DemoPdfFromLocalPdf();
            localPdf.run(OriginalPDF, NewPDF);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

public class ConvertPDFJson {

    private static String OriginalPDF;
    private static String NewPDF;
    private static String JsonFile;

    public static void main(String args[]) {
        try {
            OriginalPDF = args[0];
            JsonFile = args[1];
            NewPDF = args[2];
  
            //Generate searchable PDF from local pdf
            ConvertPdfToLocalPdfJson localPdf = new ConvertPdfToLocalPdfJson();
            localPdf.runlocal(OriginalPDF,JsonFile, NewPDF);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

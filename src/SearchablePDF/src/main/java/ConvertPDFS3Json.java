public class ConvertPDFS3Json {

    private static String InBucket;
    private static String OriginalPDF;
    private static String NewPDF;
    private static String JsonFile;

    public static void main(String args[]) {
        try {
            InBucket = args[0];
            OriginalPDF = args[1];
            JsonFile = args[2];
            NewPDF = args[3];
  
            //Generate searchable PDF from local pdf
            ConvertPdfToLocalPdfJson localPdf = new ConvertPdfToLocalPdfJson();
            localPdf.runs3(InBucket,OriginalPDF,JsonFile, NewPDF);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

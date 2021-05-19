public class ConvertPDFS3 {

    private static String InBucket;
    private static String InPDF;
    private static String OutBucket;
    private static String OutPDF;

    public static void main(String args[]) {
        try {
            InBucket = args[0];
            InPDF = args[1];
            OutBucket = args[2];
            OutPDF = args[3];
  
           //Generate searchable PDF from pdf in Amazon S3 bucket
           ConvertPDFfromS3 s3Pdf = new ConvertPDFfromS3();
           s3Pdf.run(InBucket, InPDF, OutBucket, OutPDF);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

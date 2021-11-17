import com.amazon.textract.pdf.ImageType;
import com.amazon.textract.pdf.PDFDocument;
import com.amazon.textract.pdf.TextLine;
// import com.amazonaws.services.textract.AmazonTextract;
// import com.amazonaws.services.textract.AmazonTextractClientBuilder;
// import com.amazonaws.services.textract.model.*;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;



import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
 
import java.io.FileReader;
import java.util.Iterator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ConvertPdfToLocalPdfJson {

    private List<TextLine> extractText(int pageNum, String jsonName){

        // AmazonTextract client = AmazonTextractClientBuilder.defaultClient();

        // DetectDocumentTextRequest request = new DetectDocumentTextRequest()
        //         .withDocument(new Document()
        //                 .withBytes(imageBytes));

        JSONParser parser = new JSONParser();
        List<TextLine> lines = new ArrayList<TextLine>();
        System.out.println("page : " + jsonName);
		try {

            BufferedReader reader = new BufferedReader(new FileReader(
                jsonName));

            // Read lines from file.
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                // Split line on comma.
                String[] parts = line.split("\t");

                Double left = Double.parseDouble(parts[0]);
                Double top = Double.parseDouble(parts[1]); 
                Double width = Double.parseDouble(parts[2]); 
                Double height = Double.parseDouble(parts[3]);
                
                String text = parts[4]; 

                int p =Integer.parseInt(parts[5]);  
                if (p == pageNum){
                    lines.add(new TextLine(left,top, width, height,text));}

            }

            reader.close();
            int size = lines.size();
 
            // print the size of list
            System.out.println("Size of list = "            + size);
			//Object obj = parser.parse(new FileReader(jsonName));
            
            
			// A JSON object. Key value pairs are unordered. JSONObject supports java.util.Map interface.
		    //JSONObject jsonObject =  (JSONObject) obj;


            //DetectDocumentTextResult result = new DetectDocumentTextResult ();
            

            //DetectDocumentTextResult result = new DetectDocumentTextResult()
            //    .withDocument(new Document()
            //            .withBytes(fileBytes));
            
            //List<Block>  blocks = (JSONArray) jsonObject.get("Blocks");
            //List<Block> blocks = result.getBlocks();
            //BoundingBox boundingBox = null;
            //for (Block block : blocks) {
            //    if ((block.getBlockType()).equals("LINE")) {
             //       boundingBox = block.getGeometry().getBoundingBox();
             //       lines.add(new TextLine(boundingBox.getLeft(),
            //              boundingBox.getTop(),
            //                boundingBox.getWidth(),
             //               boundingBox.getHeight(),
             //               block.getText()));
            //}
        //}

            
			// A JSON array. JSONObject supports java.util.List interface.
			
 
			// An iterator over a collection. Iterator takes the place of Enumeration in the Java Collections Framework.
			// Iterators differ from enumerations in two ways:
			// 1. Iterators allow the caller to remove elements from the underlying collection during the iteration with well-defined semantics.
			// 2. Method names have been improved.
			
		} catch (Exception e) {
			e.printStackTrace();
		}

        

    return lines;
    }

    public void runlocal(String documentName, String jsonName, String outputDocumentName)throws IOException {
        System.out.println("Generating searchable pdf from: " + documentName);
        PDFDocument pdfDocument = new PDFDocument();
        PDDocument inputDocument = PDDocument.load(new File(documentName));
        run(inputDocument,pdfDocument, jsonName, outputDocumentName);
    }

    public void runs3(String inBucketName, String documentName, String jsonName, String outputDocumentName)throws IOException {
        System.out.println("Generating searchable pdf from: " + inBucketName + "/" + documentName);

        //Get input pdf document from Amazon S3
        InputStream inputPdf = getPdfFromS3(inBucketName, documentName);

        //Create new PDF document
        PDFDocument pdfDocument = new PDFDocument();

        //For each page add text layer and image in the pdf document
        PDDocument inputDocument = PDDocument.load(inputPdf);
        run(inputDocument,pdfDocument, jsonName, outputDocumentName);
    }

    public void run(PDDocument inputDocument, PDFDocument pdfDocument, String jsonName, String outputDocumentName) throws IOException {


        List<TextLine> lines = null;
        BufferedImage image = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        ByteBuffer imageBytes = null;
        int pageNum = 1;


        PDFRenderer pdfRenderer = new PDFRenderer(inputDocument);
        for (int page = 0; page < inputDocument.getNumberOfPages(); ++page) {

            //Render image
            image = pdfRenderer.renderImageWithDPI(page, 300, org.apache.pdfbox.rendering.ImageType.BINARY);//.RGB);


            //Get image bytes
            byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIOUtil.writeImage(image, "jpeg", byteArrayOutputStream);
            byteArrayOutputStream.flush();
            imageBytes = ByteBuffer.wrap(byteArrayOutputStream.toByteArray());

            //Extract text
            lines = extractText(pageNum, jsonName);
            

            //Add extracted text to pdf page
            pdfDocument.addPage(image, ImageType.JPEG, lines);

            System.out.println("Processed page index: " + pageNum);
            pageNum++;
        }

        inputDocument.close();

        //Save PDF to local disk
        try (OutputStream outputStream = new FileOutputStream(outputDocumentName)) {
            pdfDocument.save(outputStream);
            pdfDocument.close();
        }

        System.out.println("Generated searchable pdf: " + outputDocumentName);
    }

    private InputStream getPdfFromS3(String bucketName, String documentName) throws IOException {

        AmazonS3 s3client = AmazonS3ClientBuilder.defaultClient();
        com.amazonaws.services.s3.model.S3Object fullObject = s3client.getObject(new GetObjectRequest(bucketName, documentName));
        InputStream in = fullObject.getObjectContent();
        return in;
    }

}

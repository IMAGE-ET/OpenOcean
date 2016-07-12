package open.dolphin.letter;

import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Date;
import open.dolphin.client.ClientContext;
import open.dolphin.helper.UserDocumentHelper;
import open.dolphin.infomodel.UserModel;
import open.dolphin.project.Project;

/**
 * 紹介状の PDF メーカー。
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class Reply2PDFMaker extends AbstractLetterPDFMaker {
 
    @Override
    public String create() {

        try {

            Document document = new Document(
                    PageSize.A4,
                    getMarginLeft(),
                    getMarginRight(),
                    getMarginTop(),
                    getMarginBottom());
            
            String path = UserDocumentHelper.createPathToDocument(
                    getDocumentDir(),       // PDF File を置く場所
                    ClientContext.getMyBundle(Reply2PDFMaker.class).getString("docTitle.report"),             // 文書名
                    EXT_PDF,                // 拡張子 
                    model.getPatientName(), // 患者氏名 
                    new Date());            // 日付
          
            Path pathObj = Paths.get(path);
            setPathToPDF(pathObj.toAbsolutePath().toString());         // 呼び出し側で取り出せるように保存する
            PdfWriter.getInstance(document, Files.newOutputStream(pathObj));
            document.open();
            
            // Font
            baseFont = BaseFont.createFont(HEISEI_MIN_W3, UNIJIS_UCS2_HW_H, false);
            titleFont = new Font(baseFont, getTitleFontSize());
            bodyFont = new Font(baseFont, getBodyFontSize());

            // タイトル
            Paragraph para = new Paragraph(ClientContext.getMyBundle(Reply2PDFMaker.class).getString("paragrap.report"), titleFont);
            para.setAlignment(Element.ALIGN_CENTER);
            document.add(para);

            document.add(new Paragraph("　"));

            // 紹介元病院
            Paragraph para2;
            String hosp = model.getClientHospital();
            if (hosp == null || (hosp.equals(""))) {
                para2 = new Paragraph("　", bodyFont);
                para2.setAlignment(Element.ALIGN_LEFT);
                document.add(para2);
            } else {
                para2 = new Paragraph(hosp, bodyFont);
                para2.setAlignment(Element.ALIGN_LEFT);
                document.add(para2);
            }

            // 紹介元診療科
            String dept = model.getClientDept();
            if (dept == null || (dept.equals(""))) {
                para2 = new Paragraph("　", bodyFont);
                para2.setAlignment(Element.ALIGN_LEFT);
                document.add(para2);
            } else {
                if (!dept.endsWith(ClientContext.getMyBundle(Reply2PDFMaker.class).getString("paragrap.dept"))) {
                    dept += ClientContext.getMyBundle(Reply2PDFMaker.class).getString("paragrap.dept");
                }
                para2 = new Paragraph(dept, bodyFont);
                para2.setAlignment(Element.ALIGN_LEFT);
                document.add(para2);
            }

            // 紹介元医師
            StringBuilder sb = new StringBuilder();
            if (model.getClientDoctor()!=null) {
                sb.append(model.getClientDoctor());
                sb.append(" ");
            }
            sb.append(ClientContext.getMyBundle(Reply2PDFMaker.class).getString("text.doctorTitle"));
            // title
            String title = Project.getString("letter.atesaki.title");
            if (title!=null && (!title.equals("無し"))) {
                sb.append(" ").append(title);
            }
            para2 = new Paragraph(sb.toString(), bodyFont);
            para2.setAlignment(Element.ALIGN_LEFT);
            document.add(para2);

            document.add(new Paragraph("　"));

            // 拝啓
            para2 = new Paragraph(ClientContext.getMyBundle(Reply2PDFMaker.class).getString("paragrap.dearSirs"), bodyFont);
            para2.setAlignment(Element.ALIGN_LEFT);
            document.add(para2);

            // 挨拶
            para2 = new Paragraph(ClientContext.getMyBundle(Reply2PDFMaker.class).getString("paragrap.greetings"), bodyFont);
            para2.setAlignment(Element.ALIGN_LEFT);
            document.add(para2);

            // 患者受診
            String visitedDate = model.getItemValue(Reply2Impl.ITEM_VISITED_DATE);
            String informed = model.getTextValue(Reply2Impl.TEXT_INFORMED_CONTENT);
            
            String fmt = ClientContext.getMyBundle(Reply2PDFMaker.class).getString("meesageFormat.patientVisit");
            String text = new MessageFormat(fmt).format(new Object[]{
                model.getPatientName(),
                getDateString(model.getPatientBirthday()),
                model.getPatientAge(),
                getDateString(visitedDate)
            });
            para2 = new Paragraph(text, bodyFont);
            para2.setAlignment(Element.ALIGN_LEFT);
            document.add(para2);

            sb = new StringBuilder();
            sb.append(ClientContext.getMyBundle(Reply2PDFMaker.class).getString("paragrap.greetings2"));
            para2 = new Paragraph(sb.toString(), bodyFont);
            para2.setAlignment(Element.ALIGN_LEFT);
            document.add(para2);

            // 敬具
            para2 = new Paragraph(ClientContext.getMyBundle(Reply2PDFMaker.class).getString("paragrap.yoursSincerely"), bodyFont);
            para2.setAlignment(Element.ALIGN_RIGHT);
            document.add(para2);

            document.add(new Paragraph("　"));

            // 所見等
            para2 = new Paragraph(ClientContext.getMyBundle(Reply2PDFMaker.class).getString("paragraph.objectiveNotes"), bodyFont);
            para2.setAlignment(Element.ALIGN_LEFT);
            document.add(para2);

            // 内容
            Table lTable = new Table(1); //テーブル・オブジェクトの生成
            lTable.setPadding(2);
            lTable.setWidth(100);

            sb = new StringBuilder();
            sb.append(informed);
            sb.append("\n");
            sb.append("　");
            lTable.addCell(new Phrase(sb.toString(), bodyFont));
            document.add(lTable);

            document.add(new Paragraph("　"));
            //document.add(new Paragraph("　"));

            // 日付
            String dateStr = getDateString(model.getStarted());
            para = new Paragraph(dateStr, bodyFont);
            para.setAlignment(Element.ALIGN_RIGHT);
            document.add(para);

            // 病院の住所、電話
            if (model.getConsultantAddress()==null) {
                UserModel user = Project.getUserModel();
                String zipCode = user.getFacilityModel().getZipCode();
                String address = user.getFacilityModel().getAddress();
                sb = new StringBuilder();
                sb.append(zipCode);
                sb.append(" ");
                sb.append(address);
                para2 = new Paragraph(sb.toString(), bodyFont);
                para2.setAlignment(Element.ALIGN_RIGHT);
                document.add(para2);
            } else {
                sb = new StringBuilder();
                sb.append(model.getConsultantZipCode());
                sb.append(" ");
                sb.append(model.getConsultantAddress());
                para2 = new Paragraph(sb.toString(), bodyFont);
                para2.setAlignment(Element.ALIGN_RIGHT);
                document.add(para2);
            }
            
            if (model.getConsultantTelephone()==null) {
                sb = new StringBuilder();
                sb.append(ClientContext.getMyBundle(Reply2PDFMaker.class).getString("paragrap.telephone")).append(" ");
                sb.append(Project.getUserModel().getFacilityModel().getTelephone());
                para2 = new Paragraph(sb.toString(), bodyFont);
                para2.setAlignment(Element.ALIGN_RIGHT);
                document.add(para2);
            } else {
                sb = new StringBuilder();
                sb.append(ClientContext.getMyBundle(Reply2PDFMaker.class).getString("paragrap.telephone")).append(" ");
                sb.append(model.getConsultantTelephone());
                para2 = new Paragraph(sb.toString(), bodyFont);
                para2.setAlignment(Element.ALIGN_RIGHT);
                document.add(para2);
            }

            // 差出人病院
            para2 = new Paragraph(model.getConsultantHospital(), bodyFont);
            para2.setAlignment(Element.ALIGN_RIGHT);
            document.add(para2);

            // 差出人医師
            sb = new StringBuilder();
            sb.append(model.getConsultantDoctor());
            sb.append(" ").append(ClientContext.getMyBundle(Reply2PDFMaker.class).getString("paragraph.seal"));
            para2 = new Paragraph(sb.toString(), bodyFont);
            para2.setAlignment(Element.ALIGN_RIGHT);
            document.add(para2);

            document.add(new Paragraph("　"));

            document.close();
            
            return getPathToPDF();

        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(this.getClass().getName()).warning(ex.getMessage());
            throw new RuntimeException(ERROR_IO);
        } catch (DocumentException ex) {
            java.util.logging.Logger.getLogger(this.getClass().getName()).warning(ex.getMessage());
            throw new RuntimeException(ERROR_PDF);
        }
    }
}


























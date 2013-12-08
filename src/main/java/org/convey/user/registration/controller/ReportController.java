package org.convey.user.registration.controller;

import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.LayoutManager;
import ar.com.fdvs.dj.core.layout.ListLayoutManager;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.FastReportBuilder;
import ar.com.fdvs.dj.domain.constants.GroupLayout;
import ar.com.fdvs.dj.domain.constants.Page;
import ar.com.fdvs.dj.domain.entities.DJGroup;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.fill.JRFileVirtualizer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.convey.user.registration.model.User;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/report/")
public class ReportController {

    protected static final Log log = LogFactory.getLog(ReportController.class);
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ReportController.class);
    private static final int BUFFER_SIZE = 4096;
    protected JasperPrint jasperPrint;

    private DynamicReport dynamicReport;
    private JasperReport jasperReport;
    private Map params = new HashMap();

    protected LayoutManager getLayoutManager() {
        return new ListLayoutManager();
    }

    @RequestMapping(method = RequestMethod.GET, value = "xlsx")
    public void generatePdfReport(HttpServletRequest request, HttpServletResponse response) throws Exception {

        List<User> usersList = UserFactory.createBeanCollection();
        User user = usersList.get(0);

        int index = 0;
        while (++index <= 50000) {
            usersList.add((User) org.apache.commons.lang.SerializationUtils.clone(user));
        }

        JRFileVirtualizer virtualizer = new JRFileVirtualizer(10, System.getProperty("java.io.tempdir"));
        params.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
        JRDataSource jrDataSource = new JRBeanCollectionDataSource(usersList);
        dynamicReport = buildReport();

        jasperReport = DynamicJasperHelper.generateJasperReport(dynamicReport, getLayoutManager(), params);

        if (dynamicReport != null)
            jasperPrint = JasperFillManager.fillReport(jasperReport, params, jrDataSource);
        else
            jasperPrint = JasperFillManager.fillReport(jasperReport, params);


        JRXlsxExporter exporter = new JRXlsxExporter();
        exporter.setParameter(JRXlsExporterParameter.JASPER_PRINT, jasperPrint);
        exporter.setParameter(JRXlsExporterParameter.OUTPUT_FILE_NAME, System.getProperty("user.dir") + "/target/100kReport.xlsx");

        exporter.exportReport();
        FileSystemResource fileSystemResource = new FileSystemResource(System.getProperty("user.dir") + "/target/100kReport.xlsx");
        downloadFile(request, response, fileSystemResource);

    }

    private void downloadFile(HttpServletRequest request, HttpServletResponse response, FileSystemResource fl) throws IOException {
        // construct the complete absolute path of the file

        String fullPath = fl.getPath();
        File downloadFile = fl.getFile();
        FileInputStream inputStream = new FileInputStream(downloadFile);

        // get MIME type of the file
        String mimeType = "application/xls";
        if (mimeType == null) {
            // set to binary type if MIME mapping not found
            mimeType = "application/octet-stream";
        }
        System.out.println("MIME type: " + mimeType);

        // set content attributes for the response
        response.setContentType(mimeType);
        response.setContentLength((int) downloadFile.length());

        // set headers for the response
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"",
                downloadFile.getName());
        response.setHeader(headerKey, headerValue);

        // get output stream of the response
        OutputStream outStream = response.getOutputStream();

        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead = -1;

        // write bytes read from the input stream into the output stream
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);
        }

        inputStream.close();
        outStream.close();
    }

    public DynamicReport buildReport() throws Exception {
        FastReportBuilder drb = new FastReportBuilder();
        drb.addColumn("Id", "id", Integer.class.getName(), 30)
                .addColumn("Username", "userName", String.class.getName(), 30)
                .addColumn("Passwd", "passWord", String.class.getName(), 50)
                .addColumn("First Name", "firstName", String.class.getName(), 50)
                .addColumn("Last Name", "lastName", String.class.getName(), 50)
                .addColumn("Email", "email", String.class.getName(), 70, true)
                .addColumn("Date", "registeredDate", Date.class.getName(), 70, true, "dd/MM/yyyy", null)
                .addGroups(2) //Not used by the ListLayoutManager
                .addGroups(2) //Not used by the ListLayoutManager
                .setPrintColumnNames(true)
                .setIgnorePagination(true) //for Excel, we may dont want pagination, just a plain list
                .setMargins(0, 0, 0, 0)
                .setPageSizeAndOrientation(Page.Page_Letter_Landscape())
                .setTitle("November 2006 sales report")
                .setSubtitle("This report was generated at " + new Date())
                .setReportName("My Excel Report")
                .setUseFullPageWidth(true);

        DynamicReport dr = drb.build();

        DJGroup group = (DJGroup) dr.getColumnsGroups().iterator().next();
        group.setLayout(GroupLayout.EMPTY); //not used by ListLayoutManager

        return dr;
    }
}

package ar.com.local.tests;

import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.LayoutManager;
import ar.com.fdvs.dj.core.layout.ListLayoutManager;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.FastReportBuilder;
import ar.com.fdvs.dj.domain.constants.GroupLayout;
import ar.com.fdvs.dj.domain.constants.Page;
import ar.com.fdvs.dj.domain.entities.DJGroup;
import ar.com.fdvs.dj.test.BaseDjReportTest;
import ar.com.fdvs.dj.util.SortUtils;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRXlsAbstractExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class XlsxMultipleSheetsReportTest extends BaseDjReportTest {

    List<JasperPrint> listOfPrints;

    public DynamicReport buildReport() throws Exception {


        /**
         * Creates the DynamicReportBuilder and sets the basic options for
         * the report
         */
        FastReportBuilder drb = new FastReportBuilder();
        Style columDetail = new Style();
//		columDetail.setBorder(Border.THIN());

        drb.addColumn("State", "state", String.class.getName(), 30)
                .addColumn("Branch", "branch", String.class.getName(), 30)
                .addColumn("Product Line", "productLine", String.class.getName(), 50)
                .addColumn("Item", "item", String.class.getName(), 50)
                .addColumn("Item Code", "id", Long.class.getName(), 30, true)
                .addColumn("Quantity", "quantity", Long.class.getName(), 60, true)
                .addColumn("Amount", "amount", Float.class.getName(), 70, true)
                .addColumn("Date", "date", Date.class.getName(), 70, true, "dd/MM/yyyy", null)
                .addGroups(2) //Not used by the ListLayoutManager
                .setPrintColumnNames(true)
                .setIgnorePagination(true) //for Excel, we may dont want pagination, just a plain list
                .setMargins(0, 0, 0, 0)
                .setPageSizeAndOrientation(Page.Page_Letter_Landscape())
                .setTitle("November 2006 sales report")
                .setSubtitle("This report was generated at " + new Date())
                .setReportName("My Excel Report")
                .setDefaultStyles(null, null, null, columDetail)
                .setUseFullPageWidth(true);

        DynamicReport dr = drb.build();

        DJGroup group = (DJGroup) dr.getColumnsGroups().iterator().next();
        group.setLayout(GroupLayout.EMPTY); //not used by ListLayoutManager

        return dr;
    }

    protected LayoutManager getLayoutManager() {
        return new ListLayoutManager();
    }

    protected void exportReport() throws Exception {
        File outputFile = new File(System.getProperty("user.dir") + "/target/multi_sheet_report.xls");
        File parentFile = outputFile.getParentFile();
        if (parentFile != null)
            parentFile.mkdirs();
        FileOutputStream fos = new FileOutputStream(outputFile);
        JRXlsxExporter exporter = new JRXlsxExporter();
        exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, listOfPrints);
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, fos);
        exporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE,Boolean.TRUE);
        exporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
        exporter.setParameter(JRXlsExporterParameter.IS_IGNORE_GRAPHICS, Boolean.FALSE);
        exporter.setParameter(JRXlsAbstractExporterParameter.SHEET_NAMES, new String[] {"Sheet one", "Sheet two",
                "sheet three"});

        exporter.exportReport();
    }


    public void testReport(List<List<?>> listOfDataProviders) throws Exception {
        dr = buildReport();


        jr = DynamicJasperHelper.generateJasperReport(dr, getLayoutManager(), params);

        log.debug("Filling the report");
        listOfPrints = new ArrayList<JasperPrint>();
        for(List<?> sheetProvider : listOfDataProviders){
            JRDataSource ds = getDataSource(sheetProvider);
            listOfPrints.add(JasperFillManager.fillReport(jr, params, ds));
        }

        log.debug("Filling done!");
        log.debug("Exporting the report (pdf, xls, etc)");
        exportReport();
        log.debug("test finished");
        exportReport();

    }


    public static void main(String[] args) throws Exception {
        XlsxMultipleSheetsReportTest test = new XlsxMultipleSheetsReportTest();
        List list = new ArrayList();
        list.add(MyTestRepositoryProducts.getDummyCollectionLarge(1000));
        list.add(MyTestRepositoryProducts.getDummyCollectionLarge(1000));
        test.testReport(list);
    }


    protected JRDataSource getDataSource(List<?> list) {
        list = SortUtils.sortCollection(list,dr.getColumns());

        JRDataSource ds = new JRBeanCollectionDataSource(list);		//Create a JRDataSource, the Collection used
        //here contains dummy hardcoded objects...
        return ds;
    }

}

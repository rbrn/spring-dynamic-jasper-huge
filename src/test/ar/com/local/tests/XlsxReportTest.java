package ar.com.local.tests;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import ar.com.fdvs.dj.core.layout.LayoutManager;
import ar.com.fdvs.dj.core.layout.ListLayoutManager;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.FastReportBuilder;
import ar.com.fdvs.dj.domain.constants.GroupLayout;
import ar.com.fdvs.dj.domain.constants.Page;
import ar.com.fdvs.dj.domain.entities.DJGroup;
import ar.com.fdvs.dj.test.BaseDjReportTest;
import ar.com.fdvs.dj.test.TestRepositoryProducts;
import ar.com.fdvs.dj.util.SortUtils;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;

public class XlsxReportTest extends BaseDjReportTest {

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
        File outputFile = new File(System.getProperty("user.dir") + "/target/report.xls");
        File parentFile = outputFile.getParentFile();
        if (parentFile != null)
            parentFile.mkdirs();
        FileOutputStream fos = new FileOutputStream(outputFile);
        JRXlsxExporter exporter = new JRXlsxExporter();
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, fos);
        exporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE,Boolean.TRUE);
        exporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
        exporter.setParameter(JRXlsExporterParameter.IS_IGNORE_GRAPHICS, Boolean.FALSE);

        exporter.exportReport();
    }


    public static void main(String[] args) throws Exception {
        XlsxReportTest test = new XlsxReportTest();
        test.testReport();
    }

    @Override
    protected JRDataSource getDataSource() {
        Collection dummyCollection = TestRepositoryProducts.getDummyCollection();
        dummyCollection = SortUtils.sortCollection(dummyCollection,dr.getColumns());

        JRDataSource ds = new JRBeanCollectionDataSource(getDummyCollectionSorted(dr.getColumns()));		//Create a JRDataSource, the Collection used
        //here contains dummy hardcoded objects...
        return ds;
    }

    @Override
    public Collection getDummyCollectionSorted(List columnlist) {
        Collection dummyCollection = MyTestRepositoryProducts.getDummyCollectionLarge(100000);
        return SortUtils.sortCollection(dummyCollection, columnlist);

    }
}

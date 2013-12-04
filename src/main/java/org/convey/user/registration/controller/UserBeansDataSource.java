package org.convey.user.registration.controller;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRAbstractBeanDataSourceProvider;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.convey.user.registration.model.User;

import java.util.ArrayList;


public class UserBeansDataSource extends JRAbstractBeanDataSourceProvider {

    public UserBeansDataSource() {
        super(User.class);

    }

    @Override
    public JRField[] getFields(JasperReport report) throws JRException {

        return super.getFields(report);
    }

    @SuppressWarnings("unchecked")
    public JRDataSource create(JasperReport report) throws JRException {

        ArrayList list = new ArrayList();
        User user = new User();
        user.setFirstName("dfdfsdfsd");
        user.setLastName("sdfsdfdsfds");
        user.setId(1);
        user.setEmail("dsfgsdfdsfdsf");
        list.add(user);
        return new JRBeanCollectionDataSource(list);

    }

    public void dispose(JRDataSource dataSource) throws JRException {
        // nothing to do
    }
}
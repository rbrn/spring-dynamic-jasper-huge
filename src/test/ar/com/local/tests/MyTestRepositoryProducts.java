package ar.com.local.tests;

import ar.com.fdvs.dj.test.domain.Product;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: costin
 * Date: 12/14/13
 * Time: 1:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class MyTestRepositoryProducts {

    public static List getDummyCollectionLarge(int count) {

        SimpleDateFormat dateFormat = new SimpleDateFormat();
        dateFormat.applyPattern("dd/MM/yyyy");

        List col = new ArrayList();
        while (count-- > 0)
            col.add(new Product(new Long(count), "book" + count,
                    "Harry Potter 7" + count, "Florida", "Main Street" + count, new Long(count), new Float(count), true));

        return col;
    }
}

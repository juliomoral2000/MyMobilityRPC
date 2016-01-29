package com.enroquesw.mcs.comm.mobilityRPC.client.staticClass;

import com.enroquesw.mcs.comm.mobilityRPC.services.beans.Person;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by Julio on 19/01/2016.
 */
public class Util {
    @SuppressWarnings("unchecked")
    public static Collection<? extends Comparable> createCollection(int numItems) {
        ArrayList<Person> collection = new ArrayList<Person>();
        for (int i = 0; i < numItems; i++) {
            collection.add(new Person(
                    i,
                    "Joe_" + i,
                    "Bloggs_" + i,
                    Arrays.asList("phone_" + (i + 1), "phone_" + (i + 2)),
                    i,
                    "Street_" + i,
                    "City_" + i,
                    "Country_" + i
            ));
        }
        return collection;
    }
}

package com.yahoo.igor.spring;

import com.vaadin.flow.data.provider.AbstractDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;

import java.util.*;
import java.util.stream.Stream;

public class PersonsDataProvider extends AbstractDataProvider<Person, Object> {

    private String filterStr;
    private String comparatorKey = "";
    private ArrayList<Person> p = new ArrayList<>();


    public PersonsDataProvider() {

        for (int i = 0; i < 100000; i++) {
            p.add(new Person( i,"Просто тест " + i, (int)(1732 * Math.random())));
            p.add(new Person( 100000 +i, "John Adams" + i, (int)(1732 * Math.random())));
            p.add(new Person(200000 +i, "Thomas Jefferson" + i, (int)(1732 * Math.random())));
            p.add(new Person(300000 +i, "George Washington" + i, (int)(12344 * Math.random())));

        }
    }

    public String getFilterStr() {
        return filterStr;
    }

    public void setFilterStr(String filterStr) {
        this.filterStr = filterStr;
    }

    @Override
    public boolean isInMemory() {
        return false;
    }

    @Override
    public int size(Query<Person, Object> query) {


        long cnt = p.stream().filter(
                person -> {
                    if (filterStr != null && !"".equals(filterStr)) {
                        return person.getName().contains(filterStr);
                    } else {
                        return true;
                    }
                }
        ).count();
        System.out.println(">>>> size " + query.getOffset() + " " + query.getLimit() + " size is " + cnt);
        return (int) cnt;
    }

    @Override
    public Stream<Person> fetch(Query<Person, Object> query) {
        System.out.println(">>>> fetch " + query.getOffset() + " " + query.getLimit() + " sort order {" + query.getSortOrders() + "}");
        List<QuerySortOrder> querySortOrders = query.getSortOrders();
        comparatorKey = "";
        if ( querySortOrders != null) {
            querySortOrders.stream().forEach(
                    qso -> {
                        comparatorKey = qso.getSorted() + " " + qso.getDirection();
                        System.out.println(">>>> [" + comparatorKey + "]");
                        System.out.println();
                    }
            );
        }

        Map<String, Comparator> comparators = new HashMap<>();
        comparators.put("name ASCENDING", (Comparator<Person>) (o1, o2) -> o1.getName().compareTo(o2.getName()) );
        comparators.put("name DESCENDING", (Comparator<Person>) (o1, o2) -> o2.getName().compareTo(o1.getName()) );
        comparators.put("yearOfBirth ASCENDING", (Comparator<Person>) (o1, o2) -> new Integer(o1.getYearOfBirth()).compareTo(o2.getYearOfBirth()) );
        comparators.put("yearOfBirth DESCENDING", (Comparator<Person>) (o1, o2) -> new Integer(o2.getYearOfBirth()).compareTo(o1.getYearOfBirth()) );


        return p.stream()
                .filter(
                        person -> {
                            if (filterStr != null && !"".equals(filterStr)) {
                                return person.getName().contains(filterStr);
                            } else {
                                return true;
                            }
                        }
                )
                .sorted((Comparator<Person>) comparators.getOrDefault(comparatorKey, (Comparator<Person>) (o1, o2) -> 0))
                .skip(query.getOffset())
                .limit(query.getLimit());
        //return p.stream().limit(5);
    }

                   /* @Override
                    public void refreshItem(Person item) {

                    }*/

                    /*@Override
                    public void refreshAll() {
                        super.ref
                        System.out.println("refreshAll ");
                    }*/

                    /*@Override
                    public Registration addDataProviderListener(DataProviderListener<Person> listener) {
                        System.out.println("addDataProviderListener ");
                        return null;
                    }*/
}

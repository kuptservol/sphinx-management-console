package ru.skuptsov.sphinx.console.dao.common.hibernate.spring.util;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.Hibernate;

public class HibernateInitializer {

    private HibernateInitializer() {}

// todo: it's possible to add optimization for properties[n] n>2, to avoid count to call reflection methods and hibernate.initialize.

    public static void initializeProperties(Object entityOrCollection, String... properties) {
        try {
            for (String complexProperty : properties) {
                StringTokenizer tokenizer = new StringTokenizer(complexProperty, ".");
                List<String> props = new LinkedList<String>();
                while (tokenizer.hasMoreElements()) {
                    props.add((String) tokenizer.nextElement());
                }
                // entry point for recursion function!
                recursiveInitializeProperties(entityOrCollection, props.toArray(new String[]{}), 0);
            }
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

    private static void recursiveInitializeProperties(Object objProperty, String[] props, int idx) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (props.length == idx || objProperty == null) {
            return;
        }

        String property = props[idx];

        if (objProperty instanceof Collection) {

            for (Object item : (Collection) objProperty) {

                Object innerProperty = PropertyUtils.getProperty(item, property);
                Hibernate.initialize(innerProperty);

                recursiveInitializeProperties(innerProperty, props, idx + 1);
            }

            return;
        }

        Object innerProperty = PropertyUtils.getProperty(objProperty, property);
        if (innerProperty != null) {
            Hibernate.initialize(innerProperty);

            recursiveInitializeProperties(innerProperty, props, idx + 1);
        }
    }
}

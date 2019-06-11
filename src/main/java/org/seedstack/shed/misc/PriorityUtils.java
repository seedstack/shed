/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.shed.misc;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.ToIntFunction;

import javax.annotation.Priority;

public final class PriorityUtils {
    private PriorityUtils() {
        // no instantiation allowed
    }

    
    /***
     * A specialization of {@link #sortByPriority(List, ToIntFunction)} for a list of classes.
     * @param <T> Class definition type
     * @param someClasses the list of classes to sort.
     */
    public static <T extends Class<?>> void sortByPriority(List<T> someClasses) {
        sortByPriority(someClasses, PriorityUtils::priorityOf);
    }

    /**
     * Sort classes by <strong>descending</strong> order of their priority, meaning the class with
     * the higher priority will
     * be the first element of the sorted list. The priority is determined according to the
     * provided priority extractor.
     *
     * @param <T> Ensure type consistency
     * @param someClasses       the list of classes to sort.
     * @param priorityExtractor a function that extract a priority from an item.
     */
    public static <T> void sortByPriority(List<T> someClasses, ToIntFunction<T> priorityExtractor) {
        someClasses.sort(Collections.reverseOrder(Comparator.comparingInt(priorityExtractor)));
    }
    
    
    /**
     * A specialization of {@link #sortByPriority(List, ToIntFunction)} for a list of objects.
     *
     * @param <T> Ensure type consistency
     * @param someObjects the list of objects to sort.
     */
    public static <T extends Object> void sortByClassPriority(List<T> someObjects) {
      sortByPriority(someObjects, PriorityUtils::priorityOfClassOf);
    }


    /**
     * Retrieves the priority of a class by using the value of the {@link Priority} annotation
     * present on the class or on
     * its superclasses. If no annotation is found, the returned priority is 0.
     *
     * @param someClass the class to extract priority from.
     * @return the priority.
     */
    public static int priorityOf(Class<?> someClass) {
        while (someClass != null) {
            Priority annotation = someClass.getAnnotation(Priority.class);
            if (annotation != null) {
                return annotation.value();
            }
            someClass = someClass.getSuperclass();
        }
        return 0;
    }

    /**
     * Calls {@link #priorityOf(Class)} on the class of the specified object.
     *
     * @param object the object from which to extract class from which to extract priority.
     * @return the priority.
     */
    public static int priorityOfClassOf(Object object) {
        return priorityOf(object.getClass());
    }
}

/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.shed.misc;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.seedstack.shed.fixtures.priority.ChildOf10Priority;
import org.seedstack.shed.fixtures.priority.ChildOfNoPriority;
import org.seedstack.shed.fixtures.priority.Negative10Priority;
import org.seedstack.shed.fixtures.priority.NoPriority;
import org.seedstack.shed.fixtures.priority.Priority10;

public class PriorityUtilsTest {

  @Test
  public void testPriorityOf() throws Exception {

    Assertions.assertThat(PriorityUtils.priorityOf(NoPriority.class)).isEqualTo(0);
    Assertions.assertThat(PriorityUtils.priorityOf(ChildOfNoPriority.class)).isEqualTo(0);
    Assertions.assertThat(PriorityUtils.priorityOf(Priority10.class)).isEqualTo(10);
    Assertions.assertThat(PriorityUtils.priorityOf(ChildOf10Priority.class)).isEqualTo(10);
  }

  @Test
  public void testPriorityOfClassOf() throws Exception {

    Assertions.assertThat(PriorityUtils.priorityOfClassOf(new NoPriority())).isEqualTo(0);
    Assertions.assertThat(PriorityUtils.priorityOfClassOf(new ChildOfNoPriority())).isEqualTo(0);
    Assertions.assertThat(PriorityUtils.priorityOfClassOf(new Priority10())).isEqualTo(10);
    Assertions.assertThat(PriorityUtils.priorityOfClassOf(new ChildOf10Priority())).isEqualTo(10);
  }

  @Test
  public void testSortByPriority() throws Exception {

    List<Class<? extends Object>> classes =
        Lists.list(Priority10.class, Negative10Priority.class, NoPriority.class);

    Assertions.assertThat(classes)
        .containsExactly(Priority10.class, Negative10Priority.class, NoPriority.class);
    PriorityUtils.sortByPriority(classes);
    Assertions.assertThat(classes)
        .containsExactly(Priority10.class, NoPriority.class, Negative10Priority.class);
  }
}

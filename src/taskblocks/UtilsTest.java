/*
 * Copyright (C) Jakub Neubauer, 2007
 *
 * This file is part of TaskBlocks
 *
 * TaskBlocks is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * TaskBlocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package taskblocks;

import junit.framework.Assert;
import junit.framework.TestCase;

public class UtilsTest extends TestCase {
	
	public void testAddWorkingDays_1() {
		long monday = 13696;
		Assert.assertEquals(Utils.countFinishTime(monday, 0),  monday+ 0);
		Assert.assertEquals(Utils.countFinishTime(monday, 1),  monday+ 1);
		Assert.assertEquals(Utils.countFinishTime(monday, 2),  monday+ 2);
		Assert.assertEquals(Utils.countFinishTime(monday, 3),  monday+ 3);
		Assert.assertEquals(Utils.countFinishTime(monday, 4),  monday+ 4);
		Assert.assertEquals(Utils.countFinishTime(monday, 5),  monday+ 5);
		Assert.assertEquals(Utils.countFinishTime(monday, 6),  monday+ 6+2);
		Assert.assertEquals(Utils.countFinishTime(monday, 7),  monday+ 7+2);
		Assert.assertEquals(Utils.countFinishTime(monday, 8),  monday+ 8+2);
		Assert.assertEquals(Utils.countFinishTime(monday, 9),  monday+ 9+2);
		Assert.assertEquals(Utils.countFinishTime(monday, 10), monday+10+2);
		Assert.assertEquals(Utils.countFinishTime(monday, 11), monday+11+4);
		Assert.assertEquals(Utils.countFinishTime(monday, 12), monday+12+4);
		Assert.assertEquals(Utils.countFinishTime(monday, 13), monday+13+4);
		Assert.assertEquals(Utils.countFinishTime(monday, 14), monday+14+4);
		Assert.assertEquals(Utils.countFinishTime(monday, 15), monday+15+4);
		Assert.assertEquals(Utils.countFinishTime(monday, 16), monday+16+6);
		Assert.assertEquals(Utils.countFinishTime(monday, 17), monday+17+6);
		Assert.assertEquals(Utils.countFinishTime(monday, 18), monday+18+6);
		Assert.assertEquals(Utils.countFinishTime(monday, 19), monday+19+6);
		Assert.assertEquals(Utils.countFinishTime(monday, 20), monday+20+6);
		Assert.assertEquals(Utils.countFinishTime(monday, 21), monday+21+8);
	}
	public void testAddWorkingDays_2() {
		long tuesday = 13696 + 1;
		Assert.assertEquals(Utils.countFinishTime(tuesday, 0),  tuesday+ 0);
		Assert.assertEquals(Utils.countFinishTime(tuesday, 1),  tuesday+ 1);
		Assert.assertEquals(Utils.countFinishTime(tuesday, 2),  tuesday+ 2);
		Assert.assertEquals(Utils.countFinishTime(tuesday, 3),  tuesday+ 3);
		Assert.assertEquals(Utils.countFinishTime(tuesday, 4),  tuesday+ 4);
		Assert.assertEquals(Utils.countFinishTime(tuesday, 5),  tuesday+ 5+2);
		Assert.assertEquals(Utils.countFinishTime(tuesday, 6),  tuesday+ 6+2);
		Assert.assertEquals(Utils.countFinishTime(tuesday, 7),  tuesday+ 7+2);
		Assert.assertEquals(Utils.countFinishTime(tuesday, 8),  tuesday+ 8+2);
		Assert.assertEquals(Utils.countFinishTime(tuesday, 9),  tuesday+ 9+2);
		Assert.assertEquals(Utils.countFinishTime(tuesday, 10), tuesday+10+4);
		Assert.assertEquals(Utils.countFinishTime(tuesday, 11), tuesday+11+4);
		Assert.assertEquals(Utils.countFinishTime(tuesday, 12), tuesday+12+4);
		Assert.assertEquals(Utils.countFinishTime(tuesday, 13), tuesday+13+4);
		Assert.assertEquals(Utils.countFinishTime(tuesday, 14), tuesday+14+4);
		Assert.assertEquals(Utils.countFinishTime(tuesday, 15), tuesday+15+6);
		Assert.assertEquals(Utils.countFinishTime(tuesday, 16), tuesday+16+6);
		Assert.assertEquals(Utils.countFinishTime(tuesday, 17), tuesday+17+6);
	}
	public void testAddWorkingDays_3() {
		long wednesday = 13696 + 2;
		Assert.assertEquals(Utils.countFinishTime(wednesday, 0),  wednesday+ 0);
		Assert.assertEquals(Utils.countFinishTime(wednesday, 1),  wednesday+ 1);
		Assert.assertEquals(Utils.countFinishTime(wednesday, 2),  wednesday+ 2);
		Assert.assertEquals(Utils.countFinishTime(wednesday, 3),  wednesday+ 3);
		Assert.assertEquals(Utils.countFinishTime(wednesday, 4),  wednesday+ 4+2);
		Assert.assertEquals(Utils.countFinishTime(wednesday, 5),  wednesday+ 5+2);
		Assert.assertEquals(Utils.countFinishTime(wednesday, 6),  wednesday+ 6+2);
		Assert.assertEquals(Utils.countFinishTime(wednesday, 7),  wednesday+ 7+2);
		Assert.assertEquals(Utils.countFinishTime(wednesday, 8),  wednesday+ 8+2);
		Assert.assertEquals(Utils.countFinishTime(wednesday, 9),  wednesday+ 9+4);
		Assert.assertEquals(Utils.countFinishTime(wednesday, 10), wednesday+10+4);
		Assert.assertEquals(Utils.countFinishTime(wednesday, 11), wednesday+11+4);
		Assert.assertEquals(Utils.countFinishTime(wednesday, 12), wednesday+12+4);
		Assert.assertEquals(Utils.countFinishTime(wednesday, 13), wednesday+13+4);
		Assert.assertEquals(Utils.countFinishTime(wednesday, 14), wednesday+14+6);
		Assert.assertEquals(Utils.countFinishTime(wednesday, 15), wednesday+15+6);
		Assert.assertEquals(Utils.countFinishTime(wednesday, 16), wednesday+16+6);
		Assert.assertEquals(Utils.countFinishTime(wednesday, 17), wednesday+17+6);
	}
	public void testAddWorkingDays_4() {
		long thursday = 13696 + 3;
		Assert.assertEquals(Utils.countFinishTime(thursday, 0),  thursday+ 0);
		Assert.assertEquals(Utils.countFinishTime(thursday, 1),  thursday+ 1);
		Assert.assertEquals(Utils.countFinishTime(thursday, 2),  thursday+ 2);
		Assert.assertEquals(Utils.countFinishTime(thursday, 3),  thursday+ 3+2);
		Assert.assertEquals(Utils.countFinishTime(thursday, 4),  thursday+ 4+2);
		Assert.assertEquals(Utils.countFinishTime(thursday, 5),  thursday+ 5+2);
		Assert.assertEquals(Utils.countFinishTime(thursday, 6),  thursday+ 6+2);
		Assert.assertEquals(Utils.countFinishTime(thursday, 7),  thursday+ 7+2);
		Assert.assertEquals(Utils.countFinishTime(thursday, 8),  thursday+ 8+4);
		Assert.assertEquals(Utils.countFinishTime(thursday, 9),  thursday+ 9+4);
		Assert.assertEquals(Utils.countFinishTime(thursday, 10), thursday+10+4);
		Assert.assertEquals(Utils.countFinishTime(thursday, 11), thursday+11+4);
		Assert.assertEquals(Utils.countFinishTime(thursday, 12), thursday+12+4);
		Assert.assertEquals(Utils.countFinishTime(thursday, 13), thursday+13+6);
		Assert.assertEquals(Utils.countFinishTime(thursday, 14), thursday+14+6);
		Assert.assertEquals(Utils.countFinishTime(thursday, 15), thursday+15+6);
		Assert.assertEquals(Utils.countFinishTime(thursday, 16), thursday+16+6);
		Assert.assertEquals(Utils.countFinishTime(thursday, 17), thursday+17+6);
	}
	public void testAddWorkingDays_5() {
		long friday = 13696 + 4;
		Assert.assertEquals(Utils.countFinishTime(friday, 0),  friday+ 0);
		Assert.assertEquals(Utils.countFinishTime(friday, 1),  friday+ 1);
		Assert.assertEquals(Utils.countFinishTime(friday, 2),  friday+ 2+2);
		Assert.assertEquals(Utils.countFinishTime(friday, 3),  friday+ 3+2);
		Assert.assertEquals(Utils.countFinishTime(friday, 4),  friday+ 4+2);
		Assert.assertEquals(Utils.countFinishTime(friday, 5),  friday+ 5+2);
		Assert.assertEquals(Utils.countFinishTime(friday, 6),  friday+ 6+2);
		Assert.assertEquals(Utils.countFinishTime(friday, 7),  friday+ 7+4);
		Assert.assertEquals(Utils.countFinishTime(friday, 8),  friday+ 8+4);
		Assert.assertEquals(Utils.countFinishTime(friday, 9),  friday+ 9+4);
		Assert.assertEquals(Utils.countFinishTime(friday, 10), friday+10+4);
		Assert.assertEquals(Utils.countFinishTime(friday, 11), friday+11+4);
		Assert.assertEquals(Utils.countFinishTime(friday, 12), friday+12+6);
		Assert.assertEquals(Utils.countFinishTime(friday, 13), friday+13+6);
		Assert.assertEquals(Utils.countFinishTime(friday, 14), friday+14+6);
		Assert.assertEquals(Utils.countFinishTime(friday, 15), friday+15+6);
		Assert.assertEquals(Utils.countFinishTime(friday, 16), friday+16+6);
		Assert.assertEquals(Utils.countFinishTime(friday, 17), friday+17+8);
	}

	public void testCountWorkDuration_1() {
		long monday = 13696;
		Assert.assertEquals(Utils.countWorkDuration(monday, monday + 0),  0);
		Assert.assertEquals(Utils.countWorkDuration(monday, monday + 1),  1);
		Assert.assertEquals(Utils.countWorkDuration(monday, monday + 2),  2);
		Assert.assertEquals(Utils.countWorkDuration(monday, monday + 3),  3);
		Assert.assertEquals(Utils.countWorkDuration(monday, monday + 4),  4);
		Assert.assertEquals(Utils.countWorkDuration(monday, monday + 5),  5);
		Assert.assertEquals(Utils.countWorkDuration(monday, monday + 6),  5);
		Assert.assertEquals(Utils.countWorkDuration(monday, monday + 7),  5);
		Assert.assertEquals(Utils.countWorkDuration(monday, monday + 8),  6);
		Assert.assertEquals(Utils.countWorkDuration(monday, monday + 9),  7);
	}
	public void testCountWorkDuration_2() {
		long thusday = 13696 + 1;
		Assert.assertEquals(Utils.countWorkDuration(thusday, thusday + 0),  0);
		Assert.assertEquals(Utils.countWorkDuration(thusday, thusday + 1),  1);
		Assert.assertEquals(Utils.countWorkDuration(thusday, thusday + 2),  2);
		Assert.assertEquals(Utils.countWorkDuration(thusday, thusday + 3),  3);
		Assert.assertEquals(Utils.countWorkDuration(thusday, thusday + 4),  4);
		Assert.assertEquals(Utils.countWorkDuration(thusday, thusday + 5),  4);
		Assert.assertEquals(Utils.countWorkDuration(thusday, thusday + 6),  4);
		Assert.assertEquals(Utils.countWorkDuration(thusday, thusday + 7),  5);
		Assert.assertEquals(Utils.countWorkDuration(thusday, thusday + 8),  6);
		Assert.assertEquals(Utils.countWorkDuration(thusday, thusday + 9),  7);
	}
	public void testCountWorkDuration_3() {
		long wednesday = 13696 + 2;
		Assert.assertEquals(Utils.countWorkDuration(wednesday, wednesday + 0),  0);
		Assert.assertEquals(Utils.countWorkDuration(wednesday, wednesday + 1),  1);
		Assert.assertEquals(Utils.countWorkDuration(wednesday, wednesday + 2),  2);
		Assert.assertEquals(Utils.countWorkDuration(wednesday, wednesday + 3),  3);
		Assert.assertEquals(Utils.countWorkDuration(wednesday, wednesday + 4),  3);
		Assert.assertEquals(Utils.countWorkDuration(wednesday, wednesday + 5),  3);
		Assert.assertEquals(Utils.countWorkDuration(wednesday, wednesday + 6),  4);
		Assert.assertEquals(Utils.countWorkDuration(wednesday, wednesday + 7),  5);
		Assert.assertEquals(Utils.countWorkDuration(wednesday, wednesday + 8),  6);
		Assert.assertEquals(Utils.countWorkDuration(wednesday, wednesday + 9),  7);
	}
	public void testCountWorkDuration_4() {
		long thursday = 13696 + 3;
		Assert.assertEquals(Utils.countWorkDuration(thursday, thursday + 0),  0);
		Assert.assertEquals(Utils.countWorkDuration(thursday, thursday + 1),  1);
		Assert.assertEquals(Utils.countWorkDuration(thursday, thursday + 2),  2);
		Assert.assertEquals(Utils.countWorkDuration(thursday, thursday + 3),  2);
		Assert.assertEquals(Utils.countWorkDuration(thursday, thursday + 4),  2);
		Assert.assertEquals(Utils.countWorkDuration(thursday, thursday + 5),  3);
		Assert.assertEquals(Utils.countWorkDuration(thursday, thursday + 6),  4);
		Assert.assertEquals(Utils.countWorkDuration(thursday, thursday + 7),  5);
		Assert.assertEquals(Utils.countWorkDuration(thursday, thursday + 8),  6);
		Assert.assertEquals(Utils.countWorkDuration(thursday, thursday + 9),  7);
	}
	public void testCountWorkDuration_5() {
		long friday = 13696 + 4;
		Assert.assertEquals(Utils.countWorkDuration(friday, friday + 0),  0);
		Assert.assertEquals(Utils.countWorkDuration(friday, friday + 1),  1);
		Assert.assertEquals(Utils.countWorkDuration(friday, friday + 2),  1);
		Assert.assertEquals(Utils.countWorkDuration(friday, friday + 3),  1);
		Assert.assertEquals(Utils.countWorkDuration(friday, friday + 4),  2);
		Assert.assertEquals(Utils.countWorkDuration(friday, friday + 5),  3);
		Assert.assertEquals(Utils.countWorkDuration(friday, friday + 6),  4);
		Assert.assertEquals(Utils.countWorkDuration(friday, friday + 7),  5);
		Assert.assertEquals(Utils.countWorkDuration(friday, friday + 8),  6);
		Assert.assertEquals(Utils.countWorkDuration(friday, friday + 9),  6);
	}
	public void testCountWorkDuration_6() {
		long saturday = 13696 + 5;
		Assert.assertEquals(Utils.countWorkDuration(saturday, saturday + 0),  0);
		Assert.assertEquals(Utils.countWorkDuration(saturday, saturday + 1),  0);
		Assert.assertEquals(Utils.countWorkDuration(saturday, saturday + 2),  0);
		Assert.assertEquals(Utils.countWorkDuration(saturday, saturday + 3),  1);
		Assert.assertEquals(Utils.countWorkDuration(saturday, saturday + 4),  2);
		Assert.assertEquals(Utils.countWorkDuration(saturday, saturday + 5),  3);
		Assert.assertEquals(Utils.countWorkDuration(saturday, saturday + 6),  4);
		Assert.assertEquals(Utils.countWorkDuration(saturday, saturday + 7),  5);
		Assert.assertEquals(Utils.countWorkDuration(saturday, saturday + 8),  5);
		Assert.assertEquals(Utils.countWorkDuration(saturday, saturday + 9),  5);
		Assert.assertEquals(Utils.countWorkDuration(saturday, saturday + 10),  6);
	}
	public void testCountWorkDuration_7() {
		long sunday = 13696 + 6;
		Assert.assertEquals(Utils.countWorkDuration(sunday, sunday + 0),  0);
		Assert.assertEquals(Utils.countWorkDuration(sunday, sunday + 1),  0);
		Assert.assertEquals(Utils.countWorkDuration(sunday, sunday + 2),  1);
		Assert.assertEquals(Utils.countWorkDuration(sunday, sunday + 3),  2);
		Assert.assertEquals(Utils.countWorkDuration(sunday, sunday + 4),  3);
		Assert.assertEquals(Utils.countWorkDuration(sunday, sunday + 5),  4);
		Assert.assertEquals(Utils.countWorkDuration(sunday, sunday + 6),  5);
		Assert.assertEquals(Utils.countWorkDuration(sunday, sunday + 7),  5);
		Assert.assertEquals(Utils.countWorkDuration(sunday, sunday + 8),  5);
		Assert.assertEquals(Utils.countWorkDuration(sunday, sunday + 9),  6);
		Assert.assertEquals(Utils.countWorkDuration(sunday, sunday + 10),  7);
	}
}

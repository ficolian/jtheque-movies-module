package org.jtheque.movies.persistence.od;

import org.jtheque.movies.persistence.od.impl.MovieImpl;
import org.jtheque.movies.persistence.od.impl.CategoryImpl;
import org.jtheque.movies.persistence.od.able.Movie;
import org.jtheque.movies.persistence.od.able.Category;
import org.jtheque.core.utils.db.DaoNotes;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/*
 * This file is part of JTheque.
 * 	   
 * JTheque is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License. 
 *
 * JTheque is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JTheque.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * @author Baptiste Wicht
 */
public class CategoryImplTest {
    private CategoryImpl category;
    
    @Before
    public void setUp(){
        category = new CategoryImpl();
    }
    
    @Test
    public void testHashCode() {
        category.setId(123);
        category.setTitle("Test");
        
        CategoryImpl category2 = new CategoryImpl("Test");
        category2.setId(123);
        
        assertEquals(category.hashCode(), category2.hashCode());
        
        category.setTitle("Test 1234");
        
        assertFalse(category.hashCode() == category2.hashCode());
    }

    @Test
    public void equals() {
        category.setId(123);
        category.setTitle("Test");
        
        CategoryImpl category2 = new CategoryImpl("Test");
        category2.setId(123);
        
        assertTrue(category.equals(category2));
        assertTrue(category2.equals(category));
        
        category.setTitle("Test 1234");
        
        assertFalse(category.equals(category2));
        assertFalse(category2.equals(category));
    }
}